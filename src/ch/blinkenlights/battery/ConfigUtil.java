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


import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.DataInputStream;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.File;
import java.io.InputStream;
import android.util.Log;
import android.content.Context;

public class ConfigUtil {
	
	private final static String T             = "ConfigUtil";
	private final static String FN_PERCENTAGE = "blb-percentage"; // File to store percentage
	private final static String FN_PLUGGED    = "blb-plugstatus"; // File to store plugstatus
	private final static String FN_TIMESTAMP  = "blb-ts";         // Latest event timestamp
	private final static String FN_THEMEID    = "blb-theme";      // theme to use
	private final static String FN_C_DETAILS  = "c_show_details";
	private final static String FN_C_FENHEIT  = "c_fahrenheit";
	private final static String FN_C_NTFYCLK  = "c_notify_click";
	private final static String FN_C_CHRGNTFY = "c_charge_glowstate";
	private final static String motofile      = "/sys/devices/platform/cpcap_battery/power_supply/battery/charge_counter";   // Motorola-Percentage file
	private Context             pCTX;
	
	public ConfigUtil(Context what) {
		pCTX = what;
	}
	
	public boolean ChargeGlow() {
		return !(ConfOptionIsSet(FN_C_CHRGNTFY));
	}
	
	public void SetChargeGlow(boolean state) {
		ConfigToggle(FN_C_CHRGNTFY, !state);
	}
	
	public boolean ShowDetails() {
		return (ConfOptionIsSet(FN_C_DETAILS));
	}

	public void SetShowDetails(boolean state) {
		ConfigToggle(FN_C_DETAILS, state);
	}
	
	
	public boolean TempInFahrenheit() {
		return (ConfOptionIsSet(FN_C_FENHEIT));
	}
	
	public void SetTempInFahrenheit(boolean state) {
		ConfigToggle(FN_C_FENHEIT, state);
	}
	
	
	public boolean NotifyClickOpensPowerUsage() {
		return (ConfOptionIsSet(FN_C_NTFYCLK));
	}
	
	public void SetNotifyClickOpensPowerUsage(boolean state) {
		ConfigToggle(FN_C_NTFYCLK, state);
	}
	
	
	public boolean IsMotorola() {
		return (new File(motofile)).exists();
	}
	
	public int GetMotorolaPercent() {
		return pathToInt(motofile);
	}
	
	public int GetPlugStatus() {
		return tryRead(FN_PLUGGED);
	}
	
	public void SetPlugStatus(int what) {
		tryWrite(FN_PLUGGED,what);
	}
	
	public int GetIconFor(int prcnt, boolean plugged) {
		int setting = GetThemeId();
		
		if(plugged && setting != 6) {
			/* Calculate 'glow' theme if we are on AC
			** and NOT using theme 6 (= circle only) */
			
			if(setting >= 3) {
				setting -= 3; /* black -> white */
			}
			setting -= 3;   /* white -> golden */
		}
		
		switch(setting) {
			/* plugged themes */
			case -3:
				return (R.drawable.gfb_cr_h_000 + prcnt);    /* big golden font */
			case -2:
				return (R.drawable.gfs_cr_h_000 + prcnt);    /* small golden font */
			case -1:
				return (R.drawable.gfb_000 + prcnt);        /* golden font */
			
			/* unplugged themes */
			case 0:
				return (R.drawable.wfb_cr_h_000 + prcnt);   /* white, big font */
			case 1:
				return (R.drawable.wfs_cr_h_000 + prcnt);   /* white, small font */
			case 2:
				return (R.drawable.wfb_000 + prcnt);        /* white big font only */
			case 3:
				return (R.drawable.bfb_cr_h_000 + prcnt);   /* black, big font */
			case 4:
				return (R.drawable.bfs_cr_h_000 + prcnt);   /* black, small font */
			case 5:
				return (R.drawable.bfb_000 + prcnt);        /* black big font only */
			case 6:
				return (R.drawable.cr_h_000 + prcnt);       /* circle only */
			default:
				return 0; // ouch!
		}
	}
	
	public void SetThemeId(int theme) {
		tryWrite(FN_THEMEID, theme);
	}
	
	public int GetThemeId() {
		int theme = tryRead(FN_THEMEID);
		
		if(theme < 0 || theme > 6) {
			Log.v(T,"setting default theme, was: "+theme);
			theme = 0;
			SetThemeId(theme);
		}
		
		return theme;
	}
	
	public int GetPercentage() {
		return tryRead(FN_PERCENTAGE);
	}
	
	public void SetPercentage(int what) {
		tryWrite(FN_PERCENTAGE,what);
	}
	
	
	public int GetTimestamp() {
		return tryRead(FN_TIMESTAMP);
	}
	
	public void SetTimestamp(int what) {
		tryWrite(FN_TIMESTAMP,what);
	}
	
	
	/* write integer to config dir */
	private final void tryWrite(String storage_name, int value) {
		try {
			String outdata = value+"\n";
			FileOutputStream fos = pCTX.openFileOutput(storage_name, pCTX.MODE_PRIVATE);
			BufferedOutputStream bos = new BufferedOutputStream(fos, 512);
			bos.write(outdata.getBytes());
			bos.close();
			fos.close();
		} catch(Exception e) { Log.v(T, "tryWrite: "+e); }
	}
	
	/* read integer from config dir */
	private final int tryRead(String storage_name) {
		return pathToInt(pCTX.getFilesDir()+"/"+storage_name);
	}
	
	/* Read absolute_path and return contents as an integer */
	private final int pathToInt(String absolute_path) {
		int result = -1;
		
		try {
			String foo;
			FileInputStream fis     = new FileInputStream(absolute_path);
			BufferedInputStream bis = new BufferedInputStream(fis, 512);
			DataInputStream     dis = new DataInputStream(bis);
			foo   = dis.readLine();
			dis.close();
			bis.close();
			fis.close();
			result = Integer.valueOf(foo).intValue();
		} catch(Exception e) { Log.v(T,"pathToInit: "+e); }
		return result;
	}
	
	private boolean ConfOptionIsSet(String fid) {
		return (new File(pCTX.getFilesDir()+"/"+fid)).exists();
	}
	
	private void ConfigToggle(String fid, boolean state) {
		if(state == true) {
			tryWrite(fid, 0);
		}
		else {
			(new File(pCTX.getFilesDir()+"/"+fid)).delete();
		}
	}
	
}
