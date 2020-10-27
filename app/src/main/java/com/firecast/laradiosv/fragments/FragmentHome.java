package com.firecast.laradiosv.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.firecast.laradiosv.activities.MainActivity;
import com.firecast.laradiosv.R;

public class FragmentHome extends Fragment {

    private MainActivity mainActivity;
    private Toolbar toolbar;
    public static TabLayout tabLayout;
    public static ViewPager viewPager;
    public static int int_items = 1;
    private RelativeLayout coordinatorLayout;

    public FragmentHome() {
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mainActivity = (MainActivity) activity;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_home, container, false);
        tabLayout = (TabLayout) v.findViewById(R.id.tabs);
        viewPager = (ViewPager) v.findViewById(R.id.viewpager);

        toolbar = (Toolbar) v.findViewById(R.id.toolbar);
        setupToolbar();

        coordinatorLayout = (RelativeLayout) v.findViewById(R.id.tab_coordinator_layout);

        viewPager.setAdapter(new MyAdapter(getChildFragmentManager()));
        viewPager.setCurrentItem(0);

        tabLayout.post(new Runnable() {
            @Override
            public void run() {
                tabLayout.setupWithViewPager(viewPager);
            }
        });

        return v;

    }

    class MyAdapter extends FragmentPagerAdapter {

        public MyAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new FragmentRadio();
            }
            return null;
        }

        @Override
        public int getCount() {

            return int_items;

        }

        @Override
        public CharSequence getPageTitle(int position) {

            switch (position) {
                case 0:
                    return "Player";
            }
            return null;
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mainActivity.setupNavigationDrawer(toolbar);
    }

    private void setupToolbar() {
        toolbar.setTitle(getString(R.string.app_name));
        mainActivity.setSupportActionBar(toolbar);
    }

}

