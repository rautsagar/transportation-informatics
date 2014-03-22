package com.example.myfirstbluetooth;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class StatsActivity extends Activity {
	TextView showStats;
	DatabaseHandler db;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_stats);

		showStats = (TextView) findViewById(R.id.statText);

		db = new DatabaseHandler(getApplicationContext());

		if (db.getCount() > 0) {
			// Get data from the database
			Cursor dbResults = db.getFuelValues();

			// iterate through the results and calculate fuel usage
			dbResults.moveToFirst();
			Long preTime = dbResults.getLong(1);
			float fuelUsed = 0;
			dbResults.moveToNext();
			while (dbResults.isAfterLast() == false) {

				Long time = dbResults.getLong(1);
				int value = dbResults.getInt(2);
				Long timeDiff = (time - preTime) / 1000;
				preTime = time;

				float fuel = (float) (value / 100 / 14.7 / 454 / 6.17 * timeDiff);
				fuelUsed += fuel;
				dbResults.moveToNext();
			}
			dbResults.close();
			showStats.setText("Total fuel used: " + fuelUsed);

		}

	}

	public void backpressed(View view) {
		Intent intent = this.getParentActivityIntent();
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
	}

}
