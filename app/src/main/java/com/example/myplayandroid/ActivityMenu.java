package com.example.myplayandroid;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myplayandroid.Adapter.AdapterSetting;
import com.example.myplayandroid.Bean.UpdateBean;
import com.example.myplayandroid.Class.Item;
import com.example.myplayandroid.Util.HttpUtil;
import com.example.myplayandroid.Util.Utils;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;


public class ActivityMenu extends AppCompatActivity {

    private List<Item> itemList = new ArrayList<>();
    private List<Item> mItemList;

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
//        CardView cardView = (CardView) findViewById(R.id.card_view);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 1);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(adapterSetting);
        //设置RecyclerView的点击事件
        adapterSetting.setOnItemClickListener(new AdapterSetting.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (position == 1){
                    getUpdateInfo("https://raw.githubusercontent.com/twilightkhq/MyPlayAndroid/master/Json/info_version.json");
                }
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });
    }

    private void initItems() {
        itemList.clear();
        Item item0 = new Item("当前版本号: " + versionName, R.mipmap.information);
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
