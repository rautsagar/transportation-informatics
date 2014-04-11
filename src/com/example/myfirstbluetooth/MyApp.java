package com.example.myfirstbluetooth;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.Set;
import java.util.UUID;

import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.SystemClock;
import android.util.Log;

public class MyApp extends Application implements Constants {

	public static Context context;
	
	public boolean bluetoothSocketConnected = false;
	public boolean obdConnected = false;

	public String bluetoothStatus = "BT-NC";
	
	public String sentText = "";
	public String statusText = "";
	public String[] receivedText = {"","","","","",""};

//	public ConnectThread myBluetooth = null;
	public BluetoothAdapter mBluetoothAdapter = null;
	public BluetoothDevice mBluetoothDevice = null;
	public BluetoothSocket mSocket = null;
	public ConnectedThread connection = null;

	public int elmStatus = ELM_READY;
	
	public LogFile myLog;
	public LogFile airLog;
	public String filename = "";
	public LogExchange myConversation = null;

	public QueryVehicle mQueryVehicle = null;
	
	//The BroadcastReceiver that listens for bluetooth broadcasts
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {    
    	@Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
//            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

            if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)) {
            	Log.d("Bluetooth", "Device CONNECTED");
            } else if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {
            	Log.d("Bluetooth", "Device DISCONNECTED");
            	bluetoothStatus = "BT-NC";
            	bluetoothSocketConnected = false;
            	CollectData.mReceiveActivity.receiveMyBtStatus(bluetoothStatus);
            	setUpBluetooth();
            }           
        }
    };
    
	
	@Override
	public void onCreate() {
		super.onCreate();
		
		context = getApplicationContext();
		
		Log.d("SYS", "Application CREATED");

		// listen for Bluetooth connection changes
	    this.registerReceiver(mReceiver, new IntentFilter(BluetoothDevice.ACTION_ACL_CONNECTED));
	    this.registerReceiver(mReceiver, new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECTED));

	    
	    //Builds a name for the log file
	    Calendar c = Calendar.getInstance();
	    filename += Integer.toString(c.get(Calendar.YEAR)) + "-";
	    int x = c.get(Calendar.MONTH) + 1;
	    if (x < 10) {
	    	filename += "0";
	    }
	    filename += Integer.toString(x) + "-";
	    x = c.get(Calendar.DAY_OF_MONTH);
	    if (x < 10) {
	    	filename += "0";
	    }
	    filename += Integer.toString(x) + "-";
	    x = c.get(Calendar.HOUR_OF_DAY);
	    if (x < 10) {
	    	filename += "0";
	    }
	    filename += Integer.toString(x) + "-";
	    x = c.get(Calendar.MINUTE);
	    if (x < 10) {
	    	filename += "0";
	    }
	    filename += Integer.toString(x) + "-";
	    x = c.get(Calendar.SECOND);
	    if (x < 10) {
	    	filename += "0";
	    }
	    filename += Integer.toString(x);
	    
//	    filename += ".csv";
	    
//	    Log.d("Calendar", filename);
	    	    
		myLog = new LogFile(filename+".csv");
		myLog.write("Cmd_Time,Command,Resp_Time,Response\r");
		//airLog = new LogFile(filename+"-airlog.csv");
		//airLog.write("Time,Response\n");
		
		setUpBluetooth();
		
		mQueryVehicle = new QueryVehicle();
		
	}


    private CollectData getActivityContext() {
		// TODO Auto-generated method stub
		return null;
	}

    
    
    
	private void setUpBluetooth() {

		mBluetoothDevice = null;
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		
		if (mBluetoothAdapter == null) {
		    // Device does not support Bluetooth
			Log.d("Bluetooth", "not supported");
			return;
		} else {
			Log.d("Bluetooth", "supported");
		}

		
		// TODO - clean this up (add delay or async if BT is off)
		int state = -1;
		while (state != BluetoothAdapter.STATE_ON) {
			state = mBluetoothAdapter.getState();
			if (state == BluetoothAdapter.STATE_OFF) {
				Log.d("Bluetooth", "OFF");
				mBluetoothAdapter.enable();
			} else if (state == BluetoothAdapter.STATE_TURNING_ON) {
				Log.d("Bluetooth", "TURNING ON");
			} else if (state == BluetoothAdapter.STATE_ON) {
				Log.d("Bluetooth", "ON");
			} else if (state == BluetoothAdapter.STATE_TURNING_OFF) {
				Log.d("Bluetooth", "TURNING OFF");
			} else {
				Log.d("Bluetooth", "UNKNOWN");
			}			
		}

		String deviceAddress = "";
		Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
		// If there are paired devices
		if (pairedDevices.size() > 0) {
		    // Loop through paired devices
		    for (BluetoothDevice device : pairedDevices) {
		    	if (device.getName().equals(deviceName)) {
			        deviceAddress = device.getAddress();
					mBluetoothDevice = mBluetoothAdapter.getRemoteDevice(deviceAddress);
			        Log.d("Bluetooth device", device.getName());
			        Log.d("Bluetooth address", deviceAddress);
		    	}
		    }
		} else {
			Log.d("Bluetooth", "No devices found");
		}
		
		if (!deviceAddress.equals("")) {
/*
			myBluetooth = new ConnectThread(mBluetoothDevice);
	    	myBluetooth.start();
*/
		//	new ConnectThread(mBluetoothDevice).start();//Check this line
			obdConnected = true;
		} else {
			Log.d("Bluetooth", "OBD device not found");
		}
	}

	
	public void startBlueConnect(){
		if(obdConnected){
			new ConnectThread(mBluetoothDevice).start();
		}
		
	}
	
	public void manageConnectedSocket(BluetoothSocket mmSocket) {

    	mSocket = mmSocket;
    	Log.d("Bluetooth", "start connected thread / manage");
		connection = new ConnectedThread(mSocket);
		connection.start();
	}
    
    
