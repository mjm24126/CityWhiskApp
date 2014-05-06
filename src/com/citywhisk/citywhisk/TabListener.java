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

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.SearchView;

/*
 * Class to listen for switching between main page tabs
 */
public class TabListener<T extends Fragment> implements ActionBar.TabListener {
	private Fragment mFragment;
	private final Activity mActivity;
	private final String mTag;
	private final Class<T> mClass;
	
	//public Fragment currentfrag;
	
	public FragMap mapfrag;
	public FragSearch searchfrag;
	public FragSave savefrag;
	public FragPref preffrag;
	
	public Fragment current;
	
	private SharedPreferences settings;
	private Object supportClassObj;
	
	private SearchView searchView;
	
	/** Constructor used each time a new tab is created.
	 * activity  The host Activity, used to instantiate the fragment
	 * The identifier tag for the fragment
	 * The fragment's Class, used to instantiate the fragment
	 */
	public TabListener(Activity activity, String tag, Class<T> clz) {
		mActivity = activity;
		mTag = tag;
		mClass = clz;
		
		searchView = (SearchView) ((MainPage) mActivity).findViewById(R.id.searchPlaces);
	}
	
	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		// Check if the fragment is already initialized
		////Log.i("on tab selected",tab.getText().toString());
		if(tab.getText().toString().equals("SAVED")){
			////Log.i("infav",current.toString());
			//if ( favfrag == null ) {
			//savefrag = (FragSave) current;
			//}
			
			// check to make sure keyboard is gone
			if ( searchView != null ){
				searchView.clearFocus();
	    		InputMethodManager in = (InputMethodManager) ((MainPage)mActivity).getSystemService(Context.INPUT_METHOD_SERVICE);
	    		in.hideSoftInputFromWindow(searchView.getWindowToken(), 0);
			}
			
			// if any frag but preferences is showing we can just replace the container fragment
			if ( current == mapfrag || current == searchfrag || current == savefrag ) {
				current = new FragSave();
				mActivity.getFragmentManager().beginTransaction().replace(R.id.tabContainer, current,"SAVEFRAG").commit();
				((MainPage)mActivity).currentfrag = current;
			}
			// preferences frag is showing
			else { 
				ft.hide(current);
				current = new FragSave();
				mActivity.getFragmentManager().beginTransaction().replace(R.id.tabContainer, current,"SAVEFRAG").commit();
				((MainPage)mActivity).currentfrag = current;
			}
			
			//current = savefrag;
		}
		if(tab.getText().toString().equals("SEARCH")){
			////Log.i("insearch",current.toString());
			//if ( searchfrag == null ) {
				//searchfrag = (FragSearch) current;
			//}
			
			// check to make sure keyboard is gone
			if ( searchView != null ){
				searchView.clearFocus();
	    		InputMethodManager in = (InputMethodManager) ((MainPage)mActivity).getSystemService(Context.INPUT_METHOD_SERVICE);
	    		in.hideSoftInputFromWindow(searchView.getWindowToken(), 0);
			}
			
			// if any frag but preferences is showing we can just replace the container fragment
			if ( current == mapfrag || current == searchfrag || current == savefrag ) {
				current = new FragSearch();
				mActivity.getFragmentManager().beginTransaction().replace(R.id.tabContainer, current,"SEARCHFRAG").commit();
				((MainPage)mActivity).currentfrag = current;
			}
			// preferences frag is showing
			else { 
				ft.hide(current);
				current = new FragSearch();
				mActivity.getFragmentManager().beginTransaction().replace(R.id.tabContainer, current,"SEARCHFRAG").commit();
				((MainPage)mActivity).currentfrag = current;
			}
			
			//current = searchfrag;
			////Log.i("insearch",current.toString());
		}
		if(tab.getText().toString().equals("PREF")){  // do something depending on tab clicked
			////Log.i("inpref",current.toString());
			if (mFragment == null) {
				// If not, instantiate and add it to the activity
				mFragment = Fragment.instantiate(mActivity, mClass.getName());
				ft.add(android.R.id.content, mFragment, mTag);
				////Log.i("TABSELCTED","NEW OBJECT CREATED");
			} else {
				// If it exists, simply attach it in order to show it
				ft.show(mFragment);
				////Log.i("TABSELECTED",tab.getText().toString());
			}
			preffrag = (FragPref) mFragment;
			current = mFragment;
			////Log.i("TAB CLICKED", tab.getText().toString() + " " + mFragment);
			//populateFavObj.populateList();
		}
		if(tab.getText().toString().equals("HOME")){  // do something depending on tab clicked
			////Log.i("inhome", current.toString());
			////Log.i("TAB CLICKED", tab.getText().toString());
			

			// NEW CHANGE
			if(((MainPage) mActivity).firstItin == false) {	
				

				if ( current == mapfrag || current == searchfrag || current == savefrag ) {
					mapfrag = new FragMap();
					mActivity.getFragmentManager().beginTransaction().replace(R.id.tabContainer, mapfrag,"MAPFRAG").commit();
					current = mapfrag;
					((MainPage)mActivity).currentfrag = current;
					//mapfrag.generatemarkers();
					//supportClassObj.fitInMap(((MainPage)mActivity).itinerary, googlemap);
				}
				// preferences frag is showing
				else { 
					ft.hide(current);
					mActivity.getFragmentManager().beginTransaction().replace(R.id.tabContainer, mapfrag,"MAPFRAG").commit();
					current = mapfrag;
					((MainPage)mActivity).currentfrag = current;
				}
			}
			else {
				////Log.i("tab listener", "inside first = true");
				
				if (mFragment == null) {
					// If not, instantiate and add it to the activity
					mFragment = Fragment.instantiate(mActivity, mClass.getName());
					ft.add(R.id.tabContainer, mFragment, "MAPFRAG");
					current = mFragment;
					//mapfrag = (FragMap) mFragment;
					((MainPage)mActivity).currentfrag = current;
				} else {
					// If it exists, simply attach it in order to show it
					ft.show(mFragment);
					current = mFragment;
				}
				
				((MainPage) mActivity).firstItin = false;
			}
			////Log.i("TabListener", "mapfrag -> currentfrag");
			current = mapfrag;
		} 
		
		
		
	}

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
		if ( current == preffrag ) {
			if (mFragment != null) {
				// Detach the fragment, because another one is being attached
				ft.hide(mFragment);
			}
		}
		if(tab.getText().toString().equals("SEARCH")){
			SearchView searchView = (SearchView) ((MainPage)mActivity).findViewById(R.id.searchPlaces);
			searchView.clearFocus();
    		InputMethodManager in = (InputMethodManager) ((MainPage)mActivity).getSystemService(Context.INPUT_METHOD_SERVICE);
    		in.hideSoftInputFromWindow(searchView.getWindowToken(), 0);
		}
	}

}
