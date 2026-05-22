package com.reely.utils;

import android.content.Context;
import android.content.SharedPreferences;
public class SessionManager {

    private final SharedPreferences prefs;
    private final SharedPreferences.Editor editor;

    public SessionManager(Context context) {
        prefs = context.getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE);
        editor = prefs.edit();
    }
    public boolean login(String username) {
        if (username == null || username.trim().isEmpty()) {
            return false;
        }
        editor.putBoolean(Constants.KEY_IS_LOGGED_IN, true);
        editor.putString(Constants.KEY_USERNAME, username.trim());
        editor.apply();
        return true;
    }
    public void logout() {
        editor.putBoolean(Constants.KEY_IS_LOGGED_IN, false);
        editor.remove(Constants.KEY_USERNAME);
        editor.apply();
    }
    public boolean isLoggedIn() {
        return prefs.getBoolean(Constants.KEY_IS_LOGGED_IN, false);
    }
    public String getUsername() {
        return prefs.getString(Constants.KEY_USERNAME, "");
    }
}