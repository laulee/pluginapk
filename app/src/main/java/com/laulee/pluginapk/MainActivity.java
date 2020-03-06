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

    public void load(View view) {
        PluginManager.getInstance().loadApk(Utils.copyAssetAndWrite(this, "ne2.apk"));
    }

    public void start(View view) {
        PluginManager.getInstance().startActivity(this, "com.laulee.neplugin.NePluginActivity");
    }
}
