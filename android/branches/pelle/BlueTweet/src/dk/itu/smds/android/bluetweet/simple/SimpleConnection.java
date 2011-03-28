package dk.itu.smds.android.bluetweet.simple;

import java.util.HashSet;
import java.util.Set;

import android.util.Log;
import dk.itu.android.bluetooth.BluetoothSocket;
import dk.itu.smds.android.bluetweet.AbstractBlueTweetConnection;
import dk.itu.smds.android.bluetweet.AbstractBlueTweetService;
import dk.itu.smds.android.bluetweet.Constants;
import dk.itu.smds.android.bluetweet.Tweet;
import dk.itu.smds.android.bluetweet.TweetHash;

public class SimpleConnection extends AbstractBlueTweetConnection {

	public SimpleConnection(BluetoothSocket socket,
			AbstractBlueTweetService service, boolean connectedToLocalService) {
		super(socket, service, connectedToLocalService);
	}

	@Override
	protected void manageConnection() {
		/*
		 * Write the configuration, use the ioUtil
		 * read the first command, if it is CMD_CONF,
		 * (hint: this.getIOUtil().writeConfiguration(); 
		 *        byte cmd = this.getIOUtil().readCommand()...)
		 * read the remote configuration. Return if
		 * the command is different.
		 * call setRemoteAgeTime with the remoteConfiguration
		 * timestamp.
		 * If the remote device is a client (e.g. is connected to 
		 * the local bluetooth service), call manageServer.
		 * Call manageClient otherwise
		 */

		this.getIOUtil().writeConfiguration();

		byte cmd = this.getIOUtil().readCommand();
		TweetHash tHash = this.getIOUtil().readConfigurationOrHash();
		if (cmd == dk.itu.smds.android.bluetweet.Constants.CMD_CONF) {
			this.setRemoteAgeTime(tHash.getTimestamp());
			/*If the remote device is a client (e.g. is connected to 
			 * the local bluetooth service), call manageServer.
			 * Call manageClient otherwise
			 */

		} else {
			Log.e(TAG, "failed manageConnection(): cmd different from CMD_CONF");
			return;

		}




	}

	void manageServer() {
		/*
		 * Start a loop in which read a TweetHash or the ENDHASH
		 * command (exit the loop when the ENDHASH command is read)
		 * Compute the updates and load also the tweets unknown by
		 * the client (use service.loadTweets and service.bulkLoadTweets)
		 * Then sends all the tweets to the remote device.
		 * After the tweets, send the END command.
		 * Load the local hash (service.loadLatestsHash) and send
		 * them to the remote device. After this send the ENDHASH
		 * command.
		 * Finally, start a loop in which read a Tweet or the END
		 * command (exit the loop when the END command is read )
		 */

		byte cmd = this.getIOUtil().readCommand();
		Set<TweetHash> rmHashes = new HashSet<TweetHash>();
		while(cmd != Constants.CMD_END_HASH) {
			rmHashes.add(getIOUtil().readConfigurationOrHash());


		}

		// Outside loop? 
		this.getIOUtil().writeCommand(Constants.CMD_END);

		// Wrong -->  
		Set<TweetHash> tHashes = new HashSet<TweetHash>();
		this.service.loadTweets(tHashes, this.getReferenceTimestamp());
		Set<TweetHash> tHashesBulk = new HashSet<TweetHash>();
		this.service.bulkLoadTweets(tHashesBulk, this.getReferenceTimestamp());
		// <-- Wrong 

		this.getIOUtil().writeHashes(this.service.loadLatestsHash(this.remoteDevice.getName(), this.getReferenceTimestamp()));

		cmd = this.getIOUtil().readCommand();
		Tweet tweet = null;
		while(cmd != Constants.CMD_END) {
			tweet =  this.getIOUtil().readTweet();
		}

	}

	void manageClient() {
		/*
		 * This method reflects the manageServer one.
		 * Load the latests hash, send them, then send the ENDHASH
		 * command.
		 * Loop to read the remote tweets, until the END command
		 * is read.
		 * Loop to read the remote hashes, until the ENDHASH command
		 * is read.
		 * Load tweet updates and tweets unkwnown by the server, send
		 * then, then send the END command
		 */
		this.getIOUtil().writeHashes(this.service.loadLatestsHash(this.remoteDevice.getName(), this.getReferenceTimestamp()));
		this.getIOUtil().writeCommand(Constants.CMD_END_HASH);

		// Read remote tweets 
		Set<Tweet> rTweets = new HashSet<Tweet>();
		while (this.getIOUtil().readCommand() != Constants.CMD_END) {
			rTweets.add(this.getIOUtil().readTweet());
		}

		// Read remote tweethashes
		Set<TweetHash> rtHashes = new HashSet<TweetHash>();
		while (this.getIOUtil().readCommand() != Constants.CMD_END_HASH) {
			rtHashes.add(this.getIOUtil().readConfigurationOrHash());
		}

		/* Store new tweets */
		Set<Tweet> newTweets = new HashSet<Tweet>(); // Maybe equal to rTweets
		for (Tweet t : newTweets) {
			this.service.store(t);
		}
	}

	@Override
	protected void onCleanUp() throws Exception {
		// since we do not have any other resource
		// associated with this connection, this method
		// is left empty.
	}

}
