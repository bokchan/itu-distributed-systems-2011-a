//based on: http://www.itu.dk/~frza/teaching/SMDS2010/exercises_2.html

package dk.itu.smds.android.bt;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import dk.itu.android.bluetooth.BluetoothAdapter;
import dk.itu.android.bluetooth.BluetoothDevice;

public class DeviceListActivity extends Activity 
{
	 /* used when requesting the bluetooth-enabling activity */
    static final int REQUEST_BLUETOOTH_ENABLE = 1;
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
    	dk.itu.android.bluetooth.BluetoothAdapter.SetContext(this);
    	
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
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
        startDiscoveryButton.setOnClickListener(new OnClickListener() {
        	@Override
        	public void onClick(View arg0) {
        		startDiscovery(arg0);
        	}
        });
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
    	startDiscoveryButton.setEnabled(false);
    }
    
    public void onStart()
    {
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
   		        startActivityForResult(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE), REQUEST_BLUETOOTH_ENABLE);
   		    }
   		    //If the bluetooth module is already enabled, then we call the setup() method.
   		   	else
   		   	{
    	       setup();
   		   	}
    	}
    }
    
    //check that the returning activity is the one used to enable the bluetooth; this is done checking 
    //that the requestCode parameter is equals to the parameter we used in the startActivityForResult 
    //method (in our case, the constant REQUEST_BLUETOOTH_ENABLE)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) 
    {
    	super.onActivityResult(requestCode, resultCode, data);
    	if( REQUEST_BLUETOOTH_ENABLE == requestCode ) 
    	{
    		if( RESULT_OK == resultCode ) 
    		{
    	        setup();
    	    } 
    		else 
    		{
    	        Toast.makeText(this, "Cannot do anything if bluetooth is disabled :(", Toast.LENGTH_SHORT);
    	        finish();
    		}
    	}	
    }
    
    
    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
    
    	super.startActivityForResult(intent, requestCode);
    }
    
    
    
    /* reference to the button used to start the discovery phase */
        Button startDiscoveryButton;
        /* the local bluetooth adapter */
        BluetoothAdapter btadapter;

        /* view adapter for the list of paired devices */
        ArrayAdapter<String> pairedDevicesArrayAdapter;
        /* view adapter for the list of new devices */
        ArrayAdapter<String> newDevicesArrayAdapter;
	   
        
        
        
        
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
                	startDiscoveryButton.setEnabled(true);
                }
            }
    };
    
}