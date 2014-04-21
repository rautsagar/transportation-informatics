package com.example.myfirstbluetooth;



import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;
 
public class CustomCursorAdapter extends CursorAdapter {
 
    @SuppressWarnings("deprecation")
	public CustomCursorAdapter(Context context, Cursor c) {
        super(context, c);
    }
 
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // when the view will be created for first time,
        // we need to tell the adapters, how each item will look
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View retView = inflater.inflate(R.layout.trip_row, parent, false);
 
        return retView;
    }
 
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // here we are setting our data
        // that means, take the data from the cursor and put it in views
 
        TextView textViewTripId= (TextView) view.findViewById(R.id.tr_id);
        textViewTripId.setText(cursor.getString(cursor.getColumnIndex("trip_id")));
 
        TextView textViewTripTime = (TextView) view.findViewById(R.id.tr_time);
        textViewTripTime.setText(getCurrentDTG(cursor.getLong(cursor.getColumnIndex("time"))));
    }
    
    public static String getCurrentDTG (long l_time)
    {
      Date date = new Date (l_time);
      // Specify the date format you want to display
      SimpleDateFormat dtgFormat = new SimpleDateFormat ("E-dd-MMM:HH:mm:ss-yyyy");
      return dtgFormat.format (date);
    }
}