package com.jessescott.sonicity;

/* IMPORTS */
import java.io.File;
import java.io.IOException;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.view.View;
import android.widget.TextView;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;

import org.puredata.android.io.AudioParameters;
import org.puredata.android.io.PdAudio;
import org.puredata.android.service.PdService;
import org.puredata.android.utils.PdUiDispatcher;
import org.puredata.core.PdBase;
import org.puredata.core.utils.IoUtils;


/* PLAYACTIVITY CLASS */
public class PlayActivity extends Activity {
	
	// GLOBALS
	private static final String TAG = "SoniCity";
	
	private PdUiDispatcher dispatcher;
	private PdService pdService = null;
	
	LocationManager locationManager;
	MyLocationListener locationListener;
	Handler handler;
	
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
	
	// Service
	private final ServiceConnection pdConnection = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			pdService = ((PdService.PdBinder)service).getService();
			try {
				Log.e(TAG, "Starting Pd Service ");
				initPd();
				loadPatch();
			}
			catch(IOException e) {
				Log.e(TAG, e.toString());
				finish();
			}
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			// this method will never be called
		}
	};
	
	// Initialize Audio
	private void  initPd() throws IOException {
		Log.e(TAG, "Initializing PD ");
		
		// Audio Settings
		AudioParameters.init(this);
		int sampleRate = AudioParameters.suggestSampleRate();
		pdService.initAudio(sampleRate, 0, 2, 10.0f);
		start();
		
		// Dispatcher
		dispatcher = new PdUiDispatcher();
		PdBase.setReceiver(dispatcher);
		
	}
	
	// Start Audio
	private void start() {
		Log.e(TAG, "Starting PD ");
		if (!pdService.isRunning()) {
			Log.e(TAG, "here ");
			Intent intent = new Intent(PlayActivity.this, PlayActivity.class);
			pdService.startAudio(intent, R.drawable.icon, "SoniCity", "Return to SoniCity.");
		}
	}
	
	// Load Patch
	private void loadPatch() throws IOException {
		Log.e(TAG, "Loading PD Patch");
		File dir = getFilesDir();
		IoUtils.extractZipResource(getResources().openRawResource(R.raw.sonicity), dir, true);
		File patchFile = new File(dir, "sonicity.pd");
		PdBase.openPatch(patchFile.getAbsolutePath());
	}
	
	// Kill PD
	private void cleanupPd() {
		try {
			unbindService(pdConnection);
		} catch (IllegalArgumentException e) {
			// already unbound
			pdService = null;
		}
	}
	
	// Send Data
	private void sendLatToPd(float n) {
		PdBase.sendFloat("LAT", n);
	}
	
	private void sendLonToPd(float n) {
		PdBase.sendFloat("LON", n);
	}
	
	/* PHONE */
	
	private void initSystemServices() {
		TelephonyManager telephonyManager =
				(TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		telephonyManager.listen(new PhoneStateListener() {
			@Override
			public void onCallStateChanged(int state, String incomingNumber) {
				if (pdService == null) return;
				if (state == TelephonyManager.CALL_STATE_IDLE) {
					start(); } else {
						pdService.stopAudio(); }
			}
		}, PhoneStateListener.LISTEN_CALL_STATE);
	}
	
	/* UI */

	private void initGUI() {
		setContentView(R.layout.play_layout);
		
		// TextViews
		latitude  = (TextView) findViewById(R.id.Latitude);
		longitude = (TextView) findViewById(R.id.Longitude);
		
		ActualLatitude  = (TextView) findViewById(R.id.ActualLat);
		ActualLatitude.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.v(TAG, "Asking For New Latitude");
				ActualLatitude.setText(locationListener.getCurrentLatitude());
				float lat = Float.parseFloat(locationListener.getCurrentLatitude());
				sendLatToPd(lat);
			}
		});
		
		ActualLongitude = (TextView) findViewById(R.id.ActualLon);
		ActualLongitude.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.v(TAG, "Asking For New Longitude");
				ActualLongitude.setText(locationListener.getCurrentLongitude());
				float lon = Float.parseFloat(locationListener.getCurrentLongitude());
				sendLonToPd(lon);
			}
		});
	}
	

	
	
	/* LIFECYCLE */
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.v(TAG, " - Starting The Play Screen - ");
		
		// UI
		initGUI();
		
		// GPS
		locationListener = new MyLocationListener(this);
		
		// Telephone Services
		initSystemServices();
		
		// PD Service
		bindService(new Intent(this, PdService.class), pdConnection, BIND_AUTO_CREATE);
		
		// Runnable
		handler = new Handler();
		
		final Runnable r = new Runnable() {
			public void run() {
				Log.v(TAG, "run");
				handler.postDelayed(this, 2000);
			}
		};
		handler.postDelayed(r, 2000);
		
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		Log.v(TAG, " - Exiting From The Play Screen - ");
		
		// Stop GPS
		locationManager.removeUpdates(locationListener);
		locationManager = null;
		
		// Stop Pd
		//PdAudio.stopAudio();
	}

	@Override
	protected void onResume() {
		super.onResume();
		Log.v(TAG, " - Entering The Play Screen - ");
		
		// GPS
		locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5, locationListener);
		
		// Start Pd
		//PdAudio.startAudio(this);
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.v(TAG, " - Destroying Play Activity - ");
		
		// Kill Pd
		cleanupPd();
		//PdAudio.release();
		//PdBase.release();
	}

} /*  */