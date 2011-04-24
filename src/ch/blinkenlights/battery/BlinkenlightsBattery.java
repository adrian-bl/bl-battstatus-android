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
import android.view.View;
import android.view.View.OnClickListener;
import android.content.ComponentName;

public class BlinkenlightsBattery extends Activity
{
	private Intent bb_service_intent;
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
			setContentView(R.layout.main);
			((Button) findViewById(R.id.hide)).setOnClickListener(cb_hideMview);
			((Button) findViewById(R.id.kill)).setOnClickListener(cb_harakiri);
		}
	}

	OnClickListener cb_hideMview = new OnClickListener() {
		public void onClick(View v) {
			finish();
		}
	};
	
	OnClickListener cb_harakiri = new OnClickListener() {
		public void onClick(View v) {
			finish();
		}
	};
	
}
