package com.example.pdp5test;

import processing.core.*;

import java.io.File;
import java.io.IOException;

import org.puredata.android.io.AudioParameters;
import org.puredata.android.io.PdAudio;
import org.puredata.core.PdBase;
import org.puredata.core.PdListener;
import org.puredata.android.utils.PdUiDispatcher;
import org.puredata.core.utils.IoUtils;

import android.util.Log;
import android.widget.Toast;


public class ProcessingActivity extends PApplet {
	
	 /* ---- GLOBAL VARS ----*/ 
	 
	 private static final String TAG = "PDP5";
	 private PdUiDispatcher dispatcher;
	 private Toast toast = null;


	 /* ---- PROCESSING ----*/
	 
	 public void setup() {
		 Log.v(TAG, "Setup");
		 background(255, 0, 0);
		 fill(255);
		 textSize(48);
		 text("PDP5", 200, 200);
		 
		// PD
		try {
			initPd();
			loadPatch();
		} catch (IOException e) {
			Log.e(TAG, e.toString());
			finish();
		}
	 }
	 
	 public void draw() {
		 background(255, 0, 0);
		 fill(255);
		 ellipse(mouseX, mouseY, 50, 50);
		 sendX(mouseX);
		 sendY(mouseY);
	 }
		
	 /* ---- PURE DATA ----*/
		
		// Initialize PureData
		private void initPd() throws IOException {
			Log.v(TAG, "Init Pd");
			// Configure the audio glue
			int sampleRate = AudioParameters.suggestSampleRate();
			PdAudio.initAudio(sampleRate, 0, 2, 8, true);
			
			// Dispatcher
			dispatcher = new PdUiDispatcher();
			dispatcher.addListener("XY", new PdListener.Adapter() {
				@Override
				public void receiveFloat(String source, float x) {
					Log.v(TAG, "Received " + x);
				}
			});
			PdBase.setReceiver(dispatcher);
			
		}
		
		private void toast(final String msg) {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					if (toast == null) {
						toast = Toast.makeText(getApplicationContext(), "", Toast.LENGTH_SHORT);
					}
					toast.setText(TAG + ": " + msg);
					toast.show();
				}
			});
		}

		private void post(final String s) {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					//logs.append(s + ((s.endsWith("\n")) ? "" : "\n"));
				}
			});
		}
		
		// Load PureData Patch
		private void loadPatch() throws IOException {
			Log.v(TAG, "Load Patch");
			File dir = getFilesDir();
			IoUtils.extractZipResource(getResources().openRawResource(R.raw.finger), dir, true);
			File patchFile = new File(dir, "finger.pd");
			PdBase.openPatch(patchFile.getAbsolutePath());
		}
		
		// Send X
		public void sendX(int n) {
			//Log.v(TAG, "Sending " + n + " to Pd X" );
			PdBase.sendFloat("MOUSEX", n);
		}
		
		// Send Y
		public void sendY(int n) {
			//Log.v(TAG, "Sending " + n + " to Pd Y" );
			PdBase.sendFloat("MOUSEY", n);
		}

		
		
		/* ---- ANDROID ----*/
		
		// Resume
		@Override
		protected void onResume() {
			super.onResume();
			Log.v(TAG, "onResume");
			PdAudio.startAudio(this);
		}
		
		// Pause
		@Override
		protected void onPause() {
			super.onPause();
			Log.v(TAG, "onPause");
			PdAudio.stopAudio();
		}
		
		// Destroy
		@Override
		public void onDestroy() {
			super.onDestroy();
			Log.v(TAG, "onDestroy");
			PdAudio.release();
			PdBase.release();
		}

}
