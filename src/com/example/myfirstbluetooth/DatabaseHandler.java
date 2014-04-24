package com.example.myfirstbluetooth;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.content.ContentValues;
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
	private static final String TABLE_TRIP = "trip";
	
	//Table Columns
	//airflow Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_TIME = "time";
    private static final String KEY_VAL= "value";
    private static final String KEY_TRIP = "trip_id";
 
    //locations table column names
    private static final String TABLE_LOC = "locations";
    private static final String KEY_LOC = "loc_id";
    private static final String KEY_LAT = "latitude";
    private static final String KEY_LONG = "longitude";
    
	//expenses table column names
    private static final String TABLE_EXPENSES = "expenses";
    private static final String KEY_TITLE = "title";
    private static final String KEY_COST = "cost";
    
	public DatabaseHandler(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		/*
		 * airflow table
╔═══════╦═════════╦═════════╗
║ Field 		║Type   			  ║   Key   			║
╠═══════╬═════════╬═════════╣
║ 					║							 ║							║
║  id   		    ║ INTEGER 	  ║ PRIMARY 	║
║	 trip_id    ║ INTEGER     ║
║ time  		║ REAL    		  ║         			║
║ value 		║ INTEGER 	  ║         			║
╚═══════╩═════════╩═════════╝
		*/
		
		
		/*
		 * trip table
		╔═══════╦═════════╦═════════╗
		║ Field 		║Type   			  ║   Key   			║
		╠═══════╬═════════╬═════════╣
		║ 					║							 ║							║
		║  trip_id   ║ INTEGER 	  ║ PRIMARY 	║
		║	  time      ║  REAL            ║							║
				title			text
		════════════════════════════
		*/
		
		
		/*
		 * locations table:
		 * loc_id      INTEGER PRIMARY
		 * trip_id    INTEGER
		 * latitude	STRING
		 * longitude STRING
		 * time  REAL       
		 */
		
		/*
		 * Expenses Table
		 * 
		 * */
		
		String create = "CREATE TABLE "+ TABLE_AIRFLOW+"("+KEY_ID+" INTEGER PRIMARY KEY, "+KEY_TRIP+" INTEGER,"+KEY_TIME+" REAL, "+KEY_VAL+" INTEGER)";
		db.execSQL(create);
		create = "CREATE TABLE "+ TABLE_TRIP+"("+KEY_TRIP+" INTEGER PRIMARY KEY,"+KEY_TIME+" REAL)";
		db.execSQL(create);
		create = "CREATE TABLE "+ TABLE_LOC+" ("+KEY_LOC+" INTEGER PRIMARY KEY AUTOINCREMENT,"+KEY_TRIP+" INTEGER NOT NULL,"+ KEY_LAT+" REAL NOT NULL, "+KEY_LONG+" REAL NOT NULL,"+KEY_TIME+" REAL)";
		db.execSQL(create);
		create = "CREATE TABLE "+ TABLE_EXPENSES+"("+KEY_TIME+" REAL NOT NULL,"+KEY_TITLE+" TEXT NOT NULL,"+KEY_COST+" REAL NOT NULL)";
		db.execSQL(create);

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_AIRFLOW);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRIP);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOC);
 
        // Create tables again
        onCreate(db);

	}
	
	//Create a new Trip
	public int newTrip(){
		SQLiteDatabase db = this.getWritableDatabase();
		long id = -1;
		
		try {
			long ts = System.currentTimeMillis();
			// Inserting Row
		    ContentValues values = new ContentValues();
		    values.put(KEY_TIME, ts);
		    id = db.insert(TABLE_TRIP, null, values);
		    
		    
		} catch (Exception e) {
			// TODO: handle exception
		}
		
	    db.close(); // Closing database connection 
	    return (int) id;
	}
	
	// Adding new log entry for airflow data
	public void addEntry(int id,Long ts, int value) {
		SQLiteDatabase db = this.getWritableDatabase();
		id++;
		try {
			// Inserting Row
		    db.execSQL("INSERT INTO "+TABLE_AIRFLOW+"(trip_id,time,value) VALUES("+id+","+ts+","+value+")");
		    Log.d("put in database", "val:"+ value+"id:" + id);
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
	
	public int getCount(int tripId){
		SQLiteDatabase db = this.getReadableDatabase();
		String query = "SELECT * FROM "+TABLE_AIRFLOW+" WHERE "+KEY_TRIP+" ="+tripId;
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
	
	public Cursor getTripFuelValues(int tripId){
		
		SQLiteDatabase db = this.getReadableDatabase();
		String query = "SELECT * FROM "+TABLE_AIRFLOW+" WHERE "+KEY_TRIP+" ="+tripId;
		Cursor cursor = db.rawQuery(query, null);
		//db.close();
		return cursor;
		
	}
	
	Cursor getTripData(){
		SQLiteDatabase db = this.getReadableDatabase();
		String query = "SELECT rowid _id,* FROM trip ORDER BY rowid DESC";
		Cursor cursor = db.rawQuery(query, null);
		return cursor;
		
	}
	
	Cursor getExpenseData(){
		SQLiteDatabase db = this.getReadableDatabase();
		String query = "SELECT rowid _id,* FROM "+TABLE_EXPENSES+" ORDER BY rowid DESC";
		Cursor cursor = db.rawQuery(query, null);
		return cursor;
		}
	
float[][] getFirstLast(String id){
	
	SQLiteDatabase db = this.getReadableDatabase();
  String query = "SELECT * FROM locations WHERE  trip_id = "+id +" ORDER BY ROWID ASC LIMIT 1";
  Cursor cursor = db.rawQuery(query, null);
  if(cursor.getCount() == 0){
	  return null;
  }
  cursor.moveToFirst();
  float lat1 = cursor.getFloat(2);
  float lng1 = cursor.getFloat(3);
  cursor.close();
  query = "SELECT * FROM locations WHERE  trip_id ="+ id+" ORDER BY ROWID DESC LIMIT 1";
  cursor = db.rawQuery(query, null);
  cursor.moveToFirst();
  float lat2 = cursor.getFloat(2);
  float lng2 = cursor.getFloat(3);
  float[][] startend=  {{lat1,lng1},{lat2,lng2}};
  return startend;
	
}
	void addLoc(int tripID,double lat, double logn){
		
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		Long ts = System.currentTimeMillis();
		Log.d("locations","added id: "+tripID+ " lat"+ lat);
		values.put(KEY_TRIP, tripID);
		values.put(KEY_LAT, lat);
		values.put(KEY_LONG, logn);
		values.put(KEY_TIME, ts);
		db.insert(TABLE_LOC, null, values);
		db.close();
		
	}
	
	void addExpense(String desc, float val){
		
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		Long ts = System.currentTimeMillis();
		
		values.put(KEY_TITLE, desc);
		values.put(KEY_COST, val);
		values.put(KEY_TIME, ts);
		db.insert(TABLE_EXPENSES, null, values);
		db.close();
		
	}

	void deleteExpense(String id){
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_EXPENSES, "rowid" + "=" +id, null);
		db.close();
		
	}
	
	float getCurrentMonthExpenses(){
		
		SQLiteDatabase db = this.getReadableDatabase();
		
		  Long ts = System.currentTimeMillis();
		  SimpleDateFormat formatter=  new SimpleDateFormat("yyyy/MMM");
		  String monthNow = formatter.format(ts);
		  
		  float total = 0;
		  
		  String query = "SELECT * FROM "+TABLE_EXPENSES;
		  Cursor cursor = db.rawQuery(query, null);
		  cursor.moveToFirst();
		  
		  while(!cursor.isAfterLast()){
			  ts = cursor.getLong(cursor.getColumnIndex(KEY_TIME));
			  String entryMonth = formatter.format(ts);
			  if(entryMonth.equalsIgnoreCase(monthNow)){
				  
				  total += cursor.getFloat(cursor.getColumnIndex(KEY_COST));
			  }
			  
			  cursor.moveToNext();
		  }
		  
		  return total;
		
	}
}
