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
package com.citywhisk.citywhisk;


import java.util.ArrayList;
import java.util.Calendar;

import android.app.Activity;
import android.content.Context;

/*
 * Contains current tour ArrayList
 * So that different activity can access it, by inheriting this class
 */
public class BaseActionBar extends Activity {

	//current active itinerary
	public static Itinerary currentItin;
	
	public static int detailIndex;
	

	public static Entity searchEntity;
	public static Entity detailEntity;
	
	public static CustomListView cl;
	
	public static Context activeContext;
	
	public static Calendar itineraryStartTime;
	
	public ArrayList<Itinerary> favoriteItineraries = new ArrayList<Itinerary>();
	public static ArrayList<String> favList = new ArrayList<String>();
	
	
	public ArrayList<Itinerary> getFavoriteItineraries() {
		return favoriteItineraries;
	}
	public void setFavoriteItineraries(ArrayList<Itinerary> favoriteItineraries) {
		this.favoriteItineraries = favoriteItineraries;
	}
	
	public static double getUserLat() {
		return userLat;
	}
	public static void setUserLat(double userLat) {
		BaseActionBar.userLat = userLat;
	}

	public static double getUserLon() {
		return BaseActionBar.userLon;
	}
	public static void setUserLon(double userLon) {
		BaseActionBar.userLon = userLon;
	}

	private static double userLat;
	private static double userLon;
	
}
