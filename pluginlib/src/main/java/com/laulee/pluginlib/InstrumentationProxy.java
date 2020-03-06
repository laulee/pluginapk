package com.laulee.pluginlib;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import java.lang.reflect.Method;


/**
 * Created by laulee on 2019-08-13.
 */
public class InstrumentationProxy extends Instrumentation {

    private static final String TAG = "InstrumentationProxy";
    private static final String TARGET_INTENT = "target_intent";
    Instrumentation mBase;

    public InstrumentationProxy(Instrumentation instrumentation) {
        mBase = instrumentation;
    }

    /**
     * 执行启动activity
     *
     * @param who
     * @param contextThread
     * @param token
     * @param target
     * @param intent
     * @param requestCode
     * @param options
     * @return
     */
    public ActivityResult execStartActivity(
            Context who, IBinder contextThread, IBinder token,
            Activity target, Intent intent, int requestCode, Bundle options) {

        Log.d(TAG, "Hook 成功 " + "-----who:" + who.getClass().getName());

        try {
            Method execStartActivity = Instrumentation.class.getDeclaredMethod("execStartActivity",
                    Context.class, IBinder.class, IBinder.class, Activity.class, Intent.class, int.class, Bundle.class);
            //替换代理activity
            Intent proxyIntent = new Intent();
            proxyIntent.setClassName("com.laulee.pluginapk", StubActivity.class.getName());
            proxyIntent.putExtra(TARGET_INTENT, intent);

            return (ActivityResult) execStartActivity.invoke(mBase, who, contextThread, token, target, proxyIntent, requestCode, options);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Activity newActivity(ClassLoader cl, String className, Intent intent) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        if (intent != null && intent.getParcelableExtra(TARGET_INTENT) != null) {
            className = ((Intent) intent.getParcelableExtra(TARGET_INTENT)).getComponent().getClassName();
            cl = PluginManager.getInstance().getPluginApk().getClassLoader();
            Log.d(TAG, "newActivity 成功 " + "TARGET_INTENT-----who:" + className);
        }
        Log.d(TAG, "newActivity 成功 " + "-----who:" + className);
        return mBase.newActivity(cl, className, intent);
    }
}
