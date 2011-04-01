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
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import dk.itu.android.bluetooth.BluetoothAdapter;
import dk.itu.android.bluetooth.BluetoothDevice;
import dk.itu.android.bluetooth.BluetoothServerSocket;
import dk.itu.android.bluetooth.BluetoothSocket;


public class EchoActivity extends Activity {
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


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		((Button)findViewById(R.id.SelectDeviceButton)).setEnabled(false);
		((Button) findViewById(R.id.StartServerButton)).setEnabled(false);
	}

	@Override
	protected void onStart() {
		super.onStart();

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
				startActivityForResult(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE), REQUEST_ENABLE_BLUETOOTH);
			}
			//If the bluetooth module is already enabled, then we call the setup() method.
			else
			{
				onBlueToothEnabled();
			}
		}
	}

	private void onBlueToothEnabled() {
		((Button)findViewById(R.id.SelectDeviceButton)).setEnabled(true);
		((Button) findViewById(R.id.StartServerButton)).setEnabled(true);
	}

	/***
	 * This method just set some views visible and some other invisible, depending on what the device role (server or client):
	 * @param server
	 */
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


	/***
	 * Just an utility method to append a line to the ResponseTextView:
	 * @param line
	 */

	private void appendToResponseView( String line ) {
		String curText = responseTextView.getText().toString();
		if(curText.length()!=0) {
			curText+="\n";
		}
		curText+=line;
		responseTextView.setText(curText);
	}

	public void deviceSelected(String deviceAddress) {
		setServer(false);
		this.client = new Client(deviceAddress);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		super.onActivityResult(requestCode, resultCode, data);
		if( REQUEST_ENABLE_BLUETOOTH == requestCode ) 
		{
			if( RESULT_OK == resultCode ) 
			{
				onBlueToothEnabled();
			} 
			else 
			{
				Toast.makeText(this, "Cannot do anything if bluetooth is disabled :(", Toast.LENGTH_SHORT);
				finish();
			}
		} else if (REQUEST_CONNECT_DEVICE == requestCode) {
			if (RESULT_OK == resultCode) {
				String deviceAddress = data.getStringExtra(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
				deviceSelected(deviceAddress);
			}
		} else if (REQUEST_ENABLE_DISCOVERABLE == requestCode) {
			new Thread(server).start(); 
		}
	}


	/***
	 * Start server
	 * @param view
	 */
	public void startServer(View view) {
		setServer(true);
		this.server = new Server();

		startActivityForResult(new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE), REQUEST_ENABLE_DISCOVERABLE);
	}

	/***
	 * Stop server 
	 * @param view
	 */
	public void stopServer(View view) {
		server.stop();
	}


	public void sendMessage(View view) {
		client.message = ((EditText)findViewById(R.id.SendEditText)).getText().toString();
		new Thread(server).start();
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

		public void run(){
			try {
				socket = btadapter.listenUsingRfcommWithServiceRecord(EchoServiceName, EchoServiceUUID);
			} catch (IOException e) {
				running = false;
				Log.e("Server","Exception in socket initialization",e);

			}

			while(running) {
				BluetoothSocket clientSocket = null;
				try {
					clientSocket = socket.accept();
					BufferedReader bufReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
					final String line = bufReader.readLine().trim();

					clientSocket.getOutputStream().write( (">"+line+"\r\n").getBytes("UTF-8") );
					clientSocket.getOutputStream().flush();

					runOnUiThread(new Runnable(){

						public void run() {
							appendToResponseView("Received >> "+line);
						}
					});
				}
				catch (Exception e) {

					Log.e("Server","Exception in server loop",e);
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
			catch (Exception e) {
				if(socket != null) {
					try{socket.close();}catch(Exception ignored){}
				}
			}
					
		}
	}
}



