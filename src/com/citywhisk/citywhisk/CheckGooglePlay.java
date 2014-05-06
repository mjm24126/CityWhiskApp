/*
 * Copyright 2014 CityWhisk
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
