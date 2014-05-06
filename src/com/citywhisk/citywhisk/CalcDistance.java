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
 * This class is used by Async thread in ActiveItineray.java to calculate distance between 
 * destination
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.protocol.HttpContext;
import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Document;

import android.util.Log;

public class CalcDistance {

	String stringUrl;
	HttpClient httpClient;
	HttpResponse response;
	HttpContext localContext;
	HttpPost httpPost;
	InputStream is;
	DocumentBuilder builder;
	Document doc;

	/*
	 * function takes in lat and lon for two destination to return the distance in String (e.g.1.7 mi)
	 */

	public String getDistanceInfo(double lat1, double lng1, double lat1b, double lng1b) {	

		stringUrl = "http://maps.google.com/maps/api/directions/json?origin=" + lat1 + "," + lng1 + "&destination=" + lat1b + "," + lng1b + "&sensor=false&mode=driving&alternatives=false&units=imperial";

		String jsonOutput = null;

		URL url;
		try {
			url = new URL(stringUrl);

			StringBuilder response = new StringBuilder();
			HttpURLConnection httpconn = (HttpURLConnection)url.openConnection();
			if (httpconn.getResponseCode() == HttpURLConnection.HTTP_OK)
			{
				BufferedReader input = new BufferedReader(new InputStreamReader(httpconn.getInputStream()),8192);
				String strLine = null;
				while ((strLine = input.readLine()) != null)
				{
					response.append(strLine);
				}
				input.close();
			}
			jsonOutput = response.toString();

		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e){
			e.printStackTrace();
		}
		return jsonOutput;

	}



}
