package com.citywhisk.citywhisk;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import com.mobeta.android.dslv.DragSortController;
import com.mobeta.android.dslv.DragSortListView;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;
import android.view.ActionMode;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class CustomListView extends ArrayAdapter<Entity> implements OnMenuItemClickListener {
	
	private static final String MenuItem = null;
	Context c;
	SharedPreferences settings;
	String toogleView;
	ActionMode mActionMode;
	Entity indexToDelete;
	private ArrayList<Entity> objects;
	private Entity startingLocation;
	private Itinerary itinerary;
	MyGestureDetector gestos;

	public CustomListView(MainPage mainPage, int textViewResourceId, ArrayList<Entity> itin, Itinerary it){
		super(mainPage, textViewResourceId, itin);
		c = mainPage;
		this.objects = itin;
		settings = c.getSharedPreferences("CITYWHISK", 0);
	}
	public CustomListView(MainPage mainPage, int textViewResourceId, ArrayList<Entity> itin){
		super(mainPage, textViewResourceId, itin);
		c = mainPage;
		this.objects = itin;
		settings = c.getSharedPreferences("CITYWHISK", 0);	
	}

	private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {

	    // Called when the action mode is created; startActionMode() was called
	    @Override
	    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
	        // Inflate a menu resource providing context menu items
	        MenuInflater inflater = mode.getMenuInflater();
	        inflater.inflate(R.menu.context_menu, menu);
	        return true;
	    }

	    // Called each time the action mode is shown. Always called after onCreateActionMode, but
	    // may be called multiple times if the mode is invalidated.
	    @Override
	    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
	        return false; // Return false if nothing is done
	    }

	    // Called when the user selects a contextual menu item
	    @Override
	    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
	        switch (item.getItemId()) {
	            case R.id.delete_menu_item:
	                mode.finish(); // Action picked, so close the CAB
	                return true;
	            default:
	                return false;
	        }
	    }

	    // Called when the user exits the action mode
	    @Override
	    public void onDestroyActionMode(ActionMode mode) {
	        mActionModeCallback = null;
	        mActionMode = null;
	    }


	};
	
	public boolean areAllItemsEnabled () {
	    return false;
	}
	
	public boolean isEnabled (int position) {
	    if (position == 0) {
	       return false;
	    }
	    return true;
	}

	
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		View v = convertView;
		final String pos = Integer.toString(position);
		// first check to see if the view is null. if so, we have to inflate it.
		
		if (v == null) {
			LayoutInflater inflater = (LayoutInflater) getContext()
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = inflater.inflate(R.layout.customlist, null);
			
			if( position == 0 ){
				v.setEnabled(false);
			}
			// testing setting active itinerary stop to different color
			/*if( position == 2 ){
				v.setBackgroundResource(R.drawable.listgradient_active);
			}*/
		}
		
		final Entity i = objects.get(position);
		
		if (i != null) {

			TextView listViewRow = (TextView) v.findViewById(R.id.listViewRow);
			listViewRow.setText(i.getName());
			TextView distance = (TextView) v.findViewById(R.id.distance);
			toogleView = settings.getString("toogleView", "");
			/*
			 * Sets the distance for the rows except the last destination in Tours(list)
			 */
			if (position != 0) {

				/*
				 * Toggle the information in the ListView
				 */
				if(toogleView.equals("distance")){
					if( objects.get(position).getDistance() != null )
						distance.setText(objects.get(position).getDistance());
				}
				else if (toogleView.equals("price")){
					distance.setText(String.format("$ %.2f - $ %.2f", objects.get(position).getLowPrice(), objects.get(position).getHighPrice()));
				}
				else if(toogleView.equals("time")){
					int totalminutes = (int) objects.get(position).getTime();
					int minutes = totalminutes % 60;
					int hours = totalminutes / 60;
					String traveltime = objects.get(position).getDuration();
					if( hours == 0 ){
						distance.setText(String.format("%d mins + %s",minutes,traveltime));
					}
					else if( minutes == 0 ){
						if( hours == 1 )
							distance.setText(String.format("%d hr + %s",hours,traveltime));
						else distance.setText(String.format("%d hrs + %s",hours,traveltime));
					}
					else{
						if( hours == 1 )
							distance.setText(String.format("%d hr %d mins + %s",hours,minutes,traveltime));
						else distance.setText(String.format("%d hrs %d mins + %s",hours,minutes,traveltime));
						
					}
				}

			}
			if(position==0){
				if (toogleView.equals("distance")){
					double dist = 0;
					for( int e = 1; e < objects.size(); e++ ){
						if( objects.get(e).getDistance() != null )
							if( objects.get(e).getDistance().substring(objects.get(e).getDistance().length()-2) == "mi" )
								dist += Double.parseDouble(objects.get(e).getDistance().substring(0,objects.get(e).getDistance().length()-3));
							else if(objects.get(e).getDistance().substring(objects.get(e).getDistance().length()-2) == "ft" ){
								double ft = Double.parseDouble(objects.get(e).getDistance().substring(0,objects.get(e).getDistance().length()-3));
								dist += (ft/5280);
							}
								
					}
					
					distance.setText(String.format("Total Distance : %.1f miles", ((MainPage)c).getItinerary().getTotalDistance()));
					
				}
				else if (toogleView.equals("price")){
					
					distance.setText(String.format("Total Price : $ %.2f - $ %.2f", objects.get(position).getLowPrice(), objects.get(position).getHighPrice()));
				}
				else if(toogleView.equals("time")){
					int totalminutes = 0;
					for( Entity e : objects ){
						totalminutes += e.getTime();
					}
					int minutes = totalminutes % 60;
					int hours = totalminutes / 60;
					if( hours == 0 ){
						distance.setText(String.format("Total Time : %d mins + travel",minutes));
					}
					else if( minutes == 0 ){
						if( hours == 1 )
							distance.setText(String.format("Total Time : %d hr + travel",hours));
						else distance.setText(String.format("Total Time : %d hrs + travel",hours));
					}
					else{
						if( hours == 1 )
							distance.setText(String.format("Total Time : %d hr %d mins + travel",hours,minutes));
						else distance.setText(String.format("Total Time : %d hr %d mins + travel",hours,minutes));
						
					}
				}
				else{
					distance.setText(String.format("Total Savings : $ %.2f",objects.get(position).getSavings()));
				}
				
			}

			TextView number = (TextView) v.findViewById(R.id.number);
			//typeFace = Typeface.createFromAsset(getContext().getAssets(),
				//	"fonts/GOTHIC.TTF");
			//number.setTypeface(typeFace);
			number.setText("" + (objects.indexOf(i) + 1));

			
			/**
			 * 
			 * UNCOMMENT FOR sale tag
			 * 
			 */
			/*ImageView saleImage = (ImageView) v.findViewById(R.id.saleTag);
			if ( i.getPromo() == true ) {

				saleImage.setImageResource(R.drawable.salestag_green);
			} else {
				saleImage.setImageDrawable(null);
			}*/

		}

		/*final CheckBox entityHoldCheck = (CheckBox) v.findViewById(R.id.entityHold);
		entityHoldCheck.setFocusable(false);

		if (position == 0) {
			entityHoldCheck.setChecked(true);
			entityHoldCheck.setEnabled(false);
		} else {
			String locked = settings.getString("lockedRows", "");

			if( i.isLocked() ) {
				entityHoldCheck.setChecked(true);
			} else {
				entityHoldCheck.setChecked(false);
			}
			entityHoldCheck.setEnabled(true);
		}

		entityHoldCheck.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				SharedPreferences.Editor editor = settings.edit();

				if (entityHoldCheck.isChecked() == true) {
					((MainPage) c).lockedPositions.add(position);
					((MainPage) c).getItinerary().itin.get(position).setLocked(true);
					editor.putString("lockedRows",
							settings.getString("lockedRows", "") + pos);
				} else {
					if( ((MainPage) c).lockedPositions.contains(pos) ) {
						((MainPage) c).lockedPositions.remove(((MainPage) c).lockedPositions.indexOf(pos));
					}
					((MainPage) c).getItinerary().itin.get(position).setLocked(false);
					String str = settings.getString("lockedRows", "");
					str = str.replace(pos, "");
					editor.putString("lockedRows", str);
				}
				editor.commit();
				notifyDataSetChanged();
				((MainPage) c).controller.setLockedPositions(((MainPage) c).lockedPositions);
				//Log.i("locked pos", ((MainPage) c).lockedPositions.toString());
			}
		});*/

		ImageView draghandler = (ImageView) v.findViewById(R.id.savingImage);
		if(position==0){
			draghandler.setImageDrawable(null);
		}
		else{
			draghandler.setImageResource(R.drawable.toggle);
		}
		// the view must be returned to our activity
		return v;
	}

	@Override
	public void notifyDataSetChanged(){
		super.notifyDataSetChanged();
		
		for( int i=0; i<((MainPage) c).getItinerary().itin.size(); i++ ){
			if( ((MainPage) c).getItinerary().itin.get(i).isLocked() ){
				if( ((MainPage) c).lockedPositions.contains(i) ){
					
				}
				else{
					((MainPage) c).lockedPositions.add(i);
				}
				
			}
		}
	}
	
	@Override
	public void insert(Entity object, int index) {
		// TODO Auto-generated method stub
		super.insert(object, index);
	}

	@Override
	public void remove(Entity object) {
		// TODO Auto-generated method stub
		super.remove(object);
	}

	@Override
	public boolean onMenuItemClick(android.view.MenuItem item) {
		// TODO Auto-generated method stub
		return false;
	}
	
}