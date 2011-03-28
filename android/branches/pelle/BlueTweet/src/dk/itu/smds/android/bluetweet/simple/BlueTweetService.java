package dk.itu.smds.android.bluetweet.simple;

import java.io.IOException;

import android.util.Log;
import dk.itu.android.bluetooth.BluetoothDevice;
import dk.itu.android.bluetooth.BluetoothServerSocket;
import dk.itu.android.bluetooth.BluetoothSocket;
import dk.itu.smds.android.bluetweet.AbstractBlueTweetService;

public class BlueTweetService extends AbstractBlueTweetService {

	boolean serverRunning = true;
	BluetoothServerSocket serverSocket = null;
	Thread serverThread;

	@Override
	protected void configureBluetoothServerSocket() {
		BluetoothSocket socket = null;
		try {
			serverSocket=adapter.listenUsingRfcommWithServiceRecord(BlueTweetName, BlueTweetUuid);
			// Accept incoming requests
		} catch (IOException e) {
			Log.e(TAG,  "listenUsingRfcommWithServiceRecord() failed", e);
		}
		
		boolean connected = false;
		while(!connected) {
			try {
				socket = serverSocket.accept();
			} catch (IOException e) {

				Log.e(TAG,  "accept() failed", e);
			}
			if (socket != null) {
				synchronized (socket) {
					serverThread = new Thread(
					new SimpleConnection(socket, BlueTweetService.this, true ));
					serverThread.start();
					connected = true;
				}
			}
		}
	}

	@Override
	protected void connectToServerDevice(BluetoothDevice device) {	
		Log.d(TAG, "connect to: " + device);
		try {
			device.createRfcommSocketToServiceRecord(BlueTweetUuid);
		} catch (IOException e) {
			Log.d(TAG, "failed to connect to: " + device);
			
		}
	}

	@Override
	protected void shutdownBluetoothServerSocket() {
		serverRunning = false;
		if(serverSocket != null) {
			try{serverSocket.close();serverSocket=null;}catch(Exception ignored){}
		}
	}

}
