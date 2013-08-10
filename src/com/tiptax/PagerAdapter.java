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
        Log.d("TipTax", "Hello");

        switch (position) {
            case 0:
                Log.d("TipTax", "1");

                return Fragment.instantiate(context, MainFragment.class.getName());
            case 1:
                Log.d("TipTax", "2");

                return Fragment.instantiate(context, FinishFragment.class.getName());
        }
        System.out.println("NOT SUPPOSED TO BE HERE");
        return Fragment.instantiate(context, "MainFragment");
    }

    @Override
    public int getCount() {
        return 2;
    }
}
