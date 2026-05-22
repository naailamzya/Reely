package com.reely.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
public class WatchlistDbHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "reely_watchlist.db";

    private static final int DATABASE_VERSION = 1;

    private static WatchlistDbHelper instance;

    private static final String SQL_CREATE_TABLE =
            "CREATE TABLE " + WatchlistContract.TABLE_NAME + " (" +
                    WatchlistContract.COLUMN_ID           + " INTEGER PRIMARY KEY, " +
                    WatchlistContract.COLUMN_TITLE        + " TEXT NOT NULL, " +
                    WatchlistContract.COLUMN_OVERVIEW     + " TEXT, " +
                    WatchlistContract.COLUMN_POSTER_PATH  + " TEXT, " +
                    WatchlistContract.COLUMN_BACKDROP_PATH + " TEXT, " +
                    WatchlistContract.COLUMN_RATING       + " REAL, " +
                    WatchlistContract.COLUMN_RELEASE_DATE + " TEXT, " +
                    WatchlistContract.COLUMN_GENRE_IDS    + " TEXT, " +
                    WatchlistContract.COLUMN_ADDED_AT     + " INTEGER" +
                    ")";

    private static final String SQL_DROP_TABLE =
            "DROP TABLE IF EXISTS " + WatchlistContract.TABLE_NAME;

    private WatchlistDbHelper(Context context) {
        super(context.getApplicationContext(), DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static synchronized WatchlistDbHelper getInstance(Context context) {
        if (instance == null) {
            instance = new WatchlistDbHelper(context.getApplicationContext());
        }
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DROP_TABLE);
        onCreate(db);
    }
}