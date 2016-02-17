package com.flightpathcore.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import com.flightpathcore.base.BaseFragment;

import java.util.List;

public class PagerAdapter extends FragmentPagerAdapter {

    private List<String> fragments;
    private FragmentManager fm;
    private boolean blocked = false;

    public PagerAdapter(FragmentManager fm, List<String> fragments) {
        super(fm);
        this.fm = fm;
        this.fragments = fragments;
    }

    @Override
    public Fragment getItem(int position) {
        return fm.findFragmentByTag(fragments.get(position));
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        super.destroyItem(container, position, object);
        Fragment f = getItem(position);
        fm.beginTransaction().remove(f).commitAllowingStateLoss();
    }

    @Override
    public int getCount() {
        return fragments.size() - (blocked ? 1 : 0);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return ((BaseFragment) fm.findFragmentByTag(fragments.get(position))).getTitle();
    }

    public void blockPager() {
        blocked = true;
    }


    public void unblockPager() {
        blocked = false;
    }

}
