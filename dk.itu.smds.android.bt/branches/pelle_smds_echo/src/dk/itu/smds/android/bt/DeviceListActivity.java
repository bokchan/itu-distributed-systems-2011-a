//based on: http://www.itu.dk/~frza/teaching/SMDS2010/exercises_2.html
package dk.itu.smds.android.bt;

// XXX dont import android.bluetooth in order to get the test stff up and run 
//import android.bluetooth.BluetoothAdapter;
//import android.bluetooth.BluetoothDevice;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import dk.itu.android.bluetooth.BluetoothAdapter;
import dk.itu.android.bluetooth.BluetoothDevice;

public class DeviceListActivity extends Activity {

// delete	
//	/* used when requesting the bluetooth-enabling activity */
//	static final int REQUEST_BLUETOOTH_ENABLE = 1;

	
	public static String EXTRA_DEVICE_ADDRESS = "dk.itu.smds.android.bt.DeviceListActivity.EXTRA_DEVICE_ADDRESS";

	
	/* reference to the button used to start the discovery phase */
	Button startDiscoveryButton;
	/* the local bluetooth adapter */
	BluetoothAdapter btadapter;

	/* view adapter for the list of paired devices */
	ArrayAdapter<String> pairedDevicesArrayAdapter;
	/* view adapter for the list of new devices */
	ArrayAdapter<String> newDevicesArrayAdapter;


	//	The BroadcastReceiver for the discovery phase
	//
	//	When discovering, the android platform will emit intents containing references to the discovered devices. We need, thus, a BroadcastReceiver that listen for these intents. Letâ€™s create one, as an instance variable:

	BroadcastReceiver discoveryReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			/*do something with the intent here*/
			String action = intent.getAction();
			
			Log.i("BCRECEIVERDISCOVERY", "received intent action: " + action);
			Log.i(getPackageCodePath(), "received intent action: " + action);

			// dont do == here right?
			if (action.equals(BluetoothDevice.ACTION_FOUND)) {
				BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
					// if the devices is not yet bonded, add its name and address 
					// to the newDevicesArrayAdapter: 
					newDevicesArrayAdapter.add( device.getName()+"\n"+device.getAddress() );
					
					// XXX not clear in tutorial
					newDevicesArrayAdapter.notifyDataSetChanged();
					
					
				}
				// else
				// if it is equals to BluetoothDevice.BOND_BONDED, then the device is 
				// already listed in the PairedListView
			}

			// If the action is equals to BluetoothAdapter.ACTION_DISCOVERY_FINISHED, 
			// then re-enable the startDiscoveryButton (startDiscoveryButton.setEnabled(true);). 
			// Soon we will see that when starting discovering, we will disable the button for 
			// the duration of that phase.
			if (action.equals(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)) {
				startDiscoveryButton.setEnabled(true);
			}

		}
	};


	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);


		// testing specific 
		dk.itu.android.bluetooth.BluetoothAdapter.SetContext(this);

		/* create the view adapters */
		pairedDevicesArrayAdapter = new ArrayAdapter<String>(this,R.layout.device_name);
		newDevicesArrayAdapter = new ArrayAdapter<String>(this,R.layout.device_name);

		/* get a reference to the lists */
		ListView pairedListView = (ListView)findViewById(R.id.PairedListView);
		ListView newDevicesListView = (ListView)findViewById(R.id.NewDevicesListView);

		/* set the adapters */
//		pairedListView.setAdapter(pairedDevicesArrayAdapter);
//		newDevicesListView.setAdapter(newDevicesArrayAdapter);
		pairedListView.setOnItemClickListener(deviceClickListener);
		newDevicesListView.setOnItemClickListener(deviceClickListener);

		/* get a reference to the StartDiscoveryButton */
		startDiscoveryButton = (Button)findViewById(R.id.StartDiscoveryButton);
        startDiscoveryButton.setOnClickListener(new OnClickListener() {
        	@Override
        	public void onClick(View arg0) {
        		startDiscovery(arg0);
        	}
        });

	}

	/** ensure discovery process is closed down **/
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();

		// Since discovering is a heavy process, when exiting from the 
		// application we want to make sure that we terminate it; this 
		// is done by calling the cancelDiscovery() method on the instance of BluetoothAdapter:
		btadapter.cancelDiscovery();
		//Next we also want to unregister the broadcast receiver:
		unregisterReceiver(discoveryReceiver);
	}


	@Override
	protected void onStart() {
		
		super.onStart();

		// Then obtain a reference to the local BluetoothAdapter:
		btadapter = BluetoothAdapter.getDefaultAdapter();
		
		// note: since DeviceListActivity is called from EchoActivity
		// so no bluetooth check needed is already done and we can call up setup directly
		
		setup();

	}

	
	OnItemClickListener deviceClickListener = new OnItemClickListener() {
	    @Override
	    public void onItemClick(android.widget.AdapterView<?> av, View v, int arg2, long arg3) {
	        // cancel the discovery, as the user already selected a device
	        btadapter.cancelDiscovery();

	        // get the string in the text view
	        String info = ((TextView)v).getText().toString();
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
	
	
	
// can be deleted	
//	@Override
//	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//		// TODO Auto-generated method stub
//		super.onActivityResult(requestCode, resultCode, data);
//
//		// This method will be called when returning from an activity start using 
//		// the startActivityForResult method, just like we did in the onStart method.
//
//		// So what we will do here is to check that the returning activity is the one 
//		// used to enable the bluetooth; this is done checking that the requestCode 
//		// parameter is equals to the parameter we used in the startActivityForResult 
//		// method (in our case, the constant REQUEST_BLUETOOTH_ENABLE)
//
//		if( REQUEST_BLUETOOTH_ENABLE == requestCode ) {
//
//			//We then check that the activity result is RESULT_OK. In this case we call the setup() method, otherwise just inform the user that we actually need the bluetooth and terminate the activity.
//
//			if( RESULT_OK == resultCode ) {
//				setup();
//			} else {
//				Toast.makeText(this, "Cannot do anything if bluetooth is disabled :(", Toast.LENGTH_SHORT);
//				finish();
//			}
//		}
//
//	}

	private void setup(){

		// In this method we just add the already paired devices to the pairedDevicesArrayAdapter:

		for(BluetoothDevice device : btadapter.getBondedDevices()) {
			pairedDevicesArrayAdapter.add( device.getName()+"\n"+device.getAddress() );
		}
		//and register the discoveryReceiver to the BluetoothDevice.ACTION_FOUND and BluetoothAdapter.ACTION_DISCOVERY_FINISHED intents:

		IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
		registerReceiver(discoveryReceiver, filter);
		filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
		registerReceiver(discoveryReceiver, filter);

	}

	// used in main.xml - android:onClick="startDiscovery"
	// XXX View view is essential here - missed that part from the exercise - is that clear
	public void startDiscovery(View view){

//		System.out.println("startDiscovery");
		// use the android log stuff
		Log.i(getLocalClassName(), "andoird cat log example we are exploding...");
		
		
		// This method will be called when the user click on the Start Discovery button.
		
		// If the system is currently discovering, we cancel the current discovery 
		// and restart it (we dont want to lose some devices):
		if(btadapter.isDiscovering()){
//			System.out.println("hest 2 - btadapter.isDiscovering() is true"); - not possisble to system print use log
			btadapter.cancelDiscovery();
		}

		btadapter.startDiscovery();
		// Then we prevent the user to click another time on the Start Discovery button, at least until the current discovery is finished:

		startDiscoveryButton.setEnabled(false);

	}

}