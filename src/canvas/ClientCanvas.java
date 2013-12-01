package canvas;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.SwingUtilities;

/**
 * Canvas represents a drawing surface that allows the user to draw
 * on it using tools such as freehand, shapes, etc. and allows the
 * user to erase as well. The canvas communicates with a ServerCanvas
 * to allow collaboration on a whiteboard among multiple users
 * 
 * Differences from Canvas:
 * This is a JFrame, not a JPanel. There is a JPanel subclass inside: whiteboardPanel
 * 
 * 
 * 
 * TODO: Implement message passing protocol for each stroke
 * TODO: Change interface to use icons instead of text
 * TODO: Make interface more user friendly
 * TODO: Fix the stuff that happens on the size after resizing the window
 * 
 * 
 */
public class ClientCanvas extends JFrame{
	
	// image where the user's drawing is stored
    private Image drawingBuffer;
    
    private final ClientCanvasPanel whiteboardPanel;
    
    // whiteboard tools
    private final JToggleButton pencilModeButton;
    private final JToggleButton eraseModeButton;
    private final JToggleButton lineModeButton;
    private final JToggleButton shapeModeButton;
    private final JToggleButton[] toolButtonList;
    private final JToggleButton shapeFilledButton;
    private final JComboBox<String> shapeSelectionBox;
    private final JComboBox<String> widthSelectionBox;
    private final JLabel currentColorLabel;
    private final JButton eraseAllButton;
    
    private final String[] shapeOptions = {"Square", "Rectangle", "Circle", "Oval"};
    private final String[] widthOptions = {"5px", "10px", "20px"};
    
    
    
    /**
     * Make a canvas.
     * @param width width in pixels
     * @param height height in pixels
     */
    public ClientCanvas(int width, int height) {
    	
    	this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        whiteboardPanel = new ClientCanvasPanel(width,height);
        pencilModeButton = new JToggleButton("Pencil",  true);
        eraseModeButton = new JToggleButton("Erase",  false);
        lineModeButton = new JToggleButton("Line",  false);
        shapeModeButton = new JToggleButton("Shape",  false);
        toolButtonList = new JToggleButton[]{pencilModeButton, eraseModeButton, lineModeButton, shapeModeButton};
        shapeFilledButton = new JToggleButton("Filled",  false);
        shapeSelectionBox = new JComboBox<String>(shapeOptions);
        widthSelectionBox = new JComboBox<String>(widthOptions);

        currentColorLabel = new JLabel();
        currentColorLabel.setBackground(Color.black); // starting color is black
        currentColorLabel.setMinimumSize(new Dimension(30,30));
        currentColorLabel.setOpaque(true);
        
        eraseAllButton = new JButton("Erase All");
        
        pencilModeButton.setName("pencilModeButton");
        eraseModeButton.setName("eraseModeButton");
        lineModeButton.setName("lineModeButton");
        shapeModeButton.setName("shapeModeButton");
        shapeFilledButton.setName("shapeFilledButton");
        shapeSelectionBox.setName("shapeSelectionBox");
        widthSelectionBox.setName("widthSelectionBox");
        currentColorLabel.setName("currentColorLabel");
        eraseAllButton.setName("eraseAllButton");
        
        // set up the toolbar
        Container mainPanel = this.getContentPane();
        GroupLayout layout = new GroupLayout(mainPanel);
        mainPanel.setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);
        
