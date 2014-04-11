package com.example.myfirstbluetooth;

import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import android.app.Activity;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

public class QueryVehicle implements Constants {

	private int elmStatus = ELM_READY;
	private Vector <String> commands;
	private String communication;
	private boolean finished = true;
	private boolean airflow = false;
	public ReceiveActivity mReceiveActivity = null;

	private DatabaseHandler db;
	//Shared preferences
	private SessionManagement sharedPref;
	//Latest tripID
	private int tripID;
	
	//The timer whichh polls the OBD
	Timer commandTimer;
	Handler uiHandler;
	
	public QueryVehicle() {
	
		mReceiveActivity = new ReceiveActivity();
		commands = new Vector<String>();
		communication = "";
		db = new DatabaseHandler((MyApp)(MyApp.context));
		sharedPref = new SessionManagement((MyApp)(MyApp.context));
		tripID = sharedPref.getTrip();
		uiHandler = new Handler();
	}

	public void clearCommands() {
		commands.clear();
		communication = "";
	}
	
	public void issueCommand(String cmd) {

		clearCommands();
		commands.add(cmd);
		finished = false;
		attemptToSend();		
	}
	
	public void issueCommands(String[] cmds) {

		clearCommands();
		for (int i = 0; i < cmds.length; i++) {
			commands.add(cmds[i]);
		}
		if (commands.size() > 0) {
			finished = false;
		}
		attemptToSend();		
	}

	//Runs the RunPoll method every second
	public void poll(String[] cmds) {

		final String[] theCmds = cmds;
		commandTimer = new Timer();
		Log.d("timer","before");
		commandTimer.schedule(new RunPoll(theCmds), 0, 1000);	
	}
	
	//Stop the  poll
	void stopPoll(){
		if(commandTimer != null){
			commandTimer.cancel();
			commandTimer = null;
		}
	}
	
	
	private class RunPoll extends TimerTask {

		final String[] myCmds;
		
		public RunPoll(String[] cmds) {
			myCmds = cmds;
			
		}
		
		public void run() {
			if (finished) {
				Log.d("timer","in");
				issueCommands(myCmds);
			}
		}
	}

	
	
	private void dumpCommands() {
		String tmp = Integer.toString(commands.size()) + ": ";
		Iterator<String> i = commands.iterator();
		while (i.hasNext()) {
			tmp += i.next() + ",";			
		}
		Log.d("Commands", tmp);		
	}
	
	
	private void attemptToSend() {
		
		String c = "";
		if (elmStatus == ELM_READY) {
			
			if (commands.isEmpty()) {
				finished = true;
				communication += "\r";
				//((MyApp)(MyApp.context)).myLog.write(communication);
				Log.d("log", communication);
				communication = "";
			} else {
				c = commands.remove(0);
				
				//Check if the current command is airflow command, and set boolean if true
				airflow = c.equals("01 10");
						
				
				if(((MyApp)(MyApp.context)).connection == null){
					finished = true;
					uiHandler.post(new Runnable() {
						
						@Override
						public void run() {
							Toast.makeText(((MyApp)(MyApp.context)), "Not connected to OBD", Toast.LENGTH_SHORT).show();
							
						}
					});
		
					
				}else{
					((MyApp)(MyApp.context)).connection.write(c);
				}
				
				if (communication.length() > 0) {
					communication += ",";
				}
				communication += Long.toString(System.currentTimeMillis()) + "," + c;
			}
			
		}
	}
	
    public class ReceiveActivity extends Activity {
        // Handler gets created on the UI-thread
        private Handler mHandler = new Handler();

        // This gets executed in a non-UI thread:
        public void receiveMyMessage(long timeStamp, String msg) {
            final String str = msg;
            final long ts = timeStamp;
            final int val = interpretElmResult(str);
            final String value = Integer.toString(val);
            
            if(!value.equals("-1") && airflow){
            	//put it into the database
            	db.addEntry(tripID,ts, val);
            	airflow = false;
            	
            }

            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    // This gets executed on the UI thread so it can safely modify Views
                	communication += "," + Long.toString(ts) + "," + str + "," + value;
                }
            });
        }

        // This gets executed in a non-UI thread:
        public void receiveMyStatus(int status) {
        	
        	final int s = status;
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    // This gets executed on the UI thread so it can safely modify Views
                	elmStatus = s;
                	attemptToSend();
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
                }
            });
        }
    }

    private int interpretElmResult(String s) {
    	
    	int result = 0;
    	
    	if (s.length() > 6 && s.substring(5, 6).equals(" ")) {
    		String hexString = s.substring(6).replaceAll("\\s+","");
    		try {
        		result = Integer.parseInt(hexString, 16);    			
    		} catch (NumberFormatException e) {
    			result = -1;
    		}
    	} else {
    		return -1;
    	}
    	    	
    	return result;
    }
	
}
