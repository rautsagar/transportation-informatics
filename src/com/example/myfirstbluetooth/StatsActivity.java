package com.example.myfirstbluetooth;

import android.app.Activity;
import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.echo.holographlibrary.PieGraph;
import com.echo.holographlibrary.PieSlice;

public class StatsActivity extends Activity {
	TextView showStats, costStats;
	DatabaseHandler db;
	float fuelUsed = 0;		//Cumulative fuel value variable
	FetchFuelValues task;
	SessionManagement sharedPref;
	int tripID;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_stats);
		
		showStats = (TextView) findViewById(R.id.statText);
		costStats = (TextView)findViewById(R.id.costText);
		
		sharedPref = new SessionManagement(getApplicationContext());
		db = new DatabaseHandler(getApplicationContext());
		tripID = sharedPref.getTrip();

		if (db.getCount() > 0) {
			// Get data from the database
			Cursor dbResults = db.getFuelValues();

			// iterate through the results and calculate fuel usage
			dbResults.moveToFirst();
			Long preTime = dbResults.getLong(1);
			
			dbResults.moveToNext();
			while (dbResults.isAfterLast() == false) {

				Long time = dbResults.getLong(2);
				int value = dbResults.getInt(3);
				Long timeDiff = (time - preTime) / 1000;
				preTime = time;

				float fuel = (float) (value / 100 / 14.7 / 454 / 6.17 * timeDiff);
				fuelUsed += fuel;
				dbResults.moveToNext();
			}
			dbResults.close();
			
			showStats.setText("Total fuel used: " + fuelUsed);
			task = new FetchFuelValues();
			task.execute(); 			//Start asynchronous fetch of Fuel data & draw graph
				
		}
		

	}
	

		class FetchFuelValues extends AsyncTask<Void, Void, Float>{
			
			@Override
			protected Float doInBackground(Void... params) {
				
				QueryAPI api = new QueryAPI();
				float fuelRate = api.getFuelRate();
				return fuelRate;
			}
			
			@Override
			protected void onPostExecute(Float result) {
				// TODO Auto-generated method stub
				Log.d("rate", result+"");
				
				costStats.setText("Total cost of the trip: $"+ result*fuelUsed);
				
				PieGraph pg = (PieGraph)findViewById(R.id.graph);
				pg.removeSlices();
				PieSlice slice = new PieSlice();
				slice.setColor(Color.parseColor("#99CC00"));
				slice.setValue(2);
				pg.addSlice(slice);
				slice = new PieSlice();
				slice.setColor(Color.parseColor("#FFBB33"));
				slice.setValue(3);
				pg.addSlice(slice);
				slice = new PieSlice();
				slice.setColor(Color.parseColor("#AA66CC"));
				slice.setValue(8);
				pg.addSlice(slice);

				
			}
			
		}
		
		public void refresh(View view){
			task = new FetchFuelValues();
			task.execute(); 			//Start asynchronous fetch of Fuel data & draw graph
		
		
	}

}
