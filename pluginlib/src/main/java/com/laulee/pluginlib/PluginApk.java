package com.laulee.pluginlib;

import android.content.pm.PackageInfo;
import android.content.res.AssetManager;
import android.content.res.Resources;

/**
 * Created by laulee on 2020-03-04.
 */
public class PluginApk {

    private AssetManager assetManager;
    private ClassLoader classLoader;
    private Resources resources;
    private PackageInfo packageInfo;

    public PluginApk(AssetManager assetManager, ClassLoader classLoader, Resources resources, PackageInfo packageInfo) {
        this.assetManager = assetManager;
        this.classLoader = classLoader;
        this.resources = resources;
        this.packageInfo = packageInfo;
    }

    public AssetManager getAssetManager() {
        return assetManager;
    }

    public ClassLoader getClassLoader() {
        return classLoader;
    }

    public Resources getResources() {
        return resources;
    }

    public PackageInfo getPackageInfo() {
        return packageInfo;
    }

    @Override
    public String toString() {
        return "PluginApk{" +
                "assetManager=" + assetManager +
                ", classLoader=" + classLoader +
                ", resources=" + resources +
                ", packageInfo=" + packageInfo +
                '}';
    }
}
