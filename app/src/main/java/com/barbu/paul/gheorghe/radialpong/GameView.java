package com.barbu.paul.gheorghe.radialpong;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.PointF;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;

public class GameView extends SurfaceView implements SurfaceHolder.Callback {
	private static final String TAG = GameView.class.getSimpleName();
	
	protected SurfaceHolder surfaceHolder;
	protected GameThread gameThread;
	
	protected CircleArena arena;
	protected Ball ball;
    private Point displaySize =  new Point();

	public GameView(Context context){
		super(context);		
		this.surfaceHolder = getHolder();
		//TODO: pad no larger than 90 and no smaller than ...
		//TODO: handle activity lifetime, and interruptions like calls
		//TODO: http://stackoverflow.com/questions/1016896/how-to-get-screen-dimensions
		
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		
		if(display != null){
			display.getSize(displaySize);
		}
		
		ball = new Ball(displaySize);
        arena = new CircleArena(displaySize, ball.getRadius());
		
		surfaceHolder.addCallback(this);
		
		setFocusable(true);
	}

	@Override
	public void surfaceCreated(SurfaceHolder surfaceHolder){
        this.gameThread = new GameThread(this);
		this.gameThread.setRunning(true);
		this.gameThread.start();
		
		this.render();
		
		Log.d(TAG, "Surface created, thread started");
	}
	
	public void render(){
		Canvas c = this.surfaceHolder.lockCanvas();
		
		if(c == null){
			return;
		}
		
		try{
			arena.draw(c);
			ball.draw(c);
		}
		finally{
			this.surfaceHolder.unlockCanvasAndPost(c);
		}
	}

	public void update(){
        if(!arena.isTouched())
        {
            return;
        }

        Point p = ball.getPosition();
        float offset = ball.getRadius();

        if(p.x + offset >= displaySize.x || p.x - offset <= 0)
        {
            ball.setVelocityX(-1*ball.getVelocityX());
        }

        if(p.y + offset >= displaySize.y || p.y - offset <= 0)
        {
            ball.setVelocityY(-1*ball.getVelocityY());
        }

        ball.update();
        arena.update(ball);

        //TODO: move these into the update function of the arena and get rid of the getCenter method
        if(arena.isBallCollided(ball)){
            //TODO: 90 deg, not a number -> random
            //TODO: when the angles are too close ~5 deg, add a random number to them
            int vx = ball.getVelocityX();
            int vy = ball.getVelocityY();
            double angle = Helpers.angle(vx, vy, Math.abs(vx), 0);
            if(vy > 0)
            {
                angle = 360 - angle;
            }
            Log.d(TAG, "Angle of ball: " + angle);
            
            PointF point = Helpers.mapDisplayPointTo(ball.getPosition(), arena.getCenter());
            Log.d(TAG, "Angle to center: " + Helpers.getAngle(point.x, point.y));
            //TODO: proper "reflection" of the ball from the pad
            //TODO: animate and sound
            ball.setVelocityX(-1*this.ball.getVelocityX());
            ball.setVelocityY(-1*this.ball.getVelocityY());
        }
	}

	@Override
	public boolean onTouchEvent(MotionEvent event){
		boolean retval = this.arena.handleTouchEvent(event);
		
		if(this.gameThread.isPaused() && this.arena.isTouched()){
			this.gameThread.setPaused(false);
		}
		
		return retval;
	}
	 
	@Override
	public void surfaceChanged(SurfaceHolder surfaceHolder, int format, int width, int height){	
	}
	
	@Override
	public void surfaceDestroyed(SurfaceHolder surfaceHolder){
		boolean retry = true;
		this.gameThread.setRunning(false);
		
		while(retry){
			try{
				this.gameThread.join();
				retry = false;
			}
			catch(InterruptedException e){
				//try again
			}
		}
		
		Log.d(TAG, "Surface destroyed");
	}	
}