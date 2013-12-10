package canvas;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Line2D;
import java.util.ArrayList;

import javax.swing.JPanel;

/**
 * ClientCanvasPanel represents the actual drawing surface that allows the user to draw
 * on it using tools such as freehand, shapes, etc. and allows the
 * user to erase as well. The canvas communicates with a ServerCanvas
 * to allow collaboration on a whiteboard among multiple users
 * 
 * TODO: Write tests for message creation
 * TODO: Write tests for message receiving
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
		// this Image is only updated by messages received from the server
	    private Image drawingBuffer;
	    
	    // this is a list of recent commands done by the user
	    // this list will be iterated through every time a drawing command occurs
	    //		and will be removed from whenever a response is received notifying 
	    //		that the drawing command has been processed
	    private ArrayList<DrawingLayer> recentDrawings;
	    
		private final ClientCanvas canvas;
    	private int X1,X2,Y1,Y2;
    	
    	public ClientCanvasPanel(int width, int height, ClientCanvas canvas) {
    		this.canvas = canvas;
    		recentDrawings = new ArrayList<DrawingLayer>();
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
	        // make our drawing buffer
	        if (drawingBuffer == null) {
	            makeDrawingBuffer();
	        }
	        
	        // draw the buffer which has been updated by the server
	        g.drawImage(drawingBuffer, 0, 0, null);
	        
	        // iterate through and draw all of the client drawings 
	        // that haven't been processed by the server yet
	        for (DrawingLayer drawing: recentDrawings){
	        	g.drawImage(drawing.getImage(), 0, 0, null);
	        }
	        
	        // if we are in line/shape mode, draw on top of everything else 
	        // using the coordinates being updated by the mouse listener, since the
	        // line/shape has not been finalized yet
	        if (canvas.getCurrentTool().equals(ClientCanvas.LINE_BUTTON)){
	        	drawPencilSegment(X1, Y1, X2, Y2, (Graphics2D) g);
	        }
	        else if (canvas.getCurrentTool().equals(ClientCanvas.SHAPE_BUTTON)){
	        	drawShapeSegment(X1, Y1, X2, Y2, (Graphics2D) g);
	        }
	    }
	    
	    /*
	     * Make the drawing buffer and fill it with white
	     */
	    private synchronized void makeDrawingBuffer() {
	        drawingBuffer = createImage(canvas.getWidth(), canvas.getHeight());
	        fillWithWhite((Graphics2D) drawingBuffer.getGraphics());
	    }
	    
	    /*
	     * Make the graphics entirely white.
	     * 
	     */
	    private synchronized void fillWithWhite(Graphics2D g) {
	    	
	        g.setColor(Color.WHITE);
	        g.fillRect(0,  0,  getWidth(), getHeight());
	        
	        // IMPORTANT!  every time we draw on the internal drawing buffer, we
	        // have to notify Swing to repaint this component on the screen.
	        this.repaint();
	    }
	    
	    /*
	     * Process a click of the erase all button 
	     */
	    public synchronized void eraseAll() {
	    	DrawingLayer currentDrawing = createNewDrawing(true);
	    	fillWithWhite((Graphics2D) currentDrawing.getImage().getGraphics());
	    	
	    	//SEND MESSAGE HERE TOO
	    	System.out.println(currentDrawing.createMessage());
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
	     * Uses information from the currently selected color and width in the GUI
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
	     * Uses information from the currently selected color and width in the GUI
	     */
	    private void drawShapeSegment(int x1, int y1, int x2, int y2, Graphics2D g) {
	    	
	    	boolean fillShape = canvas.isShapeFilled();
        	g.setColor(canvas.getColor());
	        g.setStroke(canvas.getStroke());
	        
	        if (canvas.getSelectedShape().equals(ClientCanvas.SQUARE) || canvas.getSelectedShape().equals(ClientCanvas.CIRCLE)){
	        	Line2D line = getSquareCoordinates(x1, y1, x2, y2);
	        	x1 = (int) line.getX1();
	        	x2 = (int) line.getX2();
	        	y1 = (int) line.getY1();
	        	y2 = (int) line.getY2();
	        }
	        
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
	    
	    /**
	     * Adds a new DrawingLayer to the list of currentDrawings, and
	     * returns it
	     * 
	     * @param eraseAll an optional boolean that signifies 
	     * 		that this Drawing erased the whole canvas
	     * @return DrawingLayer the drawing just added
	     */
	    private synchronized DrawingLayer createNewDrawing() {
	    	drawingCounter += 1;
	    	recentDrawings.add(new DrawingLayer(canvas.getUserID()+drawingCounter, this.getWidth(), 
	    			this.getHeight(), canvas.getColor(), canvas.getStroke(), canvas.getCurrentTool(), 
	    			canvas.getSelectedShape(), canvas.isShapeFilled()));
            
	    	return recentDrawings.get(recentDrawings.size()-1);
	    }
	    private synchronized DrawingLayer createNewDrawing(boolean eraseAll){
	    	drawingCounter += 1;
	    	recentDrawings.add(new DrawingLayer(canvas.getUserID()+drawingCounter, this.getWidth(), 
	    			this.getHeight(), canvas.getColor(), canvas.getStroke(), canvas.ERASE_ALL_BUTTON, 
	    			canvas.getSelectedShape(), canvas.isShapeFilled()));
	    	return recentDrawings.get(recentDrawings.size()-1);
	    }
	    
	    /**
	     * Converts two points to represent the diagonal of a square 
	     * (x1,y1) remains as one corner of the square
	     * 
	     * @param x1
	     * @param y1
	     * @param x2
	     * @param y2
	     * @return
	     */
	    public static Line2D getSquareCoordinates(int x1, int y1, int x2, int y2){
	    	
	    	int xOrigin = Math.min(x2, x1);
	        int yOrigin = Math.min(y2, y1);
	        int xLength = Math.abs(x2-x1);
	        int yLength = Math.abs(y2-y1);

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
	    	
	    	return new Line2D.Float(new Point(xOrigin, yOrigin), new Point(xOrigin+xLength, yOrigin+yLength));
	    }

	    
	    /*
	     * Add the mouse listener that supports the user's drawing.
	     */
	    private void addDrawingController() {
	        DrawingController controller = new DrawingController();
	        addMouseListener(controller);
	        addMouseMotionListener(controller);
	    }
	    
	    /*
	     * DrawingController handles the user's drawing and mouse events.
	     */
	    private class DrawingController implements MouseListener, MouseMotionListener {
	        // store the coordinates of the last mouse event, so we can
	        // draw a line segment from that last point to the point of the next mouse event.
	        private int lastX, lastY;
	        private DrawingLayer currentDrawing;     
	
	        /*
	         * When mouse button is pressed down, start drawing.
	         */
	        public void mousePressed(MouseEvent e) {
	            lastX = e.getX();
	            lastY = e.getY();
	            currentDrawing = createNewDrawing();
	            currentDrawing.addPoint(lastX, lastY);
	        }
	
	        /*
	         * When mouse moves while a button is pressed down,
	         * draw depending on what tool is selected.
	         */
	        public void mouseDragged(MouseEvent e) {
	        	// Take the min to prevent from drawing off of the screen
	            int x = Math.min(e.getX(),getWidth());
	            int y = Math.min(e.getY(),getHeight());
	            
	            // Get the current drawing layer
	            Graphics2D currentDrawingGraphics = (Graphics2D) currentDrawing.getImage().getGraphics();
	            
	            // if we are in pencil/erase, we want to keep a running list of points, so
	            // continue adding to the drawings list of points
	            if (ClientCanvas.PENCIL_BUTTON.equals(canvas.getCurrentTool())){
		            drawPencilSegment(lastX, lastY, x, y, currentDrawingGraphics);
		            lastX = x;
		            lastY = y;
		            currentDrawing.addPoint(x, y);
	            }
	            else if (ClientCanvas.ERASE_BUTTON.equals(canvas.getCurrentTool())){
	            	drawEraseSegment(lastX, lastY, x, y, currentDrawingGraphics);
		            lastX = x;
		            lastY = y;
		            currentDrawing.addPoint(x, y);
	            }
	            else if (ClientCanvas.LINE_BUTTON.equals(canvas.getCurrentTool())){
	            	drawTempSegment(lastX, lastY, x, y);
	            }
	            else if (ClientCanvas.SHAPE_BUTTON.equals(canvas.getCurrentTool())){
	            	drawTempSegment(lastX, lastY, x, y);
	            }
	        }
	        
	        public void mouseReleased(MouseEvent e) { 
	        	// Take the min to prevent from drawing off of the screen
	            int x = Math.min(e.getX(),getWidth());
	            int y = Math.min(e.getY(),getHeight());
	            
	            // Get the current drawing layer
	            Graphics2D currentDrawingGraphics = (Graphics2D) currentDrawing.getImage().getGraphics();
	     	            
	            if (ClientCanvas.PENCIL_BUTTON.equals(canvas.getCurrentTool())){
		            drawPencilSegment(lastX, lastY, lastX, lastY, currentDrawingGraphics);
	            }
	            else if (ClientCanvas.ERASE_BUTTON.equals(canvas.getCurrentTool())){
	            	drawEraseSegment(lastX, lastY, lastX, lastY, currentDrawingGraphics);
	            }
	            else if (ClientCanvas.LINE_BUTTON.equals(canvas.getCurrentTool())){
	            	drawPencilSegment(lastX, lastY, x, y, currentDrawingGraphics);
	            }
	            else if (ClientCanvas.SHAPE_BUTTON.equals(canvas.getCurrentTool())){
	            	drawShapeSegment(lastX, lastY, x, y, currentDrawingGraphics);
	            }
	            currentDrawing.addPoint(x, y);
	            
	            // THIS IS WHERE WE WILL SEND THE MESSAGE
	            System.out.println(currentDrawing.createMessage());
	            //
	            
	            X1=X2=Y1=Y2=Integer.MAX_VALUE;// set these to be arbitrarily off of the drawing screen once weve finalized the drawing
	        }
	
	        // Ignore all these other mouse events.
	        public void mouseMoved(MouseEvent e) { }
	        public void mouseClicked(MouseEvent e) { }
	        public void mouseEntered(MouseEvent e) { }
	        public void mouseExited(MouseEvent e) { }
	    }
    }

