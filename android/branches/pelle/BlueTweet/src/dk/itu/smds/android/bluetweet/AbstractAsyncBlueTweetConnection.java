package dk.itu.smds.android.bluetweet;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import dk.itu.android.bluetooth.BluetoothSocket;
import dk.itu.smds.android.bluetweet.async.Command;

/**
 * Abstract class for asynchronous connections.
 * Provides additional methods and properties to handle
 * @author frza
 *
 */
public abstract class AbstractAsyncBlueTweetConnection extends
		AbstractBlueTweetConnection {

	
	/**
	 * True if the connection is current running
	 */
	protected boolean running = true;

	/**
	 * True if the command END has been sent.
	 * @see Constants.CMD_END
	 */
	boolean remoteEnded = false;
	/**
	 * True if the command END has been read.
	 * @see Constants.CMD_END
	 */
	boolean localEnded = false;
	
	/**
	 * The set of the hash received from the remote device
	 */
	Set<TweetHash> receivedHash = new HashSet<TweetHash>();
	
	public AbstractAsyncBlueTweetConnection(BluetoothSocket socket,
			AbstractBlueTweetService service, boolean connectedToLocalService) {
		super(socket, service, connectedToLocalService);
	}
	
	/**
	 * Check if the local and remote connections finished to interact.
	 * If both are terminated, the running field is set to false.
	 */
	protected void checkEnd() {
		if( remoteEnded && localEnded ){
			running = false;
		}
	}
	public void setLocalEnded(){ 
		localEnded = true;
		checkEnd();
	}
	public void setRemoteEnded(){
		remoteEnded = true;
		checkEnd();
	}
	public boolean ended() {
		return remoteEnded&&localEnded;
	}

	/**
	 * Add the hash to the set of read hash
	 * @param hash
	 */
	public void addReceivedHash(TweetHash hash) {
		receivedHash.add(hash);
	}

	/**
	 * Enqueue the command in the command queue.
	 * @param cmd
	 */
	public abstract void enqueue(Command cmd);

	// methods used to interact with the database. Refer to the 
	// AbstractblueTweetService methods, they serves the same thing.
	// Exposed here to avoid the necessity to expose the service
	// field.
	
	public Set<TweetHash> loadLatestHash() {
		return service.loadLatestsHash(this.remoteDevice.getAddress(), loadSince);
	}
	public List<Tweet> loadTweetUpdates(TweetHash hash) {
		return service.loadTweets(hash, loadSince);
	}
	public List<Tweet> bulkLoadTweetUpdates() {
		return service.bulkLoadTweets(receivedHash, loadSince);
	}
	public void store(Tweet tweet) {
		service.store(tweet);
	}
	
	//
}
