package com.jessescott.sonicity;

/* IMPORTS */
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

/* CLASS */
public class AboutActivity extends Activity {
	
	// GLOBALS
	private static final String TAG = "SoniCity";
	private TextView textView_Website;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// UI
		setContentView(R.layout.about_layout);
		Log.v(TAG, " - Starting The About Screen - ");
		
		// TextView
		textView_Website = (TextView) findViewById(R.id.textView_Website);
		textView_Website.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.v(TAG, "Clicked The Website Link!");
				Uri uri = Uri.parse("http://www.jesses.co.tt"); 
				Intent intent = new Intent(Intent.ACTION_VIEW, uri); 
				startActivity(intent);	
			}
		});
		
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
