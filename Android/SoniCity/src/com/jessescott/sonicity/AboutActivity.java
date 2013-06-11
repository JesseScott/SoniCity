package com.jessescott.sonicity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class AboutActivity extends Activity {
	
	// GLOBALS
	private static final String TAG = "SoniCity";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// UI
		setContentView(R.layout.about_layout);
		Log.v(TAG, " - Starting The About Screen - ");
		
	}
	
	
	/* LIFECYCLE */
	
	@Override
	protected void onPause() {
		super.onPause();
		Log.v(TAG, " - Exiting From The About Screen - ");
	}

	@Override
	protected void onResume() {
		super.onResume();
		
	}

}
