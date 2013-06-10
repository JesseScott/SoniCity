package com.jessescott.sonicity;

/* IMPORTS */
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;

/* CLASS */
public class MenuActivity extends Activity {

	// GLOBALS
	private static final String TAG = "SoniCity";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_menu);
		Log.v(TAG, " - Starting The Menu Screen - ");
	}

	
	
	/* LIFECYCLE */
	
	@Override
	protected void onPause() {
		super.onPause();
		Log.v(TAG, " - Exiting From The Menu Screen - ");
	}

	@Override
	protected void onResume() {
		super.onResume();
		
	}
	
	
} /* */
