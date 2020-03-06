package com.laulee.pluginapk;

import android.app.Application;

import com.laulee.pluginlib.PluginManager;

/**
 * Created by laulee on 2020-03-06.
 */
public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        PluginManager.getInstance().init(this);
    }
}
