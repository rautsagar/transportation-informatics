package com.example.myfirstbluetooth;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

public class EditExpense extends Activity {
	TextView showStats, costStats;
	DatabaseHandler db;
	float fuelUsed = 0;		//Cumulative fuel value variable
	SessionManagement sharedPref;
	int tripID;
   ExpenseCursorAdapter customAdapter;
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
		              
				
				   //final String rowid = cursor.getString(cursor.getColumnIndex("rowid"));
				   //String title = cursor.getString(cursor.getColumnIndex("rowid"));
				   String rowid = cursor.getString(0);
		            String title = cursor.getString(cursor.getColumnIndex("title"));  
				  deleteRow(title,rowid);
				   
		             
		          }
		});
		
		// Database query can be a time consuming task ..
        // so its safe to call database query in another thread
        // Handler, will handle this stuff for you 
  
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                customAdapter = new ExpenseCursorAdapter(EditExpense.this, db.getExpenseData());
                listview.setAdapter(customAdapter);
            }
        });
    
				
		

	}
	
	void deleteRow(String title, String id){
		final String rowid = id;
		 new AlertDialog.Builder(this)
         .setTitle("Really delete?")
         .setMessage("Are you sure you want to delete "+title+" ?")
         .setNegativeButton(android.R.string.no, null)
         .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

             public void onClick(DialogInterface arg0, int arg1) {
             	
             	// Close the app here
             	//Log.d("deleting", titlef);
             	db.deleteExpense(rowid);
                
             }
         }).create().show();
		
	}
		

}
