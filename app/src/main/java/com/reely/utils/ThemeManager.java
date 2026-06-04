package com.reely.utils;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.appcompat.app.AppCompatDelegate;

/**
 * REELY — ThemeManager (FIXED)
 *
 * Fix: AppCompatDelegate.setDefaultNightMode() harus dipanggil
 * SEBELUM Activity onCreate. Memanggil recreate() saja tidak
 * cukup kalau theme belum di-set ke AppCompatDelegate.
 *
 * Solusi: simpan ke SharedPrefs → set AppCompatDelegate →
 * Activity akan recreate dengan theme baru.
 */
public class ThemeManager {

    private ThemeManager() {}

    /**
     * Panggil di ReelyApp.onCreate() — restore theme saat app launch.
     */
    public static void applyTheme(Context context) {
        String savedTheme = getSavedTheme(context);
        applyThemeMode(savedTheme);
    }

    /**
     * Switch ke Night Cinema (dark).
     * ✅ FIX: set AppCompatDelegate dulu, baru recreate Activity.
     */
    public static void setNightCinema(Context context) {
        saveTheme(context, Constants.THEME_NIGHT);
        applyThemeMode(Constants.THEME_NIGHT);
    }

    /**
     * Switch ke Soft Cinema (light).
     * ✅ FIX: set AppCompatDelegate dulu, baru recreate Activity.
     */
    public static void setSoftCinema(Context context) {
        saveTheme(context, Constants.THEME_SOFT);
        applyThemeMode(Constants.THEME_SOFT);
    }

    public static boolean isNightCinema(Context context) {
        return Constants.THEME_NIGHT.equals(getSavedTheme(context));
    }

    public static String getSavedTheme(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(
                Constants.PREF_NAME, Context.MODE_PRIVATE);
        return prefs.getString(Constants.KEY_THEME, Constants.THEME_NIGHT);
    }

    // ── Private helpers ───────────────────────────────────────────

    private static void applyThemeMode(String theme) {
        if (Constants.THEME_SOFT.equals(theme)) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }
    }

    private static void saveTheme(Context context, String theme) {
        SharedPreferences prefs = context.getSharedPreferences(
                Constants.PREF_NAME, Context.MODE_PRIVATE);
        prefs.edit().putString(Constants.KEY_THEME, theme).apply();
    }
}