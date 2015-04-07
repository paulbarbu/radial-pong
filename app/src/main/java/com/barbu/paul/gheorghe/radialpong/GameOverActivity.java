package com.barbu.paul.gheorghe.radialpong;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;


public class GameOverActivity extends Activity {
    private static final String TAG = MenuActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_over);
    }

    public void submit(View view){
        EditText nicknameText = (EditText) findViewById(R.id.nickname);
        String nickname = nicknameText.getText().toString();

        if(nickname.length() <= 0)
        {
            Log.d(TAG, "Submitted invalid nick");
        }
        else {
            Log.d(TAG, "Submitted nick: " + nickname);
        }

    }
}
