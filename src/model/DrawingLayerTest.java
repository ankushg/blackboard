package model;

import static org.junit.Assert.*;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Image;
import java.awt.Point;
import java.awt.Stroke;
import java.util.ArrayList;

import org.junit.Test;

import client.ClientCanvas;
import client.ClientEasel;

/**
 * This class contains the testing strategy used to ensure that DrawingLayer works correctly
 * 
 * Things to test:
 * - Fields are not exposed when returning their values (except drawingImage, see docs)
 * - Points are added properly to the pointList
 * - Returns itself as a message properly, as outlined in DrawingOperationProtocol#
 * 
 * @author jlmart88
 *
 */
public class DrawingLayerTest {
	
	// test that the fields of DrawingLayer aren't unintentionally exposed
	@Test
    public void fieldsNotExposed() {
		String drawingID = "drawingID";
		int width = ClientCanvas.DEFAULT_WIDTH;
		int height = ClientCanvas.DEFAULT_HEIGHT;
		String drawingType = ClientEasel.PENCIL_BUTTON;
		String shapeType = ClientEasel.CIRCLE;
		Color color = Color.BLUE;
		Stroke stroke = new BasicStroke(30);
		boolean shapeFilled = false;
		
		DrawingLayer drawing = new DrawingLayer(drawingID, width, height, color, stroke, 
										drawingType, shapeType, shapeFilled);
		
		drawing.addPoint(0, 1);
		drawing.addPoint(1, 1);
		ArrayList<Point> expectedPointList = new ArrayList<Point>();
		expectedPointList.add(new Point(0,1));
		expectedPointList.add(new Point(1,1));
		
		drawingID = drawingType = shapeType = "";
		shapeFilled = true;
		
		color = color.BLACK;
		Color newColor = drawing.getColor();
		newColor = color.GREEN;
		
		stroke = new BasicStroke(5);
		Stroke newStroke = drawing.getStroke();
		newStroke = new BasicStroke(20);
		
		ArrayList<Point> pointList = drawing.getPointList();
		pointList.clear();
		
		assertEquals(drawing.getColor(), color.BLUE);
		assertEquals(drawing.getDrawingID(), "drawingID");
		assertEquals(drawing.getDrawingType(), ClientEasel.PENCIL_BUTTON);
		assertEquals(drawing.getPointList(), expectedPointList);
		assertEquals(drawing.getShapeFilled(), false);
		assertEquals(drawing.getShapeType(), ClientEasel.CIRCLE);
		assertEquals(drawing.getStroke(), new BasicStroke(30));
	}
	
	@Test
	public void pointsAdded() {
		String drawingID = "drawingID";
		int width = ClientCanvas.DEFAULT_WIDTH;
		int height = ClientCanvas.DEFAULT_HEIGHT;
		String drawingType = ClientEasel.PENCIL_BUTTON;
		String shapeType = ClientEasel.CIRCLE;
		Color color = Color.BLUE;
		Stroke stroke = new BasicStroke(30);
		boolean shapeFilled = false;
		
		DrawingLayer drawing = new DrawingLayer(drawingID, width, height, color, stroke, 
										drawingType, shapeType, shapeFilled);
		
		drawing.addPoint(0, 1);
		drawing.addPoint(1, 1);
		drawing.addPoint(2, 1);
		drawing.addPoint(65, 3);
		
		ArrayList<Point> expectedPointList = new ArrayList<Point>();
		expectedPointList.add(new Point(0,1));
		expectedPointList.add(new Point(1,1));
		expectedPointList.add(new Point(2,1));
		expectedPointList.add(new Point(65,3));
		
		assertEquals(drawing.getPointList(), expectedPointList);
	}
	
