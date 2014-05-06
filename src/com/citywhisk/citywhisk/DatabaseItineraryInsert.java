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

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.util.Log;

public class DatabaseItineraryInsert extends AsyncTask<String, String, String>{
	
	InputStream is=null;
	String result=null;
	String line=null;
	private int code;
	
	private String androidid;
	private String itinDemo;
	private String itinName;
	private String currentEnts;
	private String latitude;
	private String longitude;
	
	public DatabaseItineraryInsert( String id, String demo, String name, String ents, String lat, String lon ){
		androidid = id;
		itinDemo = demo;
		itinName = name;
		currentEnts = ents;
		latitude = lat;
		longitude = lon;
	}

	@Override
	protected String doInBackground(String... arg0) {
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		
	   	nameValuePairs.add(new BasicNameValuePair("demo",itinDemo));
	   	nameValuePairs.add(new BasicNameValuePair("name",itinName));
	   	nameValuePairs.add(new BasicNameValuePair("androidID",androidid));
	   	nameValuePairs.add(new BasicNameValuePair("ents",currentEnts));
	   	nameValuePairs.add(new BasicNameValuePair("lat",latitude));
	   	nameValuePairs.add(new BasicNameValuePair("lon",longitude));
	    	
    	try
    	{
			HttpClient httpclient = new DefaultHttpClient();
	        HttpPost httppost = new HttpPost("http://www.citywhisk.com/scripts/insertItinerary.php");
	        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
	        HttpResponse response = httpclient.execute(httppost); 
	        HttpEntity entity = response.getEntity();
	        is = entity.getContent();
	        //Log.e("pass 1", "connection success ");
		}
	        catch(Exception e)
		{
	        	//Log.e("Fail 1", e.toString());
//		    	Toast.makeText(getApplicationContext(), "Invalid IP Address",
//				Toast.LENGTH_LONG).show();
		}     
	        
        try
        {
            BufferedReader reader = new BufferedReader
			(new InputStreamReader(is,"iso-8859-1"),8);
            StringBuilder sb = new StringBuilder();
            while ((line = reader.readLine()) != null)
	    {
                sb.append(line + "\n");
            }
            is.close();
            result = sb.toString();
            //Log.e("pass 2", result );
		}
	        catch(Exception e)
		{
	            //Log.e("Fail 2", e.toString());
		}     
	       
		try
		{
	        JSONObject json_data = new JSONObject(result);
	        code=(json_data.getInt("code"));
			
	        if(code==1)
	        {
//	        	Toast.makeText(getBaseContext(), "Inserted Successfully",
//	        			Toast.LENGTH_SHORT).show();
            }
            else
            {
//				 Toast.makeText(getBaseContext(), "Sorry, Try Again",
//						 Toast.LENGTH_LONG).show();
	        }
		}
		catch(Exception e)
		{
	            //Log.e("Fail 3", e.toString());
		}
		return null;
	}

}
