package com.hyphenate.easemob.im.officeautomation.adapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.hyphenate.easemob.easeui.ui.EaseBaseFragment;

import java.util.ArrayList;


public class ViewPagerAdapter extends FragmentPagerAdapter {

    private ArrayList<EaseBaseFragment> fragments;

    public ViewPagerAdapter(FragmentManager fm, ArrayList<EaseBaseFragment> fragments) {
        super(fm);
        this.fragments = fragments;
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }
}