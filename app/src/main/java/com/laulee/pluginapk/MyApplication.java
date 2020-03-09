package com.laulee.pluginapk;

import android.app.Application;
import android.content.Context;

import com.laulee.pluginlib.PluginManager;

/**
 * Created by laulee on 2020-03-06.
 */
public class MyApplication extends Application {

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        PluginManager.getInstance().init(this);
    }
}
