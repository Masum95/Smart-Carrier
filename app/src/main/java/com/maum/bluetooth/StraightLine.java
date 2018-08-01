package com.maum.bluetooth;

/**
 * Created by Masum Rahman on 5/26/2018.
 */
public class StraightLine {

    public Points startPoint,endPoint;
    public Points vector;
    public double magnitude;

    public StraightLine(Points startPoint, Points endPoint) {
        this.startPoint = startPoint;
        this.endPoint = endPoint;
        vector = startPoint.subtractFrom(endPoint);
        magnitude = Math.sqrt( (startPoint.x-endPoint.x)*(startPoint.x-endPoint.x) + (startPoint.y-endPoint.y)*(startPoint.y-endPoint.y)  );
    }


    @Override
    public String toString() {
        return "StraightLine{" +
                "vector=" + vector.x + "  " + vector.y +
                '}';
    }

    public double calcAngleWith(StraightLine a)
    {
        try {
            double val = vector.dotProduct(a.vector);
            val /= (magnitude * a.magnitude);
            if (new Points(-vector.y, vector.x).dotProduct(a.vector) < 0) // rotating this vector by 90 degree anticlockWise and then
            // taking dot product to tell whether "a" vector is in clockWise or anti-clockwise direction
            {
                return  -Math.acos(val);   // negative if CW
            } else {
                return  Math.acos(val); // positive if CCW
            }
        }catch (Exception ex){
            return 100;
        }

    }

}
