/*
 * This class is responsible for network detection,
 * if no network is available application is not started
 */
package com.citywhisk.citywhisk;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;


//called at the start of the app to ensure Network connectivity
public class NetworkDetection {

	private Context _context;

	public NetworkDetection(Context context){
		this._context = context;
	}

	public boolean isConnectingToInternet(){
		ConnectivityManager connectivity = (ConnectivityManager) _context.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity != null) 
		{
			NetworkInfo[] info = connectivity.getAllNetworkInfo();
			if (info != null) 
				for (int i = 0; i < info.length; i++) 
					if (info[i].getState() == NetworkInfo.State.CONNECTED)
					{
						return true;
					}

		}
		return false;
	}

	public void showAlertDialog(Context context, String title, String message, Boolean status) {

		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setMessage(message);
		builder.setTitle(title);
		builder.setPositiveButton("Okay", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				dialog.cancel();

			}
		});
		//alertDialog.setIcon((status) ? R.drawable.success : R.drawable.fail); use this inside the fucntion to set image

		// Showing Alert Message
		builder.show();
	}
}
