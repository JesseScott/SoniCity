package com.jessescott.sonicity;

/* IMPORTS */
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.Window;

/* CLASS */
public class InitialActivity extends Activity {

	// GLOBALS
	private static final String TAG = "SoniCity";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.v(TAG, " - Starting The Initial Screen - ");
		
		// Load Layout
		setContentView(R.layout.activity_initial);
		
		// Thread
		Thread timer = new Thread(){
			@Override
			public void run() {
				//super.run();
				try { 
					// Time for 5 seconds
					sleep(5000);
					Intent i = new Intent("android.intent.action.MENU");
					startActivity(i);
				}
				catch(InterruptedException e) {
					e.printStackTrace();
				}
				finally {
					Log.v(TAG, " - Ending The Initial Screen - ");
					finish();
				}
			}
		};
		timer.start();
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.initial, menu);
		return true;
	}

} /* */
