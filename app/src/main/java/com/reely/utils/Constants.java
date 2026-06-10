package com.reely.utils;
public final class Constants {
    private Constants() {}

    public static final String TMDB_BASE_URL      = "https://api.themoviedb.org/3/";
    public static final String IMAGE_BASE_URL     = "https://image.tmdb.org/t/p/";

    public static final String IMAGE_SIZE_W185    = "w185";
    public static final String IMAGE_SIZE_W342    = "w342";
    public static final String IMAGE_SIZE_W500    = "w500";
    public static final String IMAGE_SIZE_W780    = "w780";
    public static final String IMAGE_SIZE_ORIGINAL = "original";

    public static final String PREF_NAME          = "reely_prefs";
    public static final String KEY_IS_LOGGED_IN   = "is_logged_in";
    public static final String KEY_USERNAME        = "username";
    public static final String KEY_THEME           = "selected_theme";

    public static final String THEME_NIGHT        = "night_cinema";
    public static final String THEME_SOFT         = "soft_cinema";

    public static final String EXTRA_MOVIE_ID     = "extra_movie_id";
    public static final String EXTRA_MOVIE_TITLE  = "extra_movie_title";
    public static final String EXTRA_MOVIE_POSTER = "extra_movie_poster";

    public static final int GENRE_ACTION          = 28;
    public static final int GENRE_COMEDY          = 35;
    public static final int GENRE_DRAMA           = 18;
    public static final int GENRE_HORROR          = 27;
    public static final int GENRE_ROMANCE         = 10749;
    public static final int GENRE_SCI_FI          = 878;
    public static final int GENRE_THRILLER        = 53;
    public static final int GENRE_FAMILY          = 10751;
    public static final int GENRE_ANIMATION       = 16;

    public static final int DEFAULT_PAGE          = 1;
    public static final String DEFAULT_LANGUAGE   = "en-US";

    public static final long SPLASH_DELAY_MS      = 2500L;
}