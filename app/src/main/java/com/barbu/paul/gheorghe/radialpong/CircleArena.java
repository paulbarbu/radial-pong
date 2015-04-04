package com.barbu.paul.gheorghe.radialpong;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.RectF;
import android.os.Vibrator;
import android.util.Log;
import android.view.MotionEvent;
import static com.barbu.paul.gheorghe.radialpong.Helpers.DEBUG_MODE;

public class CircleArena extends Actor {
	private class Pad extends Actor {
		protected float startAngle=0, arcStartAngle=0, radius, strokeWidth, lastTouchAngle;
        protected static final float sweepAngle=90;
       

        protected Point center;
		protected RectF boundingBox;
		private boolean touched = false;

        public Pad(final Point center, final float radius, final float strokeWidth){
			this.center = new Point(center);
			this.radius = radius;
			this.strokeWidth = strokeWidth;
			
			paint = new Paint();
			paint.setStyle(Paint.Style.STROKE);
			paint.setStrokeWidth(strokeWidth);
			paint.setColor(Color.CYAN);
			
			boundingBox = new RectF(center.x-radius, center.y-radius, center.x + radius, center.y+radius);
			//Log.d(TAG, "Pad created");
		}

		@Override
		public void update() {
		}

		@Override
		public void draw(Canvas c) {            
			c.drawArc(boundingBox, arcStartAngle, sweepAngle, false, paint);

            if(DEBUG_MODE)
            {
                Paint p = new Paint();
                p.setStyle(Paint.Style.STROKE);
                p.setStrokeWidth(10);
                p.setColor(Color.CYAN);

                c.drawArc(boundingBox, arcStartAngle, 5, false, p);

                p.setColor(Color.GREEN);
                c.drawArc(boundingBox, arcStartAngle + sweepAngle, 5, false, p);
            }
		}

        protected boolean isInsideAngle(Point p)
        {
            return isInsideAngle(new PointF(p.x, p.y), 0);
        }
        
        protected boolean isInsideAngle(PointF p)
        {
            return isInsideAngle(p, 0);
        }

        protected boolean isInsideAngle(Point p, float paddingAngle)
        {
            return isInsideAngle(new PointF(p.x, p.y), paddingAngle);
        }

        /**
         * Test if a point projection is inside the pad with regards to the angle
         *
         * The padding idea came from here:
         * https://github.com/bilthon/radial-pong/commit/86b96382583f4a28cd4a65643af037c12a54f589
         *
         * @param p the point for which the angle is to be calculated, relative to the center of the circle
         * @param paddingAngle the angle to be added to the pad so that edge collisions are nicely handled
         * @return true if the point is inside the pad, angle wise, false otherwise
         */
        protected boolean isInsideAngle(PointF p, float paddingAngle)
        {
            p = Helpers.mapDisplayPointTo(p, center);
            double angle = Helpers.getAngle(p.x, p.y);

//            Log.d(TAG, "x=" + p.x + " y=" + p.y);
//            Log.d(TAG, "angle=" + angle);
//            Log.d(TAG, "startAngle=" + startAngle);

            // when the pad is in the first quadrant it will also be in the fourth,
            // so I have to check for hit points there, too
            if(startAngle < 90 && angle <= paddingAngle + startAngle || angle >= 360+(startAngle-sweepAngle-paddingAngle))
            {
//                Log.d(TAG, "first");
                return  true;
            }

            // getAngle works in counter-clockwise order, but drawArc works clockwise (start - stop),
            // so actually the startAngle will be bigger than the endAngle in counter-clockwise order
            // since I hit first the stop then the start, and the desired angle has to be between them
            return startAngle + paddingAngle >= angle && angle >= startAngle - sweepAngle - paddingAngle;
        }

        /**
         * Test is the given point is touching the pad
         * @param touchPoint the touch point to test if is inside the pad (the stroke is considered accordingly)
         * @return true if the point is inside, false otherwise
         */
		protected boolean isInsideDistance(PointF touchPoint){
			double distToCenter = Helpers.pointDistance(new PointF(touchPoint.x, touchPoint.y), center);

			if(radius - strokeWidth/2 <= distToCenter && distToCenter <= radius + strokeWidth/2)
            {
//				Log.d(TAG, "Distance INSIDE!");
				return true;
			}

//            Log.d(TAG, "Distance OUTSIDE!");
			return false;
		}

