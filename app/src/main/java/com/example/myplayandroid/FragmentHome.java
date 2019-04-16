package com.example.myplayandroid;

/**
 * Created by zhongzhiqiang on 19-4-8.
 */

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.myplayandroid.Bean.MessageBean;
import com.example.myplayandroid.Class.Message;
import com.example.myplayandroid.Util.HttpUtil;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class FragmentHome extends Fragment implements SwipeRefreshLayout.OnRefreshListener{

    private String TAG = "MainActivity";

    private SwipeRefreshLayout swipeRefresh;
    private RecyclerView recyclerView;
    private BottomNavigationView bottomNavigationView;//底部导航栏对象

    private List<Message> messageList = new ArrayList<>();
    private AdapterMessage messageAdapter = null;
    private MyDatabaseHelper databaseHelper;

    private String firstPageUrl;
    private String nowPageUrl;
    private int nowPage;

    private GridLayoutManager mLayoutManager;
    private Handler mHandler = new Handler(Looper.getMainLooper());//获取主线程的Handler

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        firstPageUrl = getString(R.string.url_ArticleList);
        nowPageUrl = getString(R.string.url_ArticleList);
        nowPage = 0;

        View view = inflater.inflate(R.layout.fragment_home, container, false);
        swipeRefresh = (SwipeRefreshLayout) view.findViewById(R.id.refreshLayout);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        bottomNavigationView = (BottomNavigationView) view.findViewById(R.id.navigation);

        databaseHelper = new MyDatabaseHelper(ContextApplication.getContext(), "MessageList.db", null, 1);

        getMessage(firstPageUrl); //初始化首页内容

        //Fragment当中的findViewById需要通过view来使用
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        messageAdapter = new AdapterMessage(messageList, true);
        recyclerView.setAdapter(messageAdapter);

        initRefreshLayout();
        initRecyclerView();

        return view;
    }

    private void initRefreshLayout() {
        swipeRefresh.setColorSchemeResources(android.R.color.holo_blue_light, android.R.color.holo_red_light,
                android.R.color.holo_orange_light, android.R.color.holo_green_light);
                //下拉刷新
//        swipeRefresh.setColorSchemeResources(R.color.colorPrimary);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshMessages();
            }
        });
    }

    private void initRecyclerView() {
        // 初始化RecyclerView的Adapter
        // 第一个参数为数据，上拉加载的原理就是分页
        // 第二个参数为hasMore，是否有新数据
        messageAdapter = new AdapterMessage(messageList, true);
        mLayoutManager = new GridLayoutManager(getContext(), 1);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(messageAdapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        // 实现上拉加载重要步骤，设置滑动监听器，RecyclerView自带的ScrollListener
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                LinearLayoutManager manager = (LinearLayoutManager) recyclerView.getLayoutManager();
                //获取最后一个完全显示的ItemPosition
                int lastVisibleItem = manager.findLastCompletelyVisibleItemPosition();
                // 在newState为滑到底部时
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    // 如果没有隐藏footView，那么最后一个条目的位置就比我们的getItemCount少1，自己可以算一下
                    if (!messageAdapter.isFadeTips() && lastVisibleItem + 1 == messageAdapter.getItemCount()) {
                        //获取下一页的Url，并将其设置为当前需要访问的页面
                        nowPageUrl = getNextPage();
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                // 然后调用updateRecyclerView方法更新RecyclerView
//                                updateRecyclerView(messageAdapter.getRealLastPosition(), messageAdapter.getRealLastPosition() + PAGE_COUNT);
                                getMessage(nowPageUrl);
                            }
                        }, 500);
                    }
                    // 如果隐藏了提示条，我们又上拉加载时，那么最后一个条目就要比getItemCount要少2
                    if (messageAdapter.isFadeTips() && lastVisibleItem + 2 == messageAdapter.getItemCount()) {
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                // 然后调用updateRecyclerView方法更新RecyclerView
//                                updateRecyclerView(messageAdapter.getRealLastPosition(), messageAdapter.getRealLastPosition() + PAGE_COUNT);
                                getMessage(nowPageUrl);
                            }
                        }, 500);
                    }
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });
    }

    //获取下一页的地址
    private String getNextPage() {

        String nextPage;
        String urlHead = nowPageUrl.substring(0, nowPageUrl.length() - 6);
        nowPage ++;
        nextPage = urlHead + nowPage + "/json";

        return nextPage;
    }

    @Override
    public void onRefresh() {

    }


    //初始化页面 发送网络请求
    private void getMessage(final String url) {

        HttpUtil.sendOkHttpRequest(url, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                //处理异常情况
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                //得到服务器的具体返回内容
                final String  responseData = response.body().string();
                //子线程内修改UI会导致程序崩溃
                //使用runOnUiThread来修改UI界面，使程序不崩溃
                //Fragment当中的runOnUiThread需要通过getActivity来使用
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        parseJSONWithGSON(responseData);        //用GSON解析JSON
                    }
                });
