package com.citywhisk.citywhisk;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.citywhisk.citywhisk.MainPage.GetDistance;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnShowListener;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

//Fragment for favorite menu
public class FragSave extends Fragment{
	
	public static final int MENU_DELETE = 1;
	
	public ArrayAdapter<String> favAdapter;
	
	ArrayList<String> favoritesList;
	
	MainPage mainPage;
	
	DatabaseManager dbManager;
	
	@Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        
    }

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceSate){
		return inflater.inflate(R.layout.fragsave, container,false);
	}

	@Override
	public void onStart() {
		super.onStart();

		final ListView myList = (ListView) getActivity().findViewById(R.id.favList);
		
		mainPage = (MainPage) getActivity();
		dbManager = mainPage.dbManager;

		favAdapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1, dbManager.getSavedItineraries());

		myList.setAdapter(favAdapter);
		favAdapter.notifyDataSetChanged();
		
		myList.setOnItemLongClickListener(new OnItemLongClickListener(){

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View v,
					int position, long arg3) {
				final AlertDialog.Builder builder = new AlertDialog.Builder(mainPage);
				String selected = ((TextView) v).getText().toString();
				
				LinearLayout layout = new LinearLayout(mainPage);
		        LinearLayout.LayoutParams parms = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		        layout.setOrientation(LinearLayout.VERTICAL);
		        layout.setLayoutParams(parms);
		        final int pos = position; 
		        
		        TextView tv = new TextView(mainPage);
		        tv.setText("Delete "+selected+"?");
		        tv.setTextColor(Color.parseColor("#646363"));
		        tv.setPadding(10, 25, 10, 25);
		        tv.setGravity(Gravity.CENTER);
		        tv.setTextSize(20);
		        LinearLayout.LayoutParams tv1Params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		        layout.addView(tv,tv1Params);
				
				builder.setView(layout);
				builder.setPositiveButton(R.string.yes, null);
				builder.setNegativeButton(R.string.no, null);
				
				final AlertDialog ad = builder.create();
				ad.setOnShowListener(new OnShowListener() {

					@Override
					public void onShow(DialogInterface dia) {
						
						Button posBtn = ad.getButton(AlertDialog.BUTTON_POSITIVE);
							posBtn.setOnClickListener(new View.OnClickListener() {

					            @Override
					            public void onClick(View view) {
						        	//Log.i("fav at position "+pos,favAdapter.getItem(pos));
							    	dbManager.removeSavedItin(favAdapter.getItem(pos));
							    	favAdapter.remove(favAdapter.getItem(pos));
									
									ad.dismiss();
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
				ad.show();
				return true;
			}
			
		});

		myList.setOnItemClickListener(new OnItemClickListener()
		{

			public void onItemClick(AdapterView<?> arg0, final View arg1, final int pos,long arg3) {
				
				final AlertDialog.Builder builder = new AlertDialog.Builder(mainPage);
				String selected = ((TextView) arg1).getText().toString();
				
				LinearLayout layout = new LinearLayout(mainPage);
		        LinearLayout.LayoutParams parms = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		        layout.setOrientation(LinearLayout.VERTICAL);
		        layout.setLayoutParams(parms);
		        
		        TextView tv = new TextView(mainPage);
		        tv.setText("Load "+selected+"?");
		        tv.setTextColor(Color.parseColor("#646363"));
		        tv.setPadding(10, 25, 10, 25);
		        tv.setGravity(Gravity.CENTER);
		        tv.setTextSize(20);
		        LinearLayout.LayoutParams tv1Params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		        layout.addView(tv,tv1Params);
				
				builder.setView(layout);
				builder.setPositiveButton(R.string.yes, null);
				builder.setNegativeButton(R.string.no, null);
				
				final AlertDialog ad = builder.create();
				ad.setOnShowListener(new OnShowListener() {

					@Override
					public void onShow(DialogInterface dia) {
						
						Button posBtn = ad.getButton(AlertDialog.BUTTON_POSITIVE);
							posBtn.setOnClickListener(new View.OnClickListener() {

					            @Override
					            public void onClick(View view) {
					            	String selected = ((TextView) arg1).getText().toString();
									Itinerary saved = dbManager.getSavedItinerary(selected);
									
									mainPage.setItinerary(saved);
									mainPage.new GetDistance().execute();
									
									CalcDistanceAfterDragAsync calcDistanceAfterDragAsync = new CalcDistanceAfterDragAsync(mainPage.getItinerary(), mainPage, mainPage.cl);
									calcDistanceAfterDragAsync.execute();
									//mainPage.cl.notifyDataSetChanged();
									
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

									// Re-center the googleMap
									mainPage.updateMap();
									mainPage.updateList();
									
									ad.dismiss();
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
				ad.show();
			}
		});
		
		Button addToFav = (Button) getActivity().findViewById(R.id.addToFavButton);

		addToFav.setOnClickListener( new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
				LayoutInflater inflater = getActivity().getLayoutInflater();;
				builder.setView(inflater.inflate(R.layout.nameitinerary, null));
				builder.setPositiveButton(R.string.save, null);
				builder.setNegativeButton(R.string.cancel, null);
				final AlertDialog ad = builder.create();
				ad.setOnShowListener(new OnShowListener() {

					@Override
					public void onShow(DialogInterface dia) {
						Button pos = ad.getButton(AlertDialog.BUTTON_POSITIVE);
				        pos.setOnClickListener(new View.OnClickListener() {

				            @Override
				            public void onClick(View view) {
				            	Editable name = ((EditText) ad.findViewById(R.id.nameItineraryField)).getText();
								TextView tv = ((TextView) ad.findViewById(R.id.nameAlertField));
								if( name.toString().matches("")) {
									tv.setText("* Please name this itinerary *");
								}
								else if( dbManager.getSavedItineraries().contains(name.toString())){
									tv.setText("* Itinerary names must be unique *");
								}
								else{
									// insert into db, set name and id 
									mainPage.getItinerary().setItinName(name.toString());
									
									long id = dbManager.insertItinerary(mainPage.getItinerary());
									mainPage.getItinerary().setItinID((int) id);
									
									// update saved itineraries listview
									mainPage.favList = dbManager.getSavedItineraries();
									favAdapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1, dbManager.getSavedItineraries());
									myList.setAdapter(favAdapter);
									favAdapter.notifyDataSetChanged();
									
									//Dismiss once everything is OK.
					                ad.dismiss();
								}
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
				ad.show();
			}
			
		});

	}
	
	// This is executed when the user select an option
	@Override
	public boolean onContextItemSelected(MenuItem item) {
	AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
	switch (item.getItemId()) {
	    case 1:
	    	//Log.i("fav at position "+info.position,favAdapter.getItem(info.position));
	    	dbManager.removeSavedItin(favAdapter.getItem(info.position));
	    	favAdapter.remove(favAdapter.getItem(info.position));
	        return true;
	    default:
	        return super.onContextItemSelected(item);
	   }
	}
	
	
	class GetSavedItin extends AsyncTask<String, Void, Void> {
		
		ProgressDialog pDialog;
		Itinerary saved;
	    /**
	     * Before starting background thread Show Progress Dialog
	     * */
	    @Override
	    protected void onPreExecute() {
	        super.onPreExecute();
	        if (pDialog == null) {
            	pDialog = mainPage.createProgressDialog(mainPage);
            	pDialog.show();
            } else {
                pDialog.show();
            }
	    }
	
	    /**
	     * getting search results from url
	     * @return 
	     * */
	    protected Void doInBackground(String... args) {
	    	saved = dbManager.getSavedItinerary(args[0]);
	    	saved.setCurrentEnts(saved.itin);
			
			//getDistances();
	    	//mainPage.itinerary = saved;
			//mainPage.getDistances();
			//Log.i("","doinbg");
			return null;
	    }
	
	    public void getDistances(){
    		saved.setTotalDistance(0);
    		saved.calcDist = new CalcDistance();
    		for( int i=0; i<saved.itin.size() - 1; i++ ){
    			double lat1 = Double.parseDouble(saved.itin.get(i).getLatitude());
    			double lon1 = Double.parseDouble(saved.itin.get(i).getLongitude());
    			double lat2 = Double.parseDouble(saved.itin.get(i+1).getLatitude());
    			double lon2 = Double.parseDouble(saved.itin.get(i+1).getLongitude());
    			
    			String jsonString = saved.calcDist.getDistanceInfo(lat1,lon1,lat2,lon2);
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
    				saved.itin.get(i+1).setDistance(distance);
    				saved.setTotalDistance(saved.getTotalDistance() + Double.parseDouble(distance.substring(0, distance.length()-3)));
    				
    				JSONObject durationObject = leg.getJSONObject("duration");
    				saved.itin.get(i+1).setDuration(durationObject.getString("text"));
    			} catch (JSONException e) {
    				// TODO Auto-generated catch block
    				e.printStackTrace();
    			}
    		}
    	}
	    
	    /**
	     * After completing background task Dismiss the progress dialog
	     * **/
	    protected void onPostExecute(String file_url) {
	    	mainPage.itinerary = saved;
	    	mainPage.cl = new CustomListView(mainPage, R.layout.customlist, mainPage.getItinerary().itin, mainPage.getItinerary());
	    	mainPage.lV.setAdapter(mainPage.cl);
	    	mainPage.cl.notifyDataSetChanged();
	    	mainPage.currentItin = mainPage.getItinerary();
	    	mainPage.updateMap();
    		
    		//reset the locked rows for new itinerary
    		SharedPreferences.Editor editor = mainPage.settings.edit();
    		editor.putString("lockedRows", "0");
    		editor.commit();
    		
    		// Wade - update the string to reflect the moved elements
    		String newLocked = "0";
    		

    		editor = mainPage.settings.edit();

    		editor.putString("lockedRows", newLocked+"");
    		editor.commit();
    		mainPage.firstItin = false;

    		// dismiss the dialog after getting all products
            pDialog.dismiss();
            
	        // dismiss the dialog after getting all products
	        //pDialog.dismiss();
	        //itinList = (ListView) mainPage.findViewById(R.id.listViewMain);

	        
//			mainPage.cl = new CustomListView(mainPage, R.layout.customlist, mainPage.getItinerary().itin, mainPage.getItinerary());
//			mainPage.lV.setAdapter(mainPage.cl);
//			mainPage.cl.notifyDataSetChanged();
	        
	    }

	}
}
