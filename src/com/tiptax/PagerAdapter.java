package com.tiptax;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

/**
 * Created by koy on 09/08/13.
 */
public class PagerAdapter extends FragmentPagerAdapter {
    private final Context context;

    public PagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        this.context = context;
    }


    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return Fragment.instantiate(context, MainFragment.class.getName());
            case 1:
                return Fragment.instantiate(context, FinishFragment.class.getName());
            default:
                return Fragment.instantiate(context, MainFragment.class.getName());
        }
    }

    @Override
    public int getCount() {
        return 2;
    }
}
