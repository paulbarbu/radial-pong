package com.barbu.paul.gheorghe.radialpong;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import java.util.Observable;
import java.util.Observer;

public class GameActivity extends Activity implements Observer {
	private static final String TAG = GameActivity.class.getSimpleName();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(new GameView(this));
		
		Log.d(TAG, "GameActivity Created");
	}

    public void update(Observable obs, Object obj)
    {
        Intent gameOver = new Intent(this, GameOverActivity.class);

        gameOver.putExtra(Score.EXTRA_LIVES, obj.toString());

        startActivity(gameOver);
        finish();
    }
}
