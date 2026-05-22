package com.reely.database;

public final class WatchlistContract {
    private WatchlistContract() {}

    public static final String TABLE_NAME       = "watchlist";

    public static final String COLUMN_ID           = "movie_id";

    public static final String COLUMN_TITLE        = "title";

    public static final String COLUMN_OVERVIEW     = "overview";

    public static final String COLUMN_POSTER_PATH  = "poster_path";

    public static final String COLUMN_BACKDROP_PATH = "backdrop_path";

    public static final String COLUMN_RATING       = "rating";

    public static final String COLUMN_RELEASE_DATE = "release_date";

    public static final String COLUMN_GENRE_IDS    = "genre_ids";

    public static final String COLUMN_ADDED_AT     = "added_at";
}