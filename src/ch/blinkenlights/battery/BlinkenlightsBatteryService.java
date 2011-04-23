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

import java.io.FileInputStream;
import java.io.DataInputStream;
import java.io.BufferedInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.File;
import java.io.InputStream;

public class BlinkenlightsBatteryService extends Service {
	
	private final static String T = "BlinkenlightsBatteryService.class: ";
	private final IBinder bb_binder = new LocalBinder();
	
	private NotificationManager notify_manager;
	private Intent              notify_intent;
	private PendingIntent       notify_pintent;
	
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
		notify_intent  = new Intent(this, BlinkenlightsBattery.class);
		notify_pintent = PendingIntent.getActivity(this, 0, notify_intent, 0);
		
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
			int level   = intent.getIntExtra("level", 0);
			int scale   = intent.getIntExtra("scale", 100);
			int temp    = intent.getIntExtra("temperature", 0);
			int plugged = intent.getIntExtra("plugged",0);
			int voltage = intent.getIntExtra("voltage",0);
			int prcnt   = level*100/scale;
			
			File moto_prcnt = new File("/sys/devices/platform/cpcap_battery/power_supply/battery/charge_counter");
			try {
				String              foo = "";
				FileInputStream     fis = new FileInputStream(moto_prcnt);
				BufferedInputStream bis = new BufferedInputStream(fis);
				DataInputStream     dis = new DataInputStream(bis);
				foo   = dis.readLine();
				prcnt = Integer.valueOf(foo).intValue();
				
				dis.close();
				bis.close();
				fis.close();
			}
			catch(Exception e) {
				Log.d(T,"Exception: "+e);
			}
			
			String ntitle = (plugged == 0 ? "On Battery since" : "Connected since") + " ????";
			String ntext  = prcnt+"%  voltage: "+(voltage==0? "??" : voltage);
			
			Log.d(T,"current battery percentage at " + prcnt);
			Log.d(T,"voltage is "+voltage);
			
			Notification this_notify = new Notification(R.drawable.test, null, System.currentTimeMillis());
			this_notify.flags |= Notification.FLAG_ONGOING_EVENT | Notification.FLAG_NO_CLEAR;
			this_notify.setLatestEventInfo(getApplicationContext(), ntitle, ntext, notify_pintent);
			notify_manager.notify(0, this_notify);
		}
	};
	
}
