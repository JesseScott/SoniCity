package com.jessescott.sonicity;

/* IMPORTS */
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;

/* CLASS */
public class MenuActivity extends Activity {

	// GLOBALS
	private static final String TAG = "SoniCity";
	private Button about_button;
	private Button help_button;
	private Button settings_button;
	private Button start_button;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// UI
		setContentView(R.layout.menu_layout);
		Log.v(TAG, " - Starting The Menu Screen - ");
		
		// Buttons
		about_button = (Button) findViewById(R.id.about_button);
		about_button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				//startActivity(new Intent("android.intent.action.SECOND"));
			}
		});
		
		help_button = (Button) findViewById(R.id.help_button);
		help_button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				//startActivity(new Intent("android.intent.action.SECOND"));
			}
		});
		
		settings_button = (Button) findViewById(R.id.settings_button);
		settings_button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				//startActivity(new Intent("android.intent.action.SECOND"));
			}
		});
		
		start_button = (Button) findViewById(R.id.start_button);
		start_button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				//startActivity(new Intent("android.intent.action.SECOND"));
			}
		});
		
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
