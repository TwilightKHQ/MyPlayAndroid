package com.example.myplayandroid;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.FileProvider;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import android.widget.TextView;
import android.widget.Toast;

import com.example.myplayandroid.Adapter.AdapterSetting;
import com.example.myplayandroid.Bean.UpdateBean;
import com.example.myplayandroid.Class.Item;
import com.example.myplayandroid.Util.HttpUtil;
import com.example.myplayandroid.Util.MyProgressBarDialog;
import com.example.myplayandroid.Util.Utils;
import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.example.myplayandroid.ContextApplication.getContext;


public class ActivityMenu extends AppCompatActivity {

    private String TAG = "Test";

    private List<Item> itemList = new ArrayList<>();

    private DownloadTask downloadTask;

    private MyProgressBarDialog myDialog;

    private AdapterSetting adapterSetting = new AdapterSetting(itemList);

    private boolean isCanceled = false;
    private boolean isPaused = false;

    public String versionName;
    public int versionCode;

    String downloadUrl = "https://github.com/TwilightKHQ/MyPlayAndroid/releases/download/1.0/app-release.apk";

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
            String fileName = downloadUrl.substring(downloadUrl.lastIndexOf("/"));
            String directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();
            File file = new File(directory + fileName);
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
                                //判断文件是否下载完成
                                ContextApplication.getContext().getMainLooper();
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        //需要在子线程中处理的逻辑
                                        try {
                                            long downloadedLength = file.length();
                                            long contentLength = getContentLength(downloadUrl);
                                            if ( downloadedLength == contentLength) {
                                                handler.sendEmptyMessage(0x00);
                                            } else {
                                                handler.sendEmptyMessage(0x01);
                                            }

                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }).start();

                            }
                        });
                        dialog.setNegativeButton("取消", null);
                        dialog.show();
                        break;
                    case 3:
                        //判断文件是否存在，存在则删除安装包
                        if (file.exists()) {
                            file.delete();
                        } else {
                            Toast.makeText(ContextApplication.getContext(), "安装包不存在", Toast.LENGTH_SHORT).show();
                        }
                }
            }

            @Override
            public void onItemLongClick(View view, int position) {
            }
        });

        myDialog = new MyProgressBarDialog(this);
        myDialog.getmCancelButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isCanceled = true;
                cancelDownload(downloadUrl);
                Toast.makeText(ContextApplication.getContext(), "已取消", Toast.LENGTH_SHORT).show();
                if (myDialog.isShowing()){
                    myDialog.dismiss();
                }
            }
        });
        myDialog.getmPausedButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isPaused = true;
                pauseDownload();
                Toast.makeText(ContextApplication.getContext(), "已暂停", Toast.LENGTH_SHORT).show();
                myDialog.getmPausedButton().setText("继续");
            }
        });
    }

    private void initItems() {
        itemList.clear();
        Item item0 = new Item("当前版本:  " + versionName, R.mipmap.information);
        itemList.add(item0);
        Item item1 = new Item("检查更新", R.mipmap.update);
        itemList.add(item1);
        Item item2 = new Item("下载安装包", R.mipmap.download);
        itemList.add(item2);
        Item item3 = new Item("删除安装包", R.mipmap.delete);
        itemList.add(item3);
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

    private DownloadListener listener = new DownloadListener() {
        //显示下载进度
        @Override
        public void onProgress(final int progress) {
            if (myDialog != null && myDialog.isShowing()) {
                Log.d(TAG, "onProgress: " + progress);
                myDialog.setmProgressBar(progress);
                myDialog.setmProgressPercent(progress);
            }
        }

        @Override
        public void onSuccess() {
            downloadTask = null;
            //下载完成后关闭Dialog
            if (myDialog.isShowing()) {
                myDialog.cancel();
            }
            install();
        }

        @Override
        public void onFailed() {
            downloadTask = null;
            //下载失败时将前台服务通知关闭，并创建一个下载失败的通知
            Toast.makeText(ActivityMenu.this, "下载失败", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onPaused() {
            downloadTask = null;
        }

        @Override
        public void onCanceled() {
            downloadTask = null;
        }
    };

    private void startDownload(String downloadUrl) {
        myDialog.show();
        myDialog.setmProgressBar(0);
        myDialog.setmProgressPercent(0);
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

    //发送网络请求
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
                versionCode = Utils.getVersionCode(getContext());
                if (latestCode > versionCode){
                    handler.sendEmptyMessage(0x100);
                }
            }
        });
    }

    //通过Handler来处理异步消息
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0x00:
                    Toast.makeText(ContextApplication.getContext(), "安装包已下载", Toast.LENGTH_SHORT).show();
                    install();
                    break;
                case 0x01:
                    startDownload(getString(R.string.download_apk));
                    break;
                case 0x100:
                    Toast.makeText(getContext(), "检测到更新", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    };

    private void install() {
        String filename = downloadUrl.substring(downloadUrl.lastIndexOf("/"));
        String filePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath() + filename;
        Log.i(TAG, "开始执行安装: " + filePath);
        File apkFile = new File(filePath);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Log.w(TAG, "版本大于 N ，开始使用 fileProvider 进行安装");
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Uri contentUri = FileProvider.getUriForFile(
                    getContext()
                    , "com.example.myplayandroid.fileprovider"
                    , apkFile);
            intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
        } else {
            Log.w(TAG, "正常进行安装");
            intent.setDataAndType(Uri.fromFile(apkFile), "application/vnd.android.package-archive");
        }
        startActivity(intent);
    }

    private long getContentLength(String downloadUrl) throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(downloadUrl)
                .build();
        Response response = client.newCall(request).execute();
        if (response != null && response.isSuccessful()) {
            long contentLength = response.body().contentLength();
            response.close();
            return contentLength;
        }
        return 0;
    }

}
