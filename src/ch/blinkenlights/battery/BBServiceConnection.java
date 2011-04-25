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

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;

public class BBServiceConnection implements ServiceConnection {
	public BlinkenlightsBatteryService bbsvc;

	public void onServiceConnected(ComponentName name, IBinder service) {
		bbsvc = ((BlinkenlightsBatteryService.LocalBinder) service).getService();
	}
	
	public void onServiceDisconnected(ComponentName name) {
		bbsvc = null;
	}
}

