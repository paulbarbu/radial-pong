package com.barbu.paul.gheorghe.radialpong;

import java.util.Random;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.Log;
import android.view.MotionEvent;

public class Ball extends Actor {
	private float radius;
	private Point pos = new Point();
	private Paint paint;
	private int vx, vy;

	private static final float FACTOR = 0.10f;
	private static final int SPEED = 64; //this is the squared speed TODO: set from outside, this is the number of pixels per 30th part of a second (see MAX_FPS)
	private static final String TAG = Ball.class.getSimpleName();
	
	public Ball(Point displaySize){
		paint = new Paint();
		paint.setColor(0xFF0000FF); //TODO: set it from outside
		paint.setStyle(Paint.Style.FILL);

        pos.x = displaySize.x/2;
        pos.y = displaySize.y/2;
        radius = Math.min(pos.x, pos.y) * FACTOR;

        //set the direction
        Random r = new Random();
        int val = r.nextInt(SPEED+1);

        vx = (int) Math.sqrt(val) * Helpers.boolToSign(r.nextBoolean());
        vy = (int) Math.sqrt(SPEED-val) * Helpers.boolToSign(r.nextBoolean());

		Log.d(TAG, "Ball created!\n radius=" + radius + "\npos=" + pos);
		
	}

    public Point getPosition(){
        return pos;
    }

    public void setPosition(Point p){
        pos = p;
    }
	
	public int getVelocityX(){
		return vx;
	}
	
	public int getVelocityY(){
		return vy;
	}
	
	public void setVelocityX(int val){
		vx = val;
	}
	
	public void setVelocityY(int val){
		vy = val;
	}
	
	public float getRadius(){
		return radius;
	}
	
	@Override
	public void update() {
		pos.x += vx;
		pos.y += vy;
	}
	
	@Override
	public void draw(Canvas c) {
		c.drawCircle(pos.x, pos.y, radius, paint);
	}

}
