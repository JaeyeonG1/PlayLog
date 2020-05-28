package com.quickids.playlog.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;

public class MainFragmentAdapter extends FragmentPagerAdapter {
    private ArrayList<Fragment> items;
    private ArrayList<String> itemNames;

    public MainFragmentAdapter(@NonNull FragmentManager fm, ArrayList<Fragment> items, ArrayList<String> itemNames) {
        super(fm);
        this.items = items;
        this.itemNames = itemNames;
    }

    @NonNull
    @Override
    public CharSequence getPageTitle(int position) {
        return itemNames.get(position);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return items.get(position);
    }

    @Override
    public int getCount() {
        return items.size();
    }
}
