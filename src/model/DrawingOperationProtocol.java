package model;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Point;
import java.awt.Stroke;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Queue;

import client.ClientCanvas;
import client.ClientCanvasPanel;

/**
 * A class to serve as the transition between DrawingLayers and
 * the message protocol for communicating with a WhiteboardServer
 * using the following protocol:
 * 
 * DRAWING_OPERATION: OPERATION SPACE BRUSH FILLED_FLAG? SPACE POINTS SPACE DRAWING_ID | ERASE_ALL
 * 
 * ERASE_ALL: "eraseAll" SPACE DRAWING_ID
 * BRUSH: "-c" SPACE "[" INT "]" SPACE "-w" SPACE "[" INT "]" SPACE
 * OPERATION: "drawLineSegment" | "eraseLineSegment" | "drawRectangle" | "drawOval"
 * POINTS: "-p" SPACE "[" POINT{2,} "]"
 * POINT: INT SPACE INT SPACE?
 * DRAWING_ID: "-i" SPACE "[" STRING "]"
 * FILLED_FLAG: SPACE "-f" 
 * STRING: ([a-z] | [A-Z] | INT | SPACE)+
 * INT: [0-9]+
 * SPACE: " "
 *  
 * @author jlmart88
 *
 */
public class DrawingOperationProtocol {
	
	public static final String LINE_MESSAGE = "drawLineSegment";
	public static final String ERASE_MESSAGE = "eraseLineSegment";
	public static final String OVAL_MESSAGE = "drawOval"; 
	public static final String RECTANGLE_MESSAGE = "drawRectangle";
	public static final String ERASE_ALL_MESSAGE = "eraseAll";
	public static final String[] DRAWING_MESSAGE_LIST = {LINE_MESSAGE, ERASE_MESSAGE, OVAL_MESSAGE, RECTANGLE_MESSAGE, ERASE_ALL_MESSAGE};
	
	/**
	 * Creates a message to be sent to the server for how to draw the input drawing 
	 * 
	 * @param DrawingLayer the drawing to convert to a message
	 * @return String message to send to the server
	 */
	public static String createMessage(DrawingLayer drawing){
		
		String out = "";
		ArrayList<Point> pointList = drawing.getPointList();
		
		// determine which type of drawing operation we should send
		if (drawing.getDrawingType().equals(ClientCanvas.PENCIL_BUTTON) || drawing.getDrawingType().equals(ClientCanvas.LINE_BUTTON)){
			out += LINE_MESSAGE;
		}
		else if (drawing.getDrawingType().equals(ClientCanvas.ERASE_BUTTON)){
			out += ERASE_MESSAGE;
		}
		else if (drawing.getDrawingType().equals(ClientCanvas.SHAPE_BUTTON)){
			if (drawing.getShapeType().equals(ClientCanvas.CIRCLE) || drawing.getShapeType().equals(ClientCanvas.OVAL)){
				out += OVAL_MESSAGE;
			}
			else{
				out += RECTANGLE_MESSAGE;
			}
		}
		else {
			out += ERASE_ALL_MESSAGE+" -i ["+drawing.getDrawingID()+"]";
			return out;
		}
		
		
		// add the brush info
		out += " -c [" + drawing.getColor().getRGB() + "]";
		out += " -w [" + (int) ((BasicStroke) drawing.getStroke()).getLineWidth() + "]";
		if (drawing.getShapeFilled()) out += " -f";
		out += " -p ";
		
		// add the points to the message
		if (pointList.size()<2){ // if we dont have at least two points, then pretend we are drawing at the origin
			out += "[0 0 0 0]";
		}
		else{
			out += "[";
			if ((drawing.getDrawingType().equals(ClientCanvas.SHAPE_BUTTON)) && 
					(drawing.getShapeType().equals(ClientCanvas.CIRCLE) || drawing.getShapeType().equals(ClientCanvas.SQUARE))){
				Line2D line = ClientCanvasPanel.getSquareCoordinates(pointList.get(0).x, pointList.get(0).y, 
																		pointList.get(1).x, pointList.get(1).y);
				out +=(int) line.getX1()+" "+(int) line.getY1()+" "+(int) line.getX2()+" "+(int) line.getY2();
			}
			else{
				for (Point point:pointList){
					out += point.x+" "+point.y+" ";
				}
				out = out.substring(0, out.length()-1);
			}
			out += "]";
		}

		out += " -i ["+drawing.getDrawingID()+"]";
		return out;
	}
	
