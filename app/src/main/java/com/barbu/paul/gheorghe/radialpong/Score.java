package com.barbu.paul.gheorghe.radialpong;

import java.util.Observable;

public class Score extends Observable {
    public final static String EXTRA_LIVES = "com.barbu.paul.gheorghe.radialpong.LIVES";
    private int lives;
    private int points;

    public Score(int l, int p)
    {
        lives = l;
        points = p;
    }

    public int getLives()
    {
        return lives;
    }

    public int getPoints()
    {
        return points;
    }

    public boolean isGameOver()
    {
        return lives <= 0;
    }

    public void incrementLives()
    {
        lives++;
    }

    public void decrementLives()
    {
        lives--;

        if(isGameOver())
        {
            setChanged();
            notifyObservers(String.valueOf(points));
        }
    }

    public void incrementPoints()
    {
        points++;
    }

//    public void decrementPoints()
//    {
//        points--;
//    }
}
