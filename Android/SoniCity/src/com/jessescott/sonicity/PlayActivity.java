package com.jessescott.sonicity;

/* IMPORTS */
import java.io.File;
import java.io.IOException;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;
import android.os.Bundle;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationListener;
import android.location.GpsStatus.Listener;
import android.location.GpsStatus.NmeaListener;

import org.puredata.android.io.AudioParameters;
import org.puredata.android.io.PdAudio;
import org.puredata.android.service.PdService;
import org.puredata.android.utils.PdUiDispatcher;
import org.puredata.core.PdBase;
import org.puredata.core.PdListener;
import org.puredata.core.utils.IoUtils;


/* PLAYACTIVITY CLASS */
public class PlayActivity extends Activity {
	
	// GLOBALS
	private static final String TAG = "SoniCity";
	private PdUiDispatcher dispatcher;
	LocationManager locationManager;
	MyLocationListener locationListener;
	Paint paint = new Paint();
	
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
		IoUtils.extractZipResource(
				getResources().openRawResource(R.raw.sonicity), dir, true);
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
		TelephonyManager telephonyManager =
				(TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
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
		
		// GPS
		locationListener = new MyLocationListener(this);

		// UI
		setContentView(R.layout.play_layout);
		//setContentView(locationListener);
		
		// PD
		try {
			initPd();
			loadPatch();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// Telephone Services
		initSystemServices();
		
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		Log.v(TAG, " - Exiting From The Play Screen - ");
		PdAudio.stopAudio();
	}

	@Override
	protected void onResume() {
		super.onResume();
		// GPS
		Log.v(TAG, " - Asking For GPS - ");
		locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 5, locationListener);
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.v(TAG, " - Destroying Play Activity - ");
		
		// Stop GPS
		locationManager.removeUpdates(locationListener);
		
		// Stop Audio
		PdAudio.release();
		PdBase.release();
	}



/* PLAYACTIVITY CLASS */
class MyLocationListener implements LocationListener {

  public MyLocationListener(Context context) {
		super();
  }

  // Define all LocationListener methods
  public void onLocationChanged(Location location) {
    // Save new GPS data
    currentLatitude  = (float)location.getLatitude();
    currentLongitude = (float)location.getLongitude();
    currentAltitude = (float)location.getAltitude();
    currentAccuracy  = (float)location.getAccuracy();
	currentSpeed = (float)location.getSpeed();
	currentBearing = (float)location.getBearing();
    currentProvider  = location.getProvider();
    
    // Log
    Log.v(TAG, "Lat is " + currentLatitude);
    Log.v(TAG, "Lon is " + currentLongitude);
    
    // Send To Pd
    sendLat(currentLatitude);
    sendLon(currentLongitude);
  }

  public void onProviderDisabled (String provider) { 
    currentProvider = "";
  }

  public void onProviderEnabled (String provider) { 
    currentProvider = provider;
  }

  public void onStatusChanged (String provider, int status, Bundle extras) {
    // Nothing yet...
  }
  

  
} /* */

} /*  */