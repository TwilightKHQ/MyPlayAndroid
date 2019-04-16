package com.example.myplayandroid;

/**
 * Created by zhongzhiqiang on 19-3-26.
 */

public interface DownloadListener {

    void onProgress(int progress);

    void onSuccess();

    void onFailed();

    void onPaused();

    void onCanceled();

}
