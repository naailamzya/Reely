package com.reely.utils;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.appcompat.app.AppCompatDelegate;

/**
 * REELY — ThemeManager (FIXED)
 *
 * Root cause sebelumnya:
 * AndroidManifest menggunakan Theme.Reely.NightCinema yang hardcoded dark,
 * sehingga AppCompatDelegate tidak bisa override ke light.
 *
 * Fix:
 * - Theme di Manifest diganti ke Theme.Reely (DayNight-aware)
 * - AppCompatDelegate.setDefaultNightMode() sekarang bekerja karena
 *   parent theme adalah DayNight, bukan Dark fixed
 * - applyTheme() dipanggil di ReelyApp.onCreate() sebelum Activity apapun dibuat
 */
public class ThemeManager {

    private ThemeManager() {}

    /**
     * Panggil di ReelyApp.onCreate() untuk restore theme saat launch.
     */
    public static void applyTheme(Context context) {
        String savedTheme = getSavedTheme(context);
        applyMode(savedTheme);
    }

    /**
     * Switch ke Night Cinema (dark).
     * setDefaultNightMode() harus dipanggil SEBELUM Activity recreate().
     */
    public static void setNightCinema(Context context) {
        saveTheme(context, Constants.THEME_NIGHT);
        applyMode(Constants.THEME_NIGHT);
    }

    /**
     * Switch ke Soft Cinema (light).
     */
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

    // ── Private ───────────────────────────────────────────────────

    private static void applyMode(String theme) {
        if (Constants.THEME_SOFT.equals(theme)) {
            // Light mode
            AppCompatDelegate.setDefaultNightMode(
                    AppCompatDelegate.MODE_NIGHT_NO);
        } else {
            // Dark mode (default)
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