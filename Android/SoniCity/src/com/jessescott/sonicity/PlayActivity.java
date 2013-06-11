package com.jessescott.sonicity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

public class PlayActivity extends Activity {
	
	// GLOBALS
	private static final String TAG = "SoniCity";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// UI
		setContentView(R.layout.play_layout);
		Log.v(TAG, " - Starting The Play Screen - ");
		
	}
	
	
	/* LIFECYCLE */
	
	@Override
	protected void onPause() {
		super.onPause();
		Log.v(TAG, " - Exiting From The Play Screen - ");
	}

	@Override
	protected void onResume() {
		super.onResume();
		
	}

}
