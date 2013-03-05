package com.tiptax;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;

public class TipTaxPreferences extends PreferenceActivity {

    @Override
    @SuppressLint("NewApi")
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= 11) {
            getFragmentManager().beginTransaction().replace(android.R.id.content, new MyPreferenceFragment()).commit();
        } else {
            addPreferencesFromResource(R.xml.preferences);
        }
    }

    public static class MyPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);
        }
    }

}