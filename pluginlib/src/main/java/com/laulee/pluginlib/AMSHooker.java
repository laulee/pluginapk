package com.laulee.pluginlib;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * Created by laulee on 2020-03-09.
 */
public class AMSHooker {

    public static final String EXTRA_COMMAND = "extra_command";
        private static Object mIActivityManagerInstance;

    public static void hook(final Context context) {
        try {
            //获取activitymanager里面的IActivityManagerSingleton对象
            Class<?> activityManagerNativeClazz = Class.forName("android.app.ActivityManager");
            Field gDefaultField = activityManagerNativeClazz.getDeclaredField("IActivityManagerSingleton");
            gDefaultField.setAccessible(true);
            Object singleTonObject = gDefaultField.get(null);
            //获取singleTon对象里面的mInstance
            Class<?> singleTon = Class.forName("android.util.Singleton");
            final Field instanceField = singleTon.getDeclaredField("mInstance");
            instanceField.setAccessible(true);
            mIActivityManagerInstance = instanceField.get(singleTonObject);

            Class<?> iActivityManagerClass = Class.forName("android.app.IActivityManager");
            //通过代理生成新的IActivityManager
            Object proxy = Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class[]{
                    iActivityManagerClass
            }, new InvocationHandler() {
                @Override
                public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                    //拦截startService
                    if ("startService".equals(method.getName())) {
                        //第二个参数是intent
                        Intent intent = (Intent) args[1];
                        ComponentName componentName = intent.getComponent();
                        if (!componentName.getPackageName().equals(context.getPackageName())) {
                            Intent proxyIntent = new Intent();
                            proxyIntent.putExtra(EXTRA_COMMAND, intent);
                            proxyIntent.setComponent(new ComponentName(context.getPackageName(), LocalService.class.getName()));
                            args[1] = proxyIntent;
                            return method.invoke(mIActivityManagerInstance, args);
                        }
                    } else if ("stopService".equals(method.getName())) {
                        //第二个参数是intent
                        Intent intent = (Intent) args[1];
                        ComponentName componentName = intent.getComponent();
                        if (!componentName.getPackageName().equals(context.getPackageName())) {
                            //判断是否销毁宿主service
                            if (ServiceManager.stopService(intent)) {
                                Intent proxyIntent = new Intent();
                                proxyIntent.setComponent(new ComponentName(context.getPackageName(), LocalService.class.getName()));
                                args[1] = proxyIntent;
                                return method.invoke(mIActivityManagerInstance, args);
                            } else {
                                return 0;
                            }
                        }
                    }
                    return method.invoke(mIActivityManagerInstance, args);
                }
            });
            instanceField.set(singleTonObject, proxy);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public static Object getIActivityManager() {
        return mIActivityManagerInstance;
    }
}
