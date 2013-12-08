package canvas;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JPanel;

/**
 * ClientCanvasPanel represents the actual drawing surface that allows the user to draw
 * on it using tools such as freehand, shapes, etc. and allows the
 * user to erase as well. The canvas communicates with a ServerCanvas
 * to allow collaboration on a whiteboard among multiple users
 * 
 * TODO: Implement message passing protocol for each stroke
 * 
 * @author jlmart88
 *
 */
public class ClientCanvasPanel extends JPanel{

		private static final long serialVersionUID = 1L;
		
		// this counter is used to create a new drawing ID
		// for each new drawing
		private int drawingCounter = 0;
		
		// Image where the user's drawing is stored
		// this Image is only ever updated by messages received from the server
	    private Image drawingBuffer;
	    
	    // this is a list of recent commands done by the user
	    // this list will be iterated through every time a drawing command occurs
	    //		and will be removed from whenever a response is received notifying 
	    //		that the drawing command has been processed
	    private ArrayList<HashMap<String,Image>> recentDrawings;
	    
		private final ClientCanvas canvas;
    	private int X1,X2,Y1,Y2;
    	
    	public ClientCanvasPanel(int width, int height, ClientCanvas canvas) {
    		this.canvas = canvas;
    		recentDrawings = new ArrayList<HashMap<String,Image>>();
            this.setPreferredSize(new Dimension(width, height));
            addDrawingController();
            // note: we can't call makeDrawingBuffer here, because it only
            // works *after* this canvas has been added to a window.  Have to
            // wait until paintComponent() is first called.
        }
    
	    /**
	     * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
	     */
	    @Override
	    public void paintComponent(Graphics g) {
	        // If this is the first time paintComponent() is being called,
	        // make our drawing buffer.
	    	
	        if (drawingBuffer == null) {
	            makeDrawingBuffer();
	        }
	        
	        g.drawImage(drawingBuffer, 0, 0, null);
	        
	        for (HashMap<String,Image> drawing: recentDrawings){
	        	for (String drawingID: drawing.keySet()){
	        		g.drawImage(drawing.get(drawingID), 0, 0, null);
	        	}
	        }
	        
	        // Copy the drawing buffer to the screen.
	        if (canvas.getCurrentTool().equals(ClientCanvas.ERASE_BUTTON)||canvas.getCurrentTool().equals(ClientCanvas.PENCIL_BUTTON)){

	        }
	        else if (canvas.getCurrentTool().equals(ClientCanvas.LINE_BUTTON)){
	        	
	        	g.setColor(canvas.getColor());
		        ((Graphics2D) g).setStroke(canvas.getStroke());
	        	g.drawLine(X1, Y1, X2, Y2);
	        }
	        else if (canvas.getCurrentTool().equals(ClientCanvas.SHAPE_BUTTON)){
	        	drawShapeSegment(X1, Y1, X2, Y2, (Graphics2D) g);
	        }
	    }
	    
	    /*
	     * Make the drawing buffer and draw some starting content for it.
	     */
	    private void makeDrawingBuffer() {
	        drawingBuffer = createImage(canvas.getWidth(), canvas.getHeight());
	        fillWithWhite((Graphics2D) drawingBuffer.getGraphics());
	    }
	    
	    /*
	     * Make the graphics entirely white.
	     * 
	     */
	    private void fillWithWhite(Graphics2D g) {
	    	
	        g.setColor(Color.WHITE);
	        g.fillRect(0,  0,  getWidth(), getHeight());
	        
	        // IMPORTANT!  every time we draw on the internal drawing buffer, we
	        // have to notify Swing to repaint this component on the screen.
	        this.repaint();
	    }
	    
	    /*
	     * Processes a click of the erase all button 
	     */
	    public synchronized void eraseAll() {
	    	String currentDrawingID = createNewDrawing();
	    	fillWithWhite((Graphics2D) recentDrawings.get(recentDrawings.size()-1).get(currentDrawingID).getGraphics());		
	    	
	    };
	    
	    /*
	     * Draw a line between two points (x1, y1) and (x2, y2), specified in
	     * pixels relative to the upper-left corner of the drawing buffer.
	     * 
	     * Uses information from the selected color and width
	     */
	    private void drawPencilSegment(int x1, int y1, int x2, int y2, Graphics2D g) {
	    	
	        g.setColor(canvas.getColor());
	        g.setStroke(canvas.getStroke());
	        g.drawLine(x1, y1, x2, y2);
	        
	        // IMPORTANT!  every time we draw on the internal drawing buffer, we
	        // have to notify Swing to repaint this component on the screen.
	        this.repaint();
	    }
	    
