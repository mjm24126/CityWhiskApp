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
