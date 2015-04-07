package com.barbu.paul.gheorghe.radialpong;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.util.Log;

import java.util.Random;

public class Ball extends Actor {
	private float radius;
	private float vx, vy;
    private int speed;

	private static final float FACTOR = 0.10f;
	private static final String TAG = Ball.class.getSimpleName();

    public static class Builder extends Actor.Builder {
        private int color, speed, maxOffset = 10;

        public Builder(final Point displaySize)
        {
            super(displaySize);
        }

        public Builder color(int c)
        {
            color = c;
            return this;
        }

        public Builder maxOffset(int mo)
        {
            maxOffset = mo;
            return this;
        }

        /**
         * The squared speed
         *
         * @param s the squared number of pixels per 30th part of a second (see MAX_FPS)
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
        super(builder);
        speed = builder.speed;

		paint.setColor(builder.color);
		paint.setStyle(Paint.Style.FILL);

        radius = Math.min(center.x, center.y) * FACTOR;

        //set the direction
        Random r = new Random();
        int val = r.nextInt(speed+1);

        vx = (float) Math.sqrt(val) * Helpers.boolToSign(r.nextBoolean());
        vy = (float) Math.sqrt(speed-val) * Helpers.boolToSign(r.nextBoolean());

        // don't center it on the screen in order to avoid being on the
        // normal vector when colliding with the pad
        center.x += r.nextInt(builder.maxOffset) * Helpers.boolToSign(r.nextBoolean());
        center.y += r.nextInt(builder.maxOffset) * Helpers.boolToSign(r.nextBoolean());

		Log.d(TAG, "Ball created!\n radius=" + radius + "\ncenter=" + center);
		
	}

    public PointF getPosition(){
        return center;
    }

    public void setPosition(PointF p){
        center = p;
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
		center.x += vx;
		center.y += vy;
	}
	
	@Override
	public void draw(Canvas c) {
		c.drawCircle(center.x, center.y, radius, paint);
	}

}
