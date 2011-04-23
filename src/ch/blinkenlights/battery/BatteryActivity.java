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

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class BatteryActivity extends Activity
{
	private Intent bb_service_intent;
	private final BBServiceConnection bb_service_connection = new BBServiceConnection();

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.v("X", "onCreate - showing main layout - should start service");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		bb_service_intent = new Intent(this, BlinkenlightsBatteryService.class);
		startService(bb_service_intent);
		bindService(bb_service_intent, bb_service_connection, 0);
		
	}
}