	    /*
	     * Draws temporarily between two points (x1, y1) and (x2, y2), specified in
	     * pixels relative to the upper-left corner of the drawing buffer, but does not
	     * finalize the draw (the draw should be finalized in a mouseReleased event)
	     * 
	     * Uses information from the selected color and width
	     */
	    private void drawTempSegment(int x1, int y1, int x2, int y2) {
	        X1=x1;
	        X2=x2;
	        Y1=y1;
	        Y2=y2;
	        // IMPORTANT!  every time we draw on the internal drawing buffer, we
	        // have to notify Swing to repaint this component on the screen.
	        this.repaint();
	        
	    }
	    
	    /*
	     * Draw a shape between two points (x1, y1) and (x2, y2), specified in
	     * pixels relative to the upper-left corner of the drawing buffer
	     * 
	     * Uses information from the selected color and width
	     */
	    private void drawShapeSegment(int x1, int y1, int x2, int y2, Graphics2D g) {
	    	
	    	boolean fillShape = canvas.isShapeFilled();
        	g.setColor(canvas.getColor());
	        g.setStroke(canvas.getStroke());
	        
	        
	        int xOrigin = Math.min(x2, x1);
	        int yOrigin = Math.min(y2, y1);
	        int xLength = Math.abs(x2-x1);
	        int yLength = Math.abs(y2-y1);
	        
	        if (canvas.getSelectedShape().equals(ClientCanvas.RECTANGLE)){
	        	if (fillShape){
	        		g.fillRect(xOrigin, yOrigin, xLength, yLength);
	        	}
	        	else {
	        		g.drawRect(xOrigin, yOrigin, xLength, yLength); 
	        	}
	        }
	        else if (canvas.getSelectedShape().equals(ClientCanvas.OVAL)){
	        	if (fillShape){
	        		g.fillOval(xOrigin, yOrigin, xLength, yLength);
	        	}
	        	else {
	        		g.drawOval(xOrigin, yOrigin, xLength, yLength);
	        	}
	        }
	        
	        // make changes to the points to force equal sides
	        if (x2>x1&&y2>y1){// 4th quadrant
	        	if (xLength<=yLength){
	        		yLength = xLength;
	        	}
	        	else{
	        		xLength = yLength;
	        	}
	        }
	        else if (x2<x1&&y2>y1){// 3rd quadrant
	        	if (xLength<=yLength){
	        		yLength = xLength;
	        	}
	        	else{
	        		xOrigin = x1-yLength;
	        		xLength = yLength;
	        	}
	        }
	        else if (x2<x1&&y2<y1){// 2nd quadrant
	        	if (xLength<=yLength){
	        		yOrigin = y1-xLength;
	        		yLength = xLength;
	        	}
	        	else{
	        		xOrigin = x1-yLength;
	        		xLength = yLength;
	        	}
	        }
	        else if (x2>x1&&y2<y1){// 1st quadrant
	        	if (xLength<=yLength){
	        		yOrigin = y1-xLength;
	        		yLength = xLength;
	        	}
	        	else{
	        		xLength = yLength;
	        	}
	        }
	        if (canvas.getSelectedShape().equals(ClientCanvas.SQUARE)){
	        	if (fillShape){
	        		g.fillRect(xOrigin, yOrigin, xLength, yLength);
	        	}
	        	else {
	        		g.drawRect(xOrigin, yOrigin, xLength, yLength); 
	        	}
	        }
	        
	        else if (canvas.getSelectedShape().equals(ClientCanvas.CIRCLE)){
	        	if (fillShape){
	        		g.fillOval(xOrigin, yOrigin, xLength, yLength);
	        	}
	        	else {
	        		g.drawOval(xOrigin, yOrigin, xLength, yLength);
	        	}
	        }
	        
	        // IMPORTANT!  every time we draw on the internal drawing buffer, we
	        // have to notify Swing to repaint this component on the screen.
	        
	        this.repaint();
	        
	    }

	    
	    /*
	     * Erases a line between two points (x1, y1) and (x2, y2), specified in
	     * pixels relative to the upper-left corner of the drawing buffer.
	     * 
	     * Uses information from the selected width
	     */
	    private void drawEraseSegment(int x1, int y1, int x2, int y2, Graphics2D g) {
	        
	        g.setColor(Color.white);
	        g.setStroke(canvas.getStroke());
	        g.drawLine(x1, y1, x2, y2);
	        
	        // IMPORTANT!  every time we draw on the internal drawing buffer, we
	        // have to notify Swing to repaint this component on the screen.
	        this.repaint();
	    }
	    
