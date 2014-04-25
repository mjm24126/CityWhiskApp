/*
 * This class is responsible for checking of GooglePlayService on User phone
 */
package com.citywhisk.citywhisk;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
public class CheckGooglePlay {
	
	Context con;
	
	public CheckGooglePlay(Context c){
		this.con = c;
	}
	
	/* 
	 * checks for the the availability of
	 * google play service in phone
	 * closes the app, if unavailable 
	 */
	public boolean checkGooglePlayService() {
		// TODO Auto-generated method stub
		int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(con);

		// If Google Play services is available
		if (ConnectionResult.SUCCESS == resultCode) {
			// In debug mode, log the status
			//Log.i("Location Updates",
					//"Google Play services is available.");
			
			return true;
		}
		else {
			//Log.i("connection result ",""+resultCode);
			Dialog dialog = GooglePlayServicesUtil.getErrorDialog(resultCode,(Activity) con, 69);
			dialog.show();
			/*AlertDialog.Builder builder = new AlertDialog.Builder(con);
			builder.setMessage("You must have Google Play service installed");
			builder.setCancelable(false);
			builder.setPositiveButton("Okay", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub	
					((Activity)con).finish();
				}
			});
			builder.show();*/
			
			return false;

		}
	}

}
