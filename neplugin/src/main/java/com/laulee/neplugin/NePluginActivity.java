package com.laulee.neplugin;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class NePluginActivity extends BaseActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ne_plugin);
    }

    public void toast(View view) {
        Toast.makeText(this, "toast", Toast.LENGTH_LONG).show();
    }
}
