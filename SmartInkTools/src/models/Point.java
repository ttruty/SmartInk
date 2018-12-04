package models;

public abstract class Point
{
    public Point() {}

    public abstract double getX();

    public abstract void setX(double paramDouble);

    public abstract double getY();

    public abstract void setY(double paramDouble);

    public abstract int getForce();

    public abstract void setForce(int paramInt);

    public abstract long getTimeStamp();

    public abstract void setTimeStamp(long paramLong);

}

