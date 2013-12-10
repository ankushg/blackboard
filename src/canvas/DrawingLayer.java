package canvas;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Image;
import java.awt.Point;
import java.awt.Stroke;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

/**
 * A class to represent a layer in a ClientCanvasPanel. This layer is given an ID number
 * so that it can be removed once the server responds that this message has been drawn to 
 * the buffer. This class also generates the message for drawing itself to the server
 * 
 * Rep Invariant:
 * If the drawing is a pencil/erase, then pointList should contain 2>= x,y points
 * If the drawing is a shape/line, then pointList should only contain two points
 * These points should be added before createMessage is called
 * 
 * @author jlmart88
 *
 */
public class DrawingLayer {
	
	private final String drawingID;
	private final Image drawingImage;
	private final String drawingType;
	private final String shapeType;
	private final ArrayList<Point> pointList;
	private final Color color;
	private final Stroke stroke;
	private final boolean shapeFilled;
	
	public static final String LINE_MESSAGE = "drawLineSegment";
	public static final String ERASE_MESSAGE = "eraseLineSegment";
	public static final String OVAL_MESSAGE = "drawOval"; 
	public static final String RECTANGLE_MESSAGE = "drawRectangle";
	public static final String ERASE_ALL_MESSAGE = "eraseAll";
	
	// create a DrawingLayer without the optional arg shapeType
	public DrawingLayer(String drawingID, int width, int height, Color color, Stroke stroke, String drawingType){
		this.drawingID = drawingID;
		this.drawingType = drawingType;
		this.color = new Color(color.getRGB());
		BasicStroke basicStroke = (BasicStroke) stroke;
		this.stroke = new BasicStroke(basicStroke.getLineWidth(),  basicStroke.getEndCap(), basicStroke.getLineJoin());
		this.shapeType = null;
		this.shapeFilled = false;
		drawingImage = new BufferedImage(width,height,BufferedImage.TYPE_INT_ARGB);	
		pointList = new ArrayList<Point>();
	}
	// create a DrawingLayer with the optional arg shapeType
	public DrawingLayer(String drawingID, int width, int height, Color color, Stroke stroke, String drawingType, String shapeType, boolean shapeFilled){
		this.drawingID = drawingID;
		this.drawingType = drawingType;
		this.shapeType = shapeType;
		this.shapeFilled = shapeFilled;
		this.color = new Color(color.getRGB());
		BasicStroke basicStroke = (BasicStroke) stroke;
		this.stroke = new BasicStroke(basicStroke.getLineWidth(),  basicStroke.getEndCap(), basicStroke.getLineJoin());
		drawingImage = new BufferedImage(width,height,BufferedImage.TYPE_INT_ARGB);	
		pointList = new ArrayList<Point>();
	}
	
	// return the image for this drawing to be drawn on
	public Image getImage(){
		return drawingImage;
	}
	
	// return the ID of this drawing
	public String getDrawingID(){
		return drawingID;
	}
	
	// return what tool was used to create this drawing
	public String getDrawingType(){
		return drawingType;
	}
	
	// return the color of this drawing
	public Color getColor(){
		return new Color(color.getRGB());
	}
	
	// return the stroke of this drawing
	public Stroke getStroke(){
		BasicStroke basicStroke = (BasicStroke) stroke;
		return new BasicStroke(basicStroke.getLineWidth(), basicStroke.getEndCap(), basicStroke.getLineJoin());
	}
	
	
	/**
	 * Creates a message to be sent to the server for how to draw this drawing,
	 * using the following protocol:
	 * 
	 * BRUSH:    -c [color] -w [strokeWidth] -f
	 * 	Note: a BRUSH must be sent with each operation that does not use 
	 * 		the client's default; the server does not track Brush states between operations.
	 * 
     * [operation] BRUSH? (points) -i (bufferlayeridentifier)
     * drawLineSegment x1 y1 x2 y2
     * eraseLineSegment x1 y1 x2 y2
     * drawRectangle x1 y1 xLen yLen
     * drawOval x1 y1 xLen yLen
     * eraseAll 
	 * 
	 * @return String message to send to the server
	 */
	public synchronized String createMessage(){
		String out = "";
		
		// determine which type of drawing operation we should send
		if (drawingType.equals(ClientCanvas.PENCIL_BUTTON) || drawingType.equals(ClientCanvas.LINE_BUTTON)){
			out += LINE_MESSAGE;
		}
		else if (drawingType.equals(ClientCanvas.ERASE_BUTTON)){
			out += ERASE_MESSAGE;
		}
		else if (drawingType.equals(ClientCanvas.SHAPE_BUTTON)){
			if (shapeType.equals(ClientCanvas.CIRCLE) || shapeType.equals(ClientCanvas.OVAL)){
				out += OVAL_MESSAGE;
			}
			else{
				out += RECTANGLE_MESSAGE;
			}
		}
		else {
			out += ERASE_ALL_MESSAGE;
			return out;
		}
		
		
		// add the brush info
		out += " -c [" + color.getRGB() + "]";
		out += " -w [" + ((BasicStroke) stroke).getLineWidth() + "]";
		if (shapeFilled) out += " -f";
		
		// add the points to the message
		if (pointList.size()<2){ // if we dont have at least two points, then pretend we are drawing at the origin
			out += " 0 0 0 0";
		}
		else{
			if ((drawingType.equals(ClientCanvas.SHAPE_BUTTON)) && 
					(shapeType.equals(ClientCanvas.CIRCLE) || shapeType.equals(ClientCanvas.SQUARE))){
				Line2D line = ClientCanvasPanel.getSquareCoordinates(pointList.get(0).x, pointList.get(0).y, 
																		pointList.get(1).x, pointList.get(1).y);
				out += " "+(int) line.getX1()+" "+(int) line.getY1()+" "+(int) line.getX2()+" "+(int) line.getY2();
			}
			else{
				for (Point point:pointList){
					out += " "+point.x+" "+point.y;
				}
			}
		}

		out += " -i "+drawingID;
		return out;
	}
	
	/**
	 * Adds a point to the pointList of what points to draw
	 * 
	 * The points that are added to the pointList must satisfy the rep invariant
	 * 
	 * @param x the x/xLen
	 * @param y the y/yLen
	 */
	public synchronized void addPoint(int x, int y){
		pointList.add(new Point(x,y));
	}
	
	
}
