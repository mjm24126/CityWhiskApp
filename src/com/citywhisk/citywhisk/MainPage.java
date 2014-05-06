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
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.citywhisk.citywhisk.GeofenceUtils.REMOVE_TYPE;
import com.citywhisk.citywhisk.GeofenceUtils.REQUEST_TYPE;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.maps.model.LatLng;
import com.mobeta.android.dslv.DragSortController;
import com.mobeta.android.dslv.DragSortListView;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnShowListener;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.util.LruCache;
import android.text.Editable;
import android.text.Html;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager.BadTokenException;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.ShareActionProvider;
import android.widget.TextView;
import android.widget.Toast;

public class MainPage extends BaseActionBar{
	
	public DatabaseManager dbManager;
	protected MyLocationListener locListener;
	protected LocationManager locationManager;
	double startLat;
	double startLon;

	public FragMap mapfrag;
	public FragMap fragmap;
	public FragPref preffrag;
	public FragSave savefrag;
	public FragSearch searchfrag;
	
	protected Fragment currentfrag;
	protected SharedPreferences settings;
	boolean firstItin;
	private Context activeContext;
	
	//PREFERENCE FIELDS
	private int ratingPref = 3;
	private int budgetPref = 75;
	private double distancePref = 10;
	private int timePref = 240;
	private boolean useDefaultStartTime;
	private int itinStartHour;
	private int itinStartMin;
	
	protected Itinerary itinerary;
	public int currentTourIndex;
	private ActionBar actionBar;
	private ProgressDialog pd;
	public ListView lV;
	public ArrayList<String> favList;
	public DragSortController controller;
	
	public ArrayList<Integer> lockedPositions;
	
	String[] toogleView = {"distance","time"};
	
	protected ArrayList<String> jsonOutputList;
	
	protected ArrayList<Itinerary> itinList;
	private int itinListIndex;

	// search stuff
	String searchText;
	// Creating JSON Parser object
    JSONParser jParser = new JSONParser();
 
    // JSON Node names
    private static final String TAG_SUCCESS = "success";
	private static final int ADD_FROM_DETAILS = 0;
 
    ArrayList<String> searchResults;
    ListView searchList;
    JSONArray searchJson;
    
    // search result JSONArray
    JSONArray searchEnts = null;
    
    // Search view text box
    SearchView searchTextView;
    int maxImgSize;

	public ProgressDialog pDialog;
	
	MyGestureDetector gestos;
	//private LruCache<String, Entity> mMemoryCache;
	
	GPSTracker gps;
	List<Geofence> mCurrentGeofences;
    // Persistent storage for geofences
	SimpleGeofenceStore mGeofenceStorage;
	// Add geofences handler
	protected GeofenceRequester mGeofenceRequester;
    // Remove geofences handler
	protected GeofenceRemover mGeofenceRemover;
	String androidID;
	
	// Store the current request
    protected REQUEST_TYPE mRequestType;
    // Store the current type of removal
    protected REMOVE_TYPE mRemoveType;
    // Store the list of geofences to remove
    protected List<String> mGeofenceIdsToRemove;
    private MyApplication mApplication;
    private boolean locationDefaulted;
	
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        
	        setContentView(R.layout.activity_mainpage);
	        dbManager = new DatabaseManager(this);
	        
	        //creating ActionBar navigation Bar and setting up properties for it
			actionBar = getActionBar();
			actionBar.setDisplayShowHomeEnabled(true);
			actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
			actionBar.setDisplayShowTitleEnabled(false);
			actionBar.setDisplayHomeAsUpEnabled(false);
			
			gps = new GPSTracker(this);
			
			// set up shared preferences for app
			settings = getSharedPreferences("CITYWHISK", 0);
			SharedPreferences.Editor editor = settings.edit();
			editor.putString("endAtStart", "false");
			editor.putString("lockedRows", "0");
			editor.putString("toogleView", "distance");
			editor.commit();
			
			activeContext = MainPage.this;
			firstItin = true;
			currentTourIndex = 0;
			jsonOutputList = new ArrayList<String>();
			favList = new ArrayList<String>();
			lockedPositions = new ArrayList<Integer>();
			searchResults = new ArrayList<String>();
			itinList = new ArrayList<Itinerary>();
			
			
			
			
			Intent intent = getIntent();
			Bundle bundle = intent.getExtras();
			
			itinerary = (Itinerary) bundle.get("itin");
			startLat = bundle.getDouble("lat");
			startLon = bundle.getDouble("lon");
			locationDefaulted = bundle.getBoolean("defaultedLocation");
			setUserLat(startLat);
			setUserLon(startLon);
			// store the first itinerary in the list.
			itinList.add(itinerary);
			itinListIndex = 0;
			
	    	// set itinerary start time
	    	setUseDefaultStartTime(true);
	    	
	    	firstItin = false;
	    	currentItin = getItinerary();
	    	currentfrag = new Fragment();
	    	DisplayMetrics dm = getResources().getDisplayMetrics();
	    	int width = dm.widthPixels;
	    	int height = dm.heightPixels;
	    	//set max image size for bitmpas based on 150dp height in detail view * screen width
	    	maxImgSize = width * 150;
	    	
	    	setDragListeners();
			
			//createShareIntent();
			
