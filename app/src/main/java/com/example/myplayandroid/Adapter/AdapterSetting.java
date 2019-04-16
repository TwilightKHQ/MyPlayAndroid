package com.example.myplayandroid.Adapter;

import android.app.Activity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.myplayandroid.Bean.UpdateBean;
import com.example.myplayandroid.Class.Item;
import com.example.myplayandroid.ContextApplication;
import com.example.myplayandroid.R;
import com.example.myplayandroid.Util.HttpUtil;
import com.example.myplayandroid.Util.Utils;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by zhongzhiqiang on 19-4-15.
 */

public class AdapterSetting extends RecyclerView.Adapter<AdapterSetting.ViewHolder> {

    private List<Item> mItemList;

    private int versionCode;

    private OnItemClickListener listener; //设置RecyclerView的点击事件

    public void setOnItemClickListener(AdapterSetting.OnItemClickListener onItemClickListener){
        this.listener = onItemClickListener;
    }


    static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        ImageView settingImage;
        TextView settingName;

        public ViewHolder(View view) {
            super(view);
            cardView = (CardView) view;
            settingImage = (ImageView) view.findViewById(R.id.setting_image);
            settingName = (TextView) view.findViewById(R.id.setting_text);
        }
    }

    public AdapterSetting(List<Item> itemList) {
        mItemList = itemList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(ContextApplication.getContext()).inflate(R.layout.item_setting, parent, false);
        final ViewHolder viewHolder = new ViewHolder(view);
        viewHolder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = viewHolder.getAdapterPosition();
                Item item = mItemList.get(position);
                if (listener != null) {
                    listener.onItemClick(view, position);
                }
            }
        });

        return viewHolder;
    }

    @Override
    public void onBindViewHolder( ViewHolder holder, int position) {
        Item item = mItemList.get(position);
        holder.settingName.setText(item.getName());
        Glide.with(ContextApplication.getContext()).load(item.getImageId()).into(holder.settingImage);
    }

    @Override
    public int getItemCount() {
        return mItemList.size();
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
        if (Code > versionCode) {
            Toast.makeText(ContextApplication.getContext(), "检测到更新", Toast.LENGTH_SHORT).show();
        }
    }

    //自己设计点击事件接口
    public interface OnItemClickListener {
        void onItemClick(View view, int position);
        void onItemLongClick(View view, int position);
    }
}
