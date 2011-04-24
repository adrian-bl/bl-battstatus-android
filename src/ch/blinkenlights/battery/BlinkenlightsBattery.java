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
import android.widget.Button;
import android.widget.ImageView;
import android.view.View;
import android.view.View.OnClickListener;
import android.content.ComponentName;

public class BlinkenlightsBattery extends Activity
{
	private Intent bb_service_intent;
	private final BBServiceConnection bb_service_connection = new BBServiceConnection();
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		ComponentName ss_ok;
		
		super.onCreate(savedInstanceState);
		bb_service_intent = new Intent(this, BlinkenlightsBatteryService.class);
		ss_ok = startService(bb_service_intent);
		
		if(ss_ok == null) {
			Log.e("BlinkenBattery", "Ouch! Could not start service!");
			setContentView(R.layout.oops);
		}
		else {
			bindService(bb_service_intent, bb_service_connection, 0);
			setContentView(R.layout.main);
			((Button) findViewById(R.id.hide)).setOnClickListener(cb_hideMview);
			((Button) findViewById(R.id.kill)).setOnClickListener(cb_harakiri);
			((Button) findViewById(R.id.debug)).setOnClickListener(cb_debug);
		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		unbindService(bb_service_connection);
	}

	
	OnClickListener cb_hideMview = new OnClickListener() {
		public void onClick(View v) {
			finish();
		}
	};
	
	OnClickListener cb_harakiri = new OnClickListener() {
		public void onClick(View v) {
			bb_service_connection.bbsvc.harakiri();
			stopService(bb_service_intent);
			finish();
		}
	};
	
	OnClickListener cb_debug = new OnClickListener() {
		public void onClick(View v) {
			bb_service_connection.bbsvc.debug();
		}
	};
	
}
