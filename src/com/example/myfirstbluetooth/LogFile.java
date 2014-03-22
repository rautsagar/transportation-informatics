package com.example.myfirstbluetooth;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import android.os.Environment;
import android.util.Log;

public class LogFile implements Constants {

	private static String myFileName;
	
	public LogFile (String sFileName) {
		
		myFileName = sFileName;
	 }  

	public void write (String sBody) {
		
	    try {
	    	File root = new File(Environment.getExternalStorageDirectory(), LOG_DIRECTORY_NAME);
			if (!root.exists()) {
			    root.mkdirs();
			}
			File file = new File(root, myFileName);
			FileWriter writer = new FileWriter(file, true);
			writer.append(sBody);
			writer.flush();
			writer.close();
	    } catch(IOException e) {
	    	Log.d("File","write Error");
	    }
	 }  
	
}
