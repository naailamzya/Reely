package com.reely.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.reely.models.Movie;
import java.util.ArrayList;
import java.util.List;

public class WatchlistDao {
    private final WatchlistDbHelper dbHelper;

    public WatchlistDao(Context context) {
        dbHelper = WatchlistDbHelper.getInstance(context);
    }

    public boolean addToWatchlist(Movie movie) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(WatchlistContract.COLUMN_ID, movie.getId());
        values.put(WatchlistContract.COLUMN_TITLE, movie.getTitle());
        values.put(WatchlistContract.COLUMN_OVERVIEW, movie.getOverview());
        values.put(WatchlistContract.COLUMN_POSTER_PATH, movie.getPosterPath());
        values.put(WatchlistContract.COLUMN_BACKDROP_PATH, movie.getBackdropPath());
        values.put(WatchlistContract.COLUMN_RATING, movie.getVoteAverage());
        values.put(WatchlistContract.COLUMN_RELEASE_DATE, movie.getReleaseDate());
        values.put(WatchlistContract.COLUMN_GENRE_IDS, genreIdsToString(movie.getGenreIds()));
        values.put(WatchlistContract.COLUMN_ADDED_AT, System.currentTimeMillis());
        
        long result = db.insertWithOnConflict(WatchlistContract.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        return result != -1;
    }

    public boolean removeFromWatchlist(int movieId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        return db.delete(WatchlistContract.TABLE_NAME, WatchlistContract.COLUMN_ID + " = ?", new String[]{String.valueOf(movieId)}) > 0;
    }

    public List<Movie> getAllWatchlistMovies() {
        List<Movie> movies = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.query(WatchlistContract.TABLE_NAME, null, null, null, null, null, WatchlistContract.COLUMN_ADDED_AT + " DESC");
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    movies.add(cursorToMovie(cursor));
                } while (cursor.moveToNext());
            }
        } finally {
            if (cursor != null) cursor.close();
        }
        return movies;
    }

    public boolean isInWatchlist(int movieId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(WatchlistContract.TABLE_NAME, new String[]{WatchlistContract.COLUMN_ID},
                WatchlistContract.COLUMN_ID + " = ?", new String[]{String.valueOf(movieId)}, null, null, null);
        boolean exists = (cursor != null && cursor.getCount() > 0);
        if (cursor != null) cursor.close();
        return exists;
    }

    private Movie cursorToMovie(Cursor cursor) {
        Movie movie = new Movie();
        movie.setId(cursor.getInt(cursor.getColumnIndexOrThrow(WatchlistContract.COLUMN_ID)));
        movie.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(WatchlistContract.COLUMN_TITLE)));
        movie.setOverview(cursor.getString(cursor.getColumnIndexOrThrow(WatchlistContract.COLUMN_OVERVIEW)));
        movie.setPosterPath(cursor.getString(cursor.getColumnIndexOrThrow(WatchlistContract.COLUMN_POSTER_PATH)));
        movie.setBackdropPath(cursor.getString(cursor.getColumnIndexOrThrow(WatchlistContract.COLUMN_BACKDROP_PATH)));
        movie.setVoteAverage(cursor.getDouble(cursor.getColumnIndexOrThrow(WatchlistContract.COLUMN_RATING)));
        movie.setReleaseDate(cursor.getString(cursor.getColumnIndexOrThrow(WatchlistContract.COLUMN_RELEASE_DATE)));

        int genreIdx = cursor.getColumnIndex(WatchlistContract.COLUMN_GENRE_IDS);
        if (genreIdx != -1) {
            String genreIdsStr = cursor.getString(genreIdx);
            movie.setGenreIds(stringToGenreIds(genreIdsStr));
        }
        return movie;
    }

    private String genreIdsToString(List<Integer> genreIds) {
        if (genreIds == null || genreIds.isEmpty()) return "";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < genreIds.size(); i++) {
            sb.append(genreIds.get(i)).append(i == genreIds.size() - 1 ? "" : ",");
        }
        return sb.toString();
    }

    private List<Integer> stringToGenreIds(String str) {
        List<Integer> list = new ArrayList<>();
        if (str != null && !str.isEmpty()) {
            for (String s : str.split(",")) {
                try { list.add(Integer.parseInt(s.trim())); } catch (Exception ignored) {}
            }
        }
        return list;
    }
}
