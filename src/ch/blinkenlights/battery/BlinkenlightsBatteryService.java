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
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
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
	
	private final static String T = "BlinkenlightsBatteryService.class: ";
	private final IBinder bb_binder = new LocalBinder();
	
	private NotificationManager notify_manager;
	
	@Override
	public IBinder onBind(Intent i) {
		return bb_binder;
	}
	
	public class LocalBinder extends Binder {
		public BlinkenlightsBatteryService getService() {
			return BlinkenlightsBatteryService.this;
		}
	}
	
	
	@Override
	public void onCreate() {
		Log.d(T, "registering receiver");
		notify_manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		registerReceiver(bb_bcreceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
	}
	
	public void onDestory() {
		Log.d(T, "unregistering receiver");
		unregisterReceiver(bb_bcreceiver);
	}
	
	// fixme: need to register
	private final BroadcastReceiver bb_bcreceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			int level = intent.getIntExtra("level", 50);
			int scale = intent.getIntExtra("scale", 100);
			int temp  = intent.getIntExtra("temperature", 0); // can be 0!
			int prcnt = level*100/scale;
			Log.d(T,"current battery percentage at " + prcnt);
			
			Notification this_notify = new Notification(R.drawable.test, "Test text", System.currentTimeMillis());
		}
	};
	
}
