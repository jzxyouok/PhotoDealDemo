package com.example.a835127729qqcom.photodealdemo;

import android.app.Application;

import com.squareup.leakcanary.LeakCanary;

/**
 * Created by 835127729qq.com on 16/9/27.
 */
public class MyApplication extends Application{
    @Override
    public void onCreate() {
        super.onCreate();
        //LeakCanary.install(this);
    }
}
