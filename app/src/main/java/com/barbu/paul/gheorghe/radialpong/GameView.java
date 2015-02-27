package com.barbu.paul.gheorghe.radialpong;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
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
	
	@SuppressLint("NewApi")
	public GameView(Context context){
		super(context);		
		this.surfaceHolder = getHolder();
		//TODO: try to remove supresslint
		//TODO: pad no larger than 90 and no smaller than ...
		//TODO: handle activity lifetime, and interruptions like calls
		//TODO: http://stackoverflow.com/questions/1016896/how-to-get-screen-dimensions
		Point displaySize =  new Point();
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay(); //TODO: check for null
		
		if(display != null){
			display.getSize(displaySize);
		}
		
		this.arena = new CircleArena(displaySize);
		this.ball = new Ball(displaySize);
		
		this.surfaceHolder.addCallback(this);
		
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
			c.drawColor(Color.WHITE); //TODO:
			this.arena.draw(c);
			this.ball.draw(c);
		}
		finally{
			this.surfaceHolder.unlockCanvasAndPost(c);
		}
	}
	
	public void reset(){
		this.ball.init();

		this.gameThread.setPaused(true);
		this.arena.setTouched(false);
		
		this.render();
	}	
	public void update(){
		if(this.arena.isBallOutside(ball)){
			//TODO: vibrate
			this.reset();
			return;
		}
		
		if(this.arena.isBallCollided(ball)){
			//turn it around
			//TODO: animate and sound
			this.ball.setVelocityX(-1*this.ball.getVelocityX());
			this.ball.setVelocityY(-1*this.ball.getVelocityY());
		}		
		
		this.ball.update();
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