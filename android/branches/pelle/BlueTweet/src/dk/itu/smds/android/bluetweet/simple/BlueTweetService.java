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
		// IMPLEMENT ME!!
	}

	@Override
	protected void connectToServerDevice(BluetoothDevice device) {
		// IMPLEMENT ME!!
	}

	@Override
	protected void shutdownBluetoothServerSocket() {
		// IMPLEMENT ME!!
	}

}
