package model;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Image;
import java.awt.Point;
import java.awt.Stroke;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

/**
 * A class to represent a layer in a ClientCanvas. This layer is given an ID number
 * so that it can be removed once the server responds that this message has been drawn to 
 * the buffer. This class also generates the message for drawing itself to the server. 
 * 
 * 
 * 
 * The field drawingImage is provided to allow multiple drawings to be drawn on top of the buffer before
 * they are processed by the server. The attribute field should be drawn on by a ClientCanvas
 * 
 * Rep Invariant:
 * If the drawing is a pencil/erase, then pointList should contain 2>= x,y points
 * If the drawing is a shape/line, then pointList should only contain two points
 * All points in the drawing should be added before createMessage is called
 * 
 * Concurrency Argument:
 * All fields in this class (except drawingImage) that show risk of concurrency 
 * 		are only ever accessed by obtaining the lock for the class
 * The field drawingImage is not thread-safe, as it needs to be drawn on externally in order for it
 * 		to physically represent the drawing that this class abstracts. This drawingImage should be drawn on
 * 		and accessed in a thread-safe manner to prevent concurrency issues
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
	
	// create a DrawingLayer
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
	public synchronized Image getImage(){
		return drawingImage;
	}
	
	// return the ID of this drawing
	public String getDrawingID(){
		return drawingID;
	}
	
	// return what tool was used to create this drawing
	public  String getDrawingType(){
		return drawingType;
	}
	
	// return what type of shape this drawing is
	public String getShapeType(){
		return shapeType;
	}
	
	// return the list of points in this drawing
	public synchronized ArrayList<Point> getPointList(){
		ArrayList<Point> out = new ArrayList<Point>();
		for (Point point: pointList){
			out.add(point);
		}
		return out;
	}
	
	// return whether the shape is filled or not
	public boolean getShapeFilled(){
		return shapeFilled;
	}
	
	// return the color of this drawing
	public Color getColor(){
		return new Color(color.getRGB());
	}
	
	// return the stroke of this drawing
	public synchronized Stroke getStroke(){
		BasicStroke basicStroke = (BasicStroke) stroke;
		return new BasicStroke(basicStroke.getLineWidth(), basicStroke.getEndCap(), basicStroke.getLineJoin());
	}
	
	
	/**
	 * Creates a message to be sent to the server for how to draw this drawing 
	 * 
	 * @return String message to send to the server
	 */
	public synchronized String createMessage(){
		return DrawingOperationProtocol.createMessage(this);
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
