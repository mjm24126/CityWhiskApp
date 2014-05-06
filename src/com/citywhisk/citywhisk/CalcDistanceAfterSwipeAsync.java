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
 * This class is used to calculate distance between two destination outside Async thread in Activeitineray.java
 */
package com.citywhisk.citywhisk;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;

public class CalcDistanceAfterSwipeAsync extends AsyncTask<String, Void, String>{

	SupportClass supportClassObj = new SupportClass();

	Context con;

	Entity newDest;

	CalcDistance calcDistance = new CalcDistance();

	Itinerary itin;

	CustomListView cl;

	int pos;

	String jsonOutput1;
	String jsonOutput2;
	
	String newjsonOutput1;
	String newjsonOutput2;

	String distance1;
	String distance2;
	
	String newdistance1;
	String newdistance2;
	
	String duration;
	
	public CalcDistanceAfterSwipeAsync(Context con,Entity newDes, Itinerary it, int pos, CustomListView cl,Boolean lastFlag){
		this.con = con;
		this.newDest = newDes;
		this.cl = cl;
		this.itin = it;
		this.pos = pos;

	}

	protected void onPreExecute() {
		super.onPreExecute();
	}

	/*
	 * (non-Javadoc)
	 * @see android.os.AsyncTask#doInBackground(Params[])
	 * Calculate distance from google API
	 */
	
	//swiping the ititnerary will effect two distances
	@Override
	protected String doInBackground(String... arg0) {
		
		double newDestLat = Double.parseDouble(newDest.getLatitude());
		double newDestLon = Double.parseDouble(newDest.getLongitude());

		/*
		 * Destination last to the new destination
		 */
		
		double previousDestinationLat = Double.parseDouble(itin.itin.get(pos-1).getLatitude());
		double previousDestinationLon = Double.parseDouble(itin.itin.get(pos-1).getLongitude());

		/*
		 * get Direction info for new destination and destination before it
		 */
		
		newjsonOutput1 = calcDistance.getDistanceInfo(newDestLat, newDestLon, previousDestinationLat, previousDestinationLon);
		
		JSONObject jsonObject;
		try {
			//jsonObject = new JSONObject(jsonOutput1);
			jsonObject = new JSONObject(newjsonOutput1);
			// routesArray contains ALL routes
			JSONArray routesArray = jsonObject.getJSONArray("routes");
			// Grab the first route
			JSONObject route = routesArray.getJSONObject(0);
			// Take all legs from the route
			JSONArray legs = route.getJSONArray("legs");
			// Grab first leg
			JSONObject leg = legs.getJSONObject(0);

			JSONObject distanceObject = leg.getJSONObject("distance");
			newDest.setDistance(distanceObject.getString("text"));
			
			JSONObject durationObject = leg.getJSONObject("duration");
			newDest.setDuration(durationObject.getString("text"));
			
			JSONObject poObject = route.getJSONObject("overview_polyline");
			itin.getPolyline().set(pos-1,poObject.getString("points"));
			
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		/*
		 * Set distance and jSonoutput for new Location
		 */
		
		
		
		//Do not run this part if swiped part is the last destination
		if(pos < (itin.itin.size()-1))
		{
			/*
			 * Destination next to the new destination
			 */
			
			double nextDestinationLat = Double.parseDouble(itin.itin.get(pos + 1).getLatitude());
			double nextDestinationLon = Double.parseDouble(itin.itin.get(pos + 1).getLongitude());
			
			/*
			 * get Direction info for new destination and destination after it
			 */
			newjsonOutput2 = calcDistance.getDistanceInfo(nextDestinationLat, nextDestinationLon,newDestLat, newDestLon);

			try {
				jsonObject = new JSONObject(newjsonOutput2);
				// routesArray contains ALL routes
				JSONArray routesArray = jsonObject.getJSONArray("routes");
				// Grab the first route
				JSONObject route = routesArray.getJSONObject(0);
				// Take all legs from the route
				JSONArray legs = route.getJSONArray("legs");
				// Grab first leg
				JSONObject leg = legs.getJSONObject(0);

				JSONObject distanceObject = leg.getJSONObject("distance");
				itin.itin.get(pos+1).setDistance(distanceObject.getString("text"));

				JSONObject durationObject = leg.getJSONObject("duration");
				itin.itin.get(pos+1).setDuration(durationObject.getString("text"));
				
				JSONObject poObject = route.getJSONObject("overview_polyline");
				itin.getPolyline().set(pos,poObject.getString("points"));
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
	 * Updates the CustomListview
	 */
	@Override
	protected void onPostExecute(String result) {
		// TODO Auto-generated method stub
		super.onPostExecute(result);

		/*
		 * Recreate the ListView
		 */
		cl = new CustomListView((MainPage) con, R.layout.customlist, itin.itin, itin);
		((MainPage) con).lV.setAdapter(cl);
		cl.notifyDataSetChanged();
		
	}


}
