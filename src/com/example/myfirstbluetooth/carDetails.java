package com.example.myfirstbluetooth;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class carDetails extends Activity{
	SessionManagement sharedPref;
	TextView carName, insText, payText; 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.car_details);
		
		sharedPref = new SessionManagement(this);
		carName = (TextView)findViewById(R.id.car_name_label);
		insText = (TextView)findViewById(R.id.car_cost);
		payText = (TextView)findViewById(R.id.car_time);
		
		insText.setText("Insurance premium: " + sharedPref.getPremium());
		payText.setText("Monthly payment: " + sharedPref.getPayment());
		
	}

}
