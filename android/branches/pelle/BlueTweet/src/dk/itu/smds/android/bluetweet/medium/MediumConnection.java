package dk.itu.smds.android.bluetweet.medium;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import android.util.Log;
import dk.itu.android.bluetooth.BluetoothSocket;
import dk.itu.smds.android.bluetweet.AbstractAsyncBlueTweetConnection;
import dk.itu.smds.android.bluetweet.AbstractBlueTweetService;
import dk.itu.smds.android.bluetweet.Constants;
import dk.itu.smds.android.bluetweet.Tweet;
import dk.itu.smds.android.bluetweet.TweetHash;
import dk.itu.smds.android.bluetweet.async.Command;
import dk.itu.smds.android.bluetweet.async.ReadConfiguration;
import dk.itu.smds.android.bluetweet.async.ReadEnd;
import dk.itu.smds.android.bluetweet.async.ReadEndHash;
import dk.itu.smds.android.bluetweet.async.ReadHash;
import dk.itu.smds.android.bluetweet.async.ReadTweet;
import dk.itu.smds.android.bluetweet.async.WriteConfiguration;

public class MediumConnection extends AbstractAsyncBlueTweetConnection {
	
	ConnectionReader reader;
	
	public MediumConnection(BluetoothSocket socket,
			AbstractBlueTweetService service, boolean connectedToLocalService) {
		super(socket, service, connectedToLocalService);
	}
	
	public void enqueue(Command cmd) {
		// IMPLEMENT ME!!!!
		// HINT: look at the java.util.concurrent.BlockingQueue class...
	}

	@Override
	protected void manageConnection() {
		// the first operation to be executed is to write
		// the configuration. You can choose to use the
		// dk.itu.smds.android.bluetweet.async.WriteConfiguration
		// command or to use directly the ioUtil to write the
		// configuration on the output stream.
		
		// create and start a new ConnectionReader
		
		// start command execution cycle
		Command cmd;
		while(running && !isError) {
			cmd = null;
			
			// COMPLETE ME!!!!
			/*
			 * somehow, obtain a Command that was
			 * previously enqueued using the
			 * enqueue method.
			 */
			
			if(cmd != null) {
				try {
					cmd.setConn(this);
					cmd.execute();
				}catch(Exception e) {
					running = false;
					setError(e);
				}
			}
		}
	}

	@Override
	protected void onCleanUp() throws Exception {
		// COMPLETE ME!!!!
		// since we are using a separate thread to
		// read on the input stream, we need to stop it
		// when exiting
	}

	class ConnectionReader extends Thread{
		
		boolean running = true;
		void cancel() {
			// IMPLEMENT ME!!!!
		}
		
		@Override
		public void run() {
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
						// COMPLETE ME !!!
						/*
						* based on the command read
						* (CONF,TWEET,HASH,END,ENDHASH),
						* call the appropriate method on ioUtil if it is a TWEET, HASH or CONF,
						* then call the enqueue method,
						* creating the appropriate Command object.
						*/
					}
				} catch(Exception e) {
					setError(e);
					running = false;
				}
			}
		}
	}
}
