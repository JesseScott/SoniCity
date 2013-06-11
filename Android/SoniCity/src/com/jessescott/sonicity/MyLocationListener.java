package com.jessescott.sonicity;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;

/* PLAYACTIVITY CLASS */
public class MyLocationListener implements LocationListener {

  // GLOBALS
  private static final String TAG = "SoniCity";
  float currentLatitude  	= 0;
  float currentLongitude 	= 0;
  float currentAltitude 	= 0;
  float currentAccuracy  	= 0;
  float currentSpeed 		= 0;
  float currentBearing 	= 0;
  String currentProvider 	= "";
  
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
    Log.v(TAG, "Lat is " + location.getLatitude());
    Log.v(TAG, "Lon is " + location.getLongitude());
    
    // Send To Pd
    //sendLat(currentLatitude);
    //sendLon(currentLongitude);
  }

  public void onProviderDisabled (String provider) { 
    //currentProvider = "";
	Log.v(TAG, "Provider is " + provider);
  }

  public void onProviderEnabled (String provider) { 
    //currentProvider = provider;
	Log.v(TAG, "Provider is " + provider);
  }

  public void onStatusChanged (String provider, int status, Bundle extras) {
	Log.v(TAG, "Status is " + status);
  }
  
  // Custom Methods
  
  public String getCurrentLatitude() {
	  String lat = Float.toString(currentLatitude);
	  return lat;
  }
  
  public String getCurrentLongitude() {
	  String lon = Float.toString(currentLongitude);
	  return lon;
  }
  
} /* */
