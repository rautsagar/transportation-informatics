package com.example.myfirstbluetooth;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class RecurringExpenses extends Activity {
	
	TextView insuranceBox, paymentBox;
	SessionManagement session;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_recurring_expenses);
		
		session = new SessionManagement(getApplicationContext());
		insuranceBox = (TextView)findViewById(R.id.insuranceBox);
		paymentBox = (TextView)findViewById(R.id.paymentBox);
		
	}
	
	
	public void saveInfo(View view){
		
		String insVal = insuranceBox.getText().toString();
		String payVal = paymentBox.getText().toString();
		
		if(!insVal.equals("") || insVal != null){
			session.setPremium(insVal);
		}
		
		if(!payVal.equals("") || payVal != null){
			session.setPayment(payVal);
		}
		
	}

}
