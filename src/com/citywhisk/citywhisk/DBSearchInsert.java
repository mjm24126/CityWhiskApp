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

public class DBSearchInsert  extends AsyncTask<String, String, String>{
	
	InputStream is=null;
	String result=null;
	String line=null;
	private int code;
	
	private String androidid;
	private String search;
	
	public DBSearchInsert( String id, String searchText ){
		androidid = id;
		search = searchText;
	}

	@Override
	protected String doInBackground(String... arg0) {
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		
	   	nameValuePairs.add(new BasicNameValuePair("searchText",search));
	   	nameValuePairs.add(new BasicNameValuePair("androidID",androidid));
	    	
    	try
    	{
			HttpClient httpclient = new DefaultHttpClient();
	        HttpPost httppost = new HttpPost("http://www.citywhisk.com/scripts/insertSearch.php");
	        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
	        HttpResponse response = httpclient.execute(httppost); 
	        HttpEntity entity = response.getEntity();
	        is = entity.getContent();
	        //Log.e("pass 1", "connection success ");
		}
	        catch(Exception e)
		{
	        	//Log.e("Fail 1", e.toString());
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