	@Test
	public void messageCreation() {
		String drawingID1 = "drawingID1";
		String drawingID2 = "drawingID2";
		
		int width = ClientCanvas.DEFAULT_WIDTH;
		int height = ClientCanvas.DEFAULT_HEIGHT;
		
		String drawingType1 = ClientEasel.PENCIL_BUTTON;
		String drawingType2 = ClientEasel.LINE_BUTTON;
		String drawingType3 = ClientEasel.ERASE_BUTTON;
		String drawingType4 = ClientEasel.SHAPE_BUTTON;
		String drawingType5 = ClientEasel.ERASE_ALL_BUTTON;
		
		String shapeType1 = ClientEasel.CIRCLE;
		String shapeType2 = ClientEasel.SQUARE;
		String shapeType3 = ClientEasel.RECTANGLE;
		String shapeType4 = ClientEasel.OVAL;
		
		Color color1 = Color.BLUE;
		Color color2 = Color.white;
		
		BasicStroke stroke1 = new BasicStroke(30);
		BasicStroke stroke2 = new BasicStroke(10);
		
		boolean shapeFilled1 = true;
		boolean shapeFilled2 = false;
		
		ArrayList<Point> pencilPointList = new ArrayList<Point>();
		pencilPointList.add(new Point(0,1));
		pencilPointList.add(new Point(1,1));
		pencilPointList.add(new Point(2,1));
		pencilPointList.add(new Point(2,2));
		String pencilPointListMessage = "[0 1 1 1 2 1 2 2]";
		
		ArrayList<Point> shapePointList = new ArrayList<Point>();
		shapePointList.add(new Point(0,1));
		shapePointList.add(new Point(5,23));
		String shapePointListMessage = "[0 1 5 23]";
		String squarePointListMessage = "[0 1 5 6]";
		
		DrawingLayer drawingPencil = new DrawingLayer(drawingID1, width, height, color1, stroke1, 
				drawingType1, shapeType1, shapeFilled1);
		String drawingPencilMessage = "drawLineSegment -c ["+color1.getRGB()+"] -w ["+(int) stroke1.getLineWidth()
				+"] -f -p "+pencilPointListMessage+" -i ["+drawingID1+"]";
		
		DrawingLayer drawingLine = new DrawingLayer(drawingID2, width, height, color2, stroke2, 
				drawingType2, shapeType2, shapeFilled2);
		String drawingLineMessage = "drawLineSegment -c ["+color2.getRGB()+"] -w ["+(int) stroke2.getLineWidth()
				+"] -p "+shapePointListMessage+" -i ["+drawingID2+"]";
		
		DrawingLayer drawingErase = new DrawingLayer(drawingID1, width, height, color2, stroke2, 
				drawingType3, shapeType3, shapeFilled2);
		String drawingEraseMessage = "eraseLineSegment -c ["+color2.getRGB()+"] -w ["+(int) stroke2.getLineWidth()
				+"] -p "+pencilPointListMessage+" -i ["+drawingID1+"]";
		
		DrawingLayer drawingCircle = new DrawingLayer(drawingID2, width, height, color1, stroke1, 
				drawingType4, shapeType1, shapeFilled2);
		String drawingCircleMessage = "drawOval -c ["+color1.getRGB()+"] -w ["+(int) stroke1.getLineWidth()
				+"] -p "+squarePointListMessage+" -i ["+drawingID2+"]";
		
		DrawingLayer drawingSquare = new DrawingLayer(drawingID2, width, height, color1, stroke2, 
				drawingType4, shapeType2, shapeFilled1);
		String drawingSquareMessage = "drawRectangle -c ["+color1.getRGB()+"] -w ["+(int) stroke2.getLineWidth()
				+"] -f -p "+squarePointListMessage+" -i ["+drawingID2+"]";
		
		DrawingLayer drawingRectangle = new DrawingLayer(drawingID2, width, height, color2, stroke1, 
				drawingType4, shapeType3, shapeFilled2);
		String drawingRectangleMessage = "drawRectangle -c ["+color2.getRGB()+"] -w ["+(int) stroke1.getLineWidth()
				+"] -p "+shapePointListMessage+" -i ["+drawingID2+"]";
		
		DrawingLayer drawingOval = new DrawingLayer(drawingID2, width, height, color1, stroke1, 
				drawingType4, shapeType4, shapeFilled1);
		String drawingOvalMessage = "drawOval -c ["+color1.getRGB()+"] -w ["+(int) stroke1.getLineWidth()
				+"] -f -p "+shapePointListMessage+" -i ["+drawingID2+"]";
		
		DrawingLayer drawingEraseAll = new DrawingLayer(drawingID1, width, height, color1, stroke1, 
				drawingType5, shapeType2, shapeFilled2);
		String drawingEraseAllMessage = "eraseAll -i ["+drawingID1+"]";
		
		for (Point point: pencilPointList){
			drawingPencil.addPoint(point.x, point.y);
			drawingErase.addPoint(point.x, point.y);
		}
		
		for (Point point: shapePointList){
			drawingLine.addPoint(point.x, point.y);
			drawingCircle.addPoint(point.x, point.y);
			drawingSquare.addPoint(point.x, point.y);
			drawingRectangle.addPoint(point.x, point.y);
			drawingOval.addPoint(point.x, point.y);
		}
		
		assertEquals(drawingPencil.createMessage(), drawingPencilMessage);
		assertEquals(drawingLine.createMessage(), drawingLineMessage);
		assertEquals(drawingErase.createMessage(), drawingEraseMessage);
		assertEquals(drawingCircle.createMessage(), drawingCircleMessage);
		assertEquals(drawingSquare.createMessage(), drawingSquareMessage);
		assertEquals(drawingRectangle.createMessage(), drawingRectangleMessage);
		assertEquals(drawingOval.createMessage(), drawingOvalMessage);
		assertEquals(drawingEraseAll.createMessage(), drawingEraseAllMessage);		
	}

}
