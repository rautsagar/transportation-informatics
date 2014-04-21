package com.example.myfirstbluetooth;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class Dashboard extends Activity{
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_dash);
	}
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_quit:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
	
	
	@Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
            .setTitle("Really Exit?")
            .setMessage("Are you sure you want to exit?")
            .setNegativeButton(android.R.string.no, null)
            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface arg0, int arg1) {
                	
                	// Close the app here
                	
                    Dashboard.super.onBackPressed();
                }
            }).create().show();
    }
	
public void getStats(View view){
    	
    	Intent intent = new Intent(this, StatsActivity.class);
    	startActivity(intent);	
    }

public void startCollectData(View view){
	
	Intent intent = new Intent(this, CollectData.class);
	startActivity(intent);

	
}

public void startListTrips(View view){
	
	Intent intent = new Intent(this, ListTrips.class);
	startActivity(intent);
	
}

}
