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
