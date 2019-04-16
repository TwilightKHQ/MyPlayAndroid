package com.example.myplayandroid.Bean;

/**
 * Created by zhongzhiqiang on 19-4-16.
 */

public class UpdateBean {
    /**
     * versionCode : 2
     * versionName : 1.0.2
     * url : https://github.com/TwilightKHQ/MyPlayAndroid/releases/download/1.0/app-release.apk
     */
    private int versionCode;
    private String versionName;
    private String url;

    public int getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(int versionCode) {
        this.versionCode = versionCode;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
