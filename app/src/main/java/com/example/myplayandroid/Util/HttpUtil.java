package com.example.myplayandroid.Util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import com.example.myplayandroid.ContextApplication;

import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * Created by zhongzhiqiang on 19-4-3.
 */

public class HttpUtil {

    public static void sendOkHttpRequest(String address, okhttp3.Callback callback) {
        //若网络状态不可用，则弹出消息，直接return方法
        if (!isNetworkConnected(ContextApplication.getContext())) {
            Toast.makeText(ContextApplication.getContext(), "Network is Unavailable", Toast.LENGTH_SHORT).show();
            return;
        }
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(address)
                .build();
        client.newCall(request).enqueue(callback);
    }

    //用于判断网络状态是否可用
    public static boolean isNetworkConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable();
            }
        }
        return false;
    }

}