	/**
	 * Reads in a drawing message and returns a DrawingLayer representing it
	 * 
	 * @param message String following the message protocol
	 * @return DrawingLayer representing the message
	 */
	public static DrawingLayer readMessage(String message){
		
		String drawingID = "";
		String drawingType = "";
		String shapeType = "";
		ArrayList<Point> pointList = new ArrayList<Point>();
		Color color = Color.black;
		Stroke stroke = new BasicStroke();
		boolean shapeFilled = false;
		
		String[] args = message.split(" ");
		Queue<String> arguments = new LinkedList<String>(Arrays.asList(args));
		
		try {
            while ( ! arguments.isEmpty()) {
                String flag = arguments.remove();
                try {
                	if (flag.equals(ERASE_ALL_MESSAGE)){
                		drawingType = ClientCanvas.ERASE_ALL_BUTTON;
                	}
                	else if (flag.equals(LINE_MESSAGE)){
                		drawingType = ClientCanvas.LINE_BUTTON;
                	}
                	else if (flag.equals(ERASE_MESSAGE)){
                		drawingType = ClientCanvas.ERASE_BUTTON;
                	}
                	else if (flag.equals(OVAL_MESSAGE)){
                		drawingType = ClientCanvas.SHAPE_BUTTON;
                		shapeType = ClientCanvas.OVAL;
                	}
                	else if (flag.equals(RECTANGLE_MESSAGE)){
                		drawingType = ClientCanvas.SHAPE_BUTTON;
                		shapeType = ClientCanvas.RECTANGLE;
                	}
                	
                	else if (flag.equals("-c")) {
                         String colorValue = arguments.remove();
                         colorValue = colorValue.replace("[", "").replace("]", "");
                         color = new Color(Integer.parseInt(colorValue));
                    } 
                	else if (flag.equals("-w")) {
                		String widthValue = arguments.remove();
                        widthValue = widthValue.replace("[", "").replace("]", "");
                        stroke = new BasicStroke(Integer.parseInt(widthValue),BasicStroke.CAP_ROUND, 
                    			BasicStroke.JOIN_ROUND);
                    } 
                	else if (flag.equals("-p")) {
                        while (!arguments.peek().contains("-")){
                        	String xValue = arguments.remove();
                        	String yValue = arguments.remove();
                        	xValue = xValue.replace("[", "").replace("]", "");
                        	yValue = yValue.replace("[", "").replace("]", "");
                        	pointList.add(new Point(Integer.parseInt(xValue), Integer.parseInt(yValue)));
                        }

                    } 
                	else if (flag.equals("-i")) {
                        String idValue = arguments.remove();
                        idValue = idValue.replace("[", "").replace("]", "");
                        drawingID = idValue;
                    } 
                	else if (flag.equals("-f")) {
                        shapeFilled = true;
                    } else {
                        throw new IllegalArgumentException("unknown option: \"" + flag + "\"");
                    }
                } catch (NoSuchElementException nsee) {
                    throw new IllegalArgumentException("missing argument for " + flag);
                } catch (NumberFormatException nfe) {
                    throw new IllegalArgumentException("unable to parse number for " + flag);
                }
            }
        } catch (IllegalArgumentException iae) {
            System.err.println(iae.getMessage());
            System.err.println("Improperly Formatted Message, see DrawingOperationProtocol for more info");
        }
		
		DrawingLayer out = new DrawingLayer(drawingID, ClientCanvasPanel.DEFAULT_WIDTH, ClientCanvasPanel.DEFAULT_HEIGHT,
								color, stroke, drawingType, shapeType, shapeFilled);
		for (Point point: pointList){
			out.addPoint(point.x, point.y);
		}
		
		return out;
	}
}
