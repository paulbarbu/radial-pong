package com.barbu.paul.gheorghe.radialpong;

import android.graphics.Point;
import android.graphics.PointF;

public class Helpers {
	public static int boolToSign(boolean val){
		if(val){
			return 1;
		}
		
		return -1;
	}

    public static double pointDistance(Point a, Point b){
        return Math.sqrt(Math.pow(a.x - b.x, 2) + Math.pow(a.y - b.y, 2));
    }

    public static double pointDistance(PointF a, Point b){
        return Math.sqrt(Math.pow(a.x - b.x, 2) + Math.pow(a.y - b.y, 2));
    }

    public static double pointDistance(Point a, PointF b){
        return Math.sqrt(Math.pow(a.x - b.x, 2) + Math.pow(a.y - b.y, 2));
    }
}
