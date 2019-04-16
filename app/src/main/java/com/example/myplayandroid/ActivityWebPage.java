package com.example.myplayandroid;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.example.myplayandroid.Util.HttpUtil;

public class ActivityWebPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webpage);
        //获取到网页链接信息
        Intent intent = getIntent();
        String url = intent.getStringExtra("page_link");
        String name = intent.getStringExtra("page_name");
        //加载顶部的ToolBar
        Toolbar toolbar = (Toolbar) findViewById(R.id.webpage_toolbar);
        setSupportActionBar(toolbar);
        //动态设置ToolBar标题
        getSupportActionBar().setTitle(name);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        if (!HttpUtil.isNetworkConnected(ContextApplication.getContext())) {
            Toast.makeText(ContextApplication.getContext(), "Network is Unavailable", Toast.LENGTH_SHORT).show();
            return;
        }
        //利用WebView加载网页
        WebView webView = (WebView) findViewById(R.id.web_view);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl(url);
        //解决Uncaught TypeError：Cannot read property 'setItem' of null"
        //本地存储的东西，存储量比cookie大， 启动H5本地存储
        WebSettings settings = webView.getSettings();
        settings.setDomStorageEnabled(true);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_webpage, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case  R.id.heart:
                Toast.makeText(this, "You clicked Heart", Toast.LENGTH_SHORT).show();
                break;
            case  R.id.share:
                Toast.makeText(this, "You clicked Share", Toast.LENGTH_SHORT).show();
                break;
            case android.R.id.home:
                //设置返回键返回主界面
                return super.onOptionsItemSelected(menuItem);
            default:
                break;
        }
        return true;
    }
}