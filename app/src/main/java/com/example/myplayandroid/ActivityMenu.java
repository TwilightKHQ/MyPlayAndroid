package com.example.myplayandroid;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myplayandroid.Adapter.AdapterSetting;
import com.example.myplayandroid.Bean.UpdateBean;
import com.example.myplayandroid.Class.Item;
import com.example.myplayandroid.Util.HttpUtil;
import com.example.myplayandroid.Util.Utils;
import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;


public class ActivityMenu extends AppCompatActivity {

    private List<Item> itemList = new ArrayList<>();

    private DownloadTask downloadTask;

    private ProgressDialog progressDialog;

    private AdapterSetting adapterSetting = new AdapterSetting(itemList);

    public String versionName;

    public int versionCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        TextView textView = (TextView) findViewById(R.id.menu_text);
        //获取PackageManager的实例 获取当前的版本名
        PackageManager packageManager = getPackageManager();
        try {
            //getPackageName()是你当前类的包名，0代表是获取版本信息
            PackageInfo packInfo = packageManager.getPackageInfo(getPackageName(), 0);
            versionName =  packInfo.versionName;
            versionCode = packInfo.versionCode;
        } catch (Exception e) {
            e.printStackTrace();
        }

        //利用TextView设置Toolbar的标题居中
        initToolBar();

        initItems();

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.menu_recycle_view);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 1);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(adapterSetting);
        //设置RecyclerView的点击事件
        adapterSetting.setOnItemClickListener(new AdapterSetting.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                switch (position){
                    case 1:
                        Toast.makeText(ActivityMenu.this, "正在查找更新...", Toast.LENGTH_SHORT).show();
                        getUpdateInfo("https://raw.githubusercontent.com/twilightkhq/MyPlayAndroid/master/Json/info_version.json");
                        break;
                    case 2:
                        AlertDialog.Builder dialog = new AlertDialog.Builder(ActivityMenu.this);
                        dialog.setTitle("版本更新");
                        dialog.setMessage("即将开始下载最新版本安装包");

                        dialog.setCancelable(true);
                        dialog.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                DownloadUI(getString(R.string.download_apk));
                            }
                        });
                        dialog.setNegativeButton("取消", null);
                        dialog.show();
                        break;
                }
            }

            @Override
            public void onItemLongClick(View view, int position) {
            }
        });
    }

    private void DownloadUI(final String downloadUrl) {

        startDownload(downloadUrl);

        progressDialog = new ProgressDialog(ActivityMenu.this);
        progressDialog.setTitle("版本更新");
        progressDialog.setMessage("正在下载最新安装包...");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setMax(100);
        progressDialog.setButton(DialogInterface.BUTTON_NEUTRAL, "取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                cancelDownload(downloadUrl);
            }
        });
        progressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "暂停", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                pauseDownload();
            }
        });
        progressDialog.show();
    }

    private DownloadListener listener = new DownloadListener() {
        //用getNotification()构建用于显示下载进度的通知， notify()方法用于触发通知
        @Override
        public void onProgress(int progress) {
            progressDialog.setProgress(progress);
        }

        @Override
        public void onSuccess() {
            downloadTask = null;
            //下载成功时将前台服务通知关闭，
            Toast.makeText(ActivityMenu.this, "Download Success", Toast.LENGTH_SHORT).show();
            progressDialog.cancel();
        }

        @Override
        public void onFailed() {
            downloadTask = null;
            //下载失败时将前台服务通知关闭，并创建一个下载失败的通知
            Toast.makeText(ActivityMenu.this, "Download Failed", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onPaused() {
            downloadTask = null;
            Toast.makeText(ActivityMenu.this, "Paused", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCanceled() {
            downloadTask = null;
            Toast.makeText(ActivityMenu.this, "Canceled", Toast.LENGTH_SHORT).show();
        }
    };

    private void startDownload(String downloadUrl) {
        if (downloadTask == null) {
            downloadTask = new DownloadTask(listener);
            downloadTask.execute(downloadUrl);
            //创建持续运行的通知
            Toast.makeText(ActivityMenu.this, "Downloading...", Toast.LENGTH_SHORT).show();
        }
    }

    public void pauseDownload() {
        if (downloadTask != null) {
            downloadTask.pauseDownload();
        }
    }

    public void cancelDownload(String downloadUrl) {
        if (downloadTask != null) {
            downloadTask.cancelDownload();
        } else {
            if (downloadUrl != null) {
                //取消下载时需要将文件删除，并将通知关闭
                String filename = downloadUrl.substring(downloadUrl.lastIndexOf("/"));
                String directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();
                File file = new File(directory + filename);
                if (file.exists()) {
                    file.delete();
                }
                Toast.makeText(ActivityMenu.this, "Canceled", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void initItems() {
        itemList.clear();
        Item item0 = new Item("当前版本:  " + versionName, R.mipmap.information);
        itemList.add(item0);
        Item item1 = new Item("检查更新", R.mipmap.update);
        itemList.add(item1);
        Item item2= new Item("下载安装包", R.mipmap.download);
        itemList.add(item2);
    }

    private void initToolBar() {

        Toolbar toolbar = (Toolbar) findViewById(R.id.menu_toolbar);

        //导入Toolbar
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    //初始化页面 发送网络请求
    private void getUpdateInfo(String url) {

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
                versionCode = Utils.getVersionCode(ContextApplication.getContext());
                ShowCompare(latestCode);
            }
        });
    }

    private void ShowCompare(final int Code) {
        if (Code > versionCode){
           handler.sendEmptyMessage(0x1);
        }
    }

    //通过Handler来处理异步消息
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0x1:
                    Toast.makeText(ContextApplication.getContext(), "检测到更新", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

}
