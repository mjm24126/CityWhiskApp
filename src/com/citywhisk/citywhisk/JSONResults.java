package com.citywhisk.citywhisk;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ArrayAdapter;

/*
 * Background task to make call to server.
 */
public class JSONResults extends AsyncTask<Void, Void, JSONObject> {
	ArrayList<Entity> entityResults;
	String url;
	Context con;
	public ProgressDialog pDialog;
	List<NameValuePair> params;
	String method;
	
	// Creating JSON Parser object
    JSONParser jParser = new JSONParser();
    
    // constant for JSON tags
    private static final String TAG_SUCCESS = "success";
	
	public JSONResults(Context c, String url, String method, List<NameValuePair> params){
		this.url = url;
		this.con = c;
		this.params = params;
		this.method = method;
	}
	
    /**
     * Before starting background thread Show Progress Dialog
     * */
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        pDialog = new ProgressDialog(con);
        pDialog.setMessage("Searching. Please wait...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        pDialog.show();
    }

    /**
     * getting search results from url
     * */
    protected JSONObject doInBackground(Void... args) {
        
        // getting JSON string from URL
        JSONObject json = jParser.makeHttpRequest(url, method, params);

        // Check your log cat for JSON response
        ////Log.d("Search results: ", json.toString());
        
        /*try {
            // Checking for SUCCESS TAG
            int success = json.getInt(TAG_SUCCESS);

            if (success == 1) {
            	// Convert json to entities
                JSONToEntityConverter converter = new JSONToEntityConverter();
                entityResults = converter.convert(json);

            } else {
                // no entities found
            	// no search matches your request
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }*/

        return json;
    }

    /**
     * After completing background task Dismiss the progress dialog
     * **/
    protected void onPostExecute(JSONObject job) {
    	try {
            // Checking for SUCCESS TAG
            int success = job.getInt(TAG_SUCCESS);

            if (success == 1) {
            	// Convert json to entities
                JSONToEntityConverter converter = new JSONToEntityConverter();
                entityResults = converter.convertAll(job);

            } else {
                // no entities found
            	// no search matches your request
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    	
    	
        // dismiss the dialog after getting all entities
        pDialog.dismiss();
        
        /*if( entityResults != null ){
        	list.clear();
        	for( Entity ent : entityResults ){
        		list.add(ent.getName());
        	}
            ArrayAdapter<String> adaptor = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1, list);
    		searchList.setAdapter(adaptor);
    		adaptor.notifyDataSetChanged();
        }*/
    }

}