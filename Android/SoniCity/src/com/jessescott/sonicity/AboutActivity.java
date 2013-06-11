package com.jessescott.sonicity;

/* IMPORTS */
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

/* CLASS */
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