        layout.setHorizontalGroup(
        		layout.createParallelGroup(GroupLayout.Alignment.LEADING)
        			.addGroup(layout.createSequentialGroup()
        					.addComponent(pencilModeButton)
        					.addComponent(eraseModeButton)
        					.addComponent(lineModeButton)
        					.addComponent(shapeModeButton)
        					.addComponent(shapeFilledButton)
        					.addComponent(shapeSelectionBox)
        					.addComponent(widthSelectionBox)
        					.addComponent(currentColorLabel)
        					.addComponent(eraseAllButton)
        					)
        			.addComponent(whiteboardPanel)
        );
        layout.setVerticalGroup(
        		layout.createSequentialGroup()
        			.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
        					.addComponent(pencilModeButton)
        					.addComponent(eraseModeButton)
        					.addComponent(lineModeButton)
        					.addComponent(shapeModeButton)
        					.addComponent(shapeFilledButton)
        					.addComponent(shapeSelectionBox)
        					.addComponent(widthSelectionBox)
        					.addComponent(currentColorLabel)
        					.addComponent(eraseAllButton)
        					)
        			.addComponent(whiteboardPanel)
        );
        this.pack();
        //finish toolbar setup
        
        // this ActionListener is used to handle the switching between tools
        ActionListener toggleButton = new ActionListener() {
        	public void actionPerformed(ActionEvent ae) {
        		final ActionEvent action = ae;
            	// invokeLater because we are making changes to the GUI
            	SwingUtilities.invokeLater(new Runnable() {
            		public void run() {
            			toggleSelectedTool((JToggleButton) action.getSource());
            		}
            	});
        	}
        };
        
        // add the toggleButton ActionListener to the tool options
        pencilModeButton.addActionListener(toggleButton);
        eraseModeButton.addActionListener(toggleButton);
        lineModeButton.addActionListener(toggleButton);
        shapeModeButton.addActionListener(toggleButton);
        
        // set up a listener to the color selector
        currentColorLabel.addMouseListener(new MouseListener() {
        	public void mouseClicked(MouseEvent ae) {
        		Color newColor = JColorChooser.showDialog(ClientCanvas.this,
        												"Choose Drawing Color",
        												getColor());
        		
        		if (!(newColor==null)) {
        			currentColorLabel.setBackground(newColor);
        		}
        	}
        	
        	// we are only concerned with the color icon being clicked
			public void mousePressed(MouseEvent e) {}
			public void mouseReleased(MouseEvent e) {}
			public void mouseEntered(MouseEvent e) {}
			public void mouseExited(MouseEvent e) {}
        });
        
        // set up a listener to the erase all button
        eraseAllButton.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent ae){
        		String[] options = {"Yes, erase everything", "No, don't erase everything"};
        		
        		int n = JOptionPane.showOptionDialog(ClientCanvas.this,
        				"Are you sure you want to erase everything? This will clear the whiteboard " +
        						"for all users.",
        						"Warning",
        						JOptionPane.YES_NO_OPTION,
        					    JOptionPane.WARNING_MESSAGE,
        					    null,
        					    options,
        					    options[0]);
        		if (n==0){
        			whiteboardPanel.fillWithWhite();
        		}
        		
        	}
        });
        
        
        
    }
    
    /**
     * Will switch the selected tool to the one described by "button" by
     * deselecting the other toggle buttons
     * 
     * Current tools that are affected by this:
     * pencilModeButton
     * eraseModeButton
     * lineModeButton
     * shapeModeButton
     * 
     * @param button JToggleButton to switch the selected tool to
     */
    private void toggleSelectedTool(JToggleButton button){
    	if (button.isSelected()){
    		for (JToggleButton otherButton: toolButtonList){
    			if (!otherButton.equals(button)){
    				otherButton.setSelected(false);
    			}
    		}
    	}
    	else {
    		button.setSelected(true);
    	}
    };
    
    /**
     * Returns a Color object representing the currently selected color
     * 
     * @return Color object representing the currently selected color in the interface
     */
    private Color getColor(){
    	return currentColorLabel.getBackground();
    }
    
    /**
     * Returns a Stroke object representing the currently selected stroke type
     * 
     * @return Stroke object representing the currently selected stroke type in the interface
     */
    private Stroke getStroke(){
    	return new BasicStroke(Integer.parseInt(widthOptions[widthSelectionBox.getSelectedIndex()]
    			.replaceAll("px", "")),
    			BasicStroke.CAP_ROUND, 
    			BasicStroke.JOIN_ROUND);
    }
    
    /**
     * Returns a String representing the currently selected shape type
     * 
     * @return String object representing the currently selected shape type in the interface
     */
    private String getSelectedShape(){
    	return shapeOptions[shapeSelectionBox.getSelectedIndex()];
    }
    
    /**
     * Returns a JToggleButton representing the currently selected tool
     * 
     * @return JToggleButton representing the currently selected tool in the interface
     */
    private JToggleButton getCurrentTool(){
    	for (JToggleButton button: toolButtonList){
    		if (button.isSelected()) return button;
    	}
    	// should never get here, but if it does, default to being in pencil mode
    	return pencilModeButton;
    }
    
    /*
     * The class which contains the actual drawing surface for 
     * the Client Canvas
     * 
     */
    private class ClientCanvasPanel extends JPanel{
    	
    	private int X1,X2,Y1,Y2;
    	
    	public ClientCanvasPanel(int width, int height) {
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
	        
	        // Copy the drawing buffer to the screen.
	        if (getCurrentTool().equals(eraseModeButton)||getCurrentTool().equals(pencilModeButton)){
	        	g.drawImage(drawingBuffer, 0, 0, null);
	        }
	        else if (getCurrentTool().equals(lineModeButton)){
	        	g.drawImage(drawingBuffer, 0, 0, null);
	        	g.setColor(getColor());
		        ((Graphics2D) g).setStroke(getStroke());
	        	g.drawLine(X1, Y1, X2, Y2);
	        }
	        else if (getCurrentTool().equals(shapeModeButton)){
	        	drawShapeSegment(X1, Y1, X2, Y2, (Graphics2D) g);
	        }
	    }
	    
	    /*
	     * Make the drawing buffer and draw some starting content for it.
	     */
	    private void makeDrawingBuffer() {
	        drawingBuffer = createImage(getWidth(), getHeight());
	        fillWithWhite();
	    }
	    
	    /*
	     * Make the drawing buffer entirely white.
	     * 
	     * g is an optional argument, it will default to using drawingBuffer.getGraphics()
	     */
	    private void fillWithWhite(Graphics2D g) {
	        if (g==null){
	        	g = (Graphics2D) drawingBuffer.getGraphics();
	        }
	
	        g.setColor(Color.WHITE);
	        g.fillRect(0,  0,  getWidth(), getHeight());
	        
	        // IMPORTANT!  every time we draw on the internal drawing buffer, we
	        // have to notify Swing to repaint this component on the screen.
	        this.repaint();
	    }
	    private void fillWithWhite() {
	    	fillWithWhite(null);
	    }
	    
	    /*
	     * Draw a line between two points (x1, y1) and (x2, y2), specified in
	     * pixels relative to the upper-left corner of the drawing buffer.
	     * 
	     * Uses information from the selected color and width
	     */
	    private void drawPencilSegment(int x1, int y1, int x2, int y2) {
	        Graphics2D g = (Graphics2D) drawingBuffer.getGraphics();
	        
	        g.setColor(getColor());
	        g.setStroke(getStroke());
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
	     * g is an optional argument, it will default to using drawingBuffer.getGraphics()
	     * 
	     * Uses information from the selected color and width
	     */
	    private void drawShapeSegment(int x1, int y1, int x2, int y2, Graphics2D g) {
	    	boolean repaint = false;
	    	if (g==null){
	    		g = (Graphics2D) drawingBuffer.getGraphics();
	    		repaint = true;
	    	}
	    	
	    	g.drawImage(drawingBuffer, 0, 0, null);
	    	boolean fillShape = shapeFilledButton.isSelected();
        	g.setColor(getColor());
	        g.setStroke(getStroke());
	        
	        
	        int xOrigin = Math.min(x2, x1);
	        int yOrigin = Math.min(y2, y1);
	        int xLength = Math.abs(x2-x1);
	        int yLength = Math.abs(y2-y1);
	        
	        if (getSelectedShape().equals("Rectangle")){
	        	if (fillShape){
	        		g.fillRect(xOrigin, yOrigin, xLength, yLength);
	        	}
	        	else {
	        		g.drawRect(xOrigin, yOrigin, xLength, yLength); 
	        	}
	        }
	        else if (getSelectedShape().equals("Oval")){
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
	        if (getSelectedShape().equals("Square")){
	        	if (fillShape){
	        		g.fillRect(xOrigin, yOrigin, xLength, yLength);
	        	}
	        	else {
	        		g.drawRect(xOrigin, yOrigin, xLength, yLength); 
	        	}
	        }
	        
	        else if (getSelectedShape().equals("Circle")){
	        	if (fillShape){
	        		g.fillOval(xOrigin, yOrigin, xLength, yLength);
	        	}
	        	else {
	        		g.drawOval(xOrigin, yOrigin, xLength, yLength);
	        	}
	        }
	        
	        // IMPORTANT!  every time we draw on the internal drawing buffer, we
	        // have to notify Swing to repaint this component on the screen.
	        if (repaint){
	        	this.repaint();
	        };
	    }
	    private void drawShapeSegment(int x1, int y1, int x2, int y2){
	    	drawShapeSegment(x1, y1, x2, y2, null);
	    }
	    
	    /*
	     * Erases a line between two points (x1, y1) and (x2, y2), specified in
	     * pixels relative to the upper-left corner of the drawing buffer.
	     * 
	     * Uses information from the selected width
	     */
	    private void drawEraseSegment(int x1, int y1, int x2, int y2) {
	        Graphics2D g = (Graphics2D) drawingBuffer.getGraphics();
	        
	        g.setColor(Color.white);
	        g.setStroke(getStroke());
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
	    
	    /*
	     * DrawingController handles the user's freehand drawing.
	     */
	    private class DrawingController implements MouseListener, MouseMotionListener {
	        // store the coordinates of the last mouse event, so we can
	        // draw a line segment from that last point to the point of the next mouse event.
	        private int lastX, lastY; 
	
	        /*
	         * When mouse button is pressed down, start drawing.
	         */
	        public void mousePressed(MouseEvent e) {
	            lastX = e.getX();
	            lastY = e.getY();
	        }
	
	        /*
	         * When mouse moves while a button is pressed down,
	         * draw a line segment.
	         */
	        public void mouseDragged(MouseEvent e) {
	        	// Take the min to prevent from drawing off of the screen
	            int x = Math.min(e.getX(),getWidth());
	            int y = Math.min(e.getY(),getHeight());
	            
	            if (pencilModeButton.equals(getCurrentTool())){
		            drawPencilSegment(lastX, lastY, x, y);
		            lastX = x;
		            lastY = y;
	            }
	            if (eraseModeButton.equals(getCurrentTool())){
	            	drawEraseSegment(lastX, lastY, x, y);
		            lastX = x;
		            lastY = y;
	            }
	            if (lineModeButton.equals(getCurrentTool())){
	            	drawTempSegment(lastX, lastY, x, y);
	            }
	            if (shapeModeButton.equals(getCurrentTool())){
	            	drawTempSegment(lastX, lastY, x, y);
	            }
	        }
	        
	        public void mouseReleased(MouseEvent e) { 
	        	// Take the min to prevent from drawing off of the screen
	            int x = Math.min(e.getX(),getWidth());
	            int y = Math.min(e.getY(),getHeight());
	            
	            if (pencilModeButton.equals(getCurrentTool())){
		            drawPencilSegment(lastX, lastY, lastX, lastY);
	            }
	            if (eraseModeButton.equals(getCurrentTool())){
	            	drawEraseSegment(lastX, lastY, lastX, lastY);
	            }
	            if (lineModeButton.equals(getCurrentTool())){
	            	drawPencilSegment(lastX, lastY, x, y);
	            }
	            if (shapeModeButton.equals(getCurrentTool())){
	            	drawShapeSegment(lastX, lastY, x, y);
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
    
    /*
     * Main program. Make a window containing a ClientCanvas.
     */
    public static void main(String[] args) {
        // set up the UI (on the event-handling thread)
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                ClientCanvas canvas = new ClientCanvas(800, 600);
                canvas.setVisible(true);
            }
        });
    }

}
