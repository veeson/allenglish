package com.lws.allenglish.base;

import android.app.Application;

import com.lws.allenglish.Constants;
import com.lws.allenglish.util.BitmapCache;
import com.lws.allenglish.util.common.FontsOverride;
import com.lws.allenglish.util.common.PreferencesUtils;
import com.tencent.bugly.crashreport.CrashReport;

public class BaseApplication extends Application {
    private static BaseApplication app;
    private BitmapCache bitmapCache;

    public static BaseApplication getInstance() {
        return app;
    }

    public BitmapCache getBitmapCache() {
        if (bitmapCache == null) {
            bitmapCache = new BitmapCache();
        }
        return bitmapCache;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        app = this;

        // 设置默认字体
        FontsOverride.setDefaultFont(app, "MONOSPACE", "fonts/MILT_RG.ttf");

        // Bugly
        CrashReport.initCrashReport(app, "bcd6831e61", false);

        PreferencesUtils.putString(app, "sentence", Constants.DEFAULT_HISTORY_JSON);
    }
}
