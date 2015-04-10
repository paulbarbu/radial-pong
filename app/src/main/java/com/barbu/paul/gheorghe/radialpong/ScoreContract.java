package com.barbu.paul.gheorghe.radialpong;

import android.provider.BaseColumns;

public final class ScoreContract {
    private ScoreContract() {}

    public static abstract class ScoreEntry implements BaseColumns {
        public static final String TABLE_NAME = "score";
        public static final String COLUMN_NAME_NICKNAME = "nickname";
        public static final String COLUMN_NAME_SCORE = "score";
    }

    public static final String TEXT_TYPE = " TEXT";
    public static final String INTEGER_TYPE = " INTEGER";
    public static final String COMMA_SEP = ",";

    public static final String SQL_CREATE_TABLE = "CREATE TABLE " + ScoreEntry.TABLE_NAME + " (" +
            ScoreEntry._ID + INTEGER_TYPE + " PRIMARY KEY" + COMMA_SEP +
            ScoreEntry.COLUMN_NAME_NICKNAME + TEXT_TYPE + COMMA_SEP +
            ScoreEntry.COLUMN_NAME_SCORE + INTEGER_TYPE + " )";

    public static final String SQL_DELETE_TABLE = "DROP TABLE IF EXISTS " + ScoreEntry.TABLE_NAME;
}
