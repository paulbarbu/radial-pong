package com.barbu.paul.gheorghe.radialpong;

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
		protected float startAngle=0, arcStartAngle=0, radius, strokeWidth, lastTouchAngle;
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
            return isInsideAngle(new PointF(p.x, p.y), 0);
        }
        
        public boolean isInsideAngle(PointF p)
        {
            return isInsideAngle(p, 0);
        }

        public boolean isInsideAngle(Point p, float paddingAngle)
        {
            return isInsideAngle(new PointF(p.x, p.y), paddingAngle);
        }

        /**
         * The padding idea came from here:
         * https://github.com/bilthon/radial-pong/commit/86b96382583f4a28cd4a65643af037c12a54f589
         * @param p
         * @param paddingAngle
         * @return
         */
        public boolean isInsideAngle(PointF p, float paddingAngle)
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
            if(startAngle + paddingAngle >= angle && angle >= startAngle - sweepAngle - paddingAngle)
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


                PointF p = Helpers.mapDisplayPointTo(touchPoint, center);
                lastTouchAngle = (float)Helpers.getAngle(p.x, p.y);
                
				return true;
			}

			if(this.selected && action == MotionEvent.ACTION_MOVE){
                PointF p = Helpers.mapDisplayPointTo(touchPoint, center);
				float touchAngle = (float)Helpers.getAngle(p.x, p.y);

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
                
                Log.d(TAG, "touchAngle = " + touchAngle);
                Log.d(TAG, "lastTouchAngle = " + lastTouchAngle);
                Log.d(TAG, "deltaAngle = " + deltaAngle);
				Log.d(TAG, "startAngle = " + startAngle);


                lastTouchAngle = touchAngle;

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
        float paddingAngle = (float) Math.toDegrees(Math.asin(b.getRadius()/radius));
        return isBallAlmostOutside(b, (int) b.getRadius()/2) && pad.isInsideAngle(b.getPosition(), paddingAngle);
	}
}
