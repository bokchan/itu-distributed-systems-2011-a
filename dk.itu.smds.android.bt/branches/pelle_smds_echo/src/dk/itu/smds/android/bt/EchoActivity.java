package dk.itu.smds.android.bt;

import java.util.UUID;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import dk.itu.android.bluetooth.BluetoothAdapter;
import dk.itu.android.bluetooth.BluetoothDevice;
import dk.itu.android.bluetooth.BluetoothServerSocket;
import dk.itu.android.bluetooth.BluetoothSocket;

public class EchoActivity extends Activity {

	/* used when requesting the bluetooth-enabling activity */
	static final int REQUEST_BLUETOOTH_ENABLE = 1;
	
	/* request to enable the bluetooth */
	static final int REQUEST_ENABLE_BLUETOOTH = 1;
	/* request to make the device discoverable */
	static final int REQUEST_ENABLE_DISCOVERABLE = 2;
	/* request to select a device to connect to */
	static final int REQUEST_CONNECT_DEVICE = 3;

	/* the service UUID and name */
	static final UUID EchoServiceUUID = UUID.fromString("419bbc68-c365-4c5e-8793-5ebff85b908c");
	static final String EchoServiceName = "Smds.Echo";

	/* the local bluetooth adapter */
	BluetoothAdapter btadapter;

	/* identify if the user selected the device to be a server or client */
	boolean isServer;

	/* reference to the ResponseTextView */
	TextView responseTextView;

	/* the server instance */
	Server server;
	/* the client instance */
	Client client;



	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);


		//        Until we know that the bluetooth is present and 
		//        enable, disable the SelectDeviceButton and StartServerButton, e.g. findViewById( R.id.StartServerButton ).setEnabled(false);.

		findViewById( R.id.StartServerButton ).setEnabled(false);
		findViewById( R.id.SelectDeviceButton ).setEnabled(false);

	}


	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();

		// After the super.onStart() call, letâ€™s check if there is actually a bluetooth module, 
		// and check if it is enabled or not. 
		// First create a private void setup(){} empty (for now) method.
		// pelle: created bellow of onStart() 

		// Then obtain a reference to the local BluetoothAdapter:
		btadapter = BluetoothAdapter.getDefaultAdapter();

		// Here we can check if a bluetooth module is available or not; just check if 
		// the btadapter is null:
		if(btadapter == null) {
			/* uh-oh.. no bluetooth module found! */
			Toast.makeText(this, "Sorry, no bluetooth module found!", Toast.LENGTH_SHORT);

			/* terminate the activity */
			finish();
		} else if( !btadapter.isEnabled() ) {
//			pelle: not sure about this one 
			
			
//			// Otherwise, we check if the bluetooth adapter is enabled or not. 
//			// If it is not, we launch the appropriate activity to enable it. 
//			// Usually this activity will ask to the user if she wants to enable the bluetooth module.
//			startActivityForResult(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE), REQUEST_BLUETOOTH_ENABLE);
			
			onBluetoothEnabled();
			
		} else {
			// If the bluetooth module is already enabled, then we call the setup() method.
//			setup();
		}

	}

	private void onBluetoothEnabled(){
		findViewById( R.id.StartServerButton ).setEnabled(true);
		findViewById( R.id.SelectDeviceButton ).setEnabled(true);
	}
	
	
//	// pelle: not really sure about this one
//	private void setup(){
//
//		// In this method we just add the already paired devices to the pairedDevicesArrayAdapter:
//
//		for(BluetoothDevice device : btadapter.getBondedDevices()) {
//			pairedDevicesArrayAdapter.add( device.getName()+"\n"+device.getAddress() );
//		}
//		//and register the discoveryReceiver to the BluetoothDevice.ACTION_FOUND and BluetoothAdapter.ACTION_DISCOVERY_FINISHED intents:
//
//		IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
//		registerReceiver(discoveryReceiver, filter);
//		filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
//		registerReceiver(discoveryReceiver, filter);
//
//	}
	
	
//	setServer(boolean)
//
//	This method just set some views visible and some other invisible, depending on what the device role (server or client):

	private void setServer(boolean server) {
	    this.isServer = server;
	    findViewById(R.id.ResponseTextView).setVisibility(View.VISIBLE);
	    findViewById(R.id.StartServerButton).setVisibility(View.GONE);
	    findViewById(R.id.SelectDeviceButton).setVisibility(View.GONE);
	    if(!server) {
	        findViewById(R.id.SendEditText).setVisibility(View.VISIBLE);
	        findViewById(R.id.SendButton).setVisibility(View.VISIBLE);
	    } else {
	        findViewById(R.id.StopServerButton).setVisibility(View.VISIBLE);
	    }
	}
	
//	appendToResponseView(String)
//
//	Just an utility method to append a line to the ResponseTextView:

	private void appendToResponseView( String line ) {
	    String curText = responseTextView.getText().toString();
	    if(curText.length()!=0) {
	        curText+="\n";
	    }
	    curText+=line;
	    responseTextView.setText(curText);
	}
	
//	pelle: not sure about this one but if i understand it right its call up when the devicelist activity is closed?
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
//		TODO: call and implement deviceSelected(string deviceAddress)  
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		
		
		onBluetoothEnabled();
	}
	
//	deviceSelected(string deviceAddress)
//
//	This method will be called when coming back from the DeviceListActivity. 
//	Here you want to call 
//	setServer(false); and create a new Client instance: 
//	this.client = new Client(deviceAddress);
//	Since the user still has to write some message, 
//	do not start a thread with the client runnable.
	
	
	/* A class to handle the service server communication */
	private class Server implements Runnable {
		boolean running = true;
		BluetoothServerSocket socket = null;

		public void stop() {
			running = false;
			if(socket != null) {
				try{socket.close();socket=null;}catch(Exception ignored){}
			}
		}

		public void run(){}
	}
	/* A class to handle the service client communication */
	private class Client implements Runnable {
		BluetoothDevice device;
		BluetoothSocket socket;
		String message;
		public Client(String serverDeviceAddress){
			device = btadapter.getRemoteDevice(serverDeviceAddress);
		}

		public void run(){}
	}




}