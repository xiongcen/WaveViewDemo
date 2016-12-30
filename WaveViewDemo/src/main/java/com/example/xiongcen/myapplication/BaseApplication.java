package com.example.xiongcen.myapplication;

import android.app.Application;

import com.example.xiongcen.myapplication.util.NeteaseLog;

/**
 * Created by xiongcen on 16/12/30.
 */

public class BaseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        NeteaseLog.initAppPath(getPackageName());
    }
}
