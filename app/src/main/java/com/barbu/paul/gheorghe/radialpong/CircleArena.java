package com.barbu.paul.gheorghe.radialpong;

import java.util.ArrayList;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.Log;
import android.view.MotionEvent;

public class CircleArena extends Actor {
	private class Pad extends Actor {
		protected float startAngle=0, arcStartAngle=0, radius, strokeWidth;
        protected static final float sweepAngle=90;
		protected Point center;
		protected RectF boundingBox;
		private Paint paint;
		private boolean selected = false, touched = false;
		
		public Pad(final Point center, final float radius, final float strokeWidth){
			this.center = new Point(center);
			this.radius = radius;
			this.strokeWidth = strokeWidth;
			
			paint = new Paint();
			paint.setStyle(Paint.Style.STROKE);
			paint.setStrokeWidth(strokeWidth);
			paint.setColor(0xFFFF0000);
			
			boundingBox = new RectF(center.x-radius, center.y-radius, center.x + radius, center.y+radius);
			//Log.d(TAG, "Pad created");
		}
		
		@Override
		public void update() {
		}

		@Override
		public void draw(Canvas c) {            
			c.drawArc(boundingBox, arcStartAngle, sweepAngle, false, paint);

            if(false) // FIXME: debug
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
        
        public boolean isInsideAngle(Point p)
        {
            return isInsideAngle(new PointF(p.x, p.y));
        }
        
        public boolean isInsideAngle(PointF p)
        {
            p = Helpers.mapDisplayPointTo(p, center);
            double angle = Helpers.getAngle(p.x, p.y);

//            Log.d(TAG, "x=" + p.x + " y=" + p.y);
//            Log.d(TAG, "angle=" + angle);
//            Log.d(TAG, "startAngle=" + startAngle);

            // when the pad is in the first quadrant it will also be in the fourth,
            // so I have to check for hit points there, too
            if(startAngle < 90 && angle <= startAngle || angle >= 360+(startAngle-sweepAngle))
            {
//                Log.d(TAG, "first");
                return  true;
            }
            
            // getAngle works in counter-clockwise order, but drawArc works clockwise (start - stop),
            // so actually the startAngle will be bigger than the endAngle in counter-clockwise order
            // since I hit first the stop then the start, and the desired angle has to be between them
            if(startAngle >= angle && angle >= startAngle - sweepAngle)
            {
//                Log.d(TAG, "second");
                return true;
            }
            
            return false;
        }

		public boolean isInsideDistance(PointF touchPoint){
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
				this.selected = true;
				this.touched = true;
				return true;
			}

			if(this.selected && action == MotionEvent.ACTION_MOVE){
                PointF p = Helpers.mapDisplayPointTo(touchPoint, center);
				double touchAngle = Helpers.getAngle(p.x, p.y);

				//TODO: do the drag relative to the touch position
                //the drawArc method works clockwise, everything I calculate here is counter-clockwise
				arcStartAngle = (float)(360-touchAngle);//(touchAngle - lastTouchAngle);
                startAngle = (float)touchAngle;
				
//				Log.d(TAG, "projectionAngle = " + touchAngle);
//				Log.d(TAG, "startAngle = " + startAngle);

				return true;
			}
			
			if(this.selected && action == MotionEvent.ACTION_UP){
				this.selected = false;
//				Log.d(TAG, "ACTION_UP");
				return true;
			}

            return false;
		}
	}
	//TODO: set colors from outside
	private static final String TAG = CircleArena.class.getSimpleName();
	private static final float FACTOR = 0.18f; //15% //TODO try on the phone dynamically and from outside
	
	private Paint paint;
	private Point center = new Point();
	private float radius;
    private float collisionRadius;
	private Pad pad;
	private boolean skip = false;

	public CircleArena(final Point displaySize, final float ballRadius){
		this.center.x = displaySize.x/2;
		this.center.y = displaySize.y/2;
		
		this.radius = Math.min(this.center.x, this.center.y);
		
		float strokeWidth = this.radius * FACTOR;
		this.radius -= strokeWidth; // reduce the radius so I allow the stroke to be displayed on screen
				
		this.paint = new Paint();
		this.paint.setStyle(Paint.Style.STROKE);
		this.paint.setStrokeWidth(strokeWidth);
		this.paint.setColor(0xC8000000);
		
		this.pad = new Pad(center, radius, strokeWidth);
        
        this.collisionRadius = this.radius - strokeWidth/2 - ballRadius;
		
//		Log.d(TAG, "Circle arena created!\ndisplaySize: " + displaySize + "\n radius=" + this.radius +
//			"\nstrokeWidth=" + strokeWidth + "\ncenter=" + this.center);
	}

	@Override
	public void update() {
	}

	@Override
	public void draw(Canvas c) {
		c.drawCircle(this.center.x, this.center.y, this.radius, this.paint);
        
        if(false) //FIXME: DEBUG
        {
            Paint p = new Paint();
            p.setStyle(Paint.Style.STROKE);
            p.setStrokeWidth(0);
            p.setColor(Color.CYAN);
            c.drawCircle(this.center.x, this.center.y, this.radius, p);

            p.setColor(Color.GREEN);
            c.drawCircle(this.center.x, this.center.y, this.collisionRadius, p);
        }
        
		this.pad.draw(c);
	}

	@Override
	public boolean handleTouchEvent(MotionEvent event) {
		return this.pad.handleTouchEvent(event);
	}

	public boolean isTouched(){
		return pad.touched;
	}
	
	public void setTouched(boolean state){
		pad.touched = state;
		
		if(!state){
			pad.selected = false;
		}
	}
	
	public boolean isBallOutside(Ball b){
        if(Helpers.pointDistance(b.getPosition(), this.center) >= this.collisionRadius){
            return true;
        }

		return false;
	}
    
    public boolean isBallAlmostOutside(Ball b, int offset)
    {
        if(Helpers.pointDistance(b.getPosition(), this.center) >= this.collisionRadius - offset){
            return true;
        }
        
        return false;
    }
	
	public boolean isBallCollided(Ball b){
        return isBallAlmostOutside(b, (int) b.getRadius()/2) && pad.isInsideAngle(b.getPosition());
	}
}
