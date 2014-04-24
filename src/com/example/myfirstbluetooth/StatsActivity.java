package com.example.myfirstbluetooth;

import java.text.SimpleDateFormat;

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
	TextView fuelT, insT, payT, otherT;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_stats);
		
		
		
		showStats = (TextView) findViewById(R.id.statText);
		fuelT =(TextView)findViewById(R.id.fuelText);
		insT =(TextView)findViewById(R.id.insText);
		payT =(TextView)findViewById(R.id.PayText);
		otherT =(TextView)findViewById(R.id.otherText);
		
		 Long ts = System.currentTimeMillis();
		  SimpleDateFormat formatter=  new SimpleDateFormat("MMM  yyyy");
		  String monthNow = formatter.format(ts);
		  showStats.setText(monthNow);
		
		
		sharedPref = new SessionManagement(getApplicationContext());
		db = new DatabaseHandler(getApplicationContext());
		tripID = sharedPref.getTrip();
		
		for(int i = 1; i <= tripID; i++){
			

			if (db.getCount(i) > 0) {
				// Get data from the database
				Cursor dbResults = db.getTripFuelValues(i);

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
					fuelUsed += fuel;
					dbResults.moveToNext();
				}
				dbResults.close();
					
			}
			
		}

		
		
		//showStats.setText("Total fuel used: " + fuelUsed);
		task = new FetchFuelValues();
		task.execute(); 			//Start asynchronous fetch of Fuel data & draw graph

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
				sharedPref.setRate(result);
				float fuelCost = result*fuelUsed;
				float insCost = sharedPref.getPremium();
				float payCost = sharedPref.getPayment();
				float extras = db.getCurrentMonthExpenses();
				
				fuelT.setText(fuelCost+"");
				insT.setText(insCost+"");
				payT.setText(payCost+"");
				otherT.setText(extras+"");
				
				float total = fuelCost+insCost+payCost+extras;
				
				
				
				PieGraph pg = (PieGraph)findViewById(R.id.graph);
				pg.removeSlices();
				PieSlice slice = new PieSlice();
				slice.setColor(Color.parseColor("#99CC00"));
				slice.setValue((int)(fuelCost*100/total));
				pg.addSlice(slice);
				slice = new PieSlice();
				slice.setColor(Color.parseColor("#FFBB33"));
				slice.setValue((int)(insCost*100/total));
				pg.addSlice(slice);
				slice = new PieSlice();
				slice.setColor(Color.parseColor("#AA66CC"));
				slice.setValue((int)(payCost*100/total));
				pg.addSlice(slice);
				slice = new PieSlice();
				slice.setColor(Color.parseColor("#c0392b"));
				slice.setValue((int)(extras*100/total));
				pg.addSlice(slice);
				

				
			}
			
		}
		
		public void refresh(View view){
			task = new FetchFuelValues();
			task.execute(); 			//Start asynchronous fetch of Fuel data & draw graph
		
		
	}

}
