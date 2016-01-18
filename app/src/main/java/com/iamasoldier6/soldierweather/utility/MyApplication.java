package com.iamasoldier6.soldierweather.utility;

import android.app.Application;
import android.content.Context;

/**
 * Created by Iamasoldier6 on 1/15/16.
 */
public class MyApplication extends Application {

    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }

    public static Context getContext() {
        return context;
    }
}
