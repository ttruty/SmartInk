package models;

import java.util.Collection;
import java.util.Iterator;
import java.util.TreeSet;

public class InkStroke
  extends Stroke {
	
	//initialize point tree set
  TreeSet<Point> pointsSet = new TreeSet<Point>();

  // initialize label to be set when segmenting
  private String label = null;
  
  
  private int indexNumber = -1;
  private boolean indexNumberSet = false;

  //var for selected strokes
  private boolean highlight;
  
  //add all points set to stroke
  public InkStroke(Collection<Point> points) {
    pointsSet.addAll(points);
  }
  
  // overload to be able to set index of stroke with point set
  public InkStroke(Collection<Point> points, int num) {
	    this(points);
	    setIndexNumber(num);
  }

  
  public int getIndexNumber() {
	    return indexNumber;
  }

  public void setIndexNumber(int strokeNumber) {
    if (!indexNumberSet) {
      indexNumber = strokeNumber;
      indexNumberSet = true;
    }
  }

	  
  public void setLabel(String labelIn) {
    label = labelIn;
  }


  public String getLabel() {
    return label;
  }


  public long getStartOfStroke() {
    Point startingPoint = (Point) pointsSet.first();
    return startingPoint.getTimeStamp();
  }


  public long getEndOfStroke() {
    Point endingPoint = (Point) pointsSet.last();
    return endingPoint.getTimeStamp();
  }


  // time to draw stroke
  public double getStrokeDuration() {
    return (getEndOfStroke() - getStartOfStroke()) / 1000.0D;
  }
  
  //TODO get time inbetween strokes... 
  public int getNumberOfPoints() {
    return pointsSet.size();
  }


  public double getLength() {
	  // get the length of ink stroke
	  if (pointsSet.size() > 1) {
	      double length = 0.0D;
	      Iterator<Point> points = pointsSet.iterator(); //inteageor of point set
	      Point previousPoint = (Point) points.next(); // set the previos point to compare to next
	      while (points.hasNext()) {
	        Point nextPoint = (Point) points.next();

	        // calculate the distance of each point set
	        double distance = Math.hypot(nextPoint.getX() - previousPoint.getX(), nextPoint.getY() - previousPoint.getY());
	        //add to distance accumulator
	        length += distance;

	        // go to next
	        previousPoint = nextPoint;
	      }
	      return length;
	    }
	  	//if only one point distance is 0
	    return 0.0D;
	  }


  public double getDistance() {
	  //get distance from starting point to ending point 
	  // example an "0" would be close to zero,
	  // while a one would be close to length. may be usefull in determining character
	  Point startPoint = (Point) pointsSet.last();
	  Point endPoint = (Point) pointsSet.first();

	  // simple distance formula
	  return Math.hypot(endPoint.getX() - startPoint.getX(), endPoint.getY() - startPoint.getY());
  }


  public Point getFirstPoint() {
    return (Point) pointsSet.first();
  }


  public Point getLastPoint() {
    return (Point) pointsSet.last();
  }


  public String toString() {
    return "Label: " + getLabel() + " ----- Sample count: " + pointsSet.size();
  }

  public TreeSet<Point> getPointsSet() {
    return pointsSet;
  }
  

  public boolean isHighlighted() {
    return highlight;
  }

  public void setHighlighted(boolean highlightArg) {
    highlight = highlightArg;
  }

}
