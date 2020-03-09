package com.laulee.neplugin;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by laulee on 2020-03-09.
 */
public class TestService extends Service {

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        System.out.println("TestService is onCreate");
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        System.out.println("TestService is onDestroy");
    }
}
