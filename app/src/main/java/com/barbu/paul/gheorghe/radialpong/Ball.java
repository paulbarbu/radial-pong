package com.barbu.paul.gheorghe.radialpong;

import java.util.Random;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.util.Log;
import android.view.MotionEvent;

public class Ball extends Actor {
	private float radius;
	private PointF pos;
	private float vx, vy;
    private int speed;

	private static final float FACTOR = 0.10f;
	private static final String TAG = Ball.class.getSimpleName();

    public static class Builder implements IBuilder<Ball> {
        private PointF pos = new PointF();
        private int color, speed;

        public Builder(Point displaySize)
        {
            //TODO: remedy this offset
            this.pos.x = displaySize.x/2 + 50;
            this.pos.y = displaySize.y/2;
        }

        public Builder color(int c)
        {
            color = c;
            return this;
        }

        /**
         * The squared speed
         *
         * @param s the number of pixels per 30th part of a second (see MAX_FPS)
         * @return this instance
         */
        public Builder speed(int s)
        {
            speed = s;
            return this;
        }

        public Ball build()
        {
            return new Ball(this);
        }
    }
	
	private Ball(Builder builder){
        speed = builder.speed;

		paint = new Paint();
		paint.setColor(builder.color);
		paint.setStyle(Paint.Style.FILL);

        pos = builder.pos;
        radius = Math.min(pos.x, pos.y) * FACTOR;

        //set the direction
//        Random r = new Random();
//        int val = r.nextInt(SPEED+1);
//
//        vx = (int) Math.sqrt(val) * Helpers.boolToSign(r.nextBoolean());
//        vy = (int) Math.sqrt(SPEED-val) * Helpers.boolToSign(r.nextBoolean());
        vy =  8;
        vx = 0;

		Log.d(TAG, "Ball created!\n radius=" + radius + "\npos=" + pos);
		
	}

    public PointF getPosition(){
        return pos;
    }

    public void setPosition(PointF p){
        pos = p;
    }
	
	public float getVelocityX(){
		return vx;
	}
	
	public float getVelocityY(){
		return vy;
	}
	
	public void setVelocityX(final float val){
		vx = val;
	}
	
	public void setVelocityY(final float val){
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
