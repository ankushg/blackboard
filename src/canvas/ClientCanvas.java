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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.HashMap;

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
 * ClientCanvas represents the sub-GUI surrounding a ClientCanvasPanel, which 
 * allows drawing surface that allows the user to draw
 * on it using tools such as freehand, shapes, etc. and allows the
 * user to erase as well. The canvas communicates with a ServerCanvas
 * to allow collaboration on a whiteboard among multiple users
 * 
 * 
 * TODO: Change interface to use icons instead of text
 * TODO: Make interface more user friendly
 * TODO: Get the userID info from the ClientGUI after establishing a connection
 * 
 * 
 */
public class ClientCanvas extends JPanel{
    
    private final ClientCanvasPanel whiteboardPanel;
    
    // WARNING: Change this once we establish a user id protocol
    private final String userID = "fillInUserID";
    
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
    
    private final ClientGUI clientGUI;
    
    public final static String SQUARE = "Square";
    public final static String RECTANGLE = "Rectangle";
    public final static String CIRCLE = "Circle";
    public final static String OVAL = "Oval";
    
    public final static String[] SHAPE_OPTIONS = {SQUARE, RECTANGLE, CIRCLE, OVAL};
    public final static String[] WIDTH_OPTIONS = {"5px", "10px", "20px"};
    public final static String PENCIL_BUTTON = "pencilModeButton";
    public final static String ERASE_BUTTON = "eraseModeButton";
    public final static String LINE_BUTTON = "lineModeButton";
    public final static String SHAPE_BUTTON = "shapeModeButton";
    public final static String ERASE_ALL_BUTTON = "eraseAllButton";
    public final static String SHAPE_FILLED_BUTTON = "shapeFilledButton";
    public final static String SHAPE_SELECTION_BOX = "shapeSelectionBox";
    public final static String WIDTH_SELECTION_BOX = "widthSelectionBox";
    public final static String CURRENT_COLOR_LABEL = "currentColorLabel";

    
    
    /**
     * Make a canvas.
     * @param width width in pixels
     * @param height height in pixels
     */
    public ClientCanvas(int width, int height, ClientGUI clientGUI) {
        
        whiteboardPanel = new ClientCanvasPanel(width,height,this);
        
        pencilModeButton = new JToggleButton("Pencil",  true);
        eraseModeButton = new JToggleButton("Erase",  false);
        lineModeButton = new JToggleButton("Line",  false);
        shapeModeButton = new JToggleButton("Shape",  false);
        toolButtonList = new JToggleButton[]{pencilModeButton, eraseModeButton, lineModeButton, shapeModeButton};
        shapeFilledButton = new JToggleButton("Filled",  false);
        shapeSelectionBox = new JComboBox<String>(SHAPE_OPTIONS);
        widthSelectionBox = new JComboBox<String>(WIDTH_OPTIONS);

        currentColorLabel = new JLabel();
        currentColorLabel.setBackground(Color.black); // starting color is black
        currentColorLabel.setMinimumSize(new Dimension(30,30));
        currentColorLabel.setOpaque(true);
        
        eraseAllButton = new JButton("Erase All");
        
        this.clientGUI = clientGUI;
        
        pencilModeButton.setName(PENCIL_BUTTON);
        eraseModeButton.setName(ERASE_BUTTON);
        lineModeButton.setName(LINE_BUTTON);
        shapeModeButton.setName(SHAPE_BUTTON);
        shapeFilledButton.setName(SHAPE_FILLED_BUTTON);
        shapeSelectionBox.setName(SHAPE_SELECTION_BOX);
        widthSelectionBox.setName(WIDTH_SELECTION_BOX);
        currentColorLabel.setName(CURRENT_COLOR_LABEL);
        eraseAllButton.setName(ERASE_ALL_BUTTON);
        
        // set up the toolbar
        GroupLayout layout = new GroupLayout(this);
        this.setLayout(layout);
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
        currentColorLabel.addMouseListener(new MouseAdapter() {
        	public void mouseReleased(MouseEvent ae) {
        		Color newColor = JColorChooser.showDialog(ClientCanvas.this,
        												"Choose Drawing Color",
        												getColor());
        		
        		if (!(newColor==null)) {
        			currentColorLabel.setBackground(newColor);
        		}
        	}
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
        			whiteboardPanel.eraseAll();
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
    public Color getColor(){
    	return new Color(currentColorLabel.getBackground().getRGB());
    }
    
    /**
     * Returns a Stroke object representing the currently selected stroke type
     * 
     * @return Stroke object representing the currently selected stroke type in the interface
     */
    public Stroke getStroke(){
    	return new BasicStroke(Integer.parseInt(WIDTH_OPTIONS[widthSelectionBox.getSelectedIndex()]
    			.replaceAll("px", "")),
    			BasicStroke.CAP_ROUND, 
    			BasicStroke.JOIN_ROUND);
    }
    
    /**
     * Returns a String representing the currently selected shape type
     * 
     * @return String object representing the currently selected shape type in the interface
     */
    public String getSelectedShape(){
    	return SHAPE_OPTIONS[shapeSelectionBox.getSelectedIndex()];
    }
    
    /**
     * Returns a String representing the currently selected tool
     * 
     * @return String representing the currently selected tool's name in the interface
     */
    public String getCurrentTool(){
    	for (JToggleButton button: toolButtonList){
    		if (button.isSelected()) return button.getName();
    	}
    	// should never get here, but if it does, default to being in pencil mode
    	return pencilModeButton.getName();
    }
    
    /**
     * Returns a boolean representing whether the shape filled button is selected tool
     * 
     * @return Boolean whether the shape should be filled
     */
    public boolean isShapeFilled(){
    	return shapeFilledButton.isSelected();
    }
    
    /**
     * Returns a String representing the userID of the client
     * 
     * @return String object representing the userID of the client
     */
    public String getUserID(){
    	return userID;
    }
    
    /**
     * Sends a new drawing message to the WhiteboardServer
     * 
     * @see DrawingOperationProtocol# for message formatting info
     * 
     * @param message
     */
    public synchronized void sendDrawingMessage(String message){
    	clientGUI.sendMessage(message);
    }
    
    /**
     * Receives a new drawing message to the WhiteboardServer
     * 
     * @see DrawingOperationProtocol# for message formatting info
     * 
     * @param message
     */
    public synchronized void receiveDrawingMessage(String message){
    	final String drawingMessage = message;
    	SwingUtilities.invokeLater(new Runnable() {
    		@Override
			public void run() {
    			whiteboardPanel.receiveDrawingMessage(drawingMessage);
    		}
    	});
    }


}
