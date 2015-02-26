package com.barbu.paul.gheorghe.radialpong;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.Log;
import android.view.MotionEvent;

public class Arena extends Actor {
	private static final String TAG = Arena.class.getSimpleName();
	
	private Paint paint;
	private Point center = new Point();
	private float radius;
	
	public Arena(Point displaySize){
		center.x = displaySize.x/2;
		center.y = displaySize.y/2;
		
		radius = center.x < center.y ? center.x : center.y;
		
		float strokeWidth = radius * 0.15f; //15% //TODO try on the phone dynamically
		radius -= strokeWidth;
				
		paint = new Paint();
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeWidth(strokeWidth);
		paint.setColor(0xFF00FF00);
		
		Log.d(TAG, "Oval arena created, display size: " + displaySize);//.x + " Y:" + displaySize.y
	}
	
	@Override
	public void update() {
	}

	@Override
	public void draw(Canvas c) {
		c.drawCircle(this.center.x, this.center.y, this.radius, this.paint);
	}

	@Override
	public boolean handleTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		return false;
	}

}
