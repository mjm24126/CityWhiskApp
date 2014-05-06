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

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;


public class JSONToEntityConverter {
	
	private static final String TAG_ENT_ID = "entityID";
	private static final String TAG_ENT_NAME = "name";
	private static final String TAG_ENT_CAT = "category";
	private static final String TAG_ENT_ADDRESS = "address";
	private static final String TAG_ENT_DESC = "description";
	private static final String TAG_ENT_PROMO = "isPromo";
	private static final String TAG_ENT_LOW = "lowPrice";
	private static final String TAG_ENT_HIGH = "highPrice";
	private static final String TAG_ENT_RATING = "rating";
	private static final String TAG_ENT_LINK = "link";
	private static final String TAG_ENT_PHONE = "phoneNum";
	private static final String TAG_ENT_LAT = "latitude";
	private static final String TAG_ENT_LON = "longitude";
	private static final String TAG_ENT_INDOOR = "isIndoorOnly";
	private static final String TAG_ENT_OUTDOOR = "isOutdoorOnly";
	private static final String TAG_ENT_TIME = "avgTime";
	private static final String TAG_ENT_ACTS = "classes";
	private static final String TAG_ENT_DEMOS = "demos";
	private static final String TAG_ENT_IMAGES = "images";
	
	private int maxImgSize;
	
	ArrayList<Entity> entityList;
	
	public JSONToEntityConverter(){
		entityList = new ArrayList<Entity>();
	}
	public JSONToEntityConverter(int maxImgSize){
		entityList = new ArrayList<Entity>();
		this.maxImgSize = maxImgSize;  
	}
	
