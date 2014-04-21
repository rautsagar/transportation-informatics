package com.example.myfirstbluetooth;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

public class ListTrips extends Activity {
	TextView showStats, costStats;
	DatabaseHandler db;
	float fuelUsed = 0;		//Cumulative fuel value variable
	SessionManagement sharedPref;
	int tripID;
   CustomCursorAdapter customAdapter;
   private ListView listview;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_trips);
		db = new DatabaseHandler(getApplicationContext());
		listview = (ListView) findViewById(R.id.list_data);
		
		listview.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
		              int position, long id) {
		               
				
				// Get the cursor, positioned to the corresponding row in the result set
				   Cursor cursor = (Cursor) parent.getItemAtPosition(position);
		              
				// Get the state's capital from this row in the database.
				   String tripid = cursor.getString(cursor.getColumnIndex("trip_id"));
		               
		              // Launching new Activity on selecting single List Item
		              Intent i = new Intent(getApplicationContext(), tripDetails.class);
		              // sending data to new activity
		              i.putExtra("tripid", tripid);
		              startActivity(i);
		             
		          }
		});
		
		// Database query can be a time consuming task ..
        // so its safe to call database query in another thread
        // Handler, will handle this stuff for you 
  
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                customAdapter = new CustomCursorAdapter(ListTrips.this, db.getTripData());
                listview.setAdapter(customAdapter);
            }
        });
    
				
		
		

		if (db.getCount() > 0) {
			// Get data from the database
			Cursor dbResults = db.getFuelValues();

				
		}
		

	}
	

		

}
