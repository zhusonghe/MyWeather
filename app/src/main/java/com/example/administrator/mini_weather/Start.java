package com.example.administrator.mini_weather;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

/**
 * Created by shenzhu on 2016/12/23.
 */
public class Start extends Activity {
    private Handler handler= new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        initData();

    }

    private void initData() {
        SharedPreferences sp = getSharedPreferences("StartActivity", 0);
        Boolean isFirstStart = sp.getBoolean("isFirst",true);
        // 判断是否第一次打开App,如果是则跳转至引导页，否则跳转到主页
        if (isFirstStart) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent i= new Intent(Start.this,Guide.class);
                    startActivity(i);
                    finish();
                }
            }, 2000);
            SharedPreferences.Editor editor = sp.edit();
            editor.putBoolean("isFirst",false);
            editor.commit();
        } else {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent i= new Intent(Start.this,MainActivity.class);
                    startActivity(i);
                    finish();
                }
            }, 2000);
        }
    }

}
