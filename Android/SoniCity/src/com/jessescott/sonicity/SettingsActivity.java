package com.jessescott.sonicity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

public class SettingsActivity extends Activity {
	
	// GLOBALS
	private static final String TAG = "SoniCity";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// UI
		setContentView(R.layout.settings_layout);
		Log.v(TAG, " - Starting The Settings Screen - ");
		
	}
	
	
	/* LIFECYCLE */
	
	@Override
	protected void onPause() {
		super.onPause();
		Log.v(TAG, " - Exiting From The Settings Screen - ");
	}

	@Override
	protected void onResume() {
		super.onResume();
		
	}

}
