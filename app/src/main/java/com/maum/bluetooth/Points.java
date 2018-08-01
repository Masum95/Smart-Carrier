package com.maum.bluetooth;

/**
 * Created by Masum Rahman on 5/26/2018.
 */
public class Points{
    public double x,y;

    public Points(double x, double y) {
        this.x = x;
        this.y = y;
    }
    public Points subtractFrom(Points b)
    {
        return new Points(b.x-x,b.y-y);
    }
    public double distanceFrom(Points b)
    {
        return Math.sqrt((x-b.x)* (x-b.x) + (y-b.y)*(y-b.y));
    }
    public double dotProduct(Points a){  // position vector point
        return x * a.x + y * a.y;
    }

    @Override
    public String toString() {
        return "Points{" +
                 x + " "+
                 y ;
    }
}