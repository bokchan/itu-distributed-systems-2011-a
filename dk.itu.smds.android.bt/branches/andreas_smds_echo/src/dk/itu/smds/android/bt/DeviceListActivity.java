package dk.itu.smds.android.bt;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import dk.itu.android.bluetooth.BluetoothAdapter;
import dk.itu.android.bluetooth.BluetoothDevice;

public class DeviceListActivity extends Activity
{
	
	public static String EXTRA_DEVICE_ADDRESS = "dk.itu.smds.android.bt.DeviceListActivity.EXTRA_DEVICE_ADDRESS";
	 /* used when requesting the bluetooth-enabling activity */
  
  
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
    	BluetoothAdapter.SetContext(this);
    	
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        setResult(RESULT_CANCELED);
        
        /* create the view adapters */
        pairedDevicesArrayAdapter = new ArrayAdapter<String>(this,R.layout.device_name);
        newDevicesArrayAdapter = new ArrayAdapter<String>(this,R.layout.device_name);

        /* get a reference to the lists */
        ListView pairedListView = (ListView)findViewById(R.id.PairedListView);
        ListView newDevicesListView = (ListView)findViewById(R.id.NewDevicesListView);
        /*  add item listener */
		pairedListView.setOnItemClickListener(deviceClickListener);
		newDevicesListView.setOnItemClickListener(deviceClickListener);

        /* set the adapters */
        pairedListView.setAdapter(pairedDevicesArrayAdapter);
        newDevicesListView.setAdapter(newDevicesArrayAdapter);
        
        

        /* get a reference to the StartDiscoveryButton */
        startDiscoveryButton = (Button)findViewById(R.id.StartDiscoveryButton);
        
    }
    
    OnItemClickListener deviceClickListener = new OnItemClickListener() {
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