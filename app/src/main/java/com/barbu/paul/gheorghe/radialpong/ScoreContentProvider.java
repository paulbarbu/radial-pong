package com.barbu.paul.gheorghe.radialpong;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

public class ScoreContentProvider extends ContentProvider {
    public static final String PROVIDER_NAME = "com.barbu.paul.gheorghe.radialpong." + ScoreContract.ScoreEntry.TABLE_NAME;
    public static final Uri CONTENT_URI = Uri.parse("content://" + PROVIDER_NAME + "/" +ScoreContract.ScoreEntry.TABLE_NAME);

    private static final int SCORE = 1;

    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        uriMatcher.addURI(PROVIDER_NAME, ScoreContract.ScoreEntry.TABLE_NAME, SCORE);
    }

    private ScoreDbHelper scoreDbHelper;

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String s, String[] strings) {
        return 0;
    }

    @Override
    public boolean onCreate() {
        scoreDbHelper = new ScoreDbHelper(getContext());
        return true;
    }

    @Override
    public int delete(Uri uri, String s, String[] strings) {
        return 0;
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        if(uriMatcher.match(uri) == SCORE) {
            SQLiteDatabase db = scoreDbHelper.getWritableDatabase();

            long id = db.insert(ScoreContract.ScoreEntry.TABLE_NAME, null, contentValues);

            if (-1 != id) {
                return Uri.withAppendedPath(CONTENT_URI, String.valueOf(id));
            }
        }

        // not sure if this is the right return value in case of errors
        return null;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        if(uriMatcher.match(uri) == SCORE)
        {
            SQLiteDatabase db = scoreDbHelper.getReadableDatabase();
            return db.query(ScoreContract.ScoreEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder, "10");
        }
        else
        {
            return null; // this should return a Cursor with getCount() == 0 instead
        }
    }
}
