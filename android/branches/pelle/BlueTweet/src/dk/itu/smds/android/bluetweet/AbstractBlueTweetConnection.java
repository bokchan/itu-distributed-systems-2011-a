package dk.itu.smds.android.bluetweet;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.util.Log;
import dk.itu.android.bluetooth.BluetoothDevice;
import dk.itu.android.bluetooth.BluetoothSocket;


/**
 * Abstract class for BlueTweet connections.
 * A connection is an interaction with a remote device.
 * @author frza
 *
 */
public abstract class AbstractBlueTweetConnection implements Runnable {
	protected static final String TAG = "BlueTweetConnection";
	
	/**
	 * The service that created this connection
	 */
	protected AbstractBlueTweetService service;

	protected BluetoothSocket socket;
	protected BluetoothDevice remoteDevice;
	
	protected InputStream is;
	protected OutputStream os;
	
	/**
	 * When true, then an error occurred.
	 */
	protected boolean isError = false;
	/**
	 * True when the remoteDevice is 
	 * connected to the local BlueTweet service.
	 * False if the remoteDevice holds 
	 * the server.
	 */
	protected boolean connectedToLocalService;
	
	/**
	 * Utility class for handling IO
	 */
	protected BlueTweetIOUtil ioUtil;
	
	/**
	 * The max age time in milliseconds required
	 * by the remoteDevice
	 */
	long remoteAgeTime;
	/**
	 * Roughly identify when the connection
	 * started.
	 */
	protected long referenceTimestamp;
	
	/**
	 * referenceTimestamp minus remoteAgeTime
	 */
	protected long loadSince;
	
	public AbstractBlueTweetConnection(
			BluetoothSocket socket,
			AbstractBlueTweetService service,
			boolean connectedToLocalService) {
		this.socket = socket;
		this.remoteDevice = socket.getRemoteDevice();
		this.connectedToLocalService = connectedToLocalService;
		this.service = service;
	}
	
	public LocalConfiguration getLocalConfiguration() {
		return service.getLocalConfiguration();
	}
	public BlueTweetIOUtil getIOUtil(){ return ioUtil; }
	
	public void run() {
		if(!service.canConnect(this)) {
			/**
			 * usually meaning that this device is already connected
			 */
			return;
		}
		
		/**
		 * initialize the connection
		 */
		initialize();
		/**
		 * since an error can occur during initialization, check
		 */
		if(isError){return;}
		/**
		 * this timestamp will be used as a reference
		 */
		this.referenceTimestamp = System.currentTimeMillis();
		
		/**
		 * start managing the connection
		 */
		Log.i(TAG, "start managing connection...");
		manageConnection();
		
		Log.i(TAG, "connection management finished.");
		/**
		 * when returning from the manageConnection method,
		 * if there are no errors, call onConnectionEnd
		 */
		if(!isError) {
			onConnectionEnd();
		}
	}
	
	/**
	 * Call this method when an exception occur.
	 * This will inform the BlueTweetService of the fact,
	 * and call the cleanUp method.
	 * @param e
	 */
	protected void setError(Exception e) {
		isError = true;
		service.onConnectionError(this, e);
		cleanUp();
	}
	
	/**
	 * Initialize the connection, connecting the
	 * socket if required, and getting the 
	 * required IO streams
	 */
	void initialize() {
		try {
			if(!connectedToLocalService) {
				// meanining, we are trying to connect to a server device
				socket.connect();
			}
			
			is = socket.getInputStream();
			os = socket.getOutputStream();
			ioUtil = new BlueTweetIOUtil(this);
		} catch(IOException e) {
			setError(e);
		}
	}
	
	/**
	 * Close the socket. Calls onCleanUp
	 */
	void cleanUp() {
		Log.i(TAG,"cleaning up");
		try {
			socket.close();
		} catch(IOException ignored){}
		try {
			onCleanUp();
		} catch(Exception ignored){}
	}
	
	/**
	 * Cancel the connection
	 */
	void cancel() {
		cleanUp();
	}
	
	/**
	 * Called when the connection ends
	 */
	protected void onConnectionEnd() {
		cleanUp();
		service.onConnectionEnd(this);
	}
	
	/**
	 * When the remote configuration is read,
	 * call this to set the remoteAgeTime.
	 * This will set also set the <code>loadSince</code>
	 * field.
	 * @param remoteAgeTime
	 */
	public void setRemoteAgeTime(long remoteAgeTime) {
		this.remoteAgeTime = remoteAgeTime;
		this.loadSince = this.referenceTimestamp - remoteAgeTime;
	}
	
	/**
	 * This is the place where, e.g. closing running threads
	 * @throws Exception
	 */
	protected abstract void onCleanUp() throws Exception;
	
	/**
	 * Do the network stuff in this method. No, you can't throw exceptions..
	 */
	protected abstract void manageConnection();

	// various getters
	
	public long getRemoteAgeTime() {
		return remoteAgeTime;
	}


	public long getReferenceTimestamp() {
		return referenceTimestamp;
	}
	
	public long getLoadSince() {
		return loadSince;
	}

	public boolean isError() {
		return isError;
	}

	public boolean isConnectedToLocalService() {
		return connectedToLocalService;
	}
}