	    /*
	     * Add the mouse listener that supports the user's freehand drawing.
	     */
	    private void addDrawingController() {
	        DrawingController controller = new DrawingController();
	        addMouseListener(controller);
	        addMouseMotionListener(controller);
	    }
	    
	    /**
	     * Adds a new Drawing to the list of currentDrawings, and returns the ID
	     * to access it by
	     * 
	     * @return String of the Id of the drawing
	     */
	    private synchronized String createNewDrawing() {
	    	drawingCounter += 1;
	    	Image thisDrawingImage = new BufferedImage(getWidth(),getHeight(),BufferedImage.TYPE_INT_ARGB);
            String thisDrawingID = canvas.getUserID()+drawingCounter;
            HashMap<String,Image> thisDrawing = new HashMap<String,Image>();
            thisDrawing.put(thisDrawingID, thisDrawingImage);
            recentDrawings.add(thisDrawing);
	    	return thisDrawingID;
	    }
	    
	    /*
	     * DrawingController handles the user's freehand drawing.
	     */
	    private class DrawingController implements MouseListener, MouseMotionListener {
	        // store the coordinates of the last mouse event, so we can
	        // draw a line segment from that last point to the point of the next mouse event.
	        private int lastX, lastY;
	        private String currentDrawingID;
	        
	
	        /*
	         * When mouse button is pressed down, start drawing.
	         */
	        public void mousePressed(MouseEvent e) {
	            lastX = e.getX();
	            lastY = e.getY();
	            currentDrawingID = createNewDrawing();
	        }
	
	        /*
	         * When mouse moves while a button is pressed down,
	         * draw a line segment.
	         */
	        public void mouseDragged(MouseEvent e) {
	        	// Take the min to prevent from drawing off of the screen
	            int x = Math.min(e.getX(),getWidth());
	            int y = Math.min(e.getY(),getHeight());
	            Graphics2D currentDrawingGraphics = (Graphics2D) recentDrawings.get(recentDrawings.size()-1).get(currentDrawingID).getGraphics();
	            
	            if (ClientCanvas.PENCIL_BUTTON.equals(canvas.getCurrentTool())){
		            drawPencilSegment(lastX, lastY, x, y, currentDrawingGraphics);
		            lastX = x;
		            lastY = y;
	            }
	            if (ClientCanvas.ERASE_BUTTON.equals(canvas.getCurrentTool())){
	            	drawEraseSegment(lastX, lastY, x, y, currentDrawingGraphics);
		            lastX = x;
		            lastY = y;
	            }
	            if (ClientCanvas.LINE_BUTTON.equals(canvas.getCurrentTool())){
	            	drawTempSegment(lastX, lastY, x, y);
	            }
	            if (ClientCanvas.SHAPE_BUTTON.equals(canvas.getCurrentTool())){
	            	drawTempSegment(lastX, lastY, x, y);
	            }
	        }
	        
	        public void mouseReleased(MouseEvent e) { 
	        	// Take the min to prevent from drawing off of the screen
	            int x = Math.min(e.getX(),getWidth());
	            int y = Math.min(e.getY(),getHeight());
	            Graphics2D currentDrawingGraphics = (Graphics2D) recentDrawings.get(recentDrawings.size()-1).get(currentDrawingID).getGraphics();
	            
	            
	            if (ClientCanvas.PENCIL_BUTTON.equals(canvas.getCurrentTool())){
		            drawPencilSegment(lastX, lastY, lastX, lastY, currentDrawingGraphics);
	            }
	            if (ClientCanvas.ERASE_BUTTON.equals(canvas.getCurrentTool())){
	            	drawEraseSegment(lastX, lastY, lastX, lastY, currentDrawingGraphics);
	            }
	            if (ClientCanvas.LINE_BUTTON.equals(canvas.getCurrentTool())){
	            	drawPencilSegment(lastX, lastY, x, y, currentDrawingGraphics);
	            }
	            if (ClientCanvas.SHAPE_BUTTON.equals(canvas.getCurrentTool())){
	            	drawShapeSegment(lastX, lastY, x, y, currentDrawingGraphics);
	            }
	            X1=X2=Y1=Y2=Integer.MAX_VALUE;// set these to be arbitrarily off of the drawing screen
	        }
	
	        // Ignore all these other mouse events.
	        public void mouseMoved(MouseEvent e) { }
	        public void mouseClicked(MouseEvent e) { }
	        public void mouseEntered(MouseEvent e) { }
	        public void mouseExited(MouseEvent e) { }
	    }
    }

