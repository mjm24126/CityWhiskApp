/*
 * This class is responsible for updating the user current location in
 * SharedPreference (user_latitude and user_longitude)
 */
package com.citywhisk.citywhisk;

import com.google.android.gms.maps.GoogleMap;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

public class MyLocationListener implements LocationListener {
	
	LocationManager locationManager;
	GoogleMap googlemap;
	Location myLocation;
	
	public double latitude;
	public double longitude;
	SharedPreferences settings; 
	
	Context context;
	
	public MyLocationListener(GoogleMap g,Context c){
		googlemap = g;
		context = c;
		locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		//locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 50, this);
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 4000, 50,this);
		
		myLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

		if(myLocation==null){
			myLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		}
		latitude = myLocation.getLatitude();
		longitude = myLocation.getLongitude();
	}
	
	public MyLocationListener(Context c){
		context = c;
		locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 50, this);
		
		myLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

		if(myLocation==null){
			myLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		}
		latitude = myLocation.getLatitude();
		longitude = myLocation.getLongitude();
	}
	
	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		latitude = location.getLatitude();
		longitude = location.getLongitude();
		
		
		/*
		 * Update Shared preference
		 */
		settings = context.getSharedPreferences("CITYWHISK", 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString("user_latitude", ""+latitude);
		editor.putString("user_longitude", ""+longitude);
		editor.commit();
		


	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub

	}
	
	/**
     * Stop using GPS listener
     * Calling this function will stop using GPS in your app
     * */
    public void stopUsingGPS(){
        if(locationManager != null){
            locationManager.removeUpdates(MyLocationListener.this);
        }       
    }

    /**
     * Function to get latitude
     * */
    public double getLatitude(){
        if(myLocation != null){
            latitude = myLocation.getLatitude();
        }
         
        // return latitude
        return latitude;
    }
     
    /**
     * Function to get longitude
     * */
    public double getLongitude(){
        if(myLocation != null){
            longitude = myLocation.getLongitude();
        }
         
        // return longitude
        return longitude;
    }
}
