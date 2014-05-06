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

/*Rework on map after Listview drag event
 */
package com.citywhisk.citywhisk;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

public class CalcDistanceAfterDragAsync  extends AsyncTask<String, Void, String> {
	
	
	CalcDistance calcDistanceObj = new CalcDistance();
	
	SupportClass supportClassObj = new SupportClass();
	
	Context con;
	
	CustomListView cl;
	
	double lastLat = 0.0;
	double lastLon = 0.0;
	
	Itinerary tour;
	JSONObject jsonObject;
	 
	public CalcDistanceAfterDragAsync(Itinerary tour, Context con, CustomListView cl) {
		
		this.tour = tour;
		this.con = con;
		this.cl = cl;
	}
	
	protected void onPreExecute() {
		super.onPreExecute();

	}
	
	
	@Override
	protected String doInBackground(String... arg0) {
		tour.setTotalDistance(0);
		tour.clearPoly();
		for( int i=0; i<tour.itin.size() - 1; i++ ){
			String jsonOutput = null;
			String distance=null;
			double lat1 = Double.parseDouble(tour.itin.get(i).getLatitude());
			double lon1 = Double.parseDouble(tour.itin.get(i).getLongitude());
			double lat2 = Double.parseDouble(tour.itin.get(i+1).getLatitude());
			double lon2 = Double.parseDouble(tour.itin.get(i+1).getLongitude());
			String jsonString = calcDistanceObj.getDistanceInfo(lat1,lon1,lat2,lon2);
			try {
				jsonObject = new JSONObject(jsonString);
				// routesArray contains ALL routes
				JSONArray routesArray = jsonObject.getJSONArray("routes");
				// Grab the first route
				JSONObject route = routesArray.getJSONObject(0);
				// Take all legs from the route
				JSONArray legs = route.getJSONArray("legs");
				// Grab first leg
				JSONObject leg = legs.getJSONObject(0);

				JSONObject distanceObject = leg.getJSONObject("distance");
				distance = distanceObject.getString("text");
				tour.itin.get(i+1).setDistance(distance);
				if( distance.substring(distance.length()-2).equals("ft") ){
					tour.setTotalDistance(tour.getTotalDistance() + (Double.parseDouble(distance.substring(0, distance.length()-3))/5280));
				}
				else{
					tour.setTotalDistance(tour.getTotalDistance() + Double.parseDouble(distance.substring(0, distance.length()-3)));
				}
				JSONObject durationObject = leg.getJSONObject("duration");
				tour.itin.get(i+1).setDuration( durationObject.getString("text") );
				
				JSONObject poObject = route.getJSONObject("overview_polyline");
				tour.addPoly(poObject.getString("points"));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return null;
	}
	
	@Override
	protected void onPostExecute(String result) {
		super.onPostExecute(result);
		cl = new CustomListView((MainPage) con, R.layout.customlist, tour.itin, tour);
		((MainPage) con).lV.setAdapter(cl);
		cl.notifyDataSetChanged();
	}

}
