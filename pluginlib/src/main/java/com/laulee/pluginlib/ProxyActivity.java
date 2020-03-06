package com.laulee.pluginlib;

import android.app.Activity;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.os.Bundle;

public class ProxyActivity extends Activity {

    private String mClassName;
    private PluginApk mPluginApk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mClassName = getIntent().getStringExtra("className");
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
