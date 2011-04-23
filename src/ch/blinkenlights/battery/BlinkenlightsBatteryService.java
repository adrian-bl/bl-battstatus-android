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

import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.DataInputStream;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.File;
import java.io.InputStream;

public class BlinkenlightsBatteryService extends Service {
	
	private final static String T             = "BlinkenlightsBatteryService.class: ";                                       // Log Token
	private final static String FN_PERCENTAGE = "blb-percentage";                                                            // File to store percentage
	private final static String FN_PLUGGED    = "blb-plugstatus";                                                            // File to store plugstatus
	private final static String FN_TIMESTAMP  = "blb-ts";                                                                    // Latest event timestamp
	private final static String motofile      = "/sys/devices/platform/cpcap_battery/power_supply/battery/charge_counter";   // Motorola-Percentage file
	private final IBinder bb_binder           = new LocalBinder();
	private final static int first_icn        = R.drawable.r000;                                                             // First icon ID
	
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
		notify_manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		notify_intent  = new Intent(this, BlinkenlightsBattery.class);
		notify_pintent = PendingIntent.getActivity(this, 0, notify_intent, 0);
		
		registerReceiver(bb_bcreceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
	}
	
	public void onDestory() {
		unregisterReceiver(bb_bcreceiver);
	}
	
	/* Receives battery_changed events */
	private final BroadcastReceiver bb_bcreceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			int level     = intent.getIntExtra("level", 0);
			int scale     = intent.getIntExtra("scale", 100);
			int temp      = intent.getIntExtra("temperature", 0);
			int voltage   = intent.getIntExtra("voltage",0);
			int curplug   = ( intent.getIntExtra("plugged",0) == 0 ? 0 : 1 );
			int prcnt     = level*100/scale;
			
			/* TRY to get old values. -1 if failed */
			int oldprcnt  = tryRead(FN_PERCENTAGE);
			int oldplug   = tryRead(FN_PLUGGED);
			int oldts     = tryRead(FN_TIMESTAMP);
			
			/* defy (and other stupid-as-heck motorola phones return the capacity in 10% steps.
			   ..but sysfs knows the real 1%-res value */
			   //FIXME: How 'heavy' is new File... ? we could do this check at startup and set a bool
			if((new File(motofile)).exists()) {
				int xresult = pathToInt(motofile);
				if(xresult >= 0) { // only use value if read didn't fail. ;-)
					prcnt = xresult;
				}
			}
			
			/* percentage is now good in any case: check current status */
			
			/* plug changed OR we reached 100 percent */
			if( (curplug != oldplug) || (prcnt == 100) ) {
				Log.v(T, "++++ STATUS CHANGE +++++ FROM "+oldplug+" TO "+curplug);
				oldprcnt = prcnt;
				oldts    = unixtimeAsInt();
				
				tryWrite(FN_PLUGGED, curplug);
				tryWrite(FN_PERCENTAGE, prcnt);
				tryWrite(FN_TIMESTAMP, oldts);
			}
			
			// prepare interface texts
			String vx     = String.valueOf(voltage/1000.0);
			String ntext  = "" + (voltage == 0 ? "" : "voltage: "+vx+"V ");
			String ntitle = (curplug == 0 ? "Discharging" : "Charging")+" from "+oldprcnt+"%";
			
			
			if(prcnt == 100 && curplug != 0) {
				ntitle = "Fully charged since FIXME";
			}
			else {
				int timediff = unixtimeAsInt() - oldts;
				if(timediff >= 60*60) {
					ntitle += " since "+(timediff/60/60)+" Hour";
					if(timediff >= 60*60*2) {
						ntitle += "s";
					}
				}
				else if(timediff >= 120) {
					ntitle += " since "+(timediff/60)+" Minutes";
				}
			}
			
			
			/* create new notify with updated icon: icons are sorted integers :-) */
			Notification this_notify = new Notification((first_icn+prcnt), null, System.currentTimeMillis());
			this_notify.flags |= Notification.FLAG_ONGOING_EVENT | Notification.FLAG_NO_CLEAR;
			this_notify.setLatestEventInfo(getApplicationContext(), ntitle, ntext, notify_pintent);
			notify_manager.notify(0, this_notify);
			
			
		}
	};
	
	private final void tryWrite(String storage_name, int value) {
		try {
			String outdata = value+"\n";
			FileOutputStream fos = openFileOutput(storage_name, Context.MODE_PRIVATE);
			BufferedOutputStream bos = new BufferedOutputStream(fos);
			bos.write(outdata.getBytes());
			bos.close();
			fos.close();
		} catch(Exception e) { Log.v(T, "tryWrite: "+e); }
	}
	
	private final int tryRead(String storage_name) {
		return pathToInt(getFilesDir()+"/"+storage_name);
	}
	
	private final int pathToInt(String absolute_path) {
		int result = -1;
		
		try {
			String foo;
			FileInputStream fis     = new FileInputStream(absolute_path);
			BufferedInputStream bis = new BufferedInputStream(fis);
			DataInputStream     dis = new DataInputStream(bis);
			foo   = dis.readLine();
			dis.close();
			bis.close();
			fis.close();
			result = Integer.valueOf(foo).intValue();
		} catch(Exception e) { Log.v(T,"pathToInit: "+e); }
		return result;
	}
	
	private final int unixtimeAsInt() {
		return (int) (System.currentTimeMillis() / 1000L);
	}
}