		@Override
		public boolean handleTouchEvent(MotionEvent event) {
			int action = event.getAction();

            PointF touchPoint = new PointF(event.getX(), event.getY());

			if(MotionEvent.ACTION_DOWN == action && isInsideDistance(touchPoint) && isInsideAngle(touchPoint)){
//				Log.d(TAG, "ACTION_DOWN");
				this.touched = true;

                PointF p = Helpers.mapDisplayPointTo(touchPoint, center);
                lastTouchAngle = (float)Helpers.getAngle(p.x, p.y);
                
				return true;
			}

			if(this.touched && action == MotionEvent.ACTION_MOVE){
                PointF p = Helpers.mapDisplayPointTo(touchPoint, center);
				float touchAngle = (float)Helpers.getAngle(p.x, p.y);

                //deltaAngle is the offset that should be added to the startAngle of the Pad
                // relative to the startAngle so the pad doesn't jump around when it's selected
                float deltaAngle = touchAngle-lastTouchAngle;
                
                // this may happen if the user touches, say,  first at 354 deg then at 1 deg
                // in this case the delta should be 360-354 + 1 = 7 deg, not -353 deg
                if(deltaAngle < 0)
                {
                    deltaAngle = 360-lastTouchAngle + touchAngle;
                }
                
                startAngle = startAngle + deltaAngle;
                startAngle = (startAngle + 360) % 360; //wrap around the circle
                
                //the drawArc method works clockwise, everything I calculate here is counter-clockwise
				arcStartAngle = 360-startAngle;

                if(DEBUG_MODE) {
                    Log.d(TAG, "touchAngle = " + touchAngle);
                    Log.d(TAG, "lastTouchAngle = " + lastTouchAngle);
                    Log.d(TAG, "deltaAngle = " + deltaAngle);
                    Log.d(TAG, "startAngle = " + startAngle);
                }


                lastTouchAngle = touchAngle;

				return true;
			}
			
			if(this.touched && action == MotionEvent.ACTION_UP){
				this.touched = false;
//				Log.d(TAG, "ACTION_UP");
				return true;
			}

            return false;
		}
	}

	private static final String TAG = CircleArena.class.getSimpleName();

    private Point center = new Point();
    private PointF normalE = new PointF(0, 0);
    private PointF normalS = new PointF(0, 0);
	private float radius;
    private float collisionRadius;
	private Pad pad;
    private int bgColor;
    private final int bgColorIn;
    private final int bgColorOut;
    private boolean ballInside = true;
    private Vibrator vibrator;
    private long vibrateDuration;

    public static class Builder implements IBuilder<CircleArena> {
        private static final float FACTOR = 0.18f;

        private float ballRadius, radius, collisionRadius;
        private float strokeWidth;
        private Point center = new Point();
        private int color, bgColorIn, bgColorOut;
        private Vibrator vibrator;
        private long vibrateDuration;

        public Builder(final Point displaySize)
        {
            center.x = displaySize.x/2;
            center.y = displaySize.y/2;
        }

        public Builder ballRadius(final float r)
        {
            ballRadius = r;
            return this;
        }

        public Builder bgColorIn(int c)
        {
            bgColorIn = c;
            return this;
        }

        public Builder bgColorOut(int c)
        {
            bgColorOut = c;
            return this;
        }

        public Builder color(int c)
        {
            color = c;
            return this;
        }

        public Builder vibrator(Vibrator v)
        {
            vibrator = v;
            return this;
        }

        public Builder vibrateDuration(long duration)
        {
            vibrateDuration = duration;
            return this;
        }

        public CircleArena build()
        {
            radius = Math.min(center.x, center.y);
            strokeWidth = radius * FACTOR;

            radius -= strokeWidth; // reduce the radius so I allow the stroke to be displayed on screen

            collisionRadius = this.radius - strokeWidth/2 - ballRadius;

            return new CircleArena(this);
        }
    }

	private CircleArena(Builder builder){
        bgColorIn = builder.bgColorIn;
        bgColorOut = builder.bgColorOut;
        bgColor = bgColorIn;
        center = builder.center;
        radius = builder.radius;
        collisionRadius = builder.collisionRadius;
        vibrator = builder.vibrator;
        vibrateDuration = builder.vibrateDuration;

        paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(builder.strokeWidth);
        paint.setColor(builder.color);

        pad = new Pad(center, radius, builder.strokeWidth);
    }

