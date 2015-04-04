package com.barbu.paul.gheorghe.radialpong;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.view.MotionEvent;

public abstract class Actor {
    protected Paint paint = new Paint();
    protected PointF center = new PointF();

    public static abstract class Builder implements IBuilder<Actor>
    {
        protected PointF center = new PointF();

        public Builder(final Point displaySize)
        {
            center.x = displaySize.x/2;
            center.y = displaySize.y/2;
        }
    }

    public Actor(){
    }

    public Actor(Builder builder){
        center = builder.center;
    }

	public abstract void update();
	public abstract void draw(Canvas c);
	public boolean handleTouchEvent(MotionEvent event)
    {
        return false;
    }
}
