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
 * CalcCustomAddressPath being used to redraw map on change 
 * of starting location (Custom or user location)
 */
package com.citywhisk.citywhisk;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.view.WindowManager.BadTokenException;

public class CalcCustomAddressPath  extends AsyncTask<String, Void, String> {

	ProgressDialog pDialog;
	Context con;
	double lat, lon;
	Itinerary tour;

	CalcDistance calcDistance = new CalcDistance();

	SupportClass supportClassObj = new SupportClass();

	CustomListView cl;

	String distance;
	public CalcCustomAddressPath(Context con, double lat, double lon, Itinerary tour, CustomListView cl) {
		// TODO Auto-generated constructor stub
		this.lat = lat;
		this.lon = lon;
		this.con = con;
		this.tour = tour;
		this.cl = cl;
	}
	protected void onPreExecute() {
		super.onPreExecute();
		//show a progress bar until data has been loaded
		if (pDialog == null) {
        	pDialog = createProgressDialog(con);
        	pDialog.show();
        } else {
            pDialog.show();
        }

	}

	@Override
	protected String doInBackground(String... arg0) {
		double nextLat = Double.parseDouble(tour.itin.get(1).getLatitude());
		double nextlon = Double.parseDouble(tour.itin.get(1).getLongitude());

		String jsonOutput = calcDistance.getDistanceInfo(nextLat, nextlon, lat, lon );
		JSONObject jsonObject;
		try {
			jsonObject = new JSONObject(jsonOutput);
			// routesArray contains ALL routes
			JSONArray routesArray = jsonObject.getJSONArray("routes");
			// Grab the first route
			JSONObject route = routesArray.getJSONObject(0);
			// Take all legs from the route
			JSONArray legs = route.getJSONArray("legs");
			// Grab first leg
			JSONObject leg = legs.getJSONObject(0);

			JSONObject durationObject = leg.getJSONObject("distance");
			distance = durationObject.getString("text");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		tour.itin.get(0).setLatitude(""+lat);
		tour.itin.get(0).setLongitude(""+lon);
		
		//Path are drawn from 2 to 1 OR 3 to 2. So have to update tours[2] to update the path and distance on map
		tour.itin.get(1).setDistance(distance);
		tour.itin.get(1).setJsonOutput(jsonOutput);

		/*
		 * calculate the total distance time and price
		 * and update it
		 */
		supportClassObj.calcTotalDistance(tour);
		supportClassObj.calcTotalPrice(tour);
		supportClassObj.calcTotalTime(tour);
		supportClassObj.calcTotalSavings(tour);

		return null;
		
	}
	
	public static ProgressDialog createProgressDialog(Context mContext) {
        ProgressDialog dialog = new ProgressDialog(mContext);
        try {
                dialog.show();
        } catch (BadTokenException e) {

        }
        dialog.setCancelable(false);
        dialog.setIndeterminate(false);
        dialog.setContentView(R.layout.progressdialog);
        // dialog.setMessage(Message);
        return dialog;
	}


	@Override
	protected void onPostExecute(String result) {
		// TODO Auto-generated method stub
		super.onPostExecute(result);
		cl.notifyDataSetChanged();
		pDialog.dismiss();

	}
}