	@Override
	public void update() {
	}

    /**
     * Update the arena with regards to the ball's position
     *
     * There, as you can see from isBallInside and isBallOutside methods, is a gap between the "outside"
     * and the "inside" areas, this allows me to avoid the collision when the ball is coming from the
     * outside zone into the inside zone and bounce it back outside, because when the ball will
     * actually be inside (coming from outside) the collision detection algorithm won't detect a collision
     * since the radius of the inside zone is a bit smaller than the collision radius
     * So it's crucial to keep the "ballInside" variable the same while the ball is between the two zones
     *
     * @param b the ball that will influence how the arena will be updated
     */
    public void update(Ball b)
    {
        if(isBallOutside(b))
        {
            // vibrate once if the transition happened now
            if(ballInside)
            {
                vibrator.vibrate(vibrateDuration);
                //TODO: lose a heart
            }

            bgColor = bgColorOut;
            ballInside = false;
        }
        else if(isBallInside(b))
        {
            bgColor = bgColorIn;
            ballInside = true;
        }

        if(isBallCollided(b)){
            //TODO: animate and sound + randomness + 180 deg + 90 deg
            Vec2 v = new Vec2(b.getVelocityX(), b.getVelocityY());
            PointF p = b.getPosition();

            Vec2 normal = new Vec2(center.x-p.x, center.y-p.y);
            normal = normal.toUnit();

            // normal coordinates in the collision points
            if(DEBUG_MODE) {
                normalE = new PointF(p.x + normal.getX() * 100, p.y + normal.getY() * 100);
                normalS = new PointF(p.x, p.y);
            }

            //Vec2 u = normal.mul(v.dot(normal)/normal.dot(normal)); // assuming that the normal is not the unit vector
            Vec2 u = normal.mul(v.dot(normal)); // perpendicular component
            Vec2 w = v.sub(u); // parallel component
            Vec2 r = w.sub(u); // mirror

            b.setVelocityX(r.getX());
            b.setVelocityY(r.getY());

            //TODO: gain a point
        }
    }

	@Override
	public void draw(Canvas c) {
        c.drawColor(bgColor);
		c.drawCircle(this.center.x, this.center.y, this.radius, this.paint);
        
        if(DEBUG_MODE)
        {
            Paint p = new Paint();
            p.setStyle(Paint.Style.STROKE);
            p.setStrokeWidth(0);
            p.setColor(Color.CYAN);
            c.drawCircle(this.center.x, this.center.y, this.radius, p);

            p.setColor(Color.GREEN);
            c.drawCircle(this.center.x, this.center.y, this.collisionRadius, p);
        }
        
		this.pad.draw(c);

        // draw the normal in the collision point
        if(DEBUG_MODE)
        {
            Paint p = new Paint();
            p.setStyle(Paint.Style.STROKE);
            p.setStrokeWidth(0);
            p.setColor(Color.BLACK);
            c.drawLine(normalS.x, normalS.y, normalE.x, normalE.y, p);
        }
	}

	@Override
	public boolean handleTouchEvent(MotionEvent event) {
        return this.pad.handleTouchEvent(event);
	}

    /**
     * Check if the user touches the pad
     * @return true if hte pad is touched, false otherwise
     */
	public boolean isTouched(){
		return pad.touched;
	}

    protected boolean isBallOutside(Ball b)
    {
        return Helpers.pointDistance(b.getPosition(), this.center) >= this.collisionRadius + b.getRadius()/2;
    }

    protected boolean isBallInside(Ball b)
    {
        return Helpers.pointDistance(b.getPosition(), this.center) <= this.collisionRadius - b.getRadius()/2;
    }

    /**
     * Check if the ball collides with the pad
     * @param b the ball to be checked for collisions against the pad
     * @return true if the ball is colliding with the pad, false otherwise
     */
	public boolean isBallCollided(Ball b){
        float paddingAngle = (float) Math.toDegrees(Math.asin(b.getRadius() / radius));
        // if the ball is still inside and going outside (the distance to the center of the arena is greater
        // than the collision radius) and the angle of the ball is "inside" the pad then I have a collision
        return ballInside && Helpers.pointDistance(b.getPosition(), this.center) >= this.collisionRadius
            && pad.isInsideAngle(b.getPosition(), paddingAngle);
	}
}
