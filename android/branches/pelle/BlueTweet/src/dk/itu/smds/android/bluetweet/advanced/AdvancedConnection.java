package dk.itu.smds.android.bluetweet.advanced;

import android.util.Log;
import dk.itu.android.bluetooth.BluetoothSocket;
import dk.itu.smds.android.bluetweet.AbstractAsyncBlueTweetConnection;
import dk.itu.smds.android.bluetweet.AbstractBlueTweetService;
import dk.itu.smds.android.bluetweet.async.Command;

public class AdvancedConnection extends AbstractAsyncBlueTweetConnection {

	AsyncCommandExecutor executor;
	
	public AdvancedConnection(BluetoothSocket socket,
			AbstractBlueTweetService service, boolean connectedToLocalService,
			AsyncCommandExecutor executor) {
		super(socket, service, connectedToLocalService);
		this.executor = executor;
	}

	@Override
	public void enqueue(Command cmd) {
		// delegate the enqueue of the command to
		// the AsyncCommandExecutor
		this.executor.enqueue(cmd, this);
	}

	@Override
	protected void manageConnection() {
		// This method basically read the input stream,
		// and enqueue the appropriate commands.
		
		// the first operation to be executed is to write
		// the configuration. You can choose to use the
		// dk.itu.smds.android.bluetweet.async.WriteConfiguration
		// command or to use directly the ioUtil to write the
		// configuration on the output stream.
		
		byte cmd = -1;
		while(running) {
			try {
				// use the ioUtil to read the
				// next command on the input stream
				// cmd = ...

				if(cmd == -1) {
					Log.i(TAG,"end of stream reached");
					break;
				}

				switch(cmd) {
					
					// COMPLETE ME!!!!!!!
					
				}
			} catch(Exception e) {
				setError(e);
				running = false;
			}
		}
	}

	@Override
	protected void onCleanUp() throws Exception {
		// since we do not spawn threads,
		// this method can be left empty.
	}

}
