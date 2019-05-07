package models;


import java.util.TreeSet;
import java.awt.geom.Point2D;

import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.util.Callback;

public abstract class Stroke

{
    public Callback<CellDataFeatures<Stroke, String>, ObservableValue<String>> strokeIndex;

	public Stroke() {}

    public abstract void setLabel(String paramString);

    public abstract String getLabel();

    public abstract long getStartOfStroke();

    public abstract long getEndOfStroke();

    public abstract TreeSet<Point> getPointsSet();
    
    public abstract Point getFirstPoint();

    public abstract Point getLastPoint();

    public abstract double getStrokeDuration();

    public abstract double getLength();

    public abstract double getDistance();

    public abstract boolean isHighlighted();

    public abstract void setHighlighted(boolean paramBoolean);
    
    public abstract void setStrokeCluster(int strokeCluster);
    
    public abstract int getStrokCluster();

	public abstract double getAvgForce();
	
	public abstract Point2D getBBCenter();
	
	public abstract Point2D getMaxXCoordinate();
	
	public abstract Point2D getMaxYCoordinate();

	public abstract Point2D getMinXCoordinate();
	
	public abstract Point2D getMinYCoordinate();
}