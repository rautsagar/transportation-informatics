package com.example.myfirstbluetooth;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class SessionManagement {
	

	// Shared Preferences
	SharedPreferences pref;

	// Editor for Shared preferences
	Editor editor;

	// Context
	Context _context;

	// Shared pref mode
	int PRIVATE_MODE = 0;

	// Sharedpref file name
	private static final String PREF_NAME = "TIMPref";

	// Make the latest trip id available throughout the app
	private static final String LATEST_TRIP = "latestTrip";

	//Monthly insurance premium amount
	private static final String INSURANCE_VAL = "insurance_premium";
	
	//Monthly Payment on car
	private static final String PAYMENT_VAL = "monthly_payment";
	
	//Georgia fuel rate
	private static final String FUEL_RATE = "fuel_rate";

	// Constructor
	public SessionManagement(Context context) {
		this._context = context;
		pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
		editor = pref.edit();
	}

	
	public void setTrip(int id){
		editor.putInt(LATEST_TRIP, id);
		editor.commit();
	}
	
	public int getTrip(){
		int id = pref.getInt(LATEST_TRIP, -1);
		return id;
	}
	
	public void setPremium(String value){
		editor.putFloat(INSURANCE_VAL, Float.parseFloat(value));
		editor.commit();
	}
	
	public void setPayment(String value){
		editor.putFloat(PAYMENT_VAL, Float.parseFloat(value));
		editor.commit();
	}
	
	public float getPremium(){
		float id = pref.getFloat(INSURANCE_VAL, -1);
		return id;
	}
	
	public float getPayment(){
		float id = pref.getFloat(PAYMENT_VAL, -1);
		return id;
	}
	
	public void setRate(float rate){
		editor.putFloat(FUEL_RATE, rate);
		editor.commit();
	}
	
	public float getRate(){
		float rate = pref.getFloat(FUEL_RATE, -1);
		return rate;
	}
	
}
