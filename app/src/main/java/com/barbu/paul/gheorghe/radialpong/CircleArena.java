package com.barbu.paul.gheorghe.radialpong;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.util.Log;
import android.view.MotionEvent;

public class CircleArena extends Actor {
	// Time a collision should last in ms.
	// This is used to avoid detecting the same collision multiple times.
	private long COLLISION_TIME = 400;
	private long collisionTimeCache = 0;
	// As a simple workaround for the case where the ball is hitting the edge
	// of the Pad, we just add a small "invisible" extension to the pad in order
	// to make the ball bounce. It's not perfect, but simple and doesn't look that weird.
	// The paddingAngle expresses how big this "invisible extension" is going to be.
	private double paddingAngle;

	private class Pad extends Actor{
		protected float projectionAngle=45, startAngle=0, sweepAngle=90, radius, strokeWidth;
		protected Point center;
		protected RectF boundingBox;
		private Paint paint;
		private boolean selected = false, touched = false;
		
		public Pad(Point center, float radius, float strokeWidth){
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
		
		public double getDistToCenter(float x, float y){
			float xLen = x - this.center.x;
			float yLen = y - this.center.y;
			
			return Math.sqrt(xLen*xLen + yLen*yLen);
		}
		
		public boolean isInsideBounds(float x, float y){
			float touchAngle = computeAngle(x, y);
			double distToCenter = getDistToCenter(x, y);
									
			if(this.startAngle < touchAngle && touchAngle < this.startAngle + this.sweepAngle &&
					this.radius - this.strokeWidth/2 < distToCenter &&
					distToCenter < this.radius + this.strokeWidth/2){
				
				Log.d(TAG, "INSIDE!");
				return true;
			}
			
			return false;
		}
		
		public float computeAngle(float x, float y){
			float slope = (this.center.y - y)/(this.center.x - x);
			
			float angle = (float)Math.toDegrees(Math.atan(slope));
			
			if(x < this.center.x){
				angle += 180;
			}
			
			return angle % 360;
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
				this.projectionAngle = computeAngle(event.getX(), event.getY());

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
	private Pad pad;
	
	public CircleArena(Point displaySize){
		this.center.x = displaySize.x/2;
		this.center.y = displaySize.y/2;
		
		this.radius = Math.min(this.center.x, this.center.y);
		
		float strokeWidth = this.radius * FACTOR;
		this.radius -= strokeWidth;
				
		this.paint = new Paint();
		this.paint.setStyle(Paint.Style.STROKE);
		this.paint.setStrokeWidth(strokeWidth);
		this.paint.setColor(0xC8000000);
		
		this.pad = new Pad(center, radius, strokeWidth);
		
		Log.d(TAG, "Circle arena created!\ndisplaySize: " + displaySize + "\n radius=" + this.radius +
			"\nstrokeWidth=" + strokeWidth + "\ncenter=" + this.center);

	}

	@Override
	public void update() {
	}

	@Override
	public void draw(Canvas c) {
		c.drawCircle(this.center.x, this.center.y, this.radius, this.paint);
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
		if(this.pad.getDistToCenter(ballPos.x, ballPos.y) >= this.radius)
			return true;
		return false;
	}
	
	public boolean isBallCollided(Ball b){
		long timestamp = System.currentTimeMillis();
		if(timestamp - this.collisionTimeCache < this.COLLISION_TIME){
			// Collision already detected
			return false;
		}
		Point ballPos = b.getPosition();
		
		float ballAngle = this.pad.computeAngle(ballPos.x, ballPos.y); //TODO: not good placement for method, same for below
		if(this.paddingAngle == 0)
			this.paddingAngle = Math.toDegrees(Math.asin(b.getRadius()/this.radius))*0.75;
		if(this.pad.startAngle - paddingAngle < ballAngle && ballAngle < this.pad.startAngle + this.pad.sweepAngle + paddingAngle){
			float minInnerRadius = this.radius - this.pad.strokeWidth/2 - b.getRadius();
			double d = this.pad.getDistToCenter(ballPos.x, ballPos.y);
			if(d >= minInnerRadius){
				Log.d(TAG, "COLLISION!");
				this.collisionTimeCache = timestamp;
				return true;
			}
		}
		
		return false;
	}
}
