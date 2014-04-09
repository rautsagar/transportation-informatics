package com.example.myfirstbluetooth;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHandler extends SQLiteOpenHelper {

	// Database Version
	private static final int DATABASE_VERSION = 1;

	// Database Name
	private static final String DATABASE_NAME = "TransInformatics";

	// Table Name
	private static final String TABLE_AIRFLOW = "airflow";
	
	//Table Columns
	// Contacts Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_TIME = "time";
    private static final String KEY_VAL= "value";
 
	

	public DatabaseHandler(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		/*
╔═══════╦═════════╦═════════╗
║ Field 		║Type   			  ║   Key   			║
╠═══════╬═════════╬═════════╣
║ id   		    ║ INTEGER 	  ║ PRIMARY 	║
║ time  		║ REAL    		  ║         			║
║ value 		║ INTEGER 	  ║         			║
╚═══════╩═════════╩═════════╝
		*/
		
		String create = "CREATE TABLE "+ TABLE_AIRFLOW+"("+KEY_ID+" INTEGER PRIMARY KEY,"+KEY_TIME+" REAL, "+KEY_VAL+" INTEGER)";
		db.execSQL(create);
		

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_AIRFLOW);
 
        // Create tables again
        onCreate(db);

	}
	
	// Adding new log entry for airflow data
	public void addEntry(Long ts, int value) {
		SQLiteDatabase db = this.getWritableDatabase();
		
		try {
			// Inserting Row
		    db.execSQL("INSERT INTO "+TABLE_AIRFLOW+"(time,value) VALUES("+ts+","+value+")");
		    Log.d("put in database", value+"");
		} catch (Exception e) {
			// TODO: handle exception
		}
		
	    db.close(); // Closing database connection
	}
	
	public int getCount(){
		SQLiteDatabase db = this.getReadableDatabase();
		String query = "SELECT * FROM "+TABLE_AIRFLOW;
		Cursor cursor = db.rawQuery(query, null);
		int length = cursor.getCount(); 
		db.close();
		return length;
		
	}
	
	public Cursor getFuelValues(){
		SQLiteDatabase db = this.getReadableDatabase();
		String query = "SELECT * FROM "+TABLE_AIRFLOW;
		Cursor cursor = db.rawQuery(query, null);
		//db.close();
		return cursor;
		
	}
	

}
