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

/*
 * Class contains support function, being used in the application
 */


import java.sql.Time;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import android.content.Context;
import android.util.Log;

public class SupportClass {

	LatLngBounds.Builder builder = new LatLngBounds.Builder(); 
	

	/*
	 * choose marker image based on its position
	 */
	public int chooseMarkerImage(int pos){

		int markerImage;
		switch(pos){
		case 1:
			markerImage = R.drawable.pin1;
			break;

		case 2:
			markerImage = R.drawable.pin2;
			break;

		case 3:
			markerImage = R.drawable.pin3;
			break;

		case 4:
			markerImage = R.drawable.pin4;
			break;

		case 5:
			markerImage = R.drawable.pin5;
			break;
			
		case 6:
			markerImage = R.drawable.pin6;
			break;
		
		case 7:
			markerImage = R.drawable.pin7;
			break;
			
		case 8:
			markerImage = R.drawable.pin8;
			break;
			
		case 9:
			markerImage = R.drawable.pin9;
			break;
			
		case 10:
			markerImage = R.drawable.pin10;
			break;

		default:
			markerImage =  R.drawable.pin1;
			break;
		}

		return markerImage;
	}

	
	/*
	 * Calculate all totals with new classes.
	 * 
	 */
	public void calcTotals( Itinerary objects) {
		//this.calcTotalDistance(objects);
		this.calcTotalPrice(objects);
		this.calcTotalSavings(objects);
		//this.calcTotalTime(objects);
	}
	
	


	/*
	 * Calculate total distance for the Itinerary
	 */
	public void calcTotalDistance(Itinerary objects){

		int position = 0;
		double totalDistance=0.0;
		for(Entity calcTotalDistanceObj : objects.itin){
			String str = calcTotalDistanceObj.getDistance();
			if (position != 0){
				/*
				 * convert feet to miles
				 */
				if(str.contains("ft")){
					str = str.replaceAll("[^\\d.]", "");
					//Log.i("CHECK STRINGGGGGG", str);
					totalDistance = totalDistance + (Double.parseDouble(str)*0.000189394);
				}
				if(str.contains("km")){
					str = str.replaceAll("[^\\d.]", "");
					//Log.i("CHECK STRINGGGGGG", str);
					totalDistance = totalDistance + (Double.parseDouble(str)*0.621371);
				}
				else{
					
					str = str.replaceAll("[^\\d.]", "");
					//Log.i("CHECK STRINGGGGGG", str);
					totalDistance = totalDistance + Double.parseDouble(str);
				}
				
			}
			position++;
		}

		objects.itin.get(0).setDistance(String.format("%.2f mi",totalDistance));
	}


	/*
	 * Calculate total price of the itinerary with new Classes
	 */
	public void calcTotalPrice(Itinerary objects){

		int position = 0;
		double totalLowPrice=0.0;
		double totalHighPrice=0.0;
		for(Entity calcTotalPriceObj : objects.itin){
			if (position != 0){
				totalLowPrice = totalLowPrice+calcTotalPriceObj.getLowPrice();
				totalHighPrice = totalHighPrice+calcTotalPriceObj.getHighPrice();
			}
			position++;
		}
		objects.itin.get(0).setLowPrice((int)totalLowPrice);
		objects.itin.get(0).setHighPrice((int)totalHighPrice);
	}
	

	/*
	 * Calculate total savings of the itinerary with new Classes
	 */
	public void calcTotalSavings(Itinerary objects){

		int position = 0;
		double totalSavings=0.0;
		for(Entity calcTotalPriceObj : objects.itin){
			if (position != 0){
				totalSavings = totalSavings + calcTotalPriceObj.getSavings();
			}
			position++;
		}
		objects.itin.get(0).setSavings((int)totalSavings);

	}
	


	/*
	 * Calculate total time for itinerary with new Classes
	 */

	public void calcTotalTime(Itinerary objects){

		int position = 0;
		double totalTime = 0;
		for(Entity calcTotalPriceObj : objects.itin){
			if (position != 0){

				totalTime += calcTotalPriceObj.getTime();
			}
			position++;
		}
		objects.itin.get(0).setTime(totalTime);

	}
	
	/*
	 * new fit in map method for updates
	 */
	public void fitInMap(Itinerary tour, GoogleMap googleMap) {
		
		LatLngBounds.Builder builder = new LatLngBounds.Builder(); 
		for(Entity obj : tour.itin){
			double itinerary_latitude = Double.parseDouble(obj.getLatitude());
			double itinerary_longitude = Double.parseDouble(obj.getLongitude());
			
			LatLng latlng = new LatLng(itinerary_latitude, itinerary_longitude);
			builder.include(latlng);
			
			
		}
		LatLngBounds bounds = builder.build();
		googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 55));
		
	}
	
	/*
	 * new fit in map method for updates
	 */
	public void fitInMap(Itinerary tour, GoogleMap googleMap, Context con) {
		
		LatLngBounds.Builder builder = new LatLngBounds.Builder(); 
		for(Entity obj : tour.itin){
			double itinerary_latitude = Double.parseDouble(obj.getLatitude());
			double itinerary_longitude = Double.parseDouble(obj.getLongitude());
			
			LatLng latlng = new LatLng(itinerary_latitude, itinerary_longitude);
			builder.include(latlng);
			
			
		}
		LatLngBounds bounds = builder.build();
		int width =  con.getResources().getDisplayMetrics().widthPixels;
		int padding = (int) (width * .14);
		googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, padding));
		
	}

}
