/*******************************************************
 *
 * Part of ch.blinkenlights.battery
 *
 * (C) 2011 Adrian Ulrich
 *
 * Licensed under the GPLv2
 *
 *******************************************************/
package ch.blinkenlights.battery;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.content.Intent;

public class BlinkenlightsBatteryService extends Service {
	
	private final static String T = "BBService";
	private final IBinder bb_binder = new LocalBinder();
	
	@Override
	public IBinder onBind(Intent i) {
		Log.v(T,"Returning binder");
		return bb_binder;
	}
	
	public class LocalBinder extends Binder {
		public BlinkenlightsBatteryService getService() {
			return BlinkenlightsBatteryService.this;
		}
	}

	
	@Override
	public void onCreate() {
		Log.v(T, "onCreate called - should startup service");
		registerReceiver(bb_bcreceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
	}
	
	public void onDestory() {
		Log.v(T, "onDestory - removing receiver");
		unregisterReceiver(bb_bcreceiver);
	}
	
	// fixme: need to register
	private final BroadcastReceiver bb_bcreceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			int level = intent.getIntExtra("level", 50);
			int scale = intent.getIntExtra("scale", 100);
			int prcnt = level*100/scale;
			Log.v(T," *** RECEIVED *** " + prcnt);
		}
	};
	
}
