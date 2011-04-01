package dk.itu.smds.android.bt;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.UUID;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import dk.itu.android.bluetooth.BluetoothAdapter;
import dk.itu.android.bluetooth.BluetoothDevice;
import dk.itu.android.bluetooth.BluetoothServerSocket;
import dk.itu.android.bluetooth.BluetoothSocket;

public class EchoActivity extends Activity {

	/* used when requesting the bluetooth-enabling activity */
	//	static final int REQUEST_BLUETOOTH_ENABLE = 1; = same as REQUEST_ENABLE_BLUETOOTH right ? 

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

		//		Like in the previous exercise, if the requestCode is the constant 
		//		REQUEST_ENABLE_BLUETOOTH and the result is ok, call the onBluetoothEnabled method. 
		//		Otherwise notify the user and finish the activity.

		// here == is ok since REQUEST_ENABLE_BLUETOOTH is not a string
		if (requestCode == REQUEST_ENABLE_BLUETOOTH) {
			onBluetoothEnabled();
		} else {

			/* uh-oh.. no bluetooth module found! */
			Toast.makeText(this, "Sorry, no bluetooth module found!", Toast.LENGTH_SHORT);

			/* terminate the activity */
			finish();
		}


		//		This time we have to handle two more possibilities: the REQUEST_CONNECT_DEVICE 
		//		(e.g. the user selected a device to connect to) and REQUEST_ENABLE_DISCOVERABLE 
		//		(which turns the device in discoverable mode for a couple of minutes).

		//		In the first case, if the result is ok (RESULT_OK == resultCode), then call 
		//		the deviceSelected method. Remember to extract the selected device address 
		//		from the Intent data parameter:		
		//		String deviceAddress = data.getStringExtra(DeviceListActivity.EXTRA_DEVICE_ADDRESS);

		// RESULT_OK is a global static field on the activity class - right ?
		if (requestCode == RESULT_OK && requestCode == REQUEST_CONNECT_DEVICE) {
			String deviceAddress = data.getStringExtra(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
			deviceSelected(deviceAddress);
		}


		//		In the second case, it is not really important if the result is ok or cancelled, 
		//		as a client device could always connect to us because the two devices are already 
		//		paired. Anyway, just start the server thread:

		if(REQUEST_ENABLE_DISCOVERABLE == requestCode) {
			new Thread(server).start();
		}


	}



	public void deviceSelected(String deviceAddress) {

		// This method will be called when coming back from the DeviceListActivity. 
		// Here you want to call 
		setServer(false); 
		//and create a new Client instance: 
		this.client = new Client(deviceAddress); 
		// Since the user still has to write some message, 
		// do not start a thread with the client runnable.

	}

	//	deviceSelected(string deviceAddress)
	//
	//	This method will be called when coming back from the DeviceListActivity. 
	//	Here you want to call 
	//	setServer(false); and create a new Client instance: 
	//	this.client = new Client(deviceAddress);
	//	Since the user still has to write some message, 
	//	do not start a thread with the client runnable.



