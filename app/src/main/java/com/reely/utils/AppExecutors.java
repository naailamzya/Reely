package com.reely.utils;

import android.os.Handler;
import android.os.Looper;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AppExecutors {
    private static AppExecutors instance;
    private final ExecutorService diskIO;
    private final ExecutorService networkIO;
    private final Handler mainThreadHandler;

    private AppExecutors() {
        diskIO = Executors.newFixedThreadPool(3);
        networkIO = Executors.newFixedThreadPool(3);
        mainThreadHandler = new Handler(Looper.getMainLooper());
    }
    public static synchronized AppExecutors getInstance() {
        if (instance == null) {
            instance = new AppExecutors();
        }
        return instance;
    }
    public ExecutorService diskIO() {
        return diskIO;
    }
    public ExecutorService networkIO() {
        return networkIO;
    }
    public void mainThread(Runnable runnable) {
        mainThreadHandler.post(runnable);
    }
}