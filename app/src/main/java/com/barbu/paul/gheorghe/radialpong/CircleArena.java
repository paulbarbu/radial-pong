package com.barbu.paul.gheorghe.radialpong;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.os.Vibrator;
import android.util.Log;
import android.view.MotionEvent;

import static com.barbu.paul.gheorghe.radialpong.Helpers.DEBUG_MODE;

public class CircleArena extends Actor {
	private static final String TAG = CircleArena.class.getSimpleName();

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
    private Score score;

    public static class Builder extends Actor.Builder {
        private static final float FACTOR = 0.18f;

        private float ballRadius, radius, collisionRadius;
        private float strokeWidth;
        private int color, bgColorIn, bgColorOut;
        private Vibrator vibrator;
        private long vibrateDuration;
        private Score score;

        public Builder(final Point displaySize)
        {
            super(displaySize);
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

        public Builder score(Score s)
        {
            score = s;
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
        super(builder);

        score = builder.score;
        bgColorIn = builder.bgColorIn;
        bgColorOut = builder.bgColorOut;
        bgColor = bgColorIn;
        radius = builder.radius;
        collisionRadius = builder.collisionRadius;
        vibrator = builder.vibrator;
        vibrateDuration = builder.vibrateDuration;

        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(builder.strokeWidth);
        paint.setColor(builder.color);

        pad = new Pad.Builder(center)
            .radius(radius)
            .strokeWidth(builder.strokeWidth)
            .color(0xFF99CC00)
            .build();

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
            // vibrate once and lose a life if the transition happened now
            if(ballInside)
            {
                vibrator.vibrate(vibrateDuration);
                score.decrementLives();
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

            if(r.mag() < 13) {
                r = r.changeMagTo((float) r.mag() + 0.2f);
            }

            b.setVelocityX(r.getX());
            b.setVelocityY(r.getY());

            //TODO: gain a point
            score.incrementPoints();
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
		return pad.isTouched();
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
