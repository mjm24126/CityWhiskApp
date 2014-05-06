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

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.mobeta.android.dslv.DragSortListView;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LevelListDrawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.Settings;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

public class FragMap extends Fragment {

	
	protected GoogleMap googlemap;
	private ArrayList<Marker> markers;
	private Itinerary itinerary;
	private SupportClass supportClassObj;
	private ArrayList<String> jsonOutputList;
	private static View view;
	
	private MainPage parentActivity;
	private MyLocationListener locationListener;
	private LocationManager locationManager;
	
	public double latitude_user;
	public double longitude_user;
	
	private static final long SECONDS_PER_HOUR = 60;
    private static final long MILLISECONDS_PER_SECOND = 1000;
    private static final long GEOFENCE_EXPIRATION_IN_HOURS = 6;
    private static final long GEOFENCE_EXPIRATION_TIME =
            GEOFENCE_EXPIRATION_IN_HOURS *
            SECONDS_PER_HOUR *
            MILLISECONDS_PER_SECOND;
	
	public boolean itinStarted;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceSate){
		if (view != null) {
	        ViewGroup parent = (ViewGroup) view.getParent();
	        if (parent != null)
	            parent.removeView(view);
	    }
	    try {	
	        view = inflater.inflate(R.layout.fragmap, container, false);
	    } catch (InflateException e) {
	        // map is already there, just return view as it is 
	    }
	    
	    parentActivity = (MainPage) getActivity();
		googlemap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
		setUpMap();
		itinerary = parentActivity.getItinerary();
		supportClassObj = new SupportClass();
		
		
		setMapButtonBar();
		
		itinStarted = false;
		
		generateMarkers();
	    return view;
	}
	
	@Override
	public void onStart() {
		super.onStart();
		googlemap.setMyLocationEnabled(true);
	}
	
	public void onStop(){
		super .onStop();
		googlemap.setMyLocationEnabled(false);
	}
	
	/*
	 * Generate the markers for the map
	 * And calls generateRouteLine() to draw path on map
	 * 
	 */
	public void generateMarkers() {
		googlemap.clear();
		googlemap.setMyLocationEnabled(true);
		//generateRouteLine();
		double itinerary_latitude;
		double itinerary_longitude;
		String itinerary_name;

		LatLng latlng;
		markers = new ArrayList<Marker>();

		//zoom out to make sure all markers are displayed in screen
		LatLngBounds.Builder builder = new LatLngBounds.Builder(); 
		builder.include(new LatLng(parentActivity.getUserLat(),parentActivity.getUserLon()));


		for( int x=parentActivity.getItinerary().itin.size(); x > 0; x-- ){
			Entity obj = parentActivity.getItinerary().itin.get(x-1);
			itinerary_latitude = Double.parseDouble( obj.getLatitude() );
			itinerary_longitude = Double.parseDouble( obj.getLongitude() );
			itinerary_name = obj.getName();
			latlng = new LatLng(itinerary_latitude, itinerary_longitude);

			int markerImage = supportClassObj.chooseMarkerImage(x);
			
			Marker marker = googlemap.addMarker(new MarkerOptions()
				.position(latlng)
				.snippet("Navigate")
				.title(itinerary_name)
				.icon(BitmapDescriptorFactory.fromResource(markerImage)));
			
			marker.showInfoWindow();
			marker.hideInfoWindow();

			markers.add(marker);

			//setting bounds for googlemap
			builder.include(latlng);  
		}
		
		googlemap.setOnMarkerClickListener(new OnMarkerClickListener(){
			@Override
			public boolean onMarkerClick(Marker marker) {
				
				return false;
			}
			
		});
		
		for( String leg : parentActivity.getItinerary().getPolyline() ){
			handleGetDirectionsResult(decodePoly(leg));
		}
		
		// multipliers for height based off percentage of screen size map will use
		int width = this.getResources().getDisplayMetrics().widthPixels;
		int padding = (int) (width * .145);
		googlemap.moveCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 
                width, 
                (int)(this.getResources().getDisplayMetrics().heightPixels * 0.8 * 0.6 * 0.8), padding));
		
	}
	
	private ArrayList<LatLng> decodePoly(String encoded) {
        ArrayList<LatLng> poly = new ArrayList<LatLng>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;
        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;
            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng position = new LatLng((double) lat / 1E5, (double) lng / 1E5);
            poly.add(position);
        }
        return poly;
    }

	public void handleGetDirectionsResult(ArrayList<LatLng> directionPoints) {
		PolylineOptions rectLine = new PolylineOptions().width(5).color(Color.RED);
		//Log.i("dir points size", String.valueOf(directionPoints.size()));
		for(int i = 0 ; i < directionPoints.size() ; i++) 
		{          
			rectLine.add(directionPoints.get(i));
		}
		googlemap.addPolyline(rectLine);
		
	}

	public void setUpMap()
	{
		locationListener = new MyLocationListener(googlemap,parentActivity);
		
		googlemap.setMyLocationEnabled(true);	
		googlemap.setOnMyLocationButtonClickListener(new OnMyLocationButtonClickListener(){

			@Override
			public boolean onMyLocationButtonClick() {
				// TODO Auto-generated method stub
				supportClassObj.fitInMap(parentActivity.getItinerary(), googlemap, parentActivity);
				return true;
			}
			
		});
		
		locationManager = (LocationManager) parentActivity.getSystemService(Context.LOCATION_SERVICE);
		
		Criteria criteria = new Criteria();
		String provider = locationManager.getBestProvider(criteria, true);
		
		locationManager.requestLocationUpdates(provider, 4000, 0,locationListener);
		
		

		
		parentActivity.cl.notifyDataSetChanged();
		parentActivity.getItinerary().itin.get(0).setLatitude(parentActivity.getUserLat()+"");
		parentActivity.getItinerary().itin.get(0).setLongitude(parentActivity.getUserLon()+"");
		
		googlemap.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {
			
			@Override
			public void onInfoWindowClick(Marker marker) {
				double lat = marker.getPosition().latitude;
				double lon = marker.getPosition().longitude;
				
				Intent intent = new Intent(Intent.ACTION_VIEW,Uri.parse("http://maps.google.com/maps/api/directions/?" + "saddr="+ parentActivity.getItinerary().itin.get(0).getLatitude() + "," + parentActivity.getItinerary().itin.get(0).getLongitude() + "&daddr=" + lat + "," + lon+"&mode=driving"));
				intent.setClassName("com.google.android.apps.maps","com.google.android.maps.MapsActivity");
				startActivity(intent);
			}
		});

		//stores the current user location. 
		SharedPreferences.Editor editor = parentActivity.settings.edit();	
		editor.putString("lockedRows", "0");
		editor.commit();
		
	}
	
	private void setMapButtonBar() {


		//final Button leftArrow = (Button) view.findViewById(R.id.mainPageLeftArrow);
		//final Button rightArrow = (Button) view.findViewById(R.id.mainPageRightArrow);
		final Button startItinButton = (Button) view.findViewById(R.id.startItinButton);

		/*leftArrow.setText("");
		leftArrow.setEnabled(false);
		
		leftArrow.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				parentActivity.itinerary = parentActivity.getLastItinerary();
				
				if( parentActivity.getItinListIndex() == 0 ){
					leftArrow.setText("");
					leftArrow.setEnabled(false);
				}
				
		    	parentActivity.cl = new CustomListView(parentActivity, R.layout.customlist, parentActivity.getItinerary().itin, parentActivity.getItinerary());
		    	parentActivity.lV.setAdapter(parentActivity.cl);
		    	parentActivity.cl.notifyDataSetChanged();
		    	parentActivity.currentItin = parentActivity.getItinerary();
		    	parentActivity.updateMap();
		    	//Log.i("last button","size: "+parentActivity.itinList.size()+" index: "+parentActivity.getItinListIndex());
			}
		});*/

		/*rightArrow.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				/*
				 * Change the image (left arrow)
				 *
				leftArrow.setText("<");
				leftArrow.setEnabled(true);
				
				// if the current itin is the last in the list
				if( parentActivity.getItinListIndex() == parentActivity.itinList.size()-1 ){
					googlemap.clear();
					parentActivity.newItinerary();
					//Log.i("next button","new itin. size: "+parentActivity.itinList.size()+" index: "+parentActivity.getItinListIndex());
				}
				else{
					parentActivity.itinerary = parentActivity.getNextItinerary();	
			    	parentActivity.cl = new CustomListView(parentActivity, R.layout.customlist, parentActivity.getItinerary().itin, parentActivity.getItinerary());
			    	parentActivity.lV.setAdapter(parentActivity.cl);
			    	parentActivity.cl.notifyDataSetChanged();
			    	parentActivity.currentItin = parentActivity.getItinerary();
			    	parentActivity.updateMap();
			    	//Log.i("next button","not new itin. size: "+parentActivity.itinList.size()+" index: "+parentActivity.getItinListIndex());
				}
				
				resetStartItinButton();
			}
		});*/

		startItinButton.setOnClickListener( new OnClickListener() {
			@Override
			public void onClick(View v) {
				googlemap.clear();
				parentActivity.newItinerary();
			}
		});
		/*setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if( !itinStarted ){
					itinStarted = true;
					startItinButton.setText("STOP ITINERARY");
					startItinButton.setBackgroundResource(R.drawable.recenterbutton_end);
					parentActivity.mRequestType = GeofenceUtils.REQUEST_TYPE.ADD;
					for( Entity e : parentActivity.getItinerary().itin ){
						e.setLocked(true);
						// add geofence for each entity
						SimpleGeofence geo = new SimpleGeofence(
				                String.valueOf(e.getTourId()),
				                Double.valueOf(e.getLatitude()),
				                Double.valueOf(e.getLongitude()),
				                Float.valueOf(10),
				                GEOFENCE_EXPIRATION_TIME,
				                // This geofence records only entry transitions
				                Geofence.GEOFENCE_TRANSITION_ENTER);
				        // Store this flat version
				        parentActivity.mGeofenceStorage.setGeofence(String.valueOf(e.getTourId()), geo);
				        parentActivity.mCurrentGeofences.add(geo.toGeofence());
				        // Start the request. Fail if there's already a request in progress
				        try {
				            // Try to add geofences
				        	//parentActivity.mGeofenceRequester.addGeofences(parentActivity.mCurrentGeofences);
				        } catch (UnsupportedOperationException ex) {
				            // Notify user that previous request hasn't finished.
				            Toast.makeText(parentActivity, "add geofence in progress",
				                        Toast.LENGTH_LONG).show();
				        }
					}
					parentActivity.updateList();
					//rightArrow.setEnabled(false);
					
					
					StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

					StrictMode.setThreadPolicy(policy); 
					ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
					
					nameValuePairs.add(new BasicNameValuePair("itinID",parentActivity.getItinerary().getId()));
					//Log.i("itin id",parentActivity.getItinerary().getId());
					nameValuePairs.add(new BasicNameValuePair("startorstop","Start"));
					
					InputStream is;
					//http post
					try{
					        HttpClient httpclient = new DefaultHttpClient();
					        HttpPost httppost = new HttpPost("http://citywhisk.com/scripts/insertItinerary.php");
					        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
					        HttpResponse response = httpclient.execute(httppost);
					        HttpEntity entity = response.getEntity();
					        is = entity.getContent();
					
					        BufferedReader reader = new BufferedReader(new InputStreamReader(is,"iso-8859-1"),8);
					        StringBuilder sb = new StringBuilder();
					        String line = null;
					        while ((line = reader.readLine()) != null) {
					                sb.append(line + "\n");
					        }
					        is.close();
					 
					        //i("start onclick",result);
					}catch(Exception ex){
						//////Log.i("Insert exception", ex.getMessage());
					}
				}
				else {
					resetStartItinButton();
					for( int i=1; i<parentActivity.getItinerary().itin.size(); i++ ){
						parentActivity.getItinerary().itin.get(i).setLocked(false);
					}
					parentActivity.updateList();
					//rightArrow.setEnabled(true);
					parentActivity.lockedPositions.clear();
					
					SharedPreferences.Editor editor = parentActivity.settings.edit();
					editor.putString("lockedRows", "");
					editor.commit();
					
					// remove geofences
					for( Entity e: parentActivity.getItinerary().itin ){
						//parentActivity.mGeofenceIdsToRemove.add(String.valueOf(e.getTourId()));
					}

			        /*
			         * Record the removal as remove by list. If a connection error occurs,
			         * the app can automatically restart the removal if Google Play services
			         * can fix the error
			         *
			        //parentActivity.mRemoveType = GeofenceUtils.REMOVE_TYPE.LIST;
					
					StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

					StrictMode.setThreadPolicy(policy); 
					String result = "";
					ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
					
					nameValuePairs.add(new BasicNameValuePair("itinID",parentActivity.getItinerary().getId()+""));
					nameValuePairs.add(new BasicNameValuePair("startorstop","Stop"));
					
					InputStream is;
					//http post
					try{
					        HttpClient httpclient = new DefaultHttpClient();
					        HttpPost httppost = new HttpPost("http://citywhisk.com/scripts/insertItinerary.php");
					        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
					        HttpResponse response = httpclient.execute(httppost);
					        HttpEntity entity = response.getEntity();
					        is = entity.getContent();
					
					        BufferedReader reader = new BufferedReader(new InputStreamReader(is,"iso-8859-1"),8);
					        StringBuilder sb = new StringBuilder();
					        String line = null;
					        while ((line = reader.readLine()) != null) {
					                sb.append(line + "\n");
					        }
					        is.close();
					 
					        result=sb.toString();
					        //i("start onclick",result);
					}catch(Exception ex){
						//////Log.i("Insert exception", ex.getMessage());
					}
				}
			}
		});*/
		
	}
	
	public void resetStartItinButton(){
		final Button startItinButton = (Button) view.findViewById(R.id.startItinButton);
		if( itinStarted ) {
			itinStarted = false;
			startItinButton.setText("START ITINERARY");
			startItinButton.setBackgroundResource(R.drawable.mapbarbutton);
		}
	}
}
