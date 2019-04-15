package com.example.myplayandroid;

/**
 * Created by zhongzhiqiang on 19-4-8.
 * 欢迎界面
 */

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;

public class ActivitySplash extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //隐藏ActionBar
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_splash);
        //        隐藏状态栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        //        隐藏标题栏
        //这个为什么会出现问题
//        getSupportActionBar().hide();
        //        创建子线程
        Thread Welcome_Thread = new Thread() {

            @Override
            public void run() {
                try {
                    sleep(3000);        //使程序休眠五秒
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                    finish();               //关闭当前活动
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        //启动线程
        Welcome_Thread.start();
    }
}
