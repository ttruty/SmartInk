package models;

import static java.lang.Float.NaN;

import java.awt.geom.Point2D;
import java.text.DecimalFormat;
import java.text.NumberFormat;

public class InkPoint
        extends Point implements Comparable<Point>
        
{
    private double xCoordinate;
    private double yCoordinate;
    private long timeStamp;
    private int force; //max seems to be 128
    private String label = null;
    
    private boolean extrapolatedPoint = false; // pen will trunkate if pen does not move

    // constructor for set from raw data
    public InkPoint(double xIn, double yIn, long timeStampIn, int forceIn)
    {
        xCoordinate = xIn;
        yCoordinate = yIn;
        timeStamp = timeStampIn;
        force = forceIn;
    }
    
    public InkPoint(double xIn, double yIn, long timeStampIn)
    {
        xCoordinate = xIn;
        yCoordinate = yIn;
        timeStamp = timeStampIn;
    }
    
    public InkPoint(double xIn, double yIn, long timeStampIn, int pressureIn, boolean extraPolated) {
        this(xIn, yIn, timeStampIn, pressureIn);
        extrapolatedPoint = extraPolated;
    }

    
    public InkPoint(double xIn, double yIn, long timeStampIn, int pressureIn, boolean extraPolated, boolean hookletStatus) {
        this(xIn, yIn, timeStampIn, pressureIn, extraPolated);
    }

    public InkPoint(double xIn, double yIn, int timeStampIn, String labelIn)
    {
        this(xIn, yIn, timeStampIn);
        label = labelIn;
    }
    public double getX()
    {
        return xCoordinate;
    }

    public void setX(double x)
    {
        xCoordinate = x;
    }

    public double getY()
    {
        return yCoordinate;
    }

    public void setY(double y)
    {
        yCoordinate = y;
    }

    public void setTimeStamp(long timeStamp)
    {
       this.timeStamp = timeStamp;
    }

    public int getForce()
    {
        return force;
    }

    public void setForce(int forceIn)
    {
        force = forceIn;
    }
    
    public boolean isExtrapolated()
    {
        return extrapolatedPoint;
    }


    public long getTimeStamp()
    {
        return timeStamp;
    }
    
    public boolean equals(Object o)
    {
        if (this == o) {
            return true;
        }
        if ((o instanceof InkPoint)) {
            InkPoint otherPenPoint = (InkPoint)o;
            if ((otherPenPoint.getTimeStamp() == timeStamp) &&
                    (otherPenPoint.getX() == xCoordinate) &&
                    (otherPenPoint.getY() == yCoordinate)) {
                return true;
            }
        }

        return false;
    }
    
    public boolean sameLocn(Point otherPt)
    {
        return (getX() == otherPt.getX()) && (getY() == otherPt.getY());
    }
    
    // sets format to raw data format
    static NumberFormat decFormat = new DecimalFormat("#0.0000");

    
    public boolean comesBefore(Point point2)
    {
        long point2TimeStamp = point2.getTimeStamp();
        return point2TimeStamp > timeStamp;
    }

    public boolean comesAfter(Point point2)
    {
        long point2TimeStamp = point2.getTimeStamp();
        return point2TimeStamp < timeStamp;
    }

    
    public String toString()
    {
        String returnString = "X:" + decFormat.format(xCoordinate) + " Y:" + decFormat.format(yCoordinate) + " Time:" + timeStamp + " Force: " + force;
        return returnString;
    }
    
	@Override
	public int compareTo(Point o) {
		if ((o instanceof InkPoint)) {
            InkPoint pointO = (InkPoint)o;
            long timestampO = pointO.getTimeStamp();
            if (timestampO > timeStamp)
                return -1;
            if (timestampO < timeStamp) {
                return 1;
            }
        }
        return 0;
	}
	
	public double getDistance(Point secondPoint)
    {
        if (secondPoint == null) {
            return NaN;
        }
        double endX = getX();
        double endY = getY();
        double startX = secondPoint.getX();
        double startY = secondPoint.getY();

        double xDistance = Math.abs(endX - startX);
        double yDistance = Math.abs(endY - startY);

        return Math.hypot(xDistance, yDistance);
    }


    public double getDistance(double startX, double startY)
    {
        double endX = getX();
        double endY = getY();

        double xDistance = Math.abs(endX - startX);
        double yDistance = Math.abs(endY - startY);

        return Math.hypot(xDistance, yDistance);
    }

    public double[] getPolarCoordinate(Point2D centerPoint)
    {
        double x = centerPoint.getX() - getX();
        double y = centerPoint.getY() - getY();
        double theta = 3.141592653589793D - Math.atan2(y, x);
        double radius = Math.hypot(x, y);
        return new double[] { theta, radius };
    }

    public double getDistance(Point2D secondPoint)
    {
        if (secondPoint == null) {
            return NaN;
        }
        double endX = getX();
        double endY = getY();
        double startX = secondPoint.getX();
        double startY = secondPoint.getY();

        double xDistance = Math.abs(endX - startX);
        double yDistance = Math.abs(endY - startY);

        return Math.hypot(xDistance, yDistance);
    }


    public double[] getPolarCoordinate(Point centerPoint)
    {
        double x = centerPoint.getX() - getX();
        double y = centerPoint.getY() - getY();
        double theta = 3.141592653589793D - Math.atan2(y, x);

        double radius = Math.hypot(x, y);
        return new double[] { theta, radius };
    }
}

