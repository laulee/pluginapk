package com.laulee.pluginapk;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.laulee.pluginlib.PluginManager;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void start(View view) {
        PluginManager.getInstance().startActivity(this, "com.laulee.neplugin.NePluginActivity");
    }

    public void startService(View view) {
        PluginManager.getInstance().startService(this, "com.laulee.neplugin.TestService");
    }

    public void startService2(View view) {
        PluginManager.getInstance().startService(this, "com.laulee.neplugin.TestService2");
    }

    public void stopService(View view) {
        PluginManager.getInstance().stopService(this, "com.laulee.neplugin.TestService");
    }

    public void stopService2(View view) {
        PluginManager.getInstance().stopService(this, "com.laulee.neplugin.TestService2");
    }
}
