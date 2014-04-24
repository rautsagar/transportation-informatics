package com.example.myfirstbluetooth;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class tripDetails extends Activity{

	TextView txt , martaTime, martaCost, cabCost, bikingTime, zipCost, from, to, distText, timeText, moneyText;
	LinearLayout martaBox;
	FetchTripValues task;
	
	int dist, time,bikeTime,transitTime,transitSteps;
	double cabFare, zipFare;
	String orig, dest;
	float fuelused = 0;
	float fuelMoney;
	SessionManagement pref;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_trip_details);
		txt = (TextView) findViewById(R.id.locDetails);
		martaBox = (LinearLayout)findViewById(R.id.martaDetails);
		martaTime =  (TextView) findViewById(R.id.marta_time);
		martaCost =  (TextView) findViewById(R.id.marta_cost);
		cabCost =  (TextView) findViewById(R.id.cab_cost);
		zipCost = (TextView)findViewById(R.id.zipcar_cost);
		bikingTime =  (TextView) findViewById(R.id.biking_time);
		from = (TextView) findViewById(R.id.from);
		to = (TextView) findViewById(R.id.to);
		distText = (TextView) findViewById(R.id.dist);
		timeText = (TextView) findViewById(R.id.time);
		moneyText = (TextView) findViewById(R.id.cost);
		
		pref=new SessionManagement(getApplicationContext());
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
				from.setText(firstLast[0][0]+", "+firstLast[1][0]);
				to.setText(firstLast[1][1] + "");
				
				
				//Asynchronously get the details from Google
				task = new FetchTripValues();
				task.execute(firstLast[0][0],firstLast[0][1],firstLast[1][0],firstLast[1][1]);
			}
			

			if (db.getCount(Integer.parseInt(id)) > 0) {
				// Get data from the database
				Cursor dbResults = db.getTripFuelValues(Integer.parseInt(id));

				// iterate through the results and calculate fuel usage
				dbResults.moveToFirst();
				Long preTime = dbResults.getLong(2);
				
				dbResults.moveToNext();
				while (dbResults.isAfterLast() == false) {

					Long time = dbResults.getLong(2);
					int value = dbResults.getInt(3);
					Long timeDiff = (time - preTime) / 1000;
					preTime = time;

					float fuel = (float) (value / 100 / 14.7 / 454 / 6.17 * timeDiff);
					fuelused += fuel;
					dbResults.moveToNext();
				}
				dbResults.close();
					
			}
			Log.d("fuel used", fuelused+"");
			Log.d("rate", pref.getRate()+"");
			fuelMoney = fuelused*pref.getRate();
			moneyText.setText("$"+fuelMoney);
			
		}
	}
	
	
	class FetchTripValues extends AsyncTask<Float, Void, JSONObject[]>{
		
		@Override
		protected JSONObject[] doInBackground(Float... params) {
			
			QueryAPI api = new QueryAPI();
			JSONObject distDetails = api.getRoute(params[0], params[1], params[2], params[3]);
			JSONObject bikeDetails = api.getBikeRoute(params[0], params[1], params[2], params[3]);
			//JSONObject transitDetails = api.
			JSONObject[] details = {distDetails, bikeDetails};
			return details;
		}
		
		@Override
		protected void onPostExecute(JSONObject[] jObject) {
			JSONObject carObject = jObject[0];
			JSONObject bikeObject = jObject[1];
			
			if(jObject != null){
				
				try {
					
					 dest = (carObject.getJSONArray("destination_addresses")).getString(0);
					 orig = (carObject.getJSONArray("origin_addresses")).getString(0);
					 dist = (carObject.getJSONArray("rows").getJSONObject(0).getJSONArray("elements").getJSONObject(0).getJSONObject("distance").getInt("value"));
					 time =  (carObject.getJSONArray("rows").getJSONObject(0).getJSONArray("elements").getJSONObject(0).getJSONObject("duration").getInt("value"));
					 bikeTime = (bikeObject.getJSONArray("rows").getJSONObject(0).getJSONArray("elements").getJSONObject(0).getJSONObject("duration").getInt("value"));
					 
					 //Set Biking time
					 bikingTime.setText((bikeTime/60)+"Minutes");
					 
					//Update the text view
					double miles = dist/1609.34;
				
					from.setText(orig);
					to.setText(dest);
					distText.setText(miles+" miles");
					timeText.setText((time/60) + "Min");
					
					//Calculate yellow cab fare
					float cabUnit = 1609.34f/8;
					double cabDist = Math.ceil((dist - cabUnit)/cabUnit);
					cabFare = 2.5 + (0.25*cabDist);
					cabCost.setText("Cost: "+ cabFare);
					
					float zipTime = time/3600f;
					Log.d("ziptime",zipTime+"");
					int cielHours = (int)Math.ceil(zipTime);
					Log.d("ziphours", cielHours+"");
					zipFare = 9.5*cielHours;
					zipCost.setText("Cost: "+ zipFare);
					
					
				} catch (Exception e) {
					// TODO: handle exception
				}
				
				
			}
			
			String start="Fox theater, midtown, GA";
			String end = "Lenox mall, GA";
		FetchTransitValues task2 = new FetchTransitValues();
		task2.execute(orig.replace(' ', '+'), dest.replace(' ', '+'));
		
		}
		
	}
	
	class FetchTransitValues extends AsyncTask<String, Void, JSONObject>{
		@Override
		protected JSONObject doInBackground(String... params) {
			QueryAPI api = new QueryAPI();
			JSONObject transitDetail = api.getTransit(params[0], params[1]);
			return transitDetail;
			
		}
		
		protected void onPostExecute (JSONObject jObject) {
			String status="";
			
			try {
				status = jObject.getString("status");
				Log.d("transitStatus",status);
			} catch (Exception e) {
				// TODO: handle exception
			}
			
			if( !status.equals("NOT_FOUND") &&  !status.equals("ZERO_RESULTS") ){
				try {
					
					Log.d("transit array", "getting values");
					martaBox.setVisibility(View.VISIBLE);
					JSONArray steps = jObject.getJSONArray("routes").getJSONObject(0).getJSONArray("legs").getJSONObject(0).getJSONArray("steps");
					transitTime =  jObject.getJSONArray("routes").getJSONObject(0).getJSONArray("legs").getJSONObject(0).getJSONObject("duration").getInt("value");
					Log.d("transit time",transitTime+"");
					transitSteps = 0;
					
					for (int i = 0; i < steps.length(); i++) {
						  JSONObject obj = steps.getJSONObject(i);
						  if(obj.getString("travel_mode").equals("TRANSIT")){
							  transitSteps++;
						  }
						}
	
					martaTime.setText("Time: " +(transitTime/60)+" minutes" );
					martaCost.setText("$"+(transitSteps*2.5));
				} catch (Exception e) {
					// TODO: handle exception
				}
				
				
			}else{
				
			}
		}
	}

}
