package models;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class InkPoint
        extends Point implements Comparable<Point>
        
{
    private double xCoordinate;
    private double yCoordinate;
    private long timeStamp;
    private int force; //max seems to be 128

    // constructor for set from raw data
    public InkPoint(double xIn, double yIn, long timeStampIn, int forceIn)
    {
        xCoordinate = xIn;
        yCoordinate = yIn;
        timeStamp = timeStampIn;
        force = forceIn;
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

    public long getTimeStamp()
    {
        return timeStamp;
    }
    
    // sets format to raw data format
    static NumberFormat decFormat = new DecimalFormat("#0.0000");

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
}

