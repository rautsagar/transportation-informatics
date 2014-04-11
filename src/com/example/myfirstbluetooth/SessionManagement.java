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
	
	
}