	    	//Setting up the Tab bars 
			Tab tab = actionBar.newTab()
					.setText(R.string.home_tab)
					.setTabListener(new TabListener<FragMap>(this, "Home", FragMap.class));
			actionBar.addTab(tab);

			tab = actionBar.newTab()
					.setText(R.string.search_tab)
					.setTabListener(new TabListener<FragSearch>(this, "Search", FragSearch.class));
			actionBar.addTab(tab);

			tab = actionBar.newTab()
					.setText(R.string.save_tab)
					.setTabListener(new TabListener<FragSave>(this, "Save", FragSave.class));
			actionBar.addTab(tab);
			
			tab = actionBar.newTab()
					.setText(R.string.pref_tab)
					.setTabListener(new TabListener<FragPref>(this, "Pref", FragPref.class));
			actionBar.addTab(tab);
			actionBar.setHomeButtonEnabled(false);

			
			// PREFERENCES TAB FOR LATER VERSION
			/*tab = actionBar.newTab()
					.setText(R.string.pref_tab)
					.setTabListener(new TabListener<FragPref>(this, "Pref", FragPref.class));
			actionBar.addTab(tab);*/
			
			// search intent
			handleIntent(getIntent());
			
			mApplication = (MyApplication)getApplicationContext();
			
	    }

    @SuppressLint("NewApi")
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.mainpagemenu, menu);
        
        int positionOfMenuItem = 1; // or whatever...
        MenuItem item = menu.getItem(positionOfMenuItem);
        SpannableString s = new SpannableString("Privacy and Terms");
        s.setSpan(new ForegroundColorSpan(Color.WHITE), 0, s.length(), 0);
        item.setTitle(s);
        
        // Locate MenuItem with ShareActionProvider
        /*MenuItem item = menu.findItem(R.id.menu_item_share);
        // Fetch and store ShareActionProvider
        shareActionProvider = (ShareActionProvider) item.getActionProvider();
        shareActionProvider.setShareIntent(createShareIntent());*/
        
        return true;
    }

    public boolean onMenuItemSelected(int featureId, MenuItem item) {

        switch (item.getItemId()) {
        
        /*case R.id.actionbarfirst:
			DialogFragment newFragment = new TimePickerFrag();
		    newFragment.show(getFragmentManager(), "timePicker");
			return true;*/
		case R.id.actionbar1:
			//startActivity(new Intent(this, Savings.class));
			String toogle = settings.getString("toogleView", "");
			SharedPreferences.Editor editor = settings.edit();
			int indexToogle = Arrays.asList(toogleView).indexOf(toogle);
			if(indexToogle < 1){
				indexToogle++;
			}
			else{
				indexToogle=0;
			}
			//Log.i("TOOGLE","toogle: "+toogle);
			editor.putString("toogleView", toogleView[indexToogle]);
			editor.commit();

			//cl.notifyDataSetChanged();
			updateList();
			return true;

			/*
			 * Add current itinerary to favorite
			 */
		/*case R.id.actionbar2:
			startActivity(new Intent(this, ProfileClass.class));
			//Log.i("onOptionsItemSelected","finished");
			return true;*/

			/*
			 * Privacy Policy
			 */
		/*case R.id.actionbar3:
			startActivity(new Intent(this, SharingClass.class));
			//Log.i("onOptionsItemSelected","finished");
			return true;*/

		/*case R.id.actionbar4:
			startActivity(new Intent(this, SyncingClass.class));
			//Log.i("onOptionsItemSelected","finished");
			return true;*/

		/*case R.id.menu_item_share:
			ShareActionProvider mShareActionProvider = (ShareActionProvider) item.getActionProvider();
			
			Intent shareIntent = new Intent();
			shareIntent.setAction(Intent.ACTION_SEND);
			shareIntent.putExtra(Intent.EXTRA_TEXT, "This is my text to send.");
			shareIntent.setType("text/plain");
			mShareActionProvider.setShareIntent(createShareIntent());*/

		/*case R.id.actionbar6:
			startActivity(new Intent(this, SettingsClass.class));
			//Log.i("onOptionsItemSelected","finished");
			return true;*/

		/*case R.id.actionbar7:
			startActivity(new Intent(this, HowToClass.class));
			//Log.i("onOptionsItemSelected","finished");
			return true;*/

		case R.id.actionbar8:
			Intent i = new Intent(getBaseContext(), PrivacyActivity.class);
            startActivity(i);
			////Log.i("onOptionsItemSelected","finished");
			return true;

		default:
			////Log.i("onOptionsItemSelected","finished");
			return super.onOptionsItemSelected(item);
		}
    }
 
    @Override
    protected void onStart() {
    	super.onStart();
    	
    	if( locationDefaulted ){
    		final AlertDialog.Builder builder = new AlertDialog.Builder(MainPage.this);
			
			LinearLayout layout = new LinearLayout(MainPage.this);
	        LinearLayout.LayoutParams parms = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
	        layout.setOrientation(LinearLayout.VERTICAL);
	        layout.setLayoutParams(parms);
	        
	        TextView tv = new TextView(MainPage.this);
	        tv.setText("Help us bring CityWhisk to your area by recommending your favorite businesses on our website!  In the mean time, check out this " +
	        		"itinerary.");
	        tv.setTextColor(Color.parseColor("#646363"));
	        tv.setPadding(10, 25, 10, 25);
	        tv.setGravity(Gravity.CENTER);
	        tv.setTextSize(20);
	        LinearLayout.LayoutParams tv1Params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
	        layout.addView(tv,tv1Params);
			
			builder.setView(layout);
			builder.setNeutralButton("OK", null);
			
			final AlertDialog ad = builder.create();
			ad.setOnShowListener(new OnShowListener() {

				@Override
				public void onShow(DialogInterface dia) {
					
					Button okBtn = ad.getButton(AlertDialog.BUTTON_NEUTRAL);
						okBtn.setOnClickListener(new View.OnClickListener() {

				            @Override
				            public void onClick(View view) {
				        		ad.dismiss();
				            }
				        });
			       
				}
				
			});
			ad.show();
		}
    }
    
    @Override
    protected void onPause() {
    	//removes the location Listener when app goes out of view
		super.onPause();
		if ( currentfrag == preffrag ) {
			//getFragmentManager().beginTransaction().hide(R.id.container);
		}
		if ( currentfrag == mapfrag ) { 
		//	locationManager.removeUpdates(locationListener);
		}
		if(pd != null)
			pd.dismiss();
		////Log.i("onpause current frag",currentfrag.toString());
    }
    
    @Override
    protected void onResume() {
    	super.onResume();
    	androidID = android.provider.Settings.Secure.getString(getContentResolver(),
                android.provider.Settings.Secure.ANDROID_ID);
    	
		if ( currentfrag == mapfrag ) {
			/*locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 4000, 0,locationListener);
			locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 4000, 0, locationListener);
			if( paused ) {
				generatemarkers();
				supportClassObj.fitInMap(itinerary, googlemap);
			}*/
			// handle here what has to be done when app re-opens
		}
		// Instantiate a new geofence storage area
        mGeofenceStorage = new SimpleGeofenceStore(this);

        // Instantiate the current List of geofences
        mCurrentGeofences = new ArrayList<Geofence>();
		
    }
    
    @Override
    protected void onStop() {
    	super .onStop();
    	gps.stopUsingGPS();
    }
    
    
    //Makes a call to server to generate a new itinerary (Click on right arrow)
	public void newItinerary(){
		(new GetItinerary()).execute();
		updateList();
		addToItinList(getItinerary());
	}
	
	public void updateList(){
		cl = new CustomListView(this, R.layout.customlist, getItinerary().itin, getItinerary());
		lV.setAdapter(cl);
		cl.notifyDataSetChanged();
	}

	
	public void setDragListeners(){
		//final ListView myListView;
		/*
		 * Manages the drag of list view
		 * And recalculate the path and distance appropriately
		 */
		try{
			
			lV = (DragSortListView)MainPage.this.findViewById(R.id.listViewMain);

			cl = new CustomListView(MainPage.this, R.layout.customlist, getItinerary().itin, getItinerary());
			lV.setAdapter(cl);
			
			DragSortListView.DropListener onDrop = new DragSortListView.DropListener() {

				String oldEnts;
				String newEnts;
				
				@Override
				public void drop(int from, int to) {
					if( from != 0 && to != 0  && from != to) { // can't move starting location, nothing was moved
						//Log.i("locked positions",lockedPositions.toString());
						oldEnts = getItinerary().getCurrentEnts();
						if(!(to<=0)){
							//Assuming that item is moved up the list
							int direction = -1;
							int loop_start = from;
							int loop_end = to;
	
							//For instance where the item is dragged down the list
							if(from < to) {
								direction = 1;
							}
							
							Entity target = getItinerary().itin.get(from);
	
							for(int i=loop_start;i!=loop_end;i=i+direction){
								getItinerary().itin.set(i, getItinerary().itin.get(i+direction));
							}
							getItinerary().itin.set(to, target);
							currentItin = getItinerary();
							
							lockedPositions.clear();
							for( int i=1; i<getItinerary().itin.size(); i++ ){
								if( getItinerary().itin.get(i).isLocked() ){
									lockedPositions.add(i);
								}
							}
							controller.setLockedPositions(lockedPositions);
							newEnts = getItinerary().getCurrentEnts();
	
							new GetDistance().execute();
							CalcDistanceAfterDragAsync calcDistanceAfterDragAsync = new CalcDistanceAfterDragAsync(getItinerary(), MainPage.this, cl);
							calcDistanceAfterDragAsync.execute();
							
							new DatabaseToggleInsert( androidID,oldEnts,newEnts,getItinerary().getId() ).execute();
	
							try {
								calcDistanceAfterDragAsync.get();
							} catch (InterruptedException e) {
								e.printStackTrace();
							} catch (ExecutionException e) {
								e.printStackTrace();
							}
							
							updateMap();
							updateList();
							// update itinList
							itinList.set(getItinListIndex(), getItinerary());
						}
				}
				}
			};

			/*
			 * Manages the swipe action for the listview
			 */
			DragSortListView.RemoveListener onRemove = new DragSortListView.RemoveListener() {

				@Override
				public void remove(int which) {
					if( which != 0 ){  //make sure its not starting location

						new GetNewEntity().execute(which);
						
					}
				}	
			};

			
			((DragSortListView) lV).setRemoveListener(onRemove);
			((DragSortListView) lV).setDropListener(onDrop);
			
			
			//DragSortController controller = new DragSortController((DragSortListView) myListView, ActiveItinerary.this);
			controller = new DragSortController((DragSortListView) lV);
			controller.setDragHandleId(R.id.savingImage);
			controller.setLockedPositions(lockedPositions);
			controller.setRemoveEnabled(true);
			controller.setSortEnabled(true);
			controller.setDragInitMode(1);

			lV.setOnTouchListener(controller);
			((DragSortListView) lV).setFloatViewManager(controller);
			((DragSortListView) lV).setDragEnabled(true);
			lV.setOnItemLongClickListener(new OnItemLongClickListener() {

				@SuppressLint("NewApi")
				@Override
				public boolean onItemLongClick(AdapterView<?> parent, View view,
						int position, long id) {
					final int pos = position;
					final AlertDialog.Builder builder = new AlertDialog.Builder(MainPage.this);
					String actName = getItinerary().itin.get(pos).getName();
					final String oldID = String.valueOf(getItinerary().itin.get(pos).getTourId());
					
					
					
					LinearLayout layout = new LinearLayout(MainPage.this);
			        LinearLayout.LayoutParams parms = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
			        layout.setOrientation(LinearLayout.VERTICAL);
			        layout.setLayoutParams(parms);
			        
			        TextView tv = new TextView(MainPage.this);
			        tv.setText("Delete "+actName+" from this itinerary?");
			        tv.setTextColor(Color.parseColor("#646363"));
			        tv.setPadding(10, 25, 10, 25);
			        tv.setGravity(Gravity.CENTER);
			        tv.setTextSize(20);
			        LinearLayout.LayoutParams tv1Params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
			        layout.addView(tv,tv1Params);
					
					builder.setView(layout);
					builder.setPositiveButton(R.string.delete, null);
					builder.setNegativeButton(R.string.cancel, null);
					
					final AlertDialog ad = builder.create();
					ad.setOnShowListener(new OnShowListener() {

						@Override
						public void onShow(DialogInterface dia) {
							
							Button posBtn = ad.getButton(AlertDialog.BUTTON_POSITIVE);
								posBtn.setOnClickListener(new View.OnClickListener() {
	
						            @Override
						            public void onClick(View view) {
						            	ad.dismiss();
						            	String beforeEntID, afterEntID;
						            	if( pos != 1 ){
							            	beforeEntID = String.valueOf(getItinerary().itin.get(pos-1).getTourId());
							            }else{
							            	beforeEntID = null;
							            }
							            if( pos != getItinerary().itin.size()-1 ){
							            	afterEntID = String.valueOf(getItinerary().itin.get(pos+1).getTourId());
							            }else{
							            	afterEntID = null;
							            }
							            
						            	getItinerary().itin.remove(pos);
						            	
						        		new GetDistance().execute();
						        		
						        		CalcDistanceAfterDragAsync calcDistanceAfterDragAsync = new CalcDistanceAfterDragAsync(getItinerary(), MainPage.this, cl);
										calcDistanceAfterDragAsync.execute();
										
										try {
											calcDistanceAfterDragAsync.get();
										} catch (InterruptedException e) {
											e.printStackTrace();
										} catch (ExecutionException e) {
											e.printStackTrace();
										}
										
										updateMap();
										updateList();
										// update itinList
										itinList.set(getItinListIndex(), getItinerary());
										
						        		lockedPositions.clear();
										for( int i=1; i<getItinerary().itin.size(); i++ ){
											if( getItinerary().itin.get(i).isLocked() ){
												lockedPositions.add(i);
											}
										}
										controller.setLockedPositions(lockedPositions);
										
										new DatabaseSwipeInsert(androidID,oldID,null,beforeEntID, afterEntID, String.valueOf(pos),getItinerary().getId() ).execute();
						        		
						            }
						        });
					        
					        Button neg = ad.getButton(AlertDialog.BUTTON_NEGATIVE);
					        neg.setOnClickListener(new View.OnClickListener() {
								
								@Override
								public void onClick(View v) {
									ad.dismiss();
								}
							});
						}
						
					});
					if( pos != 0 ){
						ad.show();
					}
					
					return true;
				}
			});
			lV.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					final Dialog d = new Dialog(MainPage.this);
					
					if(position !=0){
						Intent intent = new Intent(MainPage.this, DetailClass.class);
						intent.putExtra("detailIndex", position);
						startActivityForResult(intent,ADD_FROM_DETAILS);
					}
					
					else{
						d.setContentView(R.layout.chosecustomlocation);
						d.setTitle("Set Starting Location");
						
						Button customAddressBtn = (Button) d.findViewById(R.id.modifyStartingLocation);

						//final CheckBox cb = (CheckBox) d.findViewById(R.id.endAtStart);
						final EditText address = (EditText) d.findViewById(R.id.adressField);

						/*if(settings.getString("endAtStart","").equals("true")){
							cb.setChecked(true);

						}*/
						if(settings.getString("startAddress", "").length()>0){
							address.setText(settings.getString("startAddress", ""));
						}

						customAddressBtn.setOnClickListener(new View.OnClickListener() {

							@Override
							public void onClick(View v) {
								if(address.getText()!=null){

									List<Address> geocoderAddress;
									Geocoder coder = new Geocoder(MainPage.this);

									try{
										geocoderAddress = coder.getFromLocationName(address.getText().toString(), 1);
										Address location = geocoderAddress.get(0);

										if(location != null){
											SharedPreferences.Editor editor = settings.edit();
											editor.putString("latitude", ""+location.getLatitude()+"");
											editor.putString("longitude", ""+location.getLongitude()+"");
											editor.putString("startAddress", address.getText().toString());
											
											editor.commit();

											d.dismiss();

											//re-calculate the path from destination 1 to 2
											CalcCustomAddressPath calcCustomAddressPathObj = new CalcCustomAddressPath(MainPage.this, location.getLatitude(), location.getLongitude(), getItinerary(), cl);
											calcCustomAddressPathObj.execute();

											//wait for thread to complete
											calcCustomAddressPathObj.get();
											
											getItinerary().itin.get(0).setLatitude(""+location.getLatitude());
											getItinerary().itin.get(0).setLongitude(""+location.getLongitude());
											getItinerary().itin.get(0).setAddress(address.getText().toString());
											//((MainPage)c).fragmap.generateMarkers();
										}
										else{
											Toast toast = Toast.makeText(MainPage.this, "Address not found", Toast.LENGTH_LONG);
											toast.show();
										}
									}catch(Exception e){
										//Log.i("Exception in Geocoder",e.getMessage());
									}


								}

//								CheckBox cb = (CheckBox) d.findViewById(R.id.endAtStart);
//								if(cb.isChecked()==true){
//									//editor.putString("endAtStart","true");
//								}
//								else{
//									//editor.putString("endAtStart","false");
//								}
								//editor.commit();

							}


						});
						
						Button dialog_Cancel = (Button) d.findViewById(R.id.exitModifyLocation);

						dialog_Cancel.setOnClickListener(new View.OnClickListener() {

							@Override
							public void onClick(View v) {
								d.dismiss();

							}
						});
						d.show();
					}
				}
			
			});


		}catch(Exception e){
			e.printStackTrace();
			//Log.i("EXCEPTION","3");
		}

	}
	
    // Getter and Setter for default start time boolean
    public boolean isUseDefaultStartTime() {
		return useDefaultStartTime;
	}
	public void setUseDefaultStartTime(boolean useDefaultStartTime) {
		this.useDefaultStartTime = useDefaultStartTime;
	}
	public int getItinStartHour() {
		return itinStartHour;
	}
	public void setItinStartHour(int itinStartHour) {
		this.itinStartHour = itinStartHour;
		setUseDefaultStartTime( false );
	}
	public int getItinStartMin() {
		return itinStartMin;
	}
	public void setItinStartMin(int itinStartMin) {
		this.itinStartMin = itinStartMin;
		setUseDefaultStartTime( false );
	}

	// Preference Getters and Setters
	public int getRatingPref() {
		return ratingPref;
	}
	public void setRatingPref(int ratingPref) {
		this.ratingPref = ratingPref;
	}		
	public int getBudgetPref() {
		return budgetPref;
	}
	public void setBudgetPref(int budgetPref) {
		this.budgetPref = budgetPref;
	}
	public double getDistancePref() {
		return distancePref;
	}
	public void setDistancePref(double distancePref) {
		this.distancePref = distancePref;
	}
	public int getTimePref() {
		return timePref;
	}
	public void setTimePref(int timePref) {
		this.timePref = timePref;
	}
	
	public void updateMap(){
		if( currentfrag instanceof FragMap ){
			fragmap = (FragMap) getFragmentManager().findFragmentByTag("MAPFRAG");
			fragmap.googlemap.clear();
			fragmap.generateMarkers();
		}
	}

	public void addToItinList(Itinerary it){
		itinList.add(it);
		itinListIndex++;
	}
	
	public Itinerary getLastItinerary(){
		if ( itinListIndex > 0 ) itinListIndex--;
		return itinList.get(getItinListIndex());
	}
	public Itinerary getNextItinerary(){
		itinListIndex++;
		return itinList.get(getItinListIndex());
	}
	
	// create share intent
	/*private Intent createShareIntent() {
		Intent shareIntent = new Intent();
		shareIntent.setAction(Intent.ACTION_SEND);
		shareIntent.putExtra(Intent.EXTRA_TEXT, getItinerary().toShare());
		shareIntent.setType("text/plain");
		//shareIntent.putExtra(Intent.EXTRA_STREAM, uriToImage);
		shareIntent.setType("image/jpeg");
		return shareIntent;
	}*/


	public Itinerary getItinerary() {
		return itinerary;
	}


	public void setItinerary(Itinerary itinerary) {
		this.itinerary = itinerary;
		
		
	}
	
	@Override
	protected void onActivityResult(int aRequestCode, int aResultCode, Intent aData)
	{
		
		//Log.i("Result code", aResultCode+"");
	    switch (aRequestCode) {
	        case ADD_FROM_DETAILS:
	        	
				setItinerary(currentItin);
				new GetDistance().execute();
				// Re-center the googleMap
				updateMap();
				updateList();
				CalcDistanceAfterDragAsync calcDistanceAfterDragAsync = new CalcDistanceAfterDragAsync(getItinerary(), MainPage.this, cl);
				calcDistanceAfterDragAsync.execute();
				
				/*
				 *wait for Async task to complete
				 */
				try {
					calcDistanceAfterDragAsync.get();
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (ExecutionException e) {
					e.printStackTrace();
				}
	            break;
	            
	        case GeofenceUtils.CONNECTION_FAILURE_RESOLUTION_REQUEST :

                switch (aResultCode) {
                    // If Google Play services resolved the problem
                    case Activity.RESULT_OK:

                        // If the request was to add geofences
                        if (GeofenceUtils.REQUEST_TYPE.ADD == mRequestType) {

                            // Toggle the request flag and send a new request
                            mGeofenceRequester.setInProgressFlag(false);

                            // Restart the process of adding the current geofences
                            mGeofenceRequester.addGeofences(mCurrentGeofences);

                        // If the request was to remove geofences
                        } else if (GeofenceUtils.REQUEST_TYPE.REMOVE == mRequestType ){

                            // Toggle the removal flag and send a new removal request
                            mGeofenceRemover.setInProgressFlag(false);

                            // If the removal was by Intent
                            if (GeofenceUtils.REMOVE_TYPE.INTENT == mRemoveType) {

                                // Restart the removal of all geofences for the PendingIntent
                                mGeofenceRemover.removeGeofencesByIntent(
                                    mGeofenceRequester.getRequestPendingIntent());

                            // If the removal was by a List of geofence IDs
                            } else {

                                // Restart the removal of the geofence list
                                mGeofenceRemover.removeGeofencesById(mGeofenceIdsToRemove);
                            }
                        }
                    break;

                    // If any other result was returned by Google Play services
                    default:

                        // Report that Google Play services was unable to resolve the problem.
                        //Log.d(GeofenceUtils.APPTAG, "Google play services unable to resolve problem");
                }

	        
	    }
	    super.onActivityResult(aRequestCode, aResultCode, aData);
	}
	
	private void handleIntent(Intent intent) {
		////Log.i("handle intent",intent.toString());
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            searchText = intent.getStringExtra(SearchManager.QUERY);
            (new DBSearchInsert(androidID,searchText)).execute();
            //use the query to search your data somehow
            (new GetSearchResults()).execute();
           
        }
    }
	
	@Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }
	
	public static ProgressDialog createProgressDialog(Context mContext) {
        ProgressDialog dialog = new ProgressDialog(mContext);
        try {
                dialog.show();
        } catch (BadTokenException e) {

        }
        dialog.setCancelable(false);
        dialog.setIndeterminate(false);
        dialog.setContentView(R.layout.progressdialog);
        return dialog;
	}
	
	/*
	 * Background task to make call to server.
	 */
	protected class GetSearchResults extends AsyncTask<String, String, String> {
		// url to get all products list
	    private String url_search = "http://citywhisk.com/scripts/searchEntities.php";
		ArrayList<String> entityResults = new ArrayList<String>();
		FragSearch sf = (FragSearch) getFragmentManager().findFragmentByTag("SEARCHFRAG");
        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (pDialog == null) {
            	pDialog = createProgressDialog(MainPage.this);
            	pDialog.show();
            } else {
                pDialog.show();
            }
        }
 
        /**
         * getting search results from url
         * */
        protected String doInBackground(String... args) {
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("searchQuery",searchText));
            if( sf.getSearchByName() ){
            	params.add(new BasicNameValuePair("searchType","name"));
            }
            else {
            	params.add(new BasicNameValuePair("searchType","act"));
            }
            
            
            // getting JSON string from URL
            JSONObject json = jParser.makeHttpRequest(url_search, "POST", params);
 
            // Check your log cat for JSON response
            ////Log.d("Search results: ", json.toString());
            
            try {
                // Checking for SUCCESS TAG
                int success = json.getInt(TAG_SUCCESS);
 
                if (success == 1) {
                	// Convert json to just name strings
                    //JSONToEntityConverter converter = new JSONToEntityConverter();
                	searchJson = json.getJSONArray("entities");
                	JSONObject jsonOb;
                	for(int i=0;i<searchJson.length();i++){
                		jsonOb = searchJson.getJSONObject(i);
                		//Log.d("Search results: ", jsonOb.toString());
                		entityResults.add(jsonOb.getString("name"));
                	}
                    //entityResults = converter.convertAll(json);
 
                } else {
                    // no entities found
                	// no search matches your request
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
 
            return null;
        }
 
        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(String file_url) {
            
            
            searchList = (ListView) findViewById(R.id.searchList);
            SearchView searchView = (SearchView) findViewById(R.id.searchPlaces);
            
            if( entityResults != null ){
            	searchResults.clear();
            	for( String ent : entityResults ){
            		searchResults.add(ent);
            	}
	            ArrayAdapter<String> adaptor = new ArrayAdapter<String>(activeContext,android.R.layout.simple_list_item_1, searchResults);
	    		searchList.setAdapter(adaptor);
	    		
	    		adaptor.notifyDataSetChanged();
	    		
	    		searchView.clearFocus();
	    		InputMethodManager in = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
	    		in.hideSoftInputFromWindow(searchView.getWindowToken(), 0);
            }
            
         // dismiss the dialog after getting all products
            pDialog.dismiss();
        }
 
    }

	protected class GetItinerary extends AsyncTask<String, String, Integer> {
		ArrayList<Entity> entityResults;
		Itinerary it = new Itinerary();
		private JSONObject jsonObject;
		private CalcDistance calcDist;
		private String distance;
		private String url_new_itin = "http://citywhisk.com/scripts/GetItin/getItinerary103.php";
		ProgressDialog pDialog;
		JSONParser jParser = new JSONParser();
		
		DisplayMetrics dm = getResources().getDisplayMetrics();
    	int width = dm.widthPixels;
    	int height = dm.heightPixels;
    	//set max image size for bitmaps based on 150dp height in detail view * screen width
    	int maxImgSize = width * 150;
    	
        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            if (pDialog == null) {
            	pDialog = createProgressDialog(MainPage.this);
            	pDialog.show();
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
            params.add(new BasicNameValuePair("lat", String.valueOf(getUserLat())));
            params.add(new BasicNameValuePair("lon", String.valueOf(getUserLon())));
            params.add(new BasicNameValuePair("time", String.valueOf(getTimePref())));
            params.add(new BasicNameValuePair("budget", String.valueOf(getBudgetPref())));
            params.add(new BasicNameValuePair("distance", String.valueOf(getDistancePref())));
            params.add(new BasicNameValuePair("rating", String.valueOf(getRatingPref())));
            
            // getting JSON string from URL
            JSONObject json = jParser.makeHttpRequest(url_new_itin, "POST", params);
            if( json == null ) return 0;
 
            if(!json.toString().startsWith("{\"success")){
    			// connection is messed up (might have to agree to join network)
    			AlertDialog.Builder builder = new AlertDialog.Builder(MainPage.this);
    			builder.setMessage("You must be connected to Internet");
    			builder.setCancelable(false);
    			builder.setPositiveButton("Okay", new DialogInterface.OnClickListener() {

    				@Override
    				public void onClick(DialogInterface dialog, int which) {
    					// TODO Auto-generated method stub	
    					//((MainPage) con).finish();
    					moveTaskToBack(true);
    				}
    			});
    			//AlertDialog alert = builder.create();
    			//alert.show();
    		}
            
            try {
                if (json.getInt("success") == 1) {
                	
                	JSONToEntityConverter jConverter = new JSONToEntityConverter(maxImgSize);
        			ArrayList<Entity> ents = jConverter.convertAll(json);
        			
        			
        			it.itin = ents;
        			it.addStart();
        			it.setId(json.getString("itinID"));
        			it.setDemoID(json.getInt("demoID"));
        			it.setCurrentEnts(ents);
        			setItinerary(it);
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
    				if( distance.substring(distance.length()-2).equals("ft") ){
    					it.setTotalDistance(it.getTotalDistance() + (Double.parseDouble(distance.substring(0, distance.length()-3))/5280));
    				}
    				else{
    					it.setTotalDistance(it.getTotalDistance() + Double.parseDouble(distance.substring(0, distance.length()-3)));
    				}
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
        		if(pDialog!=null) {pDialog.dismiss();}
        		AlertDialog.Builder builder = new AlertDialog.Builder(MainPage.this);
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
	    		currentItin = getItinerary();
	    		CalcDistanceAfterDragAsync calcDistanceAfterDragAsync = new CalcDistanceAfterDragAsync(getItinerary(), MainPage.this, cl);
				calcDistanceAfterDragAsync.execute();
				
				try {
					calcDistanceAfterDragAsync.get();
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (ExecutionException e) {
					e.printStackTrace();
				}
	
				//rePopulate the jsonOutputList for path
				/*jsonOutputList.clear();
				for(Entity obj : getItinerary().itin){
					if(getItinerary().itin.indexOf(obj)!=0){
						jsonOutputList.add(obj.getJsonOutput());
					}
				}*/
				
	    		updateMap();
	    		updateList();
	    		
	    		//reset the locked rows for new itinerary
	//    		SharedPreferences.Editor editor = settings.edit();
	//    		editor.putString("lockedRows", "0");
	//    		editor.commit();
	    		
	    		// Wade - update the string to reflect the moved elements
	    		/*	String newLocked = "0";
	    		tempIndex = 0;
	
	    		for(Entity obj : getItinerary().itin){
	    			if( obj.isLocked() ) {
	    				newLocked = newLocked + tempIndex;
	    			}
	    			tempIndex++;
	    		}
	
	    		editor = settings.edit();
	
	    		editor.putString("lockedRows", newLocked+"");
	    		editor.commit();*/
	    		firstItin = false;
        	}
	    		// dismiss the dialog after getting all products
	        pDialog.dismiss();
        }
 
    }

	class GetDistance extends AsyncTask<Void, Void, JSONObject> {

	    /**
	     * Before starting background thread Show Progress Dialog
	     * */
	    @Override
	    protected void onPreExecute() {
	        super.onPreExecute();
	    }

	    /**
	     * getting search results from url
	     * */
	    protected JSONObject doInBackground(Void... args) {
	    	getItinerary().setTotalDistance(0);
			for( int i=0; i<getItinerary().itin.size() - 1; i++ ){
				double lat1 = Double.parseDouble(getItinerary().itin.get(i).getLatitude());
				double lon1 = Double.parseDouble(getItinerary().itin.get(i).getLongitude());
				double lat2 = Double.parseDouble(getItinerary().itin.get(i+1).getLatitude());
				double lon2 = Double.parseDouble(getItinerary().itin.get(i+1).getLongitude());
				
				CalcDistance calcDist = new CalcDistance();
				String jsonString = calcDist.getDistanceInfo(lat1,lon1,lat2,lon2);
				try {
					JSONObject jsonObject = new JSONObject(jsonString);
					// routesArray contains ALL routes
					JSONArray routesArray = jsonObject.getJSONArray("routes");
					// Grab the first route
					JSONObject route = routesArray.getJSONObject(0);
					// Take all legs from the route
					JSONArray legs = route.getJSONArray("legs");
					// Grab first leg
					JSONObject leg = legs.getJSONObject(0);

					JSONObject distanceObject = leg.getJSONObject("distance");
					String distance = distanceObject.getString("text");
					getItinerary().itin.get(i+1).setDistance(distance);
					if( distance.substring(distance.length()-2).equals("ft") ){
						getItinerary().setTotalDistance(getItinerary().getTotalDistance() + (Double.parseDouble(distance.substring(0, distance.length()-3))/5280));
					}
					else{
						getItinerary().setTotalDistance(getItinerary().getTotalDistance() + Double.parseDouble(distance.substring(0, distance.length()-3)));
					}
					JSONObject durationObject = leg.getJSONObject("duration");
					getItinerary().itin.get(i+1).setDuration(durationObject.getString("text"));
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
	        return null;
	    }

	    /**
	     * After completing background task Dismiss the progress dialog
	     * **/
	    protected void onPostExecute(String file_url) {
	    	updateList();
	    	updateMap();
	    }
	}

	protected class GetNewEntity extends AsyncTask<Integer, String, Integer> {

		private JSONObject jsonObject;
		private CalcDistance calcDist;
		private String distance;
		private String url_new_itin = "http://citywhisk.com/scripts/GetEnt/getNewEntity.php";
		ProgressDialog pDialog;
		JSONParser jParser = new JSONParser();
		Entity e = new Entity();
		
		String oldEntID;
		String newEntID;
		String beforeEntID;
		String afterEntID;
		String position;
		
        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            if (pDialog == null) {
            	pDialog = createProgressDialog(MainPage.this);
            	pDialog.show();
            } else {
                pDialog.show();
            }
            calcDist = new CalcDistance();
        }
        /**
         * getting search results from url
         * */
        protected Integer doInBackground(Integer... args) {
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("demoID",getItinerary().getDemoID()+""));
            params.add(new BasicNameValuePair("itinID",getItinerary().getId()+""));
    		getItinerary().setCurrentEnts(getItinerary().itin);
    		params.add(new BasicNameValuePair("currentEnts",getItinerary().getCurrentEnts()));
            
            // getting JSON string from URL
            JSONObject json = jParser.makeHttpRequest(url_new_itin, "POST", params);
 
            try {
				if(json.getInt("success") != 1){
					// connection is messed up (might have to agree to join network)
					AlertDialog.Builder builder = new AlertDialog.Builder(MainPage.this);
					builder.setMessage("You must be connected to Internet 1474");
					builder.setCancelable(false);
					builder.setPositiveButton("Okay", new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub	
							//((MainPage) con).finish();
							moveTaskToBack(true);
						}
					});
					//AlertDialog alert = builder.create();
					//alert.show();
				}
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
            
            // save ids
            position = String.valueOf(args[0]);
            oldEntID = String.valueOf(getItinerary().itin.get(args[0]).getTourId());
            if( args[0] != 1 ){
            	beforeEntID = String.valueOf(getItinerary().itin.get(args[0]-1).getTourId());
            }else{
            	beforeEntID = null;
            }
            if( args[0] != getItinerary().itin.size()-1 ){
            	afterEntID = String.valueOf(getItinerary().itin.get(args[0]+1).getTourId());
            }else{
            	afterEntID = null;
            }
            
            
            try {
                if (json.getInt("success") == 1) {
                	
                	JSONToEntityConverter jConverter = new JSONToEntityConverter();
    				ArrayList<Entity> ents = jConverter.convertAll(json);
    				e = ents.get(0);
    				newEntID = String.valueOf(e.getTourId());
    				getItinerary().itin.set(args[0], e);
 
                } else {
                    // no entities found
                	// no search matches your request
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
 
            return args[0];
        }
        
        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(Integer index) {

    		
    		currentItin = getItinerary();
    		CalcDistanceAfterDragAsync calcDistanceAfterDragAsync = new CalcDistanceAfterDragAsync(getItinerary(), MainPage.this, cl);
			calcDistanceAfterDragAsync.execute();
			
			try {
				calcDistanceAfterDragAsync.get();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
			
			new DatabaseSwipeInsert(androidID,oldEntID,newEntID,beforeEntID,afterEntID,position,getItinerary().getId()).execute();
			
    		updateMap();
    		updateList();
    		itinList.set(getItinListIndex(), getItinerary());

    		// dismiss the dialog after getting all products
            pDialog.dismiss();
        }
 
    }

	public int getItinListIndex() {
		return itinListIndex;
	}
}

