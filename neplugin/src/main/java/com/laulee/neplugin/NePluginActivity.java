package com.laulee.neplugin;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class NePluginActivity extends BaseActivity {

    private static final String TAG = "NePluginActivity";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ne_plugin);
    }

    public void toast(View view) {
        String packageName = getPackageName();
        String className = packageName + ".BackActivity";
        ComponentName componentName = new ComponentName(packageName, className);
        Intent intent = new Intent();
        intent.setComponent(componentName);
        startActivity(intent);
    }

    public void start(View view) {
        ComponentName componentName = new ComponentName("com.laulee.neplugin", "com.laulee.neplugin.SecondActivity");
        Intent intent = new Intent();
        intent.setComponent(componentName);
        startActivity(intent);
    }
}