//                SQLiteDatabase sqLiteDatabase = databaseHelper.getWritableDatabase();
//                ContentValues values = new ContentValues();
//                long currentTime = System.currentTimeMillis();
//                String timeNow = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(currentTime);
//                //查询语句，必须先将数据移到下一个才可以
//                Cursor cursor = sqLiteDatabase.query("Message", new String[] {"url, json, time"}, "url = ?", new String[] {url}, null, null, null);
//                cursor.moveToFirst();
//                //获取查询结果数据
//                Log.d("Test", "onResponse: " + cursor.getCount() );
//                switch (cursor.getCount()){
//                    case 0:
//                        values.put("url", url);
//                        values.put("json", responseData);
//                        values.put("time", timeNow);
//                        sqLiteDatabase.insert("Message", null, values);
//                        Log.d("Test", "onResponse: ");
//                        break;
//                    case 1:
//                        if (!responseData.equals( cursor.getString( cursor.getColumnIndex("json") ) ))
//                            values.put("json", responseData);
//                        values.put("time", timeNow);
//                        sqLiteDatabase.update("Message", values, "url = ?", new String[]{url});
//                        break;
//                    default:
//                        Toast.makeText(ContextApplication.getContext(), "缓存错误", Toast.LENGTH_SHORT).show();
//                        break;
//                }
//                //判断请求所得数据与数据库里的数据是否吻合
//                Log.d("Test", "onResponse: " + responseData.equals( cursor.getString( cursor.getColumnIndex("json") ) ) );
//                cursor.close();
            }
        });
    }

    //解析ArticleList JSON数据
    private void parseJSONWithGSON(String jsonData) {
        Gson gson = new Gson();
        MessageBean messageBean = gson.fromJson(jsonData, MessageBean.class);
        List<MessageBean.DataBean.DatasBean> datas = messageBean.getData().getDatas();
        List<Message> newDatas = new ArrayList<>();
        for (MessageBean.DataBean.DatasBean datasBean : datas){
            String author = datasBean.getAuthor();
            String category = datasBean.getSuperChapterName() + '/' + datasBean.getChapterName();
            String title = datasBean.getTitle();
            String time = datasBean.getNiceDate();
            String url = datasBean.getLink();
            Message message = new Message(author, category, title, time, url);
            newDatas.add(message);
        }

        // 根据解析出来的数据是否为空来显示
        if (newDatas.size() > 0) {
            // 然后传给Adapter，并设置hasMore为true
            messageAdapter.updateList(newDatas, true);
        } else {
            messageAdapter.updateList(null, false);
        }
    }

    //刷新Message数据
    public void refreshMessages() {

        Thread refreshThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // 重新设置当前页面为第0页
                        nowPage = 0;
                        // 重置adapter的数据源为空
                        messageAdapter.resetDatas();
                        getMessage(firstPageUrl);
                        messageAdapter.notifyDataSetChanged();
                        swipeRefresh.setRefreshing(false);
                    }
                });
            }
        });

        refreshThread.start();
    }
}
