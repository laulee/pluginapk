package com.laulee.pluginlib;

import android.app.Activity;
import android.app.Application;
import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Resources;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import dalvik.system.DexClassLoader;

/**
 * Created by laulee on 2020-03-04.
 */
public class PluginManager {

    private static final PluginManager instance = new PluginManager();
    private Application context;
    private PluginApk mPluginApk;
    private Context mBaseContext;
    private Object mPackageInfo;
    private Resources mNowResources;

    public static PluginManager getInstance() {
        return instance;
    }

    private PluginManager() {

    }

    public PluginApk getPluginApk() {
        return mPluginApk;
    }

    public void init(Application context) {
        this.context = context;
        //初始化一些成员变量和加载已安装的插件
        mPackageInfo = RefInvoke.getFieldObject(context.getBaseContext(), "mPackageInfo");
        mBaseContext = context.getBaseContext();
        mNowResources = mBaseContext.getResources();
        proxyInstrumentation();
    }

    private void proxyInstrumentation() {
        try {
            Class<?> clazz = Class.forName("android.app.ActivityThread");
            Field field = clazz.getDeclaredField("sCurrentActivityThread");
            field.setAccessible(true);
            Object object = field.get(null);

            Field instrumentationField = clazz.getDeclaredField("mInstrumentation");
            instrumentationField.setAccessible(true);
            InstrumentationProxy instrumentationProxy = new InstrumentationProxy((Instrumentation) instrumentationField.get(object));
            instrumentationField.set(object, instrumentationProxy);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    //加载apk文件
    public void loadApk(String apkPath) {
        File file = new File(apkPath);
        if (!file.exists()) {
            return;
        }
        PackageInfo packageInfo = context.getPackageManager().getPackageArchiveInfo(apkPath,
                PackageManager.GET_ACTIVITIES | PackageManager.GET_SERVICES);
        if (packageInfo == null) {
            return;
        }

        //创建classLoader
        DexClassLoader dexClassLoader = createDexClassLoader(apkPath);
        //创建resources
        AssetManager assetManager = createAssetManager(apkPath);
        Resources resources = createResource(assetManager);
        mPluginApk = new PluginApk(assetManager, dexClassLoader, resources, packageInfo);
    }

    private Resources createResource(AssetManager assetManager) {
        Resources resources = context.getBaseContext().getResources();
        try {
            Resources newResources = new Resources(assetManager, resources.getDisplayMetrics(), resources.getConfiguration());
            RefInvoke.setFieldObject(mBaseContext, "mResources", newResources);
            //这是最主要的需要替换的，如果不支持插件运行时更新，只留这一个就可以了
            RefInvoke.setFieldObject(mPackageInfo, "mResources", newResources);
            mNowResources = newResources;
            //需要清理mTheme对象，否则通过inflate方式加载资源会报错
            //如果是activity动态加载插件，则需要把activity的mTheme对象也设置为null
            RefInvoke.setFieldObject(mBaseContext, "mTheme", null);
            return mNowResources;
        } catch (Exception e) {

        }
        return null;
    }

    /**
     *
     * @param apkPath
     * @return
     */
    private AssetManager createAssetManager(String apkPath) {
        try {
            AssetManager assetManager = AssetManager.class.newInstance();
            Method method = AssetManager.class.getDeclaredMethod("addAssetPath", String.class);
            method.setAccessible(true);
            method.invoke(assetManager, mBaseContext.getPackageResourcePath());
            method.invoke(assetManager, apkPath);
            return assetManager;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @param apkPath
     * @return
     */
    private DexClassLoader createDexClassLoader(String apkPath) {
        File file = context.getDir("dex", Context.MODE_PRIVATE);
        DexClassLoader dexClassLoader = new DexClassLoader(apkPath, file.getAbsolutePath(), null, context.getClassLoader());
        return dexClassLoader;
    }

    /**
     * @param activity
     * @param className
     */
    public void startActivity(Activity activity, String className) {
        Intent intent = null;
        try {
            intent = new Intent(activity, getPluginApk().getClassLoader().loadClass(className));
            activity.startActivity(intent);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
