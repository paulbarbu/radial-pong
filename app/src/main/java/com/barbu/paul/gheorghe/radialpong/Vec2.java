package com.barbu.paul.gheorghe.radialpong;

import android.graphics.Point;
import android.graphics.PointF;

public class Vec2 {
    private float x, y;

    public Vec2(float x, float y) {
        this.x = x;
        this.y = y;
    }
    
    public Vec2(PointF p) {
        this.x = p.x;
        this.y = p.y;
    }

    public Vec2(Point p) {
        this.x = p.x;
        this.y = p.y;
    }
    
    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public static double mag(Vec2 v)
    {
        return Math.sqrt(Math.pow(v.x, 2) + Math.pow(v.y, 2));
    }

    public float dot(Vec2 v) {
        return x*v.x+y*v.y;
    }

    public double angle(Vec2 v)
    {
        return Math.toDegrees(Math.acos(dot(v)/(mag(this)*mag(v))));
    }

    public Vec2 mul(float s)
    {
        return new Vec2(x*s, y*s);
    }

    public Vec2 sub(Vec2 v)
    {
        return new Vec2(x-v.x, y-v.y);
    }

    public Vec2 toUnit()
    {
        float length = (float) mag(this);
        
        return new Vec2(x/length, y/length);
    }
}
