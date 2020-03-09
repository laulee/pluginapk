package com.laulee.pluginlib;

import android.app.Application;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import static com.laulee.pluginlib.AMSHooker.EXTRA_COMMAND;

/**
 * Created by laulee on 2020-03-09.
 */
public class ServiceManager {

    private static Map<String, Service> mServices = new HashMap<>();

    /**
     * 启动service
     *
     * @param hostService
     * @param intent
     * @param flags
     * @param startId
     */
    public static void startService(Service hostService, Intent intent, int flags, int startId) {
        Intent command = intent.getParcelableExtra(EXTRA_COMMAND);
        if (command != null) {
            ComponentName componentName = command.getComponent();
            String packageName = componentName.getPackageName();
            String className = componentName.getClassName();

            //是否已经创建
            if (mServices.get(className) != null) {
                return;
            }

            PluginApk pluginApk = PluginManager.getInstance().getPlugins().get(packageName);

            try {
                Service service = (Service) pluginApk.getClassLoader().loadClass(className).newInstance();
                //        目前创建的service实例是没有上下文的，需要调用其attach方法，才能让这个service拥有上下文环境
                //        attach
                Class<?> activityThreadClazz = Class.forName("android.app.ActivityThread");
                Object sCurrentActivityThread = RefInvoke.getFieldObject(activityThreadClazz, "sCurrentActivityThread", null);

                Object mAppThread = RefInvoke.getFieldObject(activityThreadClazz, "mAppThread", sCurrentActivityThread);
                Class<?> iInterfaceClazz = Class.forName("android.os.IInterface");
                Method asBinderMethod = iInterfaceClazz.getDeclaredMethod("asBinder");
                asBinderMethod.setAccessible(true);
                IBinder token = (IBinder) asBinderMethod.invoke(mAppThread);
                Object iActivityManager = AMSHooker.getIActivityManager();
                Method attachMethod = Service.class.getDeclaredMethod("attach", Context.class, sCurrentActivityThread.getClass(),
                        String.class, IBinder.class, Application.class, Object.class);
                attachMethod.setAccessible(true);
                attachMethod.invoke(service, hostService, sCurrentActivityThread, className, token, hostService.getApplication(), iActivityManager);
                service.onCreate();
                mServices.put(className, service);
                service.onStartCommand(intent, flags, startId);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * @param intent
     */
    public static boolean stopService(Intent intent) {
        if (intent != null) {
            ComponentName componentName = intent.getComponent();
            String className = componentName.getClassName();
            Service service = mServices.get(className);
            if (null != service) {
                service.onDestroy();
                mServices.remove(className);
            }
        }
        return mServices.isEmpty();
    }
}
