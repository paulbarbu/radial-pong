package com.barbu.paul.gheorghe.radialpong;

import android.util.Log;

public class GameThread extends Thread {
	private static final String TAG = GameThread.class.getSimpleName();
	private static final int MAX_FPS = 30; //TODO: configurable
	private static final int MAX_SKIPPED_FRAMES = 10; //TODO: configurable
	private static final int INTERVAL = 1000/MAX_FPS;
		
	private boolean running = false, paused = true;
	private GameView gameView;
	
	public GameThread(GameView gameView){
		super();
		this.gameView = gameView;
	}
	
	public void setRunning(boolean state){
		this.running = state;
		
		Log.d(TAG, "running = " + state);
	}
	
	public void setPaused(boolean state){
		this.paused = state;
		
		Log.d(TAG, "paused = " + state);
	}
	
	public boolean isRunning(){
		return this.running;
	}
	
	public boolean isPaused(){
		return this.paused;
	}
	
	@Override
	public void run(){
		int skippedFrames;
		long tickCount = 0;
		double deltaTime = 0;
		long sleepTime;
		
		while(this.running){
			skippedFrames = 0;
			++tickCount;			
			double startTime = System.nanoTime()/1000000;
			
			if(!this.paused){
				this.gameView.update();
				this.gameView.render();
			}

			deltaTime = System.nanoTime()/1000000 - startTime;

			sleepTime = (long) (INTERVAL - deltaTime);
			
			if(sleepTime > 0){
				try{
					sleep(sleepTime);
				}
				catch(InterruptedException e){
				}
			}
			
			while(sleepTime < 0 && skippedFrames < MAX_SKIPPED_FRAMES){
				this.gameView.update();
				++skippedFrames;
				sleepTime += INTERVAL;
			}			
		}
		
		Log.d(TAG, "Ran the game loop " + tickCount + " times");
	}
}
