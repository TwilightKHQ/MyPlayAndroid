package com.example.myplayandroid.Adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myplayandroid.ActivityWebPage;
import com.example.myplayandroid.Class.Message;
import com.example.myplayandroid.R;

import java.util.List;

/**
 * Created by lijianchang@yy.com on 2017/4/12.
 */

public class AdapterMessage extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Message> messageList;// 数据源

    private int normalType = 0;// 第一种ViewType，正常的item
    private int footType = 1;// 第二种ViewType，底部的提示View

    private boolean hasMore = true;// 变量，是否有更多数据
    private boolean fadeTips = false;// 变量，是否隐藏了底部的提示

    private Handler mHandler = new Handler(Looper.getMainLooper());//获取主线程的Handler

    public AdapterMessage(List<Message> messageList, boolean hasMore) {
        // 初始化变量
        this.messageList = messageList;
        this.hasMore = hasMore;
    }

    // 正常item的ViewHolder，用以缓存findView操作
    class NormalHolder extends RecyclerView.ViewHolder {

        View     messageView;
        TextView messageAuthor;
        TextView messageCategory;
        TextView messageTitle;
        TextView messageTime;

        public NormalHolder(View view) {
            super(view);
            messageView = view;
            messageAuthor = (TextView) view.findViewById(R.id.message_author);
            messageCategory = (TextView) view.findViewById(R.id.message_category);
            messageTitle = (TextView) view.findViewById(R.id.message_title);
            messageTime = (TextView) view.findViewById(R.id.message_time);
        }
    }

    // 底部footView的ViewHolder，用以缓存findView操作
    class FootHolder extends RecyclerView.ViewHolder {
        private TextView tips;

        public FootHolder(View itemView) {
            super(itemView);
            tips = (TextView) itemView.findViewById(R.id.tips);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // 根据返回的ViewType，绑定不同的布局文件
        if (viewType == normalType) {
            return new NormalHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message, parent, false));
        } else {
            return new FootHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.footview, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {

        if (holder instanceof NormalHolder) {

            ((NormalHolder) holder).messageAuthor.setText(messageList.get(position).getAuthor());
            ((NormalHolder) holder).messageCategory.setText(messageList.get(position).getCategory());
            ((NormalHolder) holder).messageTitle.setText(messageList.get(position).getTitle());
            ((NormalHolder) holder).messageTime.setText(messageList.get(position).getTime());

            //RecyclerView的点击事件
            ((NormalHolder) holder).messageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = ((NormalHolder) holder).getAdapterPosition();
                    Context context = view.getContext();
                    Message message = messageList.get(position);
                    Intent intent = new Intent(context, ActivityWebPage.class);
                    intent.putExtra("page_link", message.getUrl());
                    intent.putExtra("page_name", message.getTitle());
                    context.startActivity(intent);
                }
            });
            ((NormalHolder) holder).messageCategory.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = ((NormalHolder) holder).getAdapterPosition();
                    Message message = messageList.get(position);
                    Toast.makeText(view.getContext(), "You clicked Category " + message.getCategory(), Toast.LENGTH_SHORT).show();
                }
            });

        } else {
            // 之所以要设置可见，是因为在没有更多数据时会隐藏了这个footView
            ((FootHolder) holder).tips.setVisibility(View.VISIBLE);
            // 只有获取数据为空时，hasMore为false，所以当我们拉到底部时基本都会首先显示“正在加载更多...”
            if (hasMore) {
                // 不隐藏footView提示
                fadeTips = false;
                if (messageList.size() > 0) {
                    // 如果查询数据发现增加之后，就显示正在加载更多
                    ((FootHolder) holder).tips.setText("正在加载...");
                }
            } else {
                if (messageList.size() > 0) {
                    // 如果查询数据发现并没有增加时，就显示没有更多数据了
                    ((FootHolder) holder).tips.setText("没有更多数据了");
                    // 然后通过延时加载模拟网络请求的时间，在500ms后执行
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            // 隐藏提示条
                            ((FootHolder) holder).tips.setVisibility(View.GONE);
                            // 将fadeTips设置true
                            fadeTips = true;
                            // hasMore设为true是为了让再次拉到底时，会先显示正在加载更多
                            hasMore = true;
                        }
                    }, 500);
                }
            }
        }
    }

    // 获取条目数量，之所以要加1是因为增加了一条footView
    @Override
    public int getItemCount() {
        return messageList.size() + 1;
    }

    // 根据条目位置返回ViewType，以供onCreateViewHolder方法内获取不同的Holder
    @Override
    public int getItemViewType(int position) {
        if (position == getItemCount() - 1) {
            return footType;
        } else {
            return normalType;
        }
    }

    // 暴露接口，更新数据源，并修改hasMore的值，如果有增加数据，hasMore为true，否则为false
    public void updateList(List<Message> newDatas, boolean hasMore) {
        if (newDatas != null) {
            messageList.addAll(newDatas);
        }
        this.hasMore = hasMore;
        notifyDataSetChanged();
    }

    public boolean isFadeTips() {
        return fadeTips;
    }

    // 暴露接口，下拉刷新时，通过暴露方法将数据源置为空
    public void resetDatas() {
        messageList.clear();
        notifyDataSetChanged();
    }

    // 自定义方法，获取列表中数据源的最后一个位置，比getItemCount少1，因为不计上footView
    public int getRealLastPosition() {
        return messageList.size();
    }

}
