package com.jessescott.sonicity;

/* IMPORTS */
import java.io.File;
import java.io.IOException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationListener;


import org.puredata.android.io.AudioParameters;
import org.puredata.android.io.PdAudio;
import org.puredata.android.utils.PdUiDispatcher;
import org.puredata.core.PdBase;
import org.puredata.core.utils.IoUtils;


/* PLAYACTIVITY CLASS */
public class PlayActivity extends Activity {
	
	// GLOBALS
	private static final String TAG = "SoniCity";
	private PdUiDispatcher dispatcher;
	LocationManager locationManager;
	MyLocationListener locationListener;
	
	TextView latitude, longitude;
	TextView ActualLatitude, ActualLongitude;
	
	float currentLatitude  	= 0;
	float currentLongitude 	= 0;
	float currentAltitude 	= 0;
	float currentAccuracy  	= 0;
	float currentSpeed 		= 0;
	float currentBearing 	= 0;
	String currentProvider 	= "";
	
	/* LIBPD */
	
	// Initialize Audio
	private void  initPd() throws IOException {
		Log.v(TAG, "Initializing PD ");
		
		// Audio Settings
		AudioParameters.init(this);
		int sampleRate = AudioParameters.suggestSampleRate();
		PdAudio.initAudio(sampleRate, 0, 2, 8, true);
		
		// Dispatcher
		dispatcher = new PdUiDispatcher();
		PdBase.setReceiver(dispatcher);
		
	}
	
	private void loadPatch() throws IOException {
		Log.v(TAG, "Loading PD Patch");
		File dir = getFilesDir();
		IoUtils.extractZipResource(getResources().openRawResource(R.raw.sonicity), dir, true);
		File patchFile = new File(dir, "sonicity.pd");
		PdBase.openPatch(patchFile.getAbsolutePath());
	}
	
	/* SEND DATA */

	private void sendLat(float n) {
		PdBase.sendFloat("LAT", n);
		PdBase.sendBang("trigger");
	}
	
	private void sendLon(float n) {
		PdBase.sendFloat("LON", n);
		PdBase.sendBang("trigger");
	}
	
	/* PHONE */
	
	private void initSystemServices() {
		Log.v(TAG, "Starting System Service");
		TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		telephonyManager.listen(new PhoneStateListener() {
			@Override
			public void onCallStateChanged(int state, String incomingNumber) {
				if (state == TelephonyManager.CALL_STATE_IDLE) {
					PdAudio.startAudio(getApplicationContext());
				} else {
					PdAudio.stopAudio(); 
				}
			}
		}, PhoneStateListener.LISTEN_CALL_STATE);
	}
	
	/* UI */

	
	/* LIFECYCLE */
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.v(TAG, " - Starting The Play Screen - ");

		// UI
		setContentView(R.layout.play_layout);
		
		// PD
		try {
			initPd();
			loadPatch();
		} catch (IOException e) {
			e.printStackTrace();
		}
		// GPS
		locationListener = new MyLocationListener(this);	
		
		// Telephone Services
		initSystemServices();
		
		// TextViews
		latitude  = (TextView) findViewById(R.id.Latitude);
		longitude = (TextView) findViewById(R.id.Longitude);
		
		ActualLatitude  = (TextView) findViewById(R.id.ActualLat);
		ActualLatitude.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.v(TAG, "Asking For New Latitude");
				ActualLatitude.setText(locationListener.getCurrentLatitude());	
			}
		});
		
		ActualLongitude = (TextView) findViewById(R.id.ActualLon);
		ActualLongitude.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.v(TAG, "Asking For New Latitude");
				ActualLongitude.setText(locationListener.getCurrentLongitude());	
			}
		});
		
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		Log.v(TAG, " - Exiting From The Play Screen - ");
		// Stop GPS
		locationManager.removeUpdates(locationListener);
		locationManager = null;
		// Stop Pd
		PdAudio.stopAudio();
	}

	@Override
	protected void onResume() {
		super.onResume();
		Log.v(TAG, " - Entering The Play Screen - ");
		// GPS
		locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 5, locationListener);
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.v(TAG, " - Destroying Play Activity - ");
		// Kill Pd
		PdAudio.release();
		PdBase.release();
	}

} /*  */