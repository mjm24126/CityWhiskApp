package com.citywhisk.citywhisk;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.maps.model.LatLng;

import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager.BadTokenException;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

public class SplashActivity extends BaseActionBar implements GooglePlayServicesClient.ConnectionCallbacks,
GooglePlayServicesClient.OnConnectionFailedListener {

	CheckGooglePlay checkGooglePlay;
	private LocationClient locationClient;
	private Location userLocation;
	private int markerImage;
	private double lat;
	private double lon;
	ProgressDialog pDialog;
	private JSONObject jsonObject;
	GPSTracker gps;
	MyApplication mApplication;
	
	protected MyLocationListener locListener;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        //removing title bar
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_splash);
        
        locListener = new MyLocationListener(this);
        lat = locListener.getLatitude();
        lon = locListener.getLongitude();
        //Log.i("tttsplash lat", lat+"");
        //Log.i("tttsplash lon", lon+"");
        gps = new GPSTracker(this);
        
        /*if(gps.canGetLocation()){
        	lat = gps.getLatitude();
        	lon = gps.getLongitude();
        	setUserLat(lat);
            setUserLon(lon);
            
        }
        else{
        	gps.showSettingsAlert();
        }*/
        mApplication = (MyApplication)getApplicationContext();
        
        
        markerImage = 1;
	    
	    Random rand = new Random();
	    int pos = rand.nextInt(6 - 1 + 1) + 1;
	    
	    switch(pos){
		case 1:
			markerImage = R.drawable.rochester1;
			break;

		case 2:
			markerImage = R.drawable.rochester3;
			break;

		case 3:
			markerImage = R.drawable.rochester4;
			break;

		case 4:
			markerImage = R.drawable.rochester6;
			break;

		case 5:
			markerImage = R.drawable.rochester7;
			break;
			
		case 6:
			markerImage = R.drawable.rochester10;
			break;
			
	    }
	    
	    RelativeLayout rl = (RelativeLayout) findViewById(R.id.splashPage);
	    rl.setBackgroundResource(markerImage);
	    
		/*
		 * Start Activeitinerary.java if button is clicked
		 */
		/*TextView b_login = (TextView) findViewById(R.id.login1);
		
		Typeface typeFace=Typeface.createFromAsset(this.getAssets(),"fonts/GOTHIC.TTF");
		b_login.setTypeface(typeFace);
		
		b_login.setOnClickListener(new OnClickListener() {				//set an anonymous function for ActionListener

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startActivity(new Intent(MainActivity.this, Login.class));
			}
		});*/


		/*
		 * check network connectivity
		 */
		NetworkDetection nd = new NetworkDetection(getApplicationContext());
		if( !(nd.isConnectingToInternet()) )
		{
			AlertDialog.Builder builder = new AlertDialog.Builder(SplashActivity.this);
			builder.setMessage("You must be connected to Internet");
			builder.setCancelable(false);
			builder.setPositiveButton("Okay", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub	
					SplashActivity.this.finish();
				}
			});
			AlertDialog alert = builder.create();
			alert.show();
		}
		else{

			
		}

		/*
		 * check for google play service
		 */
		checkGooglePlay = new CheckGooglePlay(this);
		checkGooglePlay.checkGooglePlayService();
		
		(new GetItinerary(mApplication.getAndroidID())).execute();
    }

    
    @Override
    protected void onStop() {
    	super .onStop();
    	
    	if( pDialog != null )
    		pDialog.dismiss();
    }
    
    protected void onPause() {
    	super .onPause();
    	gps.stopUsingGPS();
    }
    
    protected void onResume() {
    	super .onResume();
    	
    }

	@Override
	public void onConnectionFailed(ConnectionResult arg0) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void onConnected(Bundle arg0) {
		locationClient = new LocationClient(this,this,this);
		locationClient.connect();
		
		//userLocation = locationClient.getLastLocation();
		setUserLat(lat);
		setUserLon(lon);
	}


	@Override
	public void onDisconnected() {
		// TODO Auto-generated method stub
		
	}
	
	public static ProgressDialog createProgressDialog(Context mContext) {
        ProgressDialog dialog = new ProgressDialog(mContext);
        try {
                dialog.show();
        } catch (BadTokenException e) {

        }
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.progressdialog);
        // dialog.setMessage(Message);
        return dialog;
	}
	
	/*
	 * Background task to make call to server.
	 */
	protected class GetItinerary extends AsyncTask<String, String, Integer> {
		ArrayList<Entity> entityResults;
		Itinerary it = new Itinerary();
		private CalcDistance calcDist;
		private String distance;
		private String url_new_itin = "http://citywhisk.com/scripts/GetItin/getItinerary103.php";
		
		private boolean locationDefaulted = false;
		
		JSONParser jParser = new JSONParser();
		
		DisplayMetrics dm = getResources().getDisplayMetrics();
    	int width = dm.widthPixels;
    	int height = dm.heightPixels;
    	//set max image size for bitmpas based on 150dp height in detail view * screen width
    	int maxImgSize = width * 150;
    	
    	JSONObject json;
    	
    	public GetItinerary(String andID){
    		
    	}
    	
        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (pDialog == null) {

            } else {
                pDialog.show();
            }
            calcDist = new CalcDistance();
        }
 
        /**
         * getting search results from url
         * */
        protected Integer doInBackground(String... args) {
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("androidID", mApplication.getAndroidID()));
            params.add(new BasicNameValuePair("lat", String.valueOf(lat)));
            params.add(new BasicNameValuePair("lon", String.valueOf(lon)));
            
            
            // getting JSON string from URL
            json = jParser.makeHttpRequest(url_new_itin, "POST", params);
 
            // Check your log cat for JSON response
            ////Log.d("JSON results: ", json.toString());
            if( json == null ) return 0;
            
            try {
                if (json.getInt("success") == 1) {
                	
                	if(json.getBoolean("defaultedLocation") == true ){
                		locationDefaulted = true;
                	}
                	
                	JSONToEntityConverter jConverter = new JSONToEntityConverter(maxImgSize);
        			ArrayList<Entity> ents = jConverter.convertAll(json);
        			
        			it.itin = ents;
        			it.addStart();
        			it.setId(json.getString("itinID"));
        			it.setDemoID(json.getInt("demoID"));
        			it.setCurrentEnts(ents);
        			
        			getDistances();
        			
 
                } else {
                    // no entities found
                	// no search matches your request
                }
            } catch (JSONException e) {
            	e.printStackTrace();
                
            }
 
            return null;
        }
        
        public void getDistances(){
    		it.setTotalDistance(0);
    		for( int i=0; i<it.itin.size() - 1; i++ ){
    			double lat1 = Double.parseDouble(it.itin.get(i).getLatitude());
    			double lon1 = Double.parseDouble(it.itin.get(i).getLongitude());
    			if( i == 0 ){
    				lat1 = lat;
    				lon1 = lon;
    			}
    			double lat2 = Double.parseDouble(it.itin.get(i+1).getLatitude());
    			double lon2 = Double.parseDouble(it.itin.get(i+1).getLongitude());
    			
    			String jsonString = calcDist.getDistanceInfo(lat1,lon1,lat2,lon2);
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
    				it.itin.get(i+1).setDistance(distance);
    				it.setTotalDistance(it.getTotalDistance() + Double.parseDouble(distance.substring(0, distance.length()-3)));
    				
    				JSONObject durationObject = leg.getJSONObject("duration");
    				it.itin.get(i+1).setDuration(durationObject.getString("text"));
    				
    				JSONObject poObject = route.getJSONObject("overview_polyline");
    				it.addPoly(poObject.getString("points"));
    			} catch (JSONException e) {
    				// TODO Auto-generated catch block
    				e.printStackTrace();
    			}
    		}
    	}
        
        
 
        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(Integer returncode) {
        	
        	if( returncode != null && returncode == 0 ){
        		if (pDialog != null) {
        			pDialog.dismiss();
                }
        		AlertDialog.Builder builder = new AlertDialog.Builder(SplashActivity.this);
    			builder.setMessage("You must be connected to Internet");
    			builder.setCancelable(false);
    			builder.setPositiveButton("Okay", new DialogInterface.OnClickListener() {

    				@Override
    				public void onClick(DialogInterface dialog, int which) {
    					moveTaskToBack(true);
    				}
    			});
    			builder.create().show();
        	}
        	else{
        		Intent mainIntent = new Intent(SplashActivity.this,MainPage.class);
            	mainIntent.putExtra("itin", it);
            	mainIntent.putExtra("lat", lat);
            	mainIntent.putExtra("lon", lon);
            	mainIntent.putExtra("defaultedLocation",locationDefaulted);
            	SplashActivity.this.startActivity(mainIntent);
            	SplashActivity.this.finish();
        	}
        	
        }
 
    }
    
}
