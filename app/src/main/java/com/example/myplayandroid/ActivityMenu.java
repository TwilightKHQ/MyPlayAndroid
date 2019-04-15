package com.example.myplayandroid;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class ActivityMenu extends AppCompatActivity {

    private List<Item> itemList = new ArrayList<>();

    private AdapterSetting adapterSetting = new AdapterSetting(itemList);

    private String version;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        TextView textView = (TextView) findViewById(R.id.menu_text);
        //获取PackageManager的实例
        PackageManager packageManager = getPackageManager();
        try {
            //getPackageName()是你当前类的包名，0代表是获取版本信息
            PackageInfo packInfo = packageManager.getPackageInfo(getPackageName(), 0);
            version =  packInfo.versionName;
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
    }

    private void initItems() {
        itemList.clear();
        Item item0 = new Item("当前版本号:" + version, R.mipmap.information);
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


//    /*
//     * 获取当前程序的版本名
//     */
//    private String getVersionName() throws Exception{
//        //获取packagemanager的实例
//        PackageManager packageManager = getPackageManager();
//        //getPackageName()是你当前类的包名，0代表是获取版本信息
//        PackageInfo packInfo = packageManager.getPackageInfo(getPackageName(), 0);
//        Log.e("TAG","版本号:" + packInfo.versionCode);
//        Log.e("TAG","版本名:" + packInfo.versionName);
//        return packInfo.versionName;
//    }

}
