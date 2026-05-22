package com.reely.utils;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.appcompat.app.AppCompatDelegate;

public class ThemeManager {
    private ThemeManager() {}
    public static void applyTheme(Context context) {
        String savedTheme = getSavedTheme(context);
        if (Constants.THEME_SOFT.equals(savedTheme)) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }
    }

    public static void setNightCinema(Context context) {
        saveTheme(context, Constants.THEME_NIGHT);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
    }

    public static void setSoftCinema(Context context) {
        saveTheme(context, Constants.THEME_SOFT);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
    }

    public static boolean isNightCinema(Context context) {
        return Constants.THEME_NIGHT.equals(getSavedTheme(context));
    }

    public static String getSavedTheme(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(
                Constants.PREF_NAME, Context.MODE_PRIVATE);
        return prefs.getString(Constants.KEY_THEME, Constants.THEME_NIGHT);
    }

    private static void saveTheme(Context context, String theme) {
        SharedPreferences prefs = context.getSharedPreferences(
                Constants.PREF_NAME, Context.MODE_PRIVATE);
        prefs.edit().putString(Constants.KEY_THEME, theme).apply();
    }
}