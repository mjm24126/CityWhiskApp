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

import java.io.Serializable;
import java.util.ArrayList;

import com.google.android.gms.maps.model.LatLng;

import android.content.Context;
import android.util.Log;
import android.widget.ListView;


public class Itinerary  implements Serializable{
	public ArrayList<Entity> itin;
	
	private int maxPrice = 100000;
	private int maxTime = 100000;
	private int maxDistance = 0;
	
	private int itinID;  // for sqlite database!
	private String id;   // for server database!
	
	private String name;
	
	private double totalLowCost = 0;
	private double totalHighCost = 0;
	private double totalTime = 0;
	private double totalDistance = 0;
	
	private Entity startingLocation;
	
	protected transient CalcDistance calcDist;
	private int demoID;
	
	Context con;
	
	// Creating JSON Parser object
	//transient JSONParser jParser = new JSONParser();
	//public ProgressDialog pDialog;
	private String currentEnts = "(";
	public ListView itinList;
	private ArrayList<String> polyline;
	
	public void addStart(){
		startingLocation = new Entity();
		startingLocation.setLatitude(""+((MainPage)con).getUserLat());
		startingLocation.setLongitude(""+((MainPage)con).getUserLon());
		startingLocation.setName("Starting Location");
		itin.add(0,startingLocation);
	}
	
	public void add(Entity e) {
		itin.add(e);
	}
	
	public Itinerary() {
		setItinID(-1);
		itin = new ArrayList<Entity>();
		calcDist = new CalcDistance();
		polyline = new ArrayList<String>();
	}
	
	public void print(String tag) 
	{
		for (Entity e : itin) {
			//Log.i(tag,e.getName() +", "+ e.getLatitude()+","+e.getLongitude()+", "+e.getDistance() );
		}
	}
	public String getItinName() {
		return name;
	}
	public void setItinName( String name ) {
		this.name = name;  
	}
	
	public void addPoly(String list){
		polyline.add(list);
	}

	public String toShare() {
		
		return "my itinerary's name is " + getItinName();
	}

	public int getItinID() {
		return itinID;
	}

	public void setItinID(int itinID) {
		this.itinID = itinID;
	}

	public int getDemoID() {
		return demoID;
	}

	public void setDemoID(int demoID) {
		this.demoID = demoID;
	}
	
	public void setCurrentEnts(ArrayList<Entity> ents){
		currentEnts = "(";
		int counter = 0;
		for( Entity en : ents ){
			if(counter==0) {currentEnts += en.getTourId();}
			else currentEnts += "," + en.getTourId();
			counter++;
		}
		currentEnts += ")";
	}
	
	public double getTotalLowCost() {
		return totalLowCost;
	}

	public void setTotalLowCost(double totalLowCost) {
		this.totalLowCost = totalLowCost;
	}

	public double getTotalHighCost() {
		return totalHighCost;
	}

	public void setTotalHighCost(double totalHighCost) {
		this.totalHighCost = totalHighCost;
	}
	
	public double getTotalTime() {
		return totalTime;
	}

	public void setTotalTime(double totalTime) {
		this.totalTime = totalTime;
	}

	public double getTotalDistance() {
		return totalDistance;
	}

	public void setTotalDistance(double totalDistance) {
		this.totalDistance = totalDistance;
	}

	public String getCurrentEnts() {
		currentEnts = "(";
		int counter = 0;
		for( Entity en : itin ){
			if(counter==0) {currentEnts += en.getTourId();}
			else currentEnts += "," + en.getTourId();
			counter++;
		}
		currentEnts += ")";
		return currentEnts;
	}

	public void setCurrentEnts(String currentEnts) {
		this.currentEnts = currentEnts;
	}
	
	public ArrayList<String> getPolyline() {
		return polyline;
	}

	public void clearPoly() {
		polyline.clear();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
}

