package com.laulee.neplugin;

import android.app.Activity;
import android.content.res.Resources;

import com.laulee.pluginlib.PluginManager;

/**
 * Created by laulee on 2020-03-06.
 */
public class BaseActivity extends Activity {

    @Override
    public Resources getResources() {
        return PluginManager.getInstance().getPluginApk().getResources();
    }
}
