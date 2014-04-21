package com.example.myfirstbluetooth;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class QueryAPI {
	String url;
	HttpClient httpClient = new DefaultHttpClient();

	public QueryAPI() {
	};

	public QueryAPI(String link) {
		url = link + "users/";
	};

	public float getFuelRate() {
		float rate = 5;
		InputStream inputStream = null;
		String result = null;

		try {

			HttpGet httpGet = new HttpGet("http://apify.heroku.com/api/gasprices.json");
			HttpResponse response = httpClient.execute(httpGet);
			StatusLine statusLine = response.getStatusLine();

			if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
				HttpEntity entity = response.getEntity();

				inputStream = entity.getContent();
				
				// json is UTF-8 by default
				BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"), 8);
				StringBuilder sb = new StringBuilder();

				String line = null;
				while ((line = reader.readLine()) != null) {
					sb.append(line + "\n");
				}
				result = sb.toString();
				
				JSONArray jArray = new JSONArray(result);
				
				//Loop through and get rate for Georgia
				for (int i=0; i < jArray.length(); i++){
				    try {
				        JSONObject oneObject = jArray.getJSONObject(i);
				        // Pulling items from the array
				        if(oneObject.getString("state").equalsIgnoreCase("georgia")){
				   	
				        	String fuelRate = (oneObject.getString("regular")).substring(1);
				        	rate = Float.parseFloat(fuelRate);
				        	Log.d("rate for georgia",fuelRate);
				        }
				    
				    } catch (JSONException e) {
				        // Oops
				    }
				}
				

			}

		} catch (Exception e) {

			e.printStackTrace();
		}

		return rate;
	}
	
	JSONObject getRoute(float lat1, float lng1, float lat2, float lng2){
		
		InputStream inputStream = null;
		String result = null;
		JSONObject jObject = null;

		try {
			String url = "http://maps.googleapis.com/maps/api/distancematrix/json?origins="+lat1+","+lng1+"&destinations="+lat2+","+lng2+"&sensor=false";
			HttpGet httpGet = new HttpGet(url);
			HttpResponse response = httpClient.execute(httpGet);
			StatusLine statusLine = response.getStatusLine();

			if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
				HttpEntity entity = response.getEntity();

				inputStream = entity.getContent();
				
				// json is UTF-8 by default
				BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"), 8);
				StringBuilder sb = new StringBuilder();

				String line = null;
				while ((line = reader.readLine()) != null) {
					sb.append(line + "\n");
				}
				result = sb.toString();
				
				 jObject = new JSONObject(result);
				
				

			}

		} catch (Exception e) {

			e.printStackTrace();
		}

		return jObject;
		
		
	}
	
	JSONObject getBikeRoute(float lat1, float lng1, float lat2, float lng2){
		
		InputStream inputStream = null;
		String result = null;
		JSONObject jObject = null;

		try {
			String url = "http://maps.googleapis.com/maps/api/distancematrix/json?origins="+lat1+","+lng1+"&destinations="+lat2+","+lng2+"&mode=bicycling&sensor=false";
			HttpGet httpGet = new HttpGet(url);
			HttpResponse response = httpClient.execute(httpGet);
			StatusLine statusLine = response.getStatusLine();

			if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
				HttpEntity entity = response.getEntity();

				inputStream = entity.getContent();
				
				// json is UTF-8 by default
				BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"), 8);
				StringBuilder sb = new StringBuilder();

				String line = null;
				while ((line = reader.readLine()) != null) {
					sb.append(line + "\n");
				}
				result = sb.toString();
				
				 jObject = new JSONObject(result);
				
				

			}

		} catch (Exception e) {

			e.printStackTrace();
		}

		return jObject;
		
		
	}

}