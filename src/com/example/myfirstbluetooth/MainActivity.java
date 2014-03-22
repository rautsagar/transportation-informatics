package com.example.myfirstbluetooth;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;


public class MainActivity extends Activity implements Constants {

	public TextView sentTextView, statusTextView, BTstatusTextView;
	public TextView receivedTextView1, receivedTextView2, receivedTextView3, receivedTextView4, receivedTextView5, receivedTextView6, fuelText;

	public static ReceiveActivity mReceiveActivity = null;
	
	@Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
//        Log.d("SYS", "onSaveInstanceState");
	}
     
	@Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
//        Log.d("SYS", "onRestoreInstanceState");

//        yourTextView.setText(savedInstanceState.getString("YourTextViewTextIdentifier"));
    }
	
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        Log.d("SYS", "onCreate");
/*
        if (((MyApp)getApplicationContext()).bluetoothInitiated)
        	Log.d("SYS","bluetooth Initiated");
        else
        	Log.d("SYS","bluetooth NOT Initiated");
 */       
    	sentTextView = (TextView) findViewById(R.id.command_sent);
    	statusTextView = (TextView) findViewById(R.id.status);
    	BTstatusTextView = (TextView) findViewById(R.id.BTstatus);
    	receivedTextView1 = (TextView) findViewById(R.id.response_received_1);
    	receivedTextView2 = (TextView) findViewById(R.id.response_received_2);
    	receivedTextView3 = (TextView) findViewById(R.id.response_received_3);
    	receivedTextView4 = (TextView) findViewById(R.id.response_received_4);
    	receivedTextView5 = (TextView) findViewById(R.id.response_received_5);
    	receivedTextView6 = (TextView) findViewById(R.id.response_received_6);
    	//fuelText = (TextView)findViewById(R.id.fuel_usage);
    	
    	BTstatusTextView.setText(((MyApp)getApplicationContext()).bluetoothStatus);
    	statusTextView.setText(statusText(((MyApp)getApplicationContext()).elmStatus));
		sentTextView.setText(((MyApp)getApplicationContext()).sentText);

		receivedTextView1.setText(((MyApp)getApplicationContext()).receivedText[0]);
    	receivedTextView2.setText(((MyApp)getApplicationContext()).receivedText[1]);
    	receivedTextView3.setText(((MyApp)getApplicationContext()).receivedText[2]);
    	receivedTextView4.setText(((MyApp)getApplicationContext()).receivedText[3]);
    	receivedTextView5.setText(((MyApp)getApplicationContext()).receivedText[4]);
    	receivedTextView6.setText(((MyApp)getApplicationContext()).receivedText[5]);   
    	
    	//DatabaseHandler db = new DatabaseHandler(getApplicationContext());
    	//db.fakePop();
    	
//    	if (mReceiveActivity == null) {
//    		Log.d("SYS", "created mReceiveActivity");
        	mReceiveActivity = new ReceiveActivity();
//   	} else {
//   		Log.d("SYS", "ALREADY EXISTS mReceiveActivity");
//   	}
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
            case R.id.action_fakepop:
            	fakePopulate();
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
                	
                    MainActivity.super.onBackPressed();
                }
            }).create().show();
    }
  
    public void getStats(View view){
    	
    	Intent intent = new Intent(this, StatsActivity.class);
    	startActivity(intent);

    	
    }
    
    public void fakePopulate(){
    	DatabaseHandler db = new DatabaseHandler(getApplicationContext());
    	db.fakePop();
    	
    }
 

    public void queryVehicleInit(View view) {
    	((MyApp)getApplicationContext()).mQueryVehicle.issueCommands(new String[]{"atz","ate0","atcra 7e8"});    	
    }
    
    public void queryVehicle(View view) {
    	((MyApp)getApplicationContext()).mQueryVehicle.issueCommands(new String[]{"01 10","01 0D", "01 31", "01 1F"});    	
    }
    
    public void pollVehicle(View view) {
    	((MyApp)getApplicationContext()).mQueryVehicle.poll(new String[]{"01 10","01 0D", "01 31", "01 1F"});    	    	
    }

    	
    public void crash(View view) {
    	
    	String x = null;
    	Log.d(x,x);    	
    }
    
    
    public class ReceiveActivity extends Activity {
        // Handler gets created on the UI-thread
        private Handler mHandler = new Handler();

        // This gets executed in a non-UI thread:
        public void receiveMyMessage(String msg) {
            final String str = msg;
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    // This gets executed on the UI thread so it can safely modify Views
                    receivedTextView1.setText(str);
                }
            });
        }

        // This gets executed in a non-UI thread:
        public void receiveMyMessages(String[] messages) {
            final String str0 = messages[0];
            final String str1 = messages[1];
            final String str2 = messages[2];
            final String str3 = messages[3];
            final String str4 = messages[4];
            final String str5 = messages[5];
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    // This gets executed on the UI thread so it can safely modify Views
                    receivedTextView6.setText(str5);
                    receivedTextView5.setText(str4);
                    receivedTextView4.setText(str3);
                    receivedTextView3.setText(str2);
                    receivedTextView2.setText(str1);
                    receivedTextView1.setText(str0);
                }
            });
        }

        // This gets executed in a non-UI thread:
        public void receiveMyStatus(int status) {
        	
        	final int s = status;
//        	Log.d("handler", "receiveMyStatus");
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    // This gets executed on the UI thread so it can safely modify Views
                	statusTextView.setText(statusText(s));
                }
            });
        }

        // This gets executed in a non-UI thread:
        public void receiveMyBtStatus(String status) {
        	
        	final String s = status;
//        	Log.d("handler", "receiveMyBtStatus");        	
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    // This gets executed on the UI thread so it can safely modify Views
                	BTstatusTextView.setText(s);
                }
            });
        }
    }
    
    public String statusText(int elmStatus) {
    	switch (elmStatus) {
		case ELM_BUSY:
			return ("Busy");
		case ELM_READY:
			return ("Ready");
		case ELM_ERROR:
			return ("Error");
		default:
			return ("Busy");
    	}
    }

    
//
}


