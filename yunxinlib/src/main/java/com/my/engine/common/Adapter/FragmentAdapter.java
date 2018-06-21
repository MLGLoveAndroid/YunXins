package com.my.engine.common.Adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;

/**
 * 未来属于自己
 * 作者：MLG
 * QQ:2264280742
 * 2018-4-10 14:54
 */


public class FragmentAdapter extends FragmentPagerAdapter {
    private ArrayList<Fragment> list;

    public FragmentAdapter(FragmentManager fm, ArrayList<Fragment> list) {
        super(fm);
        this.list = list;
    }

    @Override
    public Fragment getItem(int position) {
        return list.get(position);
    }

    @Override
    public int getCount() {
        return list.size();
    }
}
