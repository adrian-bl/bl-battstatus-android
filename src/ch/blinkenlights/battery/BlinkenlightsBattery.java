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
import android.app.AlertDialog;
import android.content.Intent;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.CheckBox;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuInflater;
import android.view.View.OnClickListener;
import android.content.ComponentName;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class BlinkenlightsBattery extends Activity
{
	private Intent bb_service_intent;
	private final BBServiceConnection bb_service_connection = new BBServiceConnection();
	private final static String T = "BlinkenlightsBattery";
	private ConfigUtil bconfig;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		ComponentName ss_ok;
		
		super.onCreate(savedInstanceState);
		
		bb_service_intent = new Intent(getApplicationContext(), BlinkenlightsBatteryService.class);
		ss_ok             = startService(bb_service_intent);
		bconfig           = new ConfigUtil(getApplicationContext());
		
		if(ss_ok == null) {
			Log.e(T, "Ouch! Could not start service!");
			setContentView(R.layout.oops);
		}
		else {
			Log.d(T, "binding to service");
			/* service started up: bind to it and display default dialog */
			bindService(bb_service_intent, bb_service_connection, 0);
			setContentView(R.layout.main);
		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		unbindService(bb_service_connection);
		Log.v(T, "++ on destroy finished ++");
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		finish(); // kill this activity if not visible anymore (no need to keep multiple instances open)
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.opts_menu, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.mact_exit:
				bb_service_connection.bbsvc.harakiri(); /* remove notification */
				stopService(bb_service_intent);         /* stop service        */
				finish();                               /* kill ourself        */
				return true;
			case R.id.mact_about:
				showAbout();
				return true;
			case R.id.mact_settings:
				setContentView(R.layout.config);
				initConfigDialog();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}
	
	public void initConfigDialog() {
		CheckBox details = (CheckBox)findViewById(R.id.cb_config_details);
		/* set checkboxes from config */
		details.setChecked(bconfig.ShowDetails());
		
		/* add callbacks */		
		details.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					bconfig.SetShowDetails(isChecked);
					bb_service_connection.bbsvc.updateNotifyIcon();
				}
		});
		
	}
	
	
	private void showAbout() {
		AlertDialog alertDialog = new AlertDialog.Builder(this).create();
		alertDialog.setTitle("About");
		alertDialog.setMessage("Battery Circle "+getResources().getText(R.string.app_vers)+"\n(C) 2011 Adrian Ulrich");
		alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				return;
			} }); 
		alertDialog.show();
	}
	
}
