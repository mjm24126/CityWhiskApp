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

import java.awt.*;
import java.io.Console;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import com.citywhisk.citywhisk.MainPage.GetDistance;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

//Fragment for Reset menu
public class FragSearch extends Fragment {

	private boolean searchByName;
	Button nameSearchBtn;
	Button actSearchBtn;
	
	MainPage mainPage;
	
	ArrayList<Entity> search = new ArrayList<Entity>();
	
	Entity currentTour;
	
	CalcDistance calcDistanceObj = new CalcDistance();
	
	private String currentTag = null;
	
	boolean firstTime = true;
	
	public ProgressDialog pDialog;
	
	SearchView searchTextView;
    ArrayList<Entity> list;
    ListView searchList;
	
    private boolean isInItin;
    private Itinerary it;
    
	@Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mainPage = (MainPage) activity;
    }

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceSate){
		
		return inflater.inflate(R.layout.fragsearch, container,false);
	}
	
	@Override
	public void onStart() {
		super.onStart();
		
		SearchManager searchManager = (SearchManager) mainPage.getSystemService(Context.SEARCH_SERVICE);
		
		searchByName = true;
		nameSearchBtn = (Button) getActivity().findViewById(R.id.searchByName);
		actSearchBtn = (Button) getActivity().findViewById(R.id.searchByAct);
		
		searchTextView = (SearchView) getActivity().findViewById(R.id.searchPlaces);
		searchTextView.setSearchableInfo(searchManager.getSearchableInfo(mainPage.getComponentName()));
		searchTextView.setIconifiedByDefault(false);
		
		searchList = (ListView) getActivity().findViewById(R.id.searchList);
		
		list = new ArrayList<Entity>();
		ArrayAdapter<Entity> adaptor = new ArrayAdapter<Entity>(getActivity(),android.R.layout.simple_list_item_1, list);
		searchList.setAdapter(adaptor);
		adaptor.notifyDataSetChanged();
		
		searchList.setOnItemClickListener(new OnItemClickListener()
		{

			public void onItemClick(AdapterView<?> arg0, View arg1, final int pos,long arg3) {
				
				JSONToEntityConverter converter = new JSONToEntityConverter();
				
				final Entity ent = converter.convertEnt(mainPage.searchJson, pos);
				it = mainPage.getItinerary();
				ArrayList<Integer> itinEnts = new ArrayList<Integer>();
				for( Entity e : it.itin ){
					itinEnts.add(e.getTourId());
				}
				if( !itinEnts.contains(ent.getTourId()) ){
					isInItin = false;
				}
				else{
					isInItin = true;
				}

				mainPage.searchEntity = ent;
				Intent intent = new Intent(mainPage,DetailClass.class);
				intent.putExtra("isInItin", isInItin );
				mainPage.startActivityForResult(intent,0);
			}

		});
		
		nameSearchBtn.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View view) {
				if( !searchByName ) {
					searchByName = true;
					searchToggleClick();
				}
			}
			
		});
		actSearchBtn.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View view) {
				if( searchByName ) {
					searchByName = false;
					searchToggleClick();
				}
			}
			
		});
		searchTextView.requestFocus();
	}
	
	public void searchToggleClick(){
		if ( searchByName ){
			nameSearchBtn.setBackgroundResource(R.drawable.mapbarbutton);
			actSearchBtn.setBackgroundResource(R.drawable.mapbarbuttonpressed);
		}
		else {
			nameSearchBtn.setBackgroundResource(R.drawable.mapbarbuttonpressed);
			actSearchBtn.setBackgroundResource(R.drawable.mapbarbutton);
		}
	}

	public boolean getSearchByName() {
		return searchByName;
	}
	
	

}