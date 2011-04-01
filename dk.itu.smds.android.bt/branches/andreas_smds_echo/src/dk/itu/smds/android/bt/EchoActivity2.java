package dk.itu.smds.android.bt;

import java.util.UUID;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import dk.itu.android.bluetooth.BluetoothAdapter;
import dk.itu.android.bluetooth.BluetoothDevice;
import dk.itu.android.bluetooth.BluetoothServerSocket;
import dk.itu.android.bluetooth.BluetoothSocket;


public class EchoActivity2 extends Activity {
	/* used when requesting the bluetooth-enabling activity */
	public static String EXTRA_DEVICE_ADDRESS = "dk.itu.smds.android.bt.DeviceListActivity.EXTRA_DEVICE_ADDRESS";

	/* request to enable the bluetooth */
	static final int REQUEST_ENABLE_BLUETOOTH = 1;
	/* request to make the device discoverable */
	static final int REQUEST_ENABLE_DISCOVERABLE = 2;
	/* used when requesting the bluetooth-enabling activity */
	static final int REQUEST_BLUETOOTH_ENABLE = 1;
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

	/* reference to the button used to start the discovery phase */
	Button startServerButton;

	/* reference to the button used to start the discovery phase */
	Button selectDeviceButton;

	/* view adapter for the list of paired devices */
	ArrayAdapter<String> pairedDevicesArrayAdapter;
	/* view adapter for the list of new devices */
	ArrayAdapter<String> newDevicesArrayAdapter;

	OnItemClickListener deviceClickListener = new OnItemClickListener() {
		public void onItemClick(AdapterView<?> arg0, android.view.View arg1,
				int arg2, long arg3) {
			// cancel the discovery, as the user already selected a device
			btadapter.cancelDiscovery();

			// get the string in the text view
			String info = TextView.class.cast(arg1).getText().toString();
			// ..split it and take the address part (the second string in the array)
			String addr = info.split("\n")[1];

			// create a new intent and put the selected device address
			Intent intent = new Intent();
			intent.putExtra(EXTRA_DEVICE_ADDRESS, addr);

			// set the result and finish this activity
			setResult(RESULT_OK,intent);
			finish();

		}
	};

	BroadcastReceiver discoveryReceiver = new BroadcastReceiver() 
	{
		@Override
		public void onReceive(Context context, Intent intent) 
		{
			/*do something with the intent here*/
			String action = intent.getAction();

			if (action==BluetoothDevice.ACTION_FOUND)
			{
				BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				if (device.getBondState() !=BluetoothDevice.BOND_BONDED)
				{
					newDevicesArrayAdapter.add( device.getName()+"\n"+device.getAddress() );
					newDevicesArrayAdapter.notifyDataSetChanged();
				}
			}

			if (action==BluetoothAdapter.ACTION_DISCOVERY_FINISHED)
			{
				startServerButton.setEnabled(true);
			}
		}
	};


	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		dk.itu.android.bluetooth.BluetoothAdapter.SetContext(this);

		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		setResult( RESULT_CANCELED);

		/* create the view adapters */
		pairedDevicesArrayAdapter = new ArrayAdapter<String>(this,R.layout.device_name);
		newDevicesArrayAdapter = new ArrayAdapter<String>(this,R.layout.device_name);

		/* get a reference to the lists */
		ListView pairedListView = (ListView)findViewById(R.id.PairedListView);
		ListView newDevicesListView =(ListView)findViewById(R.id.NewDevicesListView);
		/*  add item listener */
		pairedListView.setOnItemClickListener(deviceClickListener);
		newDevicesListView.setOnItemClickListener(deviceClickListener);

		/* set the adapters */
		pairedListView.setAdapter(pairedDevicesArrayAdapter);
		newDevicesListView.setAdapter(newDevicesArrayAdapter);

		/* get a reference to the StartDiscoveryButton */
		selectDeviceButton = (Button)findViewById(R.id.SelectDeviceButton);
		startServerButton = (Button)findViewById(R.id.StartServerButton);
	}

	public void onDestroy() 
	{
		btadapter.cancelDiscovery();
		unregisterReceiver(discoveryReceiver);
	}

	private void setup()
	{
		Log.i("DEVICELISTACTIVITY","Setting up receiver");
		//add the already paired devices to the pairedDevicesArrayAdapter
		for(BluetoothDevice device : btadapter.getBondedDevices()) 
		{
			pairedDevicesArrayAdapter.add( device.getName()+"\n"+device.getAddress() );
		}

		//register the discoveryReceiver to the BluetoothDevice.ACTION_FOUND and BluetoothAdapter.ACTION_DISCOVERY_FINISHED intents
		IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
		registerReceiver(discoveryReceiver, filter);
		filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
		registerReceiver(discoveryReceiver, filter);
	}

	//This method will be called when the user click on the Start Discovery button
	public void startDiscovery(View view)
	{
		Log.i("DEVICELISTACTIVITY","start discovery");
		//If the system is currently discovering, we cancel the current discovery and restart it 
		//(we don’t want to lose some devices)
		if(btadapter.isDiscovering()){btadapter.cancelDiscovery();}
		btadapter.startDiscovery();

		//Then we prevent the user to click another time on the Start Discovery button, 
		//at least until the current discovery is finished:
		startServerButton.setEnabled(false);
	}

	protected void onBluetoothEnabled() {

		startServerButton.setEnabled(true);
		selectDeviceButton.setEnabled(true);
	} 

	//This method just set some views visible and some other invisible, depending on what the device role (server or client):
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

	//Just an utility method to append a line to the ResponseTextView:

	private void appendToResponseView( String line ) {
		String curText = responseTextView.getText().toString();
		if(curText.length()!=0) {
			curText+="\n";
		}
		curText+=line;
		responseTextView.setText(curText);
	}



	@Override
	protected void onStart() {

		super.onStart();
		//obtain a reference to the local BluetoothAdapter:
		btadapter = BluetoothAdapter.getDefaultAdapter();

		//check if a bluetooth module is available or not; just check if the btadapter is null
		if(btadapter == null) 
		{
			/* uh-oh.. no bluetooth module found! */
			Toast.makeText(this, "Sorry, no bluetooth module found!", Toast.LENGTH_SHORT);

			/* terminate the activity */
			finish();
		}
		//else we check if the bluetooth adapter is enabled or not. If it is not, we launch 
		//the appropriate activity to enable it. Usually this activity will ask to the user 
		//if she wants to enable the bluetooth module.
		else
		{
			if( !btadapter.isEnabled() ) 
			{
				startActivityForResult(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE),  REQUEST_BLUETOOTH_ENABLE);
			}
			//If the bluetooth module is already enabled, then we call the setup() method.
			else
			{
				setup();
			}
		}

	}


	@Override
	public void startActivityForResult(Intent intent, int requestCode) {
		super.startActivityForResult(intent, requestCode);
	}


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

