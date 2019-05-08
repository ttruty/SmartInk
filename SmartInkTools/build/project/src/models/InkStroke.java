package models;

import static java.lang.Double.MAX_VALUE;
import static java.lang.Double.MIN_VALUE;

import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.TreeMap;
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

  /*initial stroke cluster counter to -1 
   * as this means nothing clustered, 
  0 = unclustered group
  1+ = cluster groups
  */
  private int strokeCluster = -1;
  
  private Point2D minX = new Double(MAX_VALUE, 0.0D);
  private Point2D minY = new Double(0.0D, MAX_VALUE);
  private Point2D maxX = new Double(MIN_VALUE, 0.0D);
  private Point2D maxY = new Double(0.0D, MIN_VALUE);
  
  private boolean extrapolationPerformed = false;

  private static double sampleRate = 13.0D;
  
  private double[] endPointsCorrelation = null;
  private double[] directionRegression = {0.0D, 0.0D, 0.0D};
  private double[] directionDerivativeRegression = {0.0D, 0.0D, 0.0D};
  private TreeMap<Integer, double[]> directionDerivativeMap = new TreeMap();
  private TreeMap<Integer, double[]> directionMap = new TreeMap();
  private TreeMap<Integer, double[]> directionDerivativeWrtLengthMap = new TreeMap();


  private int digitSubst = 0;

  private int latency = 0;

  private static final int numbSpatialPts = 200;
  
  //add all points set to stroke
  public InkStroke(Collection<Point> points) {
    pointsSet.addAll(points);
    refreshCoordinateCache();
  }
  
  // overload to be able to set index of stroke with point set
  public InkStroke(Collection<Point> points, int num) {
	    this(points);
	    setIndexNumber(num);
  }
  
  public void refreshCoordinateCache() {
	    if (pointsSet.isEmpty()) {
	      return;
	    }

	    minX = new Double(MAX_VALUE, 0.0D);
	    minY = new Double(0.0D, MAX_VALUE);
	    maxX = new Double(MIN_VALUE, 0.0D);
	    maxY = new Double(0.0D, MIN_VALUE);


	    for (Iterator<Point> pointsSetIterator = pointsSet.iterator(); pointsSetIterator.hasNext(); ) {
	      Point nextPoint = (Point) pointsSetIterator.next();
	      double nextPointX = nextPoint.getX();
	      double nextPointY = nextPoint.getY();


	      if (nextPointX < minX.getX()) {
	        minX = new Double(nextPoint.getX(), nextPoint.getY());
	      }


	      if (nextPointY < minY.getY()) {
	        minY = new Double(nextPoint.getX(), nextPoint.getY());
	      }


	      if (nextPointX > maxX.getX()) {
	        maxX = new Double(nextPoint.getX(), nextPoint.getY());
	      }


	      if (nextPointY > maxY.getY()) {
	        maxY = new Double(nextPoint.getX(), nextPoint.getY());
	      }
	    }
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
  
  public boolean addPoint(Point point) {
	    boolean returnValue = pointsSet.add(point);
	    if (returnValue) {
	      double pointX = point.getX();
	      double pointY = point.getY();

	      if (pointX < minX.getX()) {
	        minX = new Double(point.getX(), point.getY());
	      }

	      if (pointX > maxX.getX()) {
	        maxX = new Double(point.getX(), point.getY());
	      }

	      if (pointY < minY.getY()) {
	        minY = new Double(point.getX(), point.getY());
	      }

	      if (pointY > maxY.getY()) {
	        maxY = new Double(point.getX(), point.getY());
	      }
	    }

	    return returnValue;
	  }


	  public boolean removePoint(Point point) {
	    boolean removeValue = pointsSet.remove(point);

	    if (removeValue) {
	      refreshCoordinateCache();
	    }

	    return removeValue;
	  }


	  public void clearPoints() {
	    pointsSet.clear();


	    minX = new Double(-1.0D, 0.0D);
	    minY = new Double(0.0D, -1.0D);
	    maxX = new Double(-1.0D, 0.0D);
	    maxY = new Double(0.0D, -1.0D);
	  }

	  
	  public LinkedList<Point> getPointsByTimeStamp() {
		    LinkedList<Point> pointsList = new LinkedList();
		    for (Iterator<Point> pointIterator = pointsSet.iterator(); pointIterator.hasNext(); ) {
		      pointsList.add((Point) pointIterator.next());
		    }
		    return pointsList;
		  }


		  public boolean equals(Object o) {
		    if (o == this) {
		      return true;
		    }
		    if ((o instanceof InkStroke)) {
		      InkStroke strokeO = (InkStroke) o;

		      LinkedList<?> strokeOList = strokeO.getPointsByTimeStamp();
		      LinkedList<?> thisList = getPointsByTimeStamp();

		      if (strokeOList.size() == thisList.size()) {
		        for (int i = 0; i < strokeOList.size(); i++) {
		          Object pointO = strokeOList.get(i);
		          Object thisPoint = thisList.get(i);
		          if (!thisPoint.equals(pointO)) {
		            return false;
		          }
		        }

		        return true;
		      }
		    }

		    return false;
		  }


  @Override
  public void setLabel(String labelIn) {
    label = labelIn;
  }

  @Override
  public String getLabel() {
    return label;
  }

  @Override
  public long getStartOfStroke() {
    Point startingPoint = (Point) pointsSet.first();
    return startingPoint.getTimeStamp();
  }

  @Override
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

  @Override
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

  @Override
  public double getDistance() {
	  //get distance from starting point to ending point 
	  // example an "0" would be close to zero,
	  // while a one would be close to length. may be usefull in determining character
	  Point startPoint = (Point) pointsSet.last();
	  Point endPoint = (Point) pointsSet.first();

	  // simple distance formula
	  return Math.hypot(endPoint.getX() - startPoint.getX(), endPoint.getY() - startPoint.getY());
  }


  @Override
  public Point getFirstPoint() {
    return (Point) pointsSet.first();
  }


  @Override
  public Point getLastPoint() {
    return (Point) pointsSet.last();
  }


  @Override
  public String toString() {
    return "Label: " + getLabel() + " ----- Sample count: " + pointsSet.size();
  }

  @Override
  public TreeSet<Point> getPointsSet() {
    return pointsSet;
  }
  

  @Override
  public boolean isHighlighted() {
    return highlight;
  }

  @Override
  public void setHighlighted(boolean highlightArg) {
    highlight = highlightArg;
  }
  
	  @Override
	  public void setStrokeCluster(int strokeCluster) {
	    this.strokeCluster = strokeCluster;
	  }

	@Override
	public int getStrokCluster() {
		return strokeCluster;
		// TODO Auto-generated method stub
		
	}
	

	  public Point2D getMinXCoordinate() {
	    return minX;
	  }


	  public Point2D getMinYCoordinate() {
	    return minY;
	  }


	  public Point2D getMaxXCoordinate() {
	    return maxX;
	  }


	  public Point2D getMaxYCoordinate() {
	    return maxY;
	  }


	  public Point2D getBBCenter() {
	    return new Double((maxX.getX() + minX.getX()) / 2.0D, (maxY.getY() + minY.getY()) / 2.0D);
	  }


	  public void extrapolateMissingValues() {
	    if ((!extrapolationPerformed) && (pointsSet.size() > 1)) {
	      LinkedList<Point> points = getPointsByTimeStamp();
	      Point previousPoint = (Point) points.getFirst();
	      points.removeFirst();
	      while (!points.isEmpty()) {
	        Point nextPoint = (Point) points.getFirst();
	        points.removeFirst();

	        long nextPointTimeStamp = nextPoint.getTimeStamp();
	        long previousPointTimeStamp = previousPoint.getTimeStamp();
	        long difference = nextPointTimeStamp - previousPointTimeStamp;

	        if (difference > sampleRate) {
	          int numMissingSamples = (int) Math.floor(difference / sampleRate);

	          int avgPressure = (nextPoint.getForce() + previousPoint.getForce()) / 2;
	          double previousPointX = previousPoint.getX();
	          double previousPointY = previousPoint.getY();
	          double nextPointY = nextPoint.getY();
	          double nextPointX = nextPoint.getX();
	          double diffXUnit = (nextPointX - previousPointX) / numMissingSamples;
	          double diffYUnit = (nextPointY - previousPointY) / numMissingSamples;

	          for (int i = 1; i < numMissingSamples; i++) {
	            long timeStamp = previousPointTimeStamp + Math.round(i * sampleRate);
	            Point extrapolatedPoint = new InkPoint(previousPointX + diffXUnit * i, previousPointY + diffYUnit * i, timeStamp, avgPressure, true);
	            pointsSet.add(extrapolatedPoint);
	          }
	        }


	        previousPoint = nextPoint;
	      }


	      extrapolationPerformed = true;
	    }
	  }

	  public boolean strokeExtrapolated() {
		    return extrapolationPerformed;
		  }

		  public void setLatency(int millisec) {
		    latency = millisec;
		  }

		  public int getLatency() {
		    return latency;
		  }
  
		  @Override
		  public double getAvgForce()
		  {
			  if (pointsSet.size() > 1) {
			      double force = 0;
			      Iterator<Point> points = pointsSet.iterator(); //inteageor of point set
			      Point previousPoint = (Point) points.next(); // set the previos point to compare to next
			      while (points.hasNext()) {
			        Point nextPoint = (Point) points.next();
			        
			        //add to force accumulator
			         force += nextPoint.getForce();
			      }
			      double avgForce = (force / pointsSet.size());
			      return avgForce;
			    }
			  	//if only one point distance is only first p force
			  return pointsSet.first().getForce();
			
			  
		  }
}
