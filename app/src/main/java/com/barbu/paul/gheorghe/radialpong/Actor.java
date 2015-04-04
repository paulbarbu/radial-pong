package com.barbu.paul.gheorghe.radialpong;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.MotionEvent;

public abstract class Actor {
    protected Paint paint;
	public abstract void update();
	public abstract void draw(Canvas c);
	public boolean handleTouchEvent(MotionEvent event)
    {
        return false;
    }
}
