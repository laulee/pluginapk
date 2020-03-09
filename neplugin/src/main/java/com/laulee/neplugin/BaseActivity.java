package com.laulee.neplugin;

import android.app.Activity;
import android.content.res.Resources;

import com.laulee.pluginlib.PluginApk;
import com.laulee.pluginlib.PluginManager;

import java.util.Map;

/**
 * Created by laulee on 2020-03-06.
 */
public class BaseActivity extends Activity {

    @Override
    public Resources getResources() {
        String packageName = "com.laulee.neplugin";
        Map<String, PluginApk> plugins = PluginManager.getInstance().getPlugins();
        return plugins.get(packageName) != null ? plugins.get(packageName).getResources() : super.getResources();
    }
}
