package com.example.myplayandroid.Adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.List;

public class AdapterFragment extends FragmentStatePagerAdapter {

    Context context;
    List<Fragment> listFragment;

    public AdapterFragment(FragmentManager fragmentManager, Context context, List<Fragment> listFragment) {
        super(fragmentManager);
        this.context = context;
        this.listFragment = listFragment;
    }

    @Override
    public Fragment getItem(int position) {
        return listFragment.get(position);
    }

    @Override
    public int getCount() {
        return listFragment == null ? 0 : listFragment.size();
    }
}