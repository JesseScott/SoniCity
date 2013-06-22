package com.example.pdp5test;

import processing.core.*;

import java.io.File;
import java.io.IOException;

import org.puredata.android.io.AudioParameters;
import org.puredata.android.io.PdAudio;
import org.puredata.core.PdBase;
import org.puredata.core.utils.IoUtils;

import android.util.Log;


public class ProcessingActivity extends PApplet {
	
	 /* ---- GLOBAL VARS ----*/ 
	 
	 private static final String TAG = "PDP5";
	 
	 // PureData to processing:
	 PureDataActivity plugIt = new PureDataActivity();
	 
	 /* ---- PROCESSING ----*/
	 
	 public void setup()  {
		 Log.v(TAG, "Setup");
		 // Load Pd patch file
		try {
			initPd();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 
		 // Routine processing setup things:
		 background(255, 0, 0);
		 fill(255);
		 textSize(48);
		 text("PDP5", 200, 200);
	 }	 
	 
	 	//All the fun Processing stuff 
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
			
		 int sampleRate = AudioParameters.suggestSampleRate();
		 try {
			PdAudio.initAudio(sampleRate, 0, 2, 8, true);
			Log.v(TAG, "--> INIT AUDIO");
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		 
		 // Start audio in the context of PdActivity (plugIt)
		 PdAudio.startAudio(plugIt);
		 try {
				loadPatch();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
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
			PdBase.sendFloat("MX", n);
		}
		
		// Send Y
		public void sendY(int n) {
			//Log.v(TAG, "Sending " + n + " to Pd Y" );
			PdBase.sendFloat("MY", n);
		}
	
		
		/* ---- ANDROID ----*/

		// Resume
		@Override
		protected void onResume() {
			super.onResume();
			Log.v(TAG, "onResume");
			PdAudio.startAudio(plugIt);
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
