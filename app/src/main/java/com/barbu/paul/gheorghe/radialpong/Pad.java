package com.barbu.paul.gheorghe.radialpong;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.ShapeDrawable;
import android.util.Log;
import android.view.MotionEvent;

public class Pad extends Actor {
	private static final String TAG = Pad.class.getSimpleName();
	
	protected int x, y;
	protected ShapeDrawable shape;
	protected boolean selected = true;
	
	public Pad(int x, int y, int width, int height){
		this.x = x;
		this.y = y;
		//TODO: always move it if it was touched before (compute the position on the circle from the last touch position), don't move it on action_down
		this.shape = new ShapeDrawable();
		this.shape.setIntrinsicHeight(height);
		this.shape.setIntrinsicWidth(width);
		this.shape.setBounds(x - width/2, y - height/2, x + width/2, y + height/2);
		
		this.shape.getPaint().setColor(0xFF00FF00);
		
		Log.d(TAG, "Pad constructed");
	}
	
	protected boolean isInsideBounds(float x, float y){
		Rect bounds = this.shape.getBounds();
		
		if(x <= bounds.right && x >= bounds.left && y <= bounds.bottom && y >= bounds.top){
			return true;
		}
		
		return false;
	}
	
	@Override
	public boolean handleTouchEvent(MotionEvent event){
		int action = event.getAction(); 
		
		//TODO: move relative to the touched point, not to the center
		if(action == MotionEvent.ACTION_DOWN && isInsideBounds(event.getX(), event.getY())){
			Log.d(TAG, "ACTION_DOWN");
			this.selected = true;
			
			return true;
		}
		
		if(this.selected && action == MotionEvent.ACTION_MOVE){
			shape.setBounds((int) event.getX() - shape.getIntrinsicWidth()/2, (int) event.getY() - shape.getIntrinsicHeight()/2,
					(int) event.getX() + shape.getIntrinsicWidth()/2, (int) event.getY() + shape.getIntrinsicHeight()/2);
			return true;
		}
		
		if(this.selected && action == MotionEvent.ACTION_UP){
			Log.d(TAG, "ACTION_UP");
			this.selected = false;
			
			return true;
		}
		
		return false;
	}

	@Override
	public void update() {
		
	}

	@Override
	public void draw(Canvas c) {
		shape.draw(c);
	}
}
