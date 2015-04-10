package com.barbu.paul.gheorghe.radialpong;

import android.graphics.Point;
import android.graphics.PointF;

public class Vec2 {
    private float x, y;

    public Vec2(final float x, final float y) {
        this.x = x;
        this.y = y;
    }
    
    public Vec2(final PointF p) {
        this.x = p.x;
        this.y = p.y;
    }

    public Vec2(final Point p) {
        this.x = p.x;
        this.y = p.y;
    }
    
    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public static double mag(final Vec2 v)
    {
        return Math.sqrt(Math.pow(v.x, 2) + Math.pow(v.y, 2));
    }

    public double mag()
    {
        return mag(this);
    }

    public float dot(final Vec2 v) {
        return x*v.x+y*v.y;
    }

    public double angle(final Vec2 v)
    {
        return Math.toDegrees(Math.acos(dot(v)/(mag(this)*mag(v))));
    }

    public Vec2 addAngle(float a)
    {
        double rad = Math.toRadians(a);
        return new Vec2((float) (x+Math.cos(rad)), (float) (y+Math.sin(rad)));
    }

    public Vec2 mul(final float s)
    {
        return new Vec2(x*s, y*s);
    }

    public Vec2 sub(final Vec2 v)
    {
        return new Vec2(x-v.x, y-v.y);
    }

    public Vec2 toUnit()
    {
        float length = (float) mag(this);
        
        return new Vec2(x/length, y/length);
    }

    public Vec2 changeMagTo(float newMag)
    {
        float currentMag = (float) mag(this);

        return this.mul(newMag/currentMag);
    }
}
