package com.barbu.paul.gheorghe.radialpong;

import android.app.ListActivity;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.widget.SimpleCursorAdapter;

public class HighscoresActivity extends ListActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private SimpleCursorAdapter adapter;
    private final static String TAG = HighscoresActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "HighscoresActivity Created");

        setContentView(R.layout.activity_highscores);

        final String[] PROJECTION = {
            ScoreContract.ScoreEntry.COLUMN_NAME_NICKNAME,
            ScoreContract.ScoreEntry.COLUMN_NAME_SCORE
        };

        int[] toViews = {
            R.id.score_nickname,
            R.id.score_pts
        };

        adapter = new SimpleCursorAdapter(this,
            R.layout.score_layout, null,
            PROJECTION, toViews, 0);

        setListAdapter(adapter);
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        final String[] ID_PROJECTION = {
                ScoreContract.ScoreEntry._ID,
                ScoreContract.ScoreEntry.COLUMN_NAME_NICKNAME,
                ScoreContract.ScoreEntry.COLUMN_NAME_SCORE
        };

        return new CursorLoader(this, ScoreContentProvider.CONTENT_URI,
                ID_PROJECTION, null, null, ScoreContract.ScoreEntry.COLUMN_NAME_SCORE + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        adapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        adapter.swapCursor(null);
    }
}
