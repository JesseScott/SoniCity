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
	private PdService pdService = null;
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
	
	// Pd Service
	private final ServiceConnection pdConnection = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			pdService = ((PdService.PdBinder)service).getService();
			try {
				initPd();
				loadPatch();
			} catch (IOException e) {
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
		// Configure the audio glue
		AudioParameters.init(this);
		int sampleRate = AudioParameters.suggestSampleRate();
		pdService.initAudio(sampleRate, 0, 2, 10.0f);
		start();

		// Create and install the dispatcher
		dispatcher = new PdUiDispatcher();
		PdBase.setReceiver(dispatcher);
		dispatcher.addListener("pitch", new PdListener.Adapter() {
			@Override
			public void receiveFloat(String source, final float x) {
				//
			}
		});
	}

	private void start() {
		if (!pdService.isRunning()) {
			Intent intent = new Intent(PlayActivity.this, PlayActivity.class);
			pdService.startAudio(intent, R.drawable.icon, "SoniCity", "Return to SoniCity.");
		}
	}

	private void loadPatch() throws IOException {
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
	 protected void onDraw(Canvas canvas) {
	 	paint.setColor(Color.BLUE);
	 	paint.setTextSize(36);
	 	canvas.drawText(String.valueOf("OUR LAT IS: " + currentLatitude),  100, 100, paint);
	 	canvas.drawText(String.valueOf("OUR LON IS: " + currentLongitude), 100, 200, paint);
	 	canvas.drawText(String.valueOf("OUR ALT IS: " + currentAltitude),  100, 300, paint);
	 	canvas.drawText(String.valueOf("OUR ACC IS: " + currentAccuracy),  100, 400, paint);
	 	canvas.drawText(String.valueOf("OUR SPE IS: " + currentSpeed),     100, 500, paint);
	 	canvas.drawText(String.valueOf("OUR BEA IS: " + currentBearing),   100, 600, paint);
	 	canvas.drawText(String.valueOf("OUR PRO IS: " + currentProvider),  100, 700, paint);
	 	//invalidate();
	}
	
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
		
		// Services
		initSystemServices();
		bindService(new Intent(this, PdService.class), pdConnection, BIND_AUTO_CREATE);
		
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		Log.v(TAG, " - Exiting From The Play Screen - ");
	}

	@Override
	protected void onResume() {
		super.onResume();
		// GPS
		locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 5, locationListener);
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		unbindService(pdConnection);
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