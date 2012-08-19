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
import android.text.format.DateFormat;

import java.util.Date;
import java.text.SimpleDateFormat;

public class BlinkenlightsBatteryService extends Service {
	
	private final static String T             = "BlinkenlightsBatteryService.class: ";                                       // Log Token
	private final IBinder bb_binder           = new LocalBinder();
	private int[] battery_state = new int[4];
	
	private NotificationManager notify_manager;
	private ConfigUtil          bconfig;
	
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
		
		bconfig = new ConfigUtil(getApplicationContext());
		
		/* create notification manager stuff and register ourself as a service */
		notify_manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		
		registerReceiver(bb_bcreceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
		Log.d(T,"+++++ onCreate() finished - broadcaster registered +++++");
	}
	
	public void onDestory() {
		unregisterReceiver(bb_bcreceiver);
	}
	
	/* Receives battery_changed events */
	private final BroadcastReceiver bb_bcreceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			int level        = intent.getIntExtra("level", 0);
			int scale        = intent.getIntExtra("scale", 100);
			
			battery_state[0] = level*100/scale;                                    /* capacity percent */
			battery_state[1] = ( intent.getIntExtra("plugged",0) == 0 ? 0 : 1 );   /* plug status      */
			battery_state[2] = intent.getIntExtra("voltage",0);                    /* voltage          */
			battery_state[3] = intent.getIntExtra("temperature", 0);               /* temperature      */
			
			/* trigger update with new values */
			updateNotifyIcon();
		}};
		
	
	public void updateNotifyIcon() {
		Context ctx   = getApplicationContext();
		int prcnt     = battery_state[0];
		int curplug   = battery_state[1];
		int voltage   = battery_state[2];
		int temp      = battery_state[3];
		
		/* TRY to get old values. -1 if failed */
		int oldprcnt  = bconfig.GetPercentage();
		int oldplug   = bconfig.GetPlugStatus();
		int oldts     = bconfig.GetTimestamp();
		
		/* defy (and other stupid-as-heck motorola phones return the capacity in 10% steps.
		   ..but sysfs knows the real 1%-res value */
		if(bconfig.IsMotorola() && bconfig.GetMotorolaPercent() != 0) {
			prcnt = bconfig.GetMotorolaPercent();
		}
		
		/* absolute dummy tests for defy and co: */
		if(prcnt > 100) { prcnt = 100; }
		if(prcnt < 0)   { prcnt = 0;   }
		
		
		/* percentage is now good in any case: check current status */
		
		/* plug changed OR we reached 100 percent */
		if( (curplug != oldplug) || (prcnt == 100) ) {
			Log.d(T, "++ STATUS CHANGE ++: oldplug="+oldplug+", curplug="+curplug+", percentage="+prcnt);
			
			oldprcnt = prcnt;
			oldts    = unixtimeAsInt();
			
			bconfig.SetPlugStatus(curplug);
			bconfig.SetPercentage(prcnt);
			bconfig.SetTimestamp(oldts);
		}
		
		// prepare interface texts
		String vx      = String.valueOf(voltage/1000.0);
		String ntext   = "";
		String ntitle  = ((prcnt == 100 && curplug == 1) ? gtx(R.string.fully_charged) : 
		                  (curplug == 0 ? gtx(R.string.discharging_from)+" "+oldprcnt+"%" : 
		                  gtx(R.string.charging_from)+" "+oldprcnt+"%"));   /* Discharging from 99% */
		String timetxt = getTimeString(oldts);                              /* 12 hours */
		int icon_id    = bconfig.GetIconFor(prcnt, (curplug!=0 && bconfig.ChargeGlow()) );
		
		// set details text
		if(bconfig.ShowDetails()) {
			/* create temperature string in celsius or fareinheit */
			String dgtmp = String.valueOf(temp/10.0)+gtx(R.string.degree)+"C";
			if(bconfig.TempInFahrenheit()) {
				dgtmp = String.valueOf( ( (int)((temp * 1.8)+320) )/10.0 )+gtx(R.string.degree)+"F";
			}
			
			ntext += vx+"V, "+dgtmp;
			ntext += _ICSFILTER_( ", "+gtx(R.string.capacity_at)+" "+prcnt+"%" ); /* capacity is not displayed on ICS */
		}
		else {
			ntext += (voltage == 0 ? "" : gtx(R.string.voltage)+" "+vx+" V");
			ntext += _ICSFILTER_( " // "+gtx(R.string.capacity_at)+" "+prcnt+"%" ); /* same here: no capacity on ICS */
		}
		// end details text
		
		
		if(isICS() == true) {
			ntext = "..."+gtx(R.string.since)+" "+timetxt+" / "+ntext; /* 'since' is located in the text section on ICS */
		}
		else {
			ntitle += " "+gtx(R.string.since)+": "+timetxt; /* add 'since' info to title */
			ntext  += " "+gtx(R.string.since)+":";          /* add 'since' before event TS */
		}
		
		// Log.d(T,"Showing icon for "+prcnt+"% - using icon "+icon_id);
		
		/* create new notify with updated icon: icons are sorted integers :-) */
		Notification this_notify = new Notification(icon_id, null, System.currentTimeMillis());
		this_notify.flags |= Notification.FLAG_ONGOING_EVENT | Notification.FLAG_NO_CLEAR;
		this_notify.setLatestEventInfo(ctx, ntitle, ntext, getConfiguredIntent() );
		notify_manager.notify(0, this_notify);
	}
	
	private final String gtx(int resid) {
		return (String) getResources().getText(resid);
	}
	
	private final int unixtimeAsInt() {
		return (int) (System.currentTimeMillis() / 1000L);
	}
	
	
	/* Returns a 'human friendly' time */
	private String getTimeString(int tstamp) {
		String s     = "";
		int timediff = unixtimeAsInt() - tstamp;
		
		if(timediff > 60*60*2) {
			s = ""+(int)(timediff/60/60)+" "+gtx(R.string.hours);
		}
		else {
			String fmt_style     = (DateFormat.is24HourFormat(getApplicationContext()) ? "HH:mm" : "h:mm aa");
			SimpleDateFormat sdf = new SimpleDateFormat(fmt_style);
			s = ""+sdf.format( new Date( (long)tstamp*1000 ) );
		}
		return s;
	}
	
	/* Return an empty string on ICS */
	private String _ICSFILTER_(String s) {
		if(isICS() == true) { return ""; }
		else                { return s;  }
	}
	
	/* Returns TRUE if we are running on Android 4 */
	private boolean isICS() {
		return (android.os.Build.VERSION.SDK_INT >= 14);
	}
	
	
	public void harakiri() {
		Log.d(T, "terminating myself - unregistering receiver");
		unregisterReceiver(bb_bcreceiver);
		notify_manager.cancelAll();
	}
	
	public void debug() {
		Log.d(T, "+++ dumping status ++++");
	}
	
	public PendingIntent getConfiguredIntent() {
		PendingIntent ret = null;
		if(bconfig.NotifyClickOpensPowerUsage()) {
			ret = PendingIntent.getActivity(this, 0, (new Intent(Intent.ACTION_POWER_USAGE_SUMMARY)), PendingIntent.FLAG_UPDATE_CURRENT);
		}
		else {
			ret = PendingIntent.getActivity(this, 0, (new Intent(this, BlinkenlightsBattery.class)), 0);
		}
		return ret;
	}
	
}
