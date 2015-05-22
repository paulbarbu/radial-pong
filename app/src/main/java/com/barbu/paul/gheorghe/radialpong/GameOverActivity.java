package com.barbu.paul.gheorghe.radialpong;

import android.app.Activity;
import android.content.ContentValues;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;


public class GameOverActivity extends Activity {
    private static final String TAG = MenuActivity.class.getSimpleName();
    private int pts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_over);

        String p = getIntent().getStringExtra(Score.EXTRA_POINTS);
        pts = Integer.parseInt(p);

        TextView pointsText = (TextView) findViewById(R.id.points);
        pointsText.setText(getString(R.string.points, p));
    }

    public void submit(View view){
        EditText nicknameText = (EditText) findViewById(R.id.nickname);
        String nickname = nicknameText.getText().toString();

        if(!nickname.matches("[a-zA-Z]+"))
        {
            Log.d(TAG, "Submitted invalid nick");
            TextView pointsText = (TextView) findViewById(R.id.errorText);
            pointsText.setText(getString(R.string.non_empty_nick));
        }
        else {
            Log.d(TAG, "Submitted nick: " + nickname);

            ContentValues values = new ContentValues();
            values.put(ScoreContract.ScoreEntry.COLUMN_NAME_NICKNAME, nickname);
            values.put(ScoreContract.ScoreEntry.COLUMN_NAME_SCORE, pts);

            Uri uri = getContentResolver().insert(ScoreContentProvider.CONTENT_URI, values);

            if(null == uri)
            {
                TextView pointsText = (TextView) findViewById(R.id.errorText);
                pointsText.setText(getString(R.string.db_err));
                Log.d(TAG, "Error inserting value into DB");
            }
            else
            {
                finish();
            }
        }

    }
}
