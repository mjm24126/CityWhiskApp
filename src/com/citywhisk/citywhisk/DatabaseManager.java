package com.citywhisk.citywhisk;

import java.util.ArrayList;

import android.content.Context;

public class DatabaseManager {
	private DatabaseHelper dbHelper;
	
	public DatabaseManager(Context con) {
		dbHelper = new DatabaseHelper(con);
	}

	public DatabaseHelper getDbHelper() {
		return dbHelper;
	}

	public void setDbHelper(DatabaseHelper dbHelper) {
		this.dbHelper = dbHelper;
	}
	
	public ArrayList<String> getSavedItineraries() {
		return dbHelper.getSavedItineraries();
	}

	public Itinerary getSavedItinerary( String name ) {
		return dbHelper.getSavedItinerary(name);
	}
	
	public long insertItinerary( Itinerary it ) {
		return dbHelper.insertItinerary(it);
	}

	public void removeSavedItin(String name) {
		dbHelper.removeSavedItin(name);
		
	}
}
