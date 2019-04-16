package com.example.myplayandroid.Util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.example.myplayandroid.Bean.UpdateBean;
import com.example.myplayandroid.ContextApplication;
import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by zhongzhiqiang on 19-4-16.
 */

public class Utils {

    /**
     * 获取APP版本号
     * @param context
     * @return
     */
    public static int getVersionCode(Context context) {
        // 获取PackageManager的实例
        int version = 0;
        try {
            PackageManager packageManager = context.getPackageManager();
            //getPackageName()是你当前程序的包名
            PackageInfo packInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            version = packInfo.versionCode;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return version;
    }

    //初始化页面 发送网络请求
    public static int getLatestVersionCode(String url) {

        final int version = 0;

        HttpUtil.sendOkHttpRequest(url, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                //处理异常情况
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                //得到服务器的具体返回内容
                final String  responseData = response.body().string();
                //解析最新版本信息
                Gson gson = new Gson();
                UpdateBean updateBean = gson.fromJson(responseData, UpdateBean.class);
                final int latestCode = updateBean.getVersionCode();
                final String latestName = updateBean.getVersionName();
                final int version = latestCode;
            }
        });

        return version;
    }
}
