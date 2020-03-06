package com.laulee.pluginlib;

import android.app.Activity;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.os.Bundle;

/**
 * Created by laulee on 2020-03-06.
 */
public class StubActivity extends Activity {

    private PluginApk mPluginApk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPluginApk = PluginManager.getInstance().getPluginApk();
    }

    @Override
    public Resources getResources() {
        return mPluginApk != null ? mPluginApk.getResources() : super.getResources();
    }

    @Override
    public AssetManager getAssets() {
        return mPluginApk != null ? mPluginApk.getAssetManager() : super.getAssets();
    }

    @Override
    public ClassLoader getClassLoader() {
        return mPluginApk != null ? mPluginApk.getClassLoader() : super.getClassLoader();
    }
}
