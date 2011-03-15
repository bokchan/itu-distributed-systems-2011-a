//based on: http://www.itu.dk/~frza/teaching/SMDS2010/exercises_2.html

package dk.itu.smds.android.bt;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
//import dk.itu.android.bluetooth.BluetoothAdapter;

import android.bluetooth.BluetoothDevice;
//import dk.itu.android.bluetooth.BluetoothDevice;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

public class DeviceListActivity extends Activity {

	/* used when requesting the bluetooth-enabling activity */
	static final int REQUEST_BLUETOOTH_ENABLE = 1;

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
//	When discovering, the android platform will emit intents containing references to the discovered devices. We need, thus, a BroadcastReceiver that listen for these intents. Let’s create one, as an instance variable:
	
	BroadcastReceiver discoveryReceiver = new BroadcastReceiver() {
	    @Override
	    public void onReceive(Context context, Intent intent) {
	        /*do something with the intent here*/
	        String action = intent.getAction();
	        
	        // dont do == here right?
	        if (action.equals(BluetoothDevice.ACTION_FOUND)) {
	        	BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
	        	if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
	        		// if the devices is not yet bonded, add its name and address 
	        		// to the newDevicesArrayAdapter: 
	        		newDevicesArrayAdapter.add( device.getName()+"\n"+device.getAddress() );
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
//        dk.itu.android.bluetooth.BluetoothAdapter.SetContext(this);
        
        /* create the view adapters */
        pairedDevicesArrayAdapter = new ArrayAdapter<String>(this,R.layout.device_name);
        newDevicesArrayAdapter = new ArrayAdapter<String>(this,R.layout.device_name);

        /* get a reference to the lists */
        ListView pairedListView = (ListView)findViewById(R.id.PairedListView);
        ListView newDevicesListView = (ListView)findViewById(R.id.NewDevicesListView);

        /* set the adapters */
        pairedListView.setAdapter(pairedDevicesArrayAdapter);
        newDevicesListView.setAdapter(newDevicesArrayAdapter);

        /* get a reference to the StartDiscoveryButton */
        startDiscoveryButton = (Button)findViewById(R.id.StartDiscoveryButton);
        
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
    	// TODO Auto-generated method stub
    	super.onStart();
    	
    	// After the super.onStart() call, let’s check if there is actually a bluetooth module, 
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
    		// Otherwise, we check if the bluetooth adapter is enabled or not. 
    		// If it is not, we launch the appropriate activity to enable it. 
    		// Usually this activity will ask to the user if she wants to enable the bluetooth module.
    		startActivityForResult(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE), REQUEST_BLUETOOTH_ENABLE);
    	} else {
    		// If the bluetooth module is already enabled, then we call the setup() method.
            setup();
        }
    	
    }
 
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	// TODO Auto-generated method stub
    	super.onActivityResult(requestCode, resultCode, data);
    
    	// This method will be called when returning from an activity start using 
    	// the startActivityForResult method, just like we did in the onStart method.

    	// So what we will do here is to check that the returning activity is the one 
    	// used to enable the bluetooth; this is done checking that the requestCode 
    	// parameter is equals to the parameter we used in the startActivityForResult 
    	// method (in our case, the constant REQUEST_BLUETOOTH_ENABLE)

    	if( REQUEST_BLUETOOTH_ENABLE == requestCode ) {
    		
    		//We then check that the activity result is RESULT_OK. In this case we call the setup() method, otherwise just inform the user that we actually need the bluetooth and terminate the activity.

    	    if( RESULT_OK == resultCode ) {
    	        setup();
    	    } else {
    	        Toast.makeText(this, "Cannot do anything if bluetooth is disabled :(", Toast.LENGTH_SHORT);
    	        finish();
    	    }
    	}
    	
    }
    
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
    public void startDiscovery(){

    	// This method will be called when the user click on the Start Discovery button.

    	// If the system is currently discovering, we cancel the current discovery and restart it (we don’t want to lose some devices):

    	if(btadapter.isDiscovering()){btadapter.cancelDiscovery();}
    	btadapter.startDiscovery();
    	// Then we prevent the user to click another time on the Start Discovery button, at least until the current discovery is finished:

    	startDiscoveryButton.setEnabled(false);

    }
    
}