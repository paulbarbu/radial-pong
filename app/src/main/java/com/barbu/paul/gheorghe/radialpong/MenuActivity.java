package com.barbu.paul.gheorghe.radialpong;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class MenuActivity extends Activity {
	private static final String TAG = MenuActivity.class.getSimpleName();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_menu);		

		Log.d(TAG, "MenuActivity Created");
	}
	
	public void newGame(View view){
		Intent startNewGame = new Intent(this, GameActivity.class);
		
		startActivity(startNewGame);
	}

    public void highscores(View view){
        Intent highscores = new Intent(this, HighscoresActivity.class);
        startActivity(highscores);
    }
	
}
