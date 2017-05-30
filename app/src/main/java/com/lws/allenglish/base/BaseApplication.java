package com.lws.allenglish.base;

import android.app.Application;

/**
 * Created by Wilson on 2017/5/29.
 */

public class BaseApplication extends Application {
    private static BaseApplication app;

    public static BaseApplication getInstance() {
        return app;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        app = this;
    }
}