	// used in *.xml - android:onClick="startServer"
	// XXX View view is essential here - missed that part from the exercise - is that clear
	public void startServer(View view){
		//		Called when the user press the Start Server button (without much fantasy). 
		//		Here you want to call 
		setServer(true); 
		//		create a new Server instance 
		this.server = new Server();


		//		and finally start an activity to put the device in discoverable mode

		//		TODO: - not sure about this one ?
		// taken from andreas branc
		startActivityForResult(
				new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE), 
				REQUEST_ENABLE_DISCOVERABLE
		);

	}


	public void stopServer(View view){

		// I guess that you managed to understand when this method is called.
		// Anyway, here you just want to check if the server instance if null, 
		//otherwise call the server.stop() method.

		if(this.server != null) {
			server.stop();
			//			Log.i("BCRECEIVERDISCOVERY", "received intent action: " + action);
			Log.i(getPackageCodePath(), "server stopped");
		} else {
			Log.i(getPackageCodePath(), "no running servers");
		}

	}


	public void sendMessage(View view){

		//		If the device is is client mode, then the user has to press the Send Messsage 
		//		button to actually create a communication with the server device. In this 
		//		method you want to se the client.message property with the user input text:

		client.message = ((EditText)findViewById(R.id.SendEditText)).getText().toString();

		//		and then start a thread to handle the bluetooth communication:

		new Thread(client).start();

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

		//		In the inner class Server, we still need to implement the public void run() method. To start a server, one must call the BluetoothServerSocket BluetoothAdapter.listenUsingRfcommWithServiceRecord(String,UUID) method.
		//
		//		Call it using the EchoServiceName and EchoServiceUUID fields:

		public void run() {
			try {
				socket = btadapter.listenUsingRfcommWithServiceRecord(EchoServiceName, EchoServiceUUID);
			}
			//		we need to catch an IOException here, as the signature of run does not permit to throw exceptions:

			catch(IOException e) {
				running = false;
			}
			//		Just exit directly from the server. Then we can start accepting connections:

			while(running) {
				// declare a socket
				BluetoothSocket clientSocket = null;
				try {
					//		Call the blocking method BluetoothServerSocket.accept() to wait until a connection is available:

					clientSocket = socket.accept();
					//		In our little example, we will synchronously handle a client connection. Keep in mind that a more useful service may require to handle the communication with a client in a separate thread. Our protocol is quite simple: the client sends a String terminated by a carriage and return chars (“\r\n”). So we can use a BufferedReader and read directly a line:

					BufferedReader bufReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
					final String line = bufReader.readLine().trim();
					//		Then simply get the socket OutputStream and rewrite the same line (prefixed with a “>” char), and flush the OutputStream.

					clientSocket.getOutputStream().write( (">"+line+"\r\n").getBytes("UTF-8") );
					clientSocket.getOutputStream().flush();
					//		Now, this is a bit tricky: since we are doing this in a thread different from the principal one (the UI thread), we are not allowed to make modifications to the user interface. We can, instead, call the Activity.runOnUiThread(Runnable) method; the runnable will be run in the UI thread, as soon as it is free:

					runOnUiThread(new Runnable(){
						@Override
						public void run() {
							appendToResponseView("Received >> "+line);
						}
					});
					//		We catch the IOException: if an exception occurred, we do not care too much. In a real application we should try to recover, e.g., corrupted or incomplete data

				} catch(Exception e) {
					Log.e("Server","Exception in server loop",e);
					//		in the finally block, we check if the clientSocket is null, and if it is not try to close the socket.

				} finally {
					if(clientSocket != null) {
						try{clientSocket.close();}catch(Exception ignored){}
					}
				}
			}
		}
	}
	/* A class to handle the service client communication */
	private class Client implements Runnable {
		BluetoothDevice device;
		BluetoothSocket socket;
		String message;
		public Client(String serverDeviceAddress){
			device = btadapter.getRemoteDevice(serverDeviceAddress);
		}


		//		In the client class, we want to:
		//
		//		connect to the server device, using the 
		//		Bluetoothdevice.createRfcommSocketToServiceRecord method
		//		writing the user message
		//		read the response and append it in the ResponseTextView
		//		Like for the Server class, we need to encapsulate the network commands in 
		//		a try...catch block, as every command can potentially throw an IOException.
		//
		//		In the try block, first create the BluetoothSocket socket, calling 
		//		device.createRfcommSocketToServiceRecord(EchoServiceUUID).

		public void run(){

			try {
				
				socket = device.createRfcommSocketToServiceRecord(EchoServiceUUID);
				socket.connect();
				
			    socket.getOutputStream().write( (message+"\r\n").getBytes("UTF-8") );
			    socket.getOutputStream().flush();
				
			    BufferedReader bufReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			    final String echoed = bufReader.readLine();
			    
			    runOnUiThread(new Runnable(){
					public void run() {
						appendToResponseView("Echoed >> "+echoed);
					}
				});
			    
			}
			//		we need to catch an IOException here, as the signature of run does not permit to throw exceptions:

			catch(IOException e) {
				if(socket != null) {
					try{socket.close();}catch(Exception ignored){}
				}
			}


			//			try {
			//				
			//			} catch(Exception e) {
			//				Log.e("Client","Exception in cient loop",e);
			//				//		in the finally block, we check if the clientSocket is null, and if it is not try to close the socket.
			//
			//			} finally {
			//
			//			}



		}
	}




}