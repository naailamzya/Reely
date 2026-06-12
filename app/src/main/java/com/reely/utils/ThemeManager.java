package com.reely.utils;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.appcompat.app.AppCompatDelegate;

public class ThemeManager {

    private ThemeManager() {}

    public static void applyTheme(Context context) {
        String savedTheme = getSavedTheme(context);
        applyMode(savedTheme);
    }

    public static void setNightCinema(Context context) {
        saveTheme(context, Constants.THEME_NIGHT);
        applyMode(Constants.THEME_NIGHT);
    }

    public static void setSoftCinema(Context context) {
        saveTheme(context, Constants.THEME_SOFT);
        applyMode(Constants.THEME_SOFT);
    }

    public static boolean isNightCinema(Context context) {
        return Constants.THEME_NIGHT.equals(getSavedTheme(context));
    }

    public static String getSavedTheme(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(
                Constants.PREF_NAME, Context.MODE_PRIVATE);
        return prefs.getString(Constants.KEY_THEME, Constants.THEME_NIGHT);
    }

    private static void applyMode(String theme) {
        if (Constants.THEME_SOFT.equals(theme)) {
            AppCompatDelegate.setDefaultNightMode(
                    AppCompatDelegate.MODE_NIGHT_NO);
        } else {
            AppCompatDelegate.setDefaultNightMode(
                    AppCompatDelegate.MODE_NIGHT_YES);
        }
    }

    private static void saveTheme(Context context, String theme) {
        context.getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE)
                .edit()
                .putString(Constants.KEY_THEME, theme)
                .apply();
    }
}