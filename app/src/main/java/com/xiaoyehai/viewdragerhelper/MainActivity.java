package com.xiaoyehai.viewdragerhelper;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

/**
 * ViewDragHelper:它主要用于处理ViewGroup中对子View的拖拽处理,
 * 本质是对触摸事件的解析类.
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}
