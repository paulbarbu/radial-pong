package com.barbu.paul.gheorghe.radialpong;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.os.Vibrator;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;

import java.util.Observer;

public class GameView extends SurfaceView implements SurfaceHolder.Callback {
	private static final String TAG = GameView.class.getSimpleName();
	
	protected SurfaceHolder surfaceHolder;
	protected GameThread gameThread;
	
	protected CircleArena arena;
	protected Ball ball;
    protected Score score;
    private Point displaySize =  new Point();
    private Paint scorePaint = new Paint();
    Bitmap heart = BitmapFactory.decodeResource(getResources(), R.drawable.heart);
    Bitmap point = BitmapFactory.decodeResource(getResources(), R.drawable.target);

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

        Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
		score = new Score(3, 0);

		ball = new Ball.Builder(displaySize)
                .color(0xFF33B5E5)
                .speed(25)
                .maxOffset(50)
                .build();

        arena = new CircleArena.Builder(displaySize)
                .ballRadius(ball.getRadius())
                .color(0x7FFFBB33)
                .bgColorIn(Color.WHITE)
                .bgColorOut(0xFFFF4444)
                .vibrator(v)
                .vibrateDuration(500)
                .score(score)
                .build();

        scorePaint.setAntiAlias(true);
        scorePaint.setTextSize(displaySize.y*10/100); // 10% of the display size
		
		surfaceHolder.addCallback(this);
		
		setFocusable(true);

        score.addObserver((Observer) context);
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

            drawScore(c);
		}
		finally{
			this.surfaceHolder.unlockCanvasAndPost(c);
		}
	}

    private void drawScore(Canvas c)
    {
        Rect bounds = new Rect();
        int top, left;
        final int padding = 10;

        String lives = String.valueOf(score.getLives());

        scorePaint.getTextBounds(lives, 0, lives.length(), bounds);
        top = displaySize.y - bounds.bottom - padding;
        left = displaySize.x - bounds.right - padding;
        c.drawBitmap(heart, left-heart.getWidth(), displaySize.y - heart.getHeight() - padding, scorePaint);
        c.drawText(lives, left, top, scorePaint);

        String points = String.valueOf(score.getPoints());
        scorePaint.getTextBounds(points, 0, points.length(), bounds);
        left = padding;
        top = displaySize.y - bounds.bottom - padding;
        c.drawBitmap(point, left, displaySize.y - point.getHeight() - padding, scorePaint);
        c.drawText(points, left + point.getWidth(), top, scorePaint);

    }

	public void update(){
        if(!arena.isTouched())
        {
            return;
        }

        PointF p = ball.getPosition();
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