package com.barbu.paul.gheorghe.radialpong;

import android.graphics.Point;
import android.graphics.PointF;

public class Helpers {
    public static final boolean DEBUG_MODE = false;

	public static int boolToSign(boolean val)
    {
		if(val){
			return 1;
		}
		
		return -1;
	}

    public static double pointDistance(Point a, Point b)
    {
        return Math.sqrt(Math.pow(a.x - b.x, 2) + Math.pow(a.y - b.y, 2));
    }

    public static double pointDistance(PointF a, Point b)
    {
        return Math.sqrt(Math.pow(a.x - b.x, 2) + Math.pow(a.y - b.y, 2));
    }

    public static double pointDistance(PointF a, PointF b)
    {
        return Math.sqrt(Math.pow(a.x - b.x, 2) + Math.pow(a.y - b.y, 2));
    }
    
    public static PointF mapDisplayPointTo(PointF p, Point center)
    {
        return new PointF(p.x - center.x, center.y - p.y);
    }

    public static PointF mapDisplayPointTo(Point p, PointF center)
    {
        return new PointF(p.x - center.x, center.y - p.y);
    }
    
    public static PointF mapDisplayPointTo(Point p, Point center)
    {
        return new PointF(p.x - center.x, center.y - p.y);
    }

    public static PointF mapDisplayPointTo(PointF p, PointF center)
    {
        return new PointF(p.x - center.x, center.y - p.y);
    }

    /**
     * Compute the angle of the line segment denoted by the center of the geometrical plane XOY and (x, y) with
     * the OX axis
     *
     * https://en.wikipedia.org/wiki/Atan2
     *
     * @param x X coordinate in the geometrical plane, not the screen one
     * @param y Y coordinate in the geometrical plane, not the screen one
     * @return The angle in degrees 0 <= alpha < 360
     */
    public static double getAngle(float x, float y)
    {
        // avoid the atan2 undefined case
        if (x == 0 && y == 0) {
            return 0;
        }

        double angle = Math.atan2(y, x);
        if (angle < 0) {
            angle = (angle + 2*Math.PI) % (2*Math.PI);
        }

        return Math.toDegrees(angle);
    }
}