//    @SuppressLint("NewApi")
	private class ConnectThread extends Thread {
        private BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;
                
        // TODO generate random UUID
        private final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
        public ConnectThread(BluetoothDevice device) {
            // Use a temporary object that is later assigned to mmSocket,
            // because mmSocket is final
            BluetoothSocket tmp = null;
            mmDevice = device;

/*           
            if (mSocket != null && mSocket.isConnected()) {
            	this.cancel();
            	Log.d("Bluetooth", "Closing socket");
            } else {
            	Log.d("Bluetooth", "Socket already disconnected");
            }
*/
            // Get a BluetoothSocket to connect with the given BluetoothDevice
            try {
                // MY_UUID is the app's UUID string, also used by the server code
            	tmp = mmDevice.createRfcommSocketToServiceRecord(MY_UUID);
            } catch (IOException e) { 

            	Log.d("Bluetooth", "socket create FAILED");
            }
            mmSocket = tmp;
            if (mmSocket != null) {
            	Log.d("Bluetooth", "socket create SUCCESS");            	
            }
            
        }
     
		public void run() {
            // Cancel discovery because it will slow down the connection
            mBluetoothAdapter.cancelDiscovery();
         
            while(!bluetoothSocketConnected) {
            
            try {
                // Connect the device through the socket. This will block
                // until it succeeds or throws an exception
            	Log.d("Bluetooth", "initiate connection");
                mmSocket.connect();
                bluetoothSocketConnected = true;
            } catch (IOException connectException) {
                // Unable to connect; close the socket and get out
                bluetoothSocketConnected = false;
            	Log.d("Bluetooth", "socket FAIL CONNECT");
                try {
                    mmSocket.close();
                } catch (IOException closeException) {
                	Log.d("Bluetoooth", "socket CLOSE FAIL");
                }
//                return;

                SystemClock.sleep(5000);
                
                BluetoothSocket tmp = null;
                // Get a new BluetoothSocket to connect with the given BluetoothDevice
                try {
                    // MY_UUID is the app's UUID string, also used by the server code
                	tmp = mmDevice.createRfcommSocketToServiceRecord(MY_UUID);
                } catch (IOException e) { 
                	Log.d("Bluetooth", "socket create FAILED");
                }
                mmSocket = tmp;
                if (mmSocket != null) {
                	Log.d("Bluetooth", "socket create SUCCESS");            	
                }
            
            }
            }
            
            
            // Do work to manage the connection (in a separate thread)
            manageConnectedSocket(mmSocket);
            bluetoothStatus = "BT-Ready";
            CollectData.mReceiveActivity.receiveMyBtStatus(bluetoothStatus);
            Log.d("Bluetooth", "socket CONNECTED");
        }
    
        /** Will cancel an in-progress connection, and close the socket */
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) { 
            	Log.d("Bluetooth", "Could NOT close SOCKET");
            }
        }
    }

  
    
    class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;
     
        public ConnectedThread(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;
     
            // Get the input and output streams, using temp objects because
            // member streams are final
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) { 
            	Log.d("Bluetooth", "stream connect FAILED");
            }
     
            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }
     
        public void run() {
            byte[] buffer = new byte[1024];  // buffer store for the stream
            int bytes; // bytes returned from read()
            String buf = "";
                        
            // Keep listening to the InputStream until an exception occurs
            while (true) {
                try {
                    // Read from the InputStream
                    bytes = mmInStream.read(buffer);
                    if (bytes > 0) {
                    	int i = 0;
                    	while (i < bytes) {
                    		if (buffer[i] == (int)'>') {
//                    			Log.d("status", "Ready");
                    			elmStatus = ELM_READY;
                    			myConversation.finalize();
//                    			Log.d("conversation",myConversation.getExchange());
//                    			myLog.write(myConversation.getExchange());
                    			CollectData.mReceiveActivity.receiveMyStatus(ELM_READY);
                    			mQueryVehicle.mReceiveActivity.receiveMyStatus(ELM_READY);
                      			buf="";
                    		} else {
                    			if (elmStatus == ELM_READY) {
                        			elmStatus = ELM_BUSY;
                        			CollectData.mReceiveActivity.receiveMyStatus(ELM_BUSY);
                        			mQueryVehicle.mReceiveActivity.receiveMyStatus(ELM_BUSY);                    				
                    			}
                    			if (buffer[i] > 0) {
                    				// ELM may sometimes send character code 0 - ignore it
                    				buf += new String(buffer,i,1);
                    			}
                        		if (buffer[i] == 13) { 
                        			// carriage return - response is complete
                        			if (buf.length() > 1) {
                        				// discard empty lines (carriage return only)
                                        // Send the obtained bytes to the UI activity
                                        processResponse(buf);
                                    	String r = receivedText[0].substring(0, receivedText[0].length()-1);
                                    	long timeStamp = System.currentTimeMillis();
                                        if (myConversation != null) {
                                            myConversation.addResponse(timeStamp, r);                                        	
                                        }
                                        CollectData.mReceiveActivity.receiveMyMessages(receivedText);
                                        mQueryVehicle.mReceiveActivity.receiveMyMessage(timeStamp, r);
                        			}
                                    buf = "";                    			
                        		}                    			
                    		}
                    		i++;
                    	}
                	}
                } catch (IOException e) {
                	Log.d("Bluetooth","read FAILED");
                	bluetoothStatus = "BT-NC";
                	CollectData.mReceiveActivity.receiveMyBtStatus(bluetoothStatus);
                	break;
                }
            }
        }
     

        /* Call this from the main activity to send data to the remote device */
        public void write(String msg) {
            try {
                mmOutStream.write(convertStringToBytes(msg+"\r"));
            } catch (IOException e) { 
            	Log.d("Bluetooth", "write FAILED");
            }
            elmStatus = ELM_BUSY;
			CollectData.mReceiveActivity.receiveMyStatus(ELM_BUSY);
			mQueryVehicle.mReceiveActivity.receiveMyStatus(ELM_BUSY);
            myConversation = new LogExchange(System.currentTimeMillis(), msg);
        }
     
        /* Call this from the main activity to shutdown the connection */
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) { 
            	Log.d("Bluetooth", "Could not close SOCKET");
            }
        }
    }
    
    
    
    public byte[] convertStringToBytes(String str) {
    
    	 char[] buffer = str.toCharArray();
    	 byte[] b = new byte[buffer.length];
    	 for (int i = 0; i < b.length; i++) {
    		 b[i] = (byte) buffer[i];
    	 }
    	 return b;
    }
 
    public void processResponse(String msg) {

    	receivedText[5] = new String(receivedText[4]);
    	receivedText[4] = new String(receivedText[3]);
    	receivedText[3] = new String(receivedText[2]);
    	receivedText[2] = new String(receivedText[1]);
    	receivedText[1] = new String(receivedText[0]);
    	receivedText[0] = new String(msg);    	
    }
    
 
}