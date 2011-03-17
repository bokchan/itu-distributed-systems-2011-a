package dk.itu.smds.android.bluetweet.advanced;

import dk.itu.android.bluetooth.BluetoothDevice;
import dk.itu.android.bluetooth.BluetoothServerSocket;
import dk.itu.smds.android.bluetweet.AbstractBlueTweetService;

public class BlueTweetService extends AbstractBlueTweetService {

	boolean serverRunning = true;
	BluetoothServerSocket serverSocket = null;
	Thread serverThread;
	
	AsyncCommandExecutor executor = new AsyncCommandExecutor();

	@Override
	protected void configureBluetoothServerSocket() {
		executor.start();
		
		// IMPLEMENT ME!!!!
	}

	@Override
	protected void connectToServerDevice(BluetoothDevice device) {
		// IMPLEMENT ME!!!!
	}

	@Override
	protected void shutdownBluetoothServerSocket() {
		// IMPLEMENT ME!!!!
	}

}
