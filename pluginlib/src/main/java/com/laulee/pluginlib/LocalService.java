package com.laulee.pluginlib;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by laulee on 2020-03-09.
 */
public class LocalService extends Service {

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //通过宿主service启动真实service
        ServiceManager.startService(this, intent, flags, startId);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public boolean stopService(Intent name) {
        return super.stopService(name);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
