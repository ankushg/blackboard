package model;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * I tested the two methods from DrawingOperationProtocol, drawMessage and createMessage. I first
 * simulate a server-to-canvas message by drawing on an actual board and copying the message.
 * I then pass the message into the readMessage method, which creates a drawingLayer. I make sure
 * that the drawingLayer has the same properties as the drawingAction I made in the actual board.
 * Finally, I pass the drawingLayer into the createMessage method, which creates the message String.
 * I test to see whether this String and the server generated message match.
 * 
 * I split the tests into five different operations: Line, Rectangle, Oval, EraseAll, and Pencil.
 * @author kevinwen
 */
public class DrawingOperationProtocolTest {
	
	@Test
	// Reads the drawing message of an arbitrarily placed, blue line with thickness 20.
	public void readAndCreateLineMessageTest(){
		String message = "drawLineSegment -c [-16776961] -w [20] -p [94 26 732 349] -i [blahblahblah]";
		DrawingLayer test = DrawingOperationProtocol.readMessage(message);
		assertEquals(test.getColor(), Color.blue);
		assertEquals(test.getDrawingID(), "blahblahblah");
		assertEquals(test.getShapeFilled(), false);
		assertEquals(test.getStroke(), new BasicStroke(20,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND));
		assertEquals(test.getShapeType(), "");
		ArrayList<Point> points = new ArrayList<Point>();
		points.add(new Point(94,26));
		points.add(new Point(732,349));
		assertEquals(test.getPointList(), points);
		assertEquals(test.createMessage(), message); //Tests the createMessage method for this DrawingLayer
	}
	
	@Test
	// Reads the drawing message of an arbitrarily placed, unfilled, black square with thickness 5.
	public void readAndCreateRectangleMessageTest(){
		String message = "drawRectangle -c [-16777216] -w [5] -p [114 72 228 186] -i [User01]";
		DrawingLayer test = DrawingOperationProtocol.readMessage(message);
		assertEquals(test.getColor(), Color.black);
		assertEquals(test.getDrawingID(), "User01");
		assertEquals(test.getShapeFilled(), false);
		assertEquals(test.getStroke(), new BasicStroke(5,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND));
		assertEquals(test.getShapeType(), "Rectangle");
		ArrayList<Point> points = new ArrayList<Point>();
		points.add(new Point(114,72));
		points.add(new Point(228,186));
		assertEquals(test.getPointList(), points);
		assertEquals(test.createMessage(), message); //Tests the createMessage method for this DrawingLayer
	}
	
	@Test
	// Reads the drawing message of an arbitrarily placed, filled, red oval with thickness 10.
	public void readAndCreateOvalMessageTest(){
		String message = "drawOval -c [-52480] -w [10] -f -p [185 88 419 442] -i [TestUser]";
		DrawingLayer test = DrawingOperationProtocol.readMessage(message);
		assertEquals(test.getColor(), new Color(255,51,0));
		assertEquals(test.getDrawingID(), "TestUser");
		assertEquals(test.getShapeFilled(), true);
		assertEquals(test.getStroke(), new BasicStroke(10,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND));
		assertEquals(test.getShapeType(), "Oval");
		ArrayList<Point> points = new ArrayList<Point>();
		points.add(new Point(185,88));
		points.add(new Point(419,442));
		assertEquals(test.getPointList(), points);
		assertEquals(test.createMessage(), message); //Tests the createMessage method for this DrawingLayer
	}
	
	@Test
	// Reads the drawing message of an EraseAll command.
	public void readAndCreateEraseAllTest(){
		String message = "eraseAll -i [TestUser]";
		DrawingLayer test = DrawingOperationProtocol.readMessage(message);
		assertEquals(test.getDrawingID(), "TestUser");
		assertEquals(test.getDrawingType(), "eraseAllButton");
		assertEquals(test.getShapeType(), "");
		assertEquals(test.getPointList(), new ArrayList<Point>());
		assertEquals(test.createMessage(), message); //Tests the createMessage method for this DrawingLayer
	}
	
	@Test
	// Reads the drawing message of an arbitrarily placed, freeform black dot with thickness 5
	public void readAndCreatePencilMessageTest(){
		String message = "drawLineSegment -c [-16777216] -w [5] -p [339 219 340 219 340 219] -i [User026]";
		DrawingLayer test = DrawingOperationProtocol.readMessage(message);
		assertEquals(test.getColor(), Color.black);
		assertEquals(test.getDrawingID(), "User026");
		assertEquals(test.getShapeFilled(), false);
		assertEquals(test.getStroke(), new BasicStroke(5,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND));
		assertEquals(test.getShapeType(), "");
		ArrayList<Point> points = new ArrayList<Point>();
		points.add(new Point(339,219));
		points.add(new Point(340,219));
		points.add(new Point(340,219));
		assertEquals(test.getPointList(), points);
		assertEquals(test.createMessage(), message); //Tests the createMessage method for this DrawingLayer
	}
	
}
