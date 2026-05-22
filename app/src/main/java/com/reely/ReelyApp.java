package com.reely;

import android.app.Application;
import com.reely.utils.ThemeManager;

public class ReelyApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        ThemeManager.applyTheme(this);
    }
}