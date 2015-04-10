package com.barbu.paul.gheorghe.radialpong;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.Log;
import android.view.MotionEvent;

import static com.barbu.paul.gheorghe.radialpong.Helpers.DEBUG_MODE;

public class Pad extends Actor {
    private static final String TAG = Pad.class.getSimpleName();

    protected float startAngle=0, arcStartAngle=0, radius, strokeWidth, lastTouchAngle;
    protected static final float sweepAngle=90;

    protected RectF boundingBox;
    private boolean touched = false;


    public static class Builder extends Actor.Builder {
        protected float radius=0, strokeWidth;
        protected int color;

        public Builder(PointF center)
        {
            super(center);
        }

        public Builder radius(float r)
        {
            radius = r;
            return this;
        }

        public Builder strokeWidth(float sw)
        {
            strokeWidth = sw;
            return this;
        }

        public Builder color(int c)
        {
            color = c;
            return this;
        }

        public Pad build()
        {
            return new Pad(this);
        }
    }

    private Pad(Builder builder){
        this.center = builder.center;
        this.radius = builder.radius;
        this.strokeWidth = builder.strokeWidth;

        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(strokeWidth);
        paint.setColor(builder.color);

        boundingBox = new RectF(center.x-radius, center.y-radius, center.x + radius, center.y+radius);
    }

    public boolean isTouched()
    {
        return touched;
    }

    @Override
    public void update() {
    }

    @Override
    public void draw(Canvas c) {
        c.drawArc(boundingBox, arcStartAngle, sweepAngle, false, paint);

        if(DEBUG_MODE)
        {
            Paint p = new Paint();
            p.setStyle(Paint.Style.STROKE);
            p.setStrokeWidth(10);
            p.setColor(Color.CYAN);

            c.drawArc(boundingBox, arcStartAngle, 5, false, p);

            p.setColor(Color.GREEN);
            c.drawArc(boundingBox, arcStartAngle + sweepAngle, 5, false, p);
        }
    }

    protected boolean isInsideAngle(Point p)
    {
        return isInsideAngle(new PointF(p.x, p.y), 0);
    }

    protected boolean isInsideAngle(PointF p)
    {
        return isInsideAngle(p, 0);
    }

    protected boolean isInsideAngle(Point p, float paddingAngle)
    {
        return isInsideAngle(new PointF(p.x, p.y), paddingAngle);
    }

    /**
     * Test if a point projection is inside the pad with regards to the angle
     *
     * The padding idea came from here:
     * https://github.com/bilthon/radial-pong/commit/86b96382583f4a28cd4a65643af037c12a54f589
     *
     * @param p the point for which the angle is to be calculated, relative to the center of the circle
     * @param paddingAngle the angle to be added to the pad so that edge collisions are nicely handled
     * @return true if the point is inside the pad, angle wise, false otherwise
     */
    protected boolean isInsideAngle(PointF p, float paddingAngle)
    {
        p = Helpers.mapDisplayPointTo(p, center);
        double angle = Helpers.getAngle(p.x, p.y);

//            Log.d(TAG, "x=" + p.x + " y=" + p.y);
//            Log.d(TAG, "angle=" + angle);
//            Log.d(TAG, "startAngle=" + startAngle);

        // when the pad is in the first quadrant it will also be in the fourth,
        // so I have to check for hit points there, too
        if(startAngle < 90 && angle <= paddingAngle + startAngle || angle >= 360+(startAngle-sweepAngle-paddingAngle))
        {
//                Log.d(TAG, "first");
            return  true;
        }

        // getAngle works in counter-clockwise order, but drawArc works clockwise (start - stop),
        // so actually the startAngle will be bigger than the endAngle in counter-clockwise order
        // since I hit first the stop then the start, and the desired angle has to be between them
        return startAngle + paddingAngle >= angle && angle >= startAngle - sweepAngle - paddingAngle;
    }

    /**
     * Test is the given point is touching the pad
     * @param touchPoint the touch point to test if is inside the pad (the stroke is considered accordingly)
     * @return true if the point is inside, false otherwise
     */
    protected boolean isInsideDistance(PointF touchPoint){
        double distToCenter = Helpers.pointDistance(new PointF(touchPoint.x, touchPoint.y), center);

        if(radius - strokeWidth/2 <= distToCenter && distToCenter <= radius + strokeWidth/2)
        {
//				Log.d(TAG, "Distance INSIDE!");
            return true;
        }

//            Log.d(TAG, "Distance OUTSIDE!");
        return false;
    }

    @Override
    public boolean handleTouchEvent(MotionEvent event) {
        int action = event.getAction();

        PointF touchPoint = new PointF(event.getX(), event.getY());

        if(MotionEvent.ACTION_DOWN == action && isInsideDistance(touchPoint) && isInsideAngle(touchPoint)){
//				Log.d(TAG, "ACTION_DOWN");
            this.touched = true;

            PointF p = Helpers.mapDisplayPointTo(touchPoint, center);
            lastTouchAngle = (float)Helpers.getAngle(p.x, p.y);

            return true;
        }

        if(this.touched && action == MotionEvent.ACTION_MOVE){
            PointF p = Helpers.mapDisplayPointTo(touchPoint, center);
            float touchAngle = (float)Helpers.getAngle(p.x, p.y);

            //deltaAngle is the offset that should be added to the startAngle of the Pad
            // relative to the startAngle so the pad doesn't jump around when it's selected
            float deltaAngle = touchAngle-lastTouchAngle;

            // this may happen if the user touches, say,  first at 354 deg then at 1 deg
            // in this case the delta should be 360-354 + 1 = 7 deg, not -353 deg
            if(deltaAngle < 0)
            {
                deltaAngle = 360-lastTouchAngle + touchAngle;
            }

            startAngle = startAngle + deltaAngle;
            startAngle = (startAngle + 360) % 360; //wrap around the circle

            //the drawArc method works clockwise, everything I calculate here is counter-clockwise
            arcStartAngle = 360-startAngle;

            if(DEBUG_MODE) {
                Log.d(TAG, "touchAngle = " + touchAngle);
                Log.d(TAG, "lastTouchAngle = " + lastTouchAngle);
                Log.d(TAG, "deltaAngle = " + deltaAngle);
                Log.d(TAG, "startAngle = " + startAngle);
            }


            lastTouchAngle = touchAngle;

            return true;
        }

        if(this.touched && action == MotionEvent.ACTION_UP){
            this.touched = false;
//				Log.d(TAG, "ACTION_UP");
            return true;
        }

        return false;
    }
}