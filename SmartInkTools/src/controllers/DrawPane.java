package controllers;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.TreeSet;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import models.FileDetails;
import models.Point;
import models.Stroke;

public class DrawPane extends Pane {
    private final Canvas drawCanvas;
    private FileDetails filedetails;
    private LinkedList<Stroke> data;

	public DrawPane() {
        drawCanvas = new Canvas(getWidth(), getHeight());
        getChildren().add(drawCanvas);
        widthProperty().addListener(e -> drawCanvas.setWidth(getWidth()));
        heightProperty().addListener(e -> drawCanvas.setHeight(getHeight()));
    }

    @Override
    protected void layoutChildren() {
        super.layoutChildren();

        GraphicsContext gc = drawCanvas.getGraphicsContext2D();

        gc.clearRect(0, 0, getWidth(), getHeight());

        gc.setFill(Color.RED);
        gc.fillRect(10, 10, getWidth() - 20, getHeight() - 20);

        // Paint your custom image here:
        //gc.drawImage(someImage, 0, 0);
        
     // All drawings go here
        Rectangle2D r = null;

        ArrayList<Double> xMaxMinList = new ArrayList<>();
        ArrayList<Double> yMaxMinList = new ArrayList<>();

        for (int i = 0; i < data.size(); i++) {
//            System.out.println(data.get(i));
            Stroke single_stroke = data.get(i);
//            System.out.println(single_stroke.getStrokeDuration());
//            System.out.println(single_stroke.getBBCenter());
//            System.out.println(single_stroke.getLength());
            TreeSet<Point> pointSet = single_stroke.getPointsSet();
            Iterator<Point> point = pointSet.iterator();

            ArrayList<Double> xPointList = new ArrayList<>();
            ArrayList<Double> yPointList = new ArrayList<>();
            int pointCount = 0;
            while (point.hasNext()) {
                Point value = point.next();
                xPointList.add(pointCount, value.getX());
                xMaxMinList.add(value.getX());
                yPointList.add(pointCount, value.getY());
                yMaxMinList.add(value.getY());
                pointCount++;

            }
            
            for (int p = 0; p < xPointList.size() - 1; p++) {
                //the X10 makes plotting of lines more readable
                gc.strokeLine(xPointList.get(p), yPointList.get(p), xPointList.get(p+1), yPointList.get(p+1));
            }
        }
    }
    
    public LinkedList<Stroke> getData() {
		return data;
	}

	public void setData(LinkedList<Stroke> data) {
		this.data = data;
	}

	public FileDetails getFiledetails() {
		return filedetails;
	}

	public void setFiledetails(FileDetails filedetails) {
		this.filedetails = filedetails;
	}
}