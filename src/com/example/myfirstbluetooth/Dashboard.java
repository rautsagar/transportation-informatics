package com.example.myfirstbluetooth;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

public class Dashboard extends Activity{
	DatabaseHandler db;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_dash);
		db = new DatabaseHandler(this);
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
            	Intent intent = new Intent(this,EditExpense.class);
            	startActivity(intent);	
                return true;
            case R.id.action_enterDetails:
            	enterDetails(getCurrentFocus());
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

public void enterDetails(View view){
	
	Intent intent = new Intent(this, RecurringExpenses.class);
	startActivity(intent);
	
}

public void startCarDetails(View view){
	Log.d("in","carhandler");
	Intent intent = new Intent(this, carDetails.class);
	startActivity(intent);
}

public void inputDialog(View arg0) {

	// get prompts.xml view
	LayoutInflater li = LayoutInflater.from(this);
	View promptsView = li.inflate(R.layout.prompt_expense, null);

	AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

	// set prompts.xml to alertdialog builder
	alertDialogBuilder.setView(promptsView);

	final EditText titleInput = (EditText) promptsView
			.findViewById(R.id.titleInput);
	
	final EditText expenseInput = (EditText) promptsView
			.findViewById(R.id.valInput);

	// set dialog message
	alertDialogBuilder
		.setCancelable(false)
		.setPositiveButton("OK",
		  new DialogInterface.OnClickListener() {
		    public void onClick(DialogInterface dialog,int id) {
			// get user input and set it to result
			// edit text
		String details = titleInput.getText().toString();
		float value = Float.parseFloat(expenseInput.getText().toString());
		db.addExpense(details, value);
		    }
		  })
		.setNegativeButton("Cancel",
		  new DialogInterface.OnClickListener() {
		    public void onClick(DialogInterface dialog,int id) {
			dialog.cancel();
		    }
		  });

	// create alert dialog
	AlertDialog alertDialog = alertDialogBuilder.create();

	// show it
	alertDialog.show();

}

}
