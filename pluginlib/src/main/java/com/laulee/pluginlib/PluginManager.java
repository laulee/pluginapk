package com.laulee.pluginlib;

import android.app.Activity;
import android.app.Application;
import android.app.Instrumentation;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Resources;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import dalvik.system.DexClassLoader;

/**
 * Created by laulee on 2020-03-04.
 */
public class PluginManager {

    private static final PluginManager instance = new PluginManager();
    private Application context;
    private volatile Context mBaseContext;
    public static Map<String, PluginApk> plugins = new HashMap<>();

    public static PluginManager getInstance() {
        return instance;
    }

    private PluginManager() {
    }

    public Map<String, PluginApk> getPlugins() {
        return plugins;
    }

    public void init(Application context) {
        this.context = context;
        //初始化一些成员变量和加载已安装的插件
        mBaseContext = context.getBaseContext();
        //自动加载asset下面的apk
        loadApk(context);
        //代理instrumentation创建代理activity、迷惑activityThread
        proxyInstrumentation();
        //hook ActivityThread启动service
        AMSHooker.hook(context);
    }

    /**
     * 加载插件
     *
     * @param application
     */
    private void loadApk(Application application) {
        try {
            AssetManager assetManager = application.getAssets();
            String[] paths = assetManager.list("");
            for (String path : paths) {
                if (path.endsWith(".apk")) {
                    String apkPath = Utils.copyAssetAndWrite(mBaseContext, path);
                    PluginApk item = generatePluginItem(apkPath);
                    if (item != null) {
                        System.out.println("pluginItem packageName is " + item.getPackageInfo().packageName);
                        plugins.put(item.getPackageInfo().packageName, item);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 加载插件
     *
     * @param apkPath
     * @return
     */
    private PluginApk generatePluginItem(String apkPath) {
        File file = new File(apkPath);
        if (!file.exists()) {
            return null;
        }
        PackageInfo packageInfo = context.getPackageManager().getPackageArchiveInfo(apkPath,
                PackageManager.GET_ACTIVITIES | PackageManager.GET_SERVICES);
        if (packageInfo == null) {
            return null;
        }

        //创建classLoader
        DexClassLoader dexClassLoader = createDexClassLoader(apkPath);
        //创建resources
        AssetManager assetManager = createAssetManager(apkPath);
        Resources resources = createResource(assetManager);
        PluginApk mPluginApk = new PluginApk(assetManager, dexClassLoader, resources, packageInfo);
        return mPluginApk;
    }

    /**
     * 代理
     */
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
        PluginApk pluginApk = generatePluginItem(apkPath);
        if (pluginApk != null) {
            plugins.put(pluginApk.getPackageInfo().packageName, pluginApk);
        }
    }

    /**
     * 创建resources
     *
     * @param assetManager
     * @return
     */
    private Resources createResource(AssetManager assetManager) {
        try {
            Resources resources = mBaseContext.getResources();
            Resources newResources = new Resources(assetManager, resources.getDisplayMetrics(),
                    resources.getConfiguration());
            return newResources;
        } catch (Exception e) {

        }
        return null;
    }

    /**
     * @param apkPath
     * @return
     */
    private AssetManager createAssetManager(String apkPath) {
        try {
            AssetManager assetManager = AssetManager.class.newInstance();
            Method method = AssetManager.class.getDeclaredMethod("addAssetPath", String.class);
            method.setAccessible(true);
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
            intent = new Intent();
            int end = className.lastIndexOf(".");
            String packageName = className.substring(0, end);
            ComponentName componentName = new ComponentName(packageName, className);
            intent.setComponent(componentName);
            activity.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @param activity
     * @param serviceName
     */
    public void startService(Activity activity, String serviceName) {
        Intent intent = null;
        try {
            intent = new Intent();
            int end = serviceName.lastIndexOf(".");
            String packageName = serviceName.substring(0, end);
            ComponentName componentName = new ComponentName(packageName, serviceName);
            intent.setComponent(componentName);
            activity.startService(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stopService(Activity activity, String serviceName) {
        Intent intent = null;
        try {
            intent = new Intent();
            int end = serviceName.lastIndexOf(".");
            String packageName = serviceName.substring(0, end);
            ComponentName componentName = new ComponentName(packageName, serviceName);
            intent.setComponent(componentName);
            activity.stopService(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
