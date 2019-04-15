package com.example.myplayandroid;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.List;

/**
 * Created by zhongzhiqiang on 19-4-15.
 */

public class AdapterSetting extends RecyclerView.Adapter<AdapterSetting.ViewHolder> {

    private List<Item> mItemList;

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
                Toast.makeText(ContextApplication.getContext(), "You clicked " + item.getName(), Toast.LENGTH_SHORT).show();
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
}
