package com.example.myfirstbluetooth;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.TextView;

public class tripDetails extends Activity{

	TextView txt ;
	FetchTripValues task;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_trip_details);
		txt = (TextView) findViewById(R.id.locDetails);
		DatabaseHandler db= new DatabaseHandler(getApplicationContext());

		// getting intent data
		Intent in = getIntent();
		
		Bundle extras = in.getExtras();
		if(extras != null){
			String id = extras.getString("tripid");
			float[][] firstLast = db.getFirstLast(id);
			if(firstLast == null){
				txt.setText("Trip details not present");
			}else{
				txt.setText("startLat "+ firstLast[0][0]+"\n startLong  "+firstLast[0][1]+"\n endLat"+firstLast[1][0]+"\n endLong"+firstLast[1][1]);
				
				//Asynchronously get the details from Google
				task = new FetchTripValues();
				task.execute(firstLast[0][0],firstLast[0][1],firstLast[1][0],firstLast[1][1]);
			}
			
		}
	}
	
	
	class FetchTripValues extends AsyncTask<Float, Void, JSONObject[]>{
		
		@Override
		protected JSONObject[] doInBackground(Float... params) {
			
			QueryAPI api = new QueryAPI();
			JSONObject distDetails = api.getRoute(params[0], params[1], params[2], params[3]);
			JSONObject bikeDetails = api.getBikeRoute(params[0], params[1], params[2], params[3]);
			JSONObject[] details = {distDetails, bikeDetails};
			return details;
		}
		
		@Override
		protected void onPostExecute(JSONObject[] jObject) {
			JSONObject carObject = jObject[0];
			JSONObject bikeObject = jObject[1];
			
			if(jObject != null){
				
				try {
					
					String dest = (carObject.getJSONArray("destination_addresses")).getString(0);
					String orig = (carObject.getJSONArray("origin_addresses")).getString(0);
					int dist = (carObject.getJSONArray("rows").getJSONObject(0).getJSONArray("elements").getJSONObject(0).getJSONObject("distance").getInt("value"));
					int time =  (carObject.getJSONArray("rows").getJSONObject(0).getJSONArray("elements").getJSONObject(0).getJSONObject("duration").getInt("value"));
					int bikeTime = (bikeObject.getJSONArray("rows").getJSONObject(0).getJSONArray("elements").getJSONObject(0).getJSONObject("duration").getInt("value"));
					
					//Update the text view
					txt.setText("Origin:"+orig+"\nDestination:"+dest+"\nDistance:"+dist+"\nTime "+ (time/60)+"Minutes"+"\nBike Time:"+(bikeTime/60)+"Minutes");
					
				} catch (Exception e) {
					// TODO: handle exception
				}
				
				
			}
			
			
			
		
		}
		
	}

}
