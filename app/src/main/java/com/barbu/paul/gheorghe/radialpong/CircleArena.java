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
		protected float projectionAngle=45, startAngle=0, sweepAngle=90, radius, strokeWidth;
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
			
			this.boundingBox = new RectF(center.x-radius,
				center.y-radius, center.x + radius,
				center.y+radius);
			Log.d(TAG, "Pad created");
		}
		
		@Override
		public void update() {
		}

		@Override
		public void draw(Canvas c) {
			c.drawArc(this.boundingBox, this.startAngle, this.sweepAngle, false, this.paint);
		}

		public boolean isInsideBounds(float x, float y){
			double touchAngle = computeAngle(x, y);
			double distToCenter = Helpers.pointDistance(new PointF(x, y), this.center);
            
            Log.wtf(TAG, "Touch angle: " + touchAngle);
									
			if(this.startAngle < touchAngle && touchAngle < this.startAngle + this.sweepAngle &&
					this.radius - this.strokeWidth/2 < distToCenter &&
					distToCenter < this.radius + this.strokeWidth/2){
				
				Log.d(TAG, "INSIDE!");
				return true;
			}
			
			return false;
		}

        /**
         * Compute the angle of the line segment denoted by (center.x, center.y) and (x, y) with
         * the OX axis (center.x, center.y) and (inf, center.y)
         *
         * https://en.wikipedia.org/wiki/Atan2
         *
         * @param x Global X coordinate
         * @param y Global Y coordinate
         * @return The angle in degrees 0 <= alpha <= 360
         */
		public double computeAngle(float x, float y){
			float slope = (this.center.y - y)/(this.center.x - x);

			float angle = (float)Math.toDegrees(Math.atan(slope));

			if(x < this.center.x){
				angle += 180;
			}

			return angle % 360;
            
            
//            // translate the global coords to the center of the arena
//            x -= center.x;
//            y -= center.y;
//
//            // avoid the atan2 undefined case
//            if(x == 0 && y == 0) {
//                return 0;
//            }
//
//            double angle = Math.atan2(y, x);
//            if(angle < 0) {
//                return (angle + 360) % 360;
//            }
//
//            return angle;
		}

		@Override
		public boolean handleTouchEvent(MotionEvent event) {
			int action = event.getAction();
			if(MotionEvent.ACTION_DOWN == action && isInsideBounds(event.getX(), event.getY())){
				Log.d(TAG, "ACTION_DOWN");
				this.selected = true;
				this.touched = true;
				return true;
			}
			
			if(this.selected && action == MotionEvent.ACTION_MOVE){
                // TODO: this shouldn't be a class member
				this.projectionAngle = (float) computeAngle(event.getX(), event.getY());

				//TODO: do the drag relative to the touch position
				this.startAngle = this.projectionAngle - 45;
				
				Log.d(TAG, "projectionAngle = " + this.projectionAngle);
				Log.d(TAG, "startAngle = " + this.startAngle);
				
				return true;
			}
			
			if(this.selected && action == MotionEvent.ACTION_UP){
				this.selected = false;
				Log.d(TAG, "ACTION_UP");
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
	
	private class MyPoint{
		public float x, y;
		
		MyPoint(float x, float y){
			this.x = x;
			this.y = y;
		}
	}
	
	private ArrayList<MyPoint> circlePositions = new ArrayList<CircleArena.MyPoint>();
		
	public CircleArena(final Point displaySize, final float ballRadius){
		this.center.x = displaySize.x/2;
		this.center.y = displaySize.y/2;
		
		this.radius = Math.min(this.center.x, this.center.y);
		
		float strokeWidth = 100;//this.radius * FACTOR;
		this.radius -= strokeWidth; // reduce the radius so I allow the stroke to be displayed on screen
				
		this.paint = new Paint();
		this.paint.setStyle(Paint.Style.STROKE);
		this.paint.setStrokeWidth(strokeWidth);
		this.paint.setColor(0xC8000000);
		
		this.pad = new Pad(center, radius, strokeWidth);
        
        this.collisionRadius = this.radius - strokeWidth/2 - ballRadius;
		
		Log.d(TAG, "Circle arena created!\ndisplaySize: " + displaySize + "\n radius=" + this.radius +
			"\nstrokeWidth=" + strokeWidth + "\ncenter=" + this.center);
		
		computeCirclePositions();
	}
	
	private void computeCirclePositions(){
		for(double i=0; i<2*Math.PI; i+= 0.1){
			circlePositions.add(new MyPoint((float) Math.cos(i), (float) Math.sin(i)));
		}	
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
		Point ballPos = b.getPosition();

        if(Helpers.pointDistance(ballPos, this.center) >= this.collisionRadius){
            return true;
        }
		
		return false;
	}
	
	public boolean isBallCollided(Ball b){
		if(this.skip){
			this.skip = false;
			return false;
		}
		
		Point ballPos = b.getPosition();
		
		double ballAngle = this.pad.computeAngle(ballPos.x, ballPos.y); //TODO: not good placement for method, same for below
										
		if(this.pad.startAngle < ballAngle && ballAngle < this.pad.startAngle + this.pad.sweepAngle){
			float minInnerRadius = this.radius - this.pad.strokeWidth/2;
			float maxInnerRadius = this.radius - this.pad.strokeWidth/4;
			
			for(MyPoint p : this.circlePositions){
				double d = Helpers.pointDistance(ballPos, new PointF(p.x, p.y)); //TODO: refactor
				
				if(d >= minInnerRadius && d <= maxInnerRadius){
					Log.d(TAG, "COLLISION!");
					this.skip = true;
					return true;
				}
			}
		}
		
		return false;
	}
}
