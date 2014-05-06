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

import android.app.Application;
import android.content.res.Configuration;
import android.util.Log;

public class MyApplication extends Application {
	
	private String androidID;
	private double myDistancePref;
	private int myRatingPref;
	private double myBudgetPref;
	private int myTimePref;
	private double customStartLat;
	private double customStartLon;
	
	private static MyApplication singleton;
	
	public MyApplication getInstance(){
		return singleton;
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}
 
	@Override
	public void onCreate() {
		super.onCreate();
		singleton = this;
		// get and store android id
		setAndroidID(android.provider.Settings.Secure.getString(getContentResolver(),
                android.provider.Settings.Secure.ANDROID_ID));
	}
 
	@Override
	public void onLowMemory() {
		super.onLowMemory();
	}
 
	@Override
	public void onTerminate() {
		super.onTerminate();
	}

	public String getAndroidID() {
		return androidID;
	}

	public void setAndroidID(String androidID) {
		this.androidID = androidID;
	}

	public double getMyDistancePref() {
		return myDistancePref;
	}

	public void setMyDistancePref(double myDistancePref) {
		this.myDistancePref = myDistancePref;
	}

	public int getMyRatingPref() {
		return myRatingPref;
	}

	public void setMyRatingPref(int myRatingPref) {
		this.myRatingPref = myRatingPref;
	}

	public double getMyBudgetPref() {
		return myBudgetPref;
	}

	public void setMyBudgetPref(double myBudgetPref) {
		this.myBudgetPref = myBudgetPref;
	}

	public int getMyTimePref() {
		return myTimePref;
	}

	public void setMyTimePref(int myTimePref) {
		this.myTimePref = myTimePref;
	}

	public double getCustomStartLat() {
		return customStartLat;
	}

	public void setCustomStartLat(double customStartLat) {
		this.customStartLat = customStartLat;
	}

	public double getCustomStartLon() {
		return customStartLon;
	}

	public void setCustomStartLon(double customStartLon) {
		this.customStartLon = customStartLon;
	}
}