	/*
	 * Takes a JSON object and returns an array list of entities
	 */
	public ArrayList<Entity> convertAll(JSONObject obj){
		try {
			JSONArray entArray = obj.getJSONArray("entities");
			//Log.i("ent array", entArray.toString());
			for(int i=0;i<entArray.length();i++){
				if( entArray.get(i) != null ){
					JSONObject jsonOb = entArray.getJSONObject(i);
					Entity ent = new Entity();
					if(jsonOb.has(TAG_ENT_ID)) ent.setTourId(jsonOb.getInt(TAG_ENT_ID));
					if(jsonOb.has(TAG_ENT_NAME)) ent.setName(jsonOb.getString(TAG_ENT_NAME));
					if(jsonOb.has(TAG_ENT_CAT)) ent.setCatagory(jsonOb.getString(TAG_ENT_CAT));
					if(jsonOb.has(TAG_ENT_ADDRESS)) ent.setAddress(jsonOb.getString(TAG_ENT_ADDRESS));
					if(jsonOb.has(TAG_ENT_DESC)) ent.setDescription(jsonOb.getString(TAG_ENT_DESC));
					if(jsonOb.has(TAG_ENT_PROMO)) {
						if( jsonOb.getInt(TAG_ENT_PROMO) == 1 ) ent.setPromo(true); else ent.setPromo(false);
					}
					if(jsonOb.has(TAG_ENT_LOW)) ent.setLowPrice(jsonOb.getDouble(TAG_ENT_LOW));
					if(jsonOb.has(TAG_ENT_HIGH)) ent.setHighPrice(jsonOb.getDouble(TAG_ENT_HIGH));
					if(jsonOb.has(TAG_ENT_RATING)) ent.setRating(jsonOb.getDouble(TAG_ENT_RATING));
					if(jsonOb.has(TAG_ENT_LINK)) ent.setLink(jsonOb.getString(TAG_ENT_LINK));
					if(jsonOb.has(TAG_ENT_PHONE)) ent.setPhoneNum(jsonOb.getString(TAG_ENT_PHONE));
					if(jsonOb.has(TAG_ENT_LAT)) ent.setLatitude(jsonOb.getString(TAG_ENT_LAT));
					if(jsonOb.has(TAG_ENT_LON)) ent.setLongitude(jsonOb.getString(TAG_ENT_LON));
					if(jsonOb.has(TAG_ENT_INDOOR)) {
						if( jsonOb.getInt(TAG_ENT_INDOOR) == 1 ) ent.setIndoorOnly(true); else ent.setIndoorOnly(false);
					}
					if(jsonOb.has(TAG_ENT_OUTDOOR)) {
						if( jsonOb.getInt(TAG_ENT_OUTDOOR) == 1 ) ent.setOutdoorOnly(true); else ent.setOutdoorOnly(false);
					}
					if(jsonOb.has(TAG_ENT_TIME)) ent.setTime(jsonOb.getDouble(TAG_ENT_TIME));
					if(jsonOb.has(TAG_ENT_ACTS)) ent.setActivities(jsonOb.getString(TAG_ENT_ACTS));
					if(jsonOb.has(TAG_ENT_DEMOS)) ent.setDemos(jsonOb.getString(TAG_ENT_DEMOS));
					if(jsonOb.has(TAG_ENT_IMAGES)) ent.setImages(jsonOb.getString(TAG_ENT_IMAGES));
					/*try{	
						ent.decodeImages();
					}
					catch(OutOfMemoryError oome){
						oome.printStackTrace();
					}*/
					entityList.add(ent);
				}
			}
		} 
		catch (JSONException e) {
			e.printStackTrace();
			//Log.i("message",e.getMessage());
		}
		return entityList;
	}
	/*
	 * Converts the data at given position to Entity object
	 */
	public Entity convertEnt(JSONArray array, int position){
		Entity ent = new Entity();
		try {
			//for(int i=0;i<entArray.length();i++){
				JSONObject jsonOb = array.getJSONObject(position);
				if(jsonOb.has(TAG_ENT_ID)) ent.setTourId(jsonOb.getInt(TAG_ENT_ID));
				if(jsonOb.has(TAG_ENT_NAME)) ent.setName(jsonOb.getString(TAG_ENT_NAME));
				if(jsonOb.has(TAG_ENT_CAT)) ent.setCatagory(jsonOb.getString(TAG_ENT_CAT));
				if(jsonOb.has(TAG_ENT_ADDRESS)) ent.setAddress(jsonOb.getString(TAG_ENT_ADDRESS));
				if(jsonOb.has(TAG_ENT_DESC)) ent.setDescription(jsonOb.getString(TAG_ENT_DESC));
				if(jsonOb.has(TAG_ENT_PROMO)) {
					if( jsonOb.getInt(TAG_ENT_PROMO) == 1 ) ent.setPromo(true); else ent.setPromo(false);
				}
				if(jsonOb.has(TAG_ENT_LOW)) ent.setLowPrice(jsonOb.getDouble(TAG_ENT_LOW));
				if(jsonOb.has(TAG_ENT_HIGH)) ent.setHighPrice(jsonOb.getDouble(TAG_ENT_HIGH));
				if(jsonOb.has(TAG_ENT_RATING)) ent.setRating(jsonOb.getDouble(TAG_ENT_RATING));
				if(jsonOb.has(TAG_ENT_LINK)) ent.setLink(jsonOb.getString(TAG_ENT_LINK));
				if(jsonOb.has(TAG_ENT_PHONE)) ent.setPhoneNum(jsonOb.getString(TAG_ENT_PHONE));
				if(jsonOb.has(TAG_ENT_LAT)) ent.setLatitude(jsonOb.getString(TAG_ENT_LAT));
				if(jsonOb.has(TAG_ENT_LON)) ent.setLongitude(jsonOb.getString(TAG_ENT_LON));
				if(jsonOb.has(TAG_ENT_INDOOR)) {
					if( jsonOb.getInt(TAG_ENT_INDOOR) == 1 ) ent.setIndoorOnly(true); else ent.setIndoorOnly(false);
				}
				if(jsonOb.has(TAG_ENT_OUTDOOR)) {
					if( jsonOb.getInt(TAG_ENT_OUTDOOR) == 1 ) ent.setOutdoorOnly(true); else ent.setOutdoorOnly(false);
				}
				if(jsonOb.has(TAG_ENT_TIME)) ent.setTime(jsonOb.getDouble(TAG_ENT_TIME));
				if(jsonOb.has(TAG_ENT_ACTS)) ent.setActivities(jsonOb.getString(TAG_ENT_ACTS));
				if(jsonOb.has(TAG_ENT_DEMOS)) ent.setDemos(jsonOb.getString(TAG_ENT_DEMOS));
				if(jsonOb.has(TAG_ENT_IMAGES)) ent.setImages(jsonOb.getString(TAG_ENT_IMAGES));
				/*try{	
					ent.decodeImages();
				}
				catch(OutOfMemoryError oome){
					oome.printStackTrace();
				}*/
				//entityList.add(ent);
			//}
		} 
		catch (JSONException e) {
			e.printStackTrace();
		}
		return ent;
	}
}
