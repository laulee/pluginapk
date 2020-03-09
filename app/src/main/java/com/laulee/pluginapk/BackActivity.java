package com.laulee.pluginapk;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

/**
 * Created by laulee on 2020-03-09.
 */
public class BackActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_back);
    }

    public void finishClick(View view) {
        finish();
    }
}
