package com.tiptax;

import android.os.Bundle;
import android.preference.PreferenceActivity;

public class TipTaxPreferences extends PreferenceActivity {

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);

	}

}