package dk.itu.smds.android.bluetweet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import dk.itu.android.bluetooth.BluetoothAdapter;
import dk.itu.android.bluetooth.BluetoothDevice;

/**
 * Base service class for the BlueTweet service. Provides a common skeleton for the service.
 * <br/>
 * Your task is to fill the abstract methods in a subclass.
 * @author frza
 *
 */
public abstract class AbstractBlueTweetService extends Service implements Runnable {
	/**
	 * The ACTION to be used to start the service
	 */
	public static final String ACTION = "dk.itu.android.bluetweet.START_BLUETWEET_SERVICE";
	
	/**
	 * The UUID of the service
	 */
	public static final UUID BlueTweetUuid = UUID.fromString("e555b780-2793-11df-8a39-0800200c9a66");
	
	/**
	 * The name of the service
	 */
	public static final String BlueTweetName = "BLUETWEET";
	
	/**
	 * The string to be used when calling Log methods
	 */
	protected static final String TAG = "BlueTweetService";

	// preference stuff, file and properties
	static final String PREFS_FILE = "preferences";
	static final String PREF_SCAN_INTERVAL = "scan.interval";
	static final String PREF_SCAN_DURATION = "scan.duration";
	static final String PREF_FETCH_THRESHOLD = "fetch.threshold.time";
	// end preference stuff

	/**
	 * The BluetoothAdapter
	 */
	protected BluetoothAdapter adapter;
	
	/**
	 * The configuration of the service
	 */
	protected LocalConfiguration conf;
	
	/**
	 * Through this executor, the method run
	 * is scheduled every PREF_SCAN_INTERVAL
	 * milliseconds
	 */
	protected ScheduledExecutorService scheduler;
	
	/**
	 * The device obtainer
	 */
	protected BluetoothDeviceObtainer deviceObtainer;
	
	/**
	 * Used to query the content provider in the
	 * service thread
	 */
	Handler handler;
	
	/**
	 * The collection of the current connections
	 */
	Map<String,AbstractBlueTweetConnection> connections = new HashMap<String,AbstractBlueTweetConnection>();
	
	/**
	 * True when the service has been told to exit, but it is still
	 * waiting for some threads to finish.
	 */
	boolean isExiting = false;

	/**
	 * Return the local configuration
	 * @return
	 */
	public LocalConfiguration getLocalConfiguration() {
		return conf;
	}
	
	@Override
	public void onCreate() {
		SharedPreferences settings = getSharedPreferences(PREFS_FILE, MODE_PRIVATE);
		handler = new Handler();
		adapter = BluetoothAdapter.getDefaultAdapter();
		
		conf = new LocalConfiguration(
				adapter.getAddress(), 
				1000 * settings.getLong(PREF_SCAN_INTERVAL, 20), 
				settings.getLong(PREF_SCAN_DURATION, 300), 
				1000 * settings.getLong(PREF_FETCH_THRESHOLD, 300));
		
		deviceObtainer = new BluetoothDeviceObtainer(adapter);
		
		Log.i(TAG,"configuring BlueTooth service");
		configureBluetoothServerSocket();
		
		Log.i(TAG,"starting scheduler...");
		scheduler = Executors.newSingleThreadScheduledExecutor();
		scheduler.scheduleAtFixedRate(this, 0, conf.interval, TimeUnit.MILLISECONDS);
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.i(TAG,"exiting");
		isExiting = true;
		scheduler.shutdown();
		shutdownBluetoothServerSocket();
		shutdownConnections();
	}
	
	/**
	 * Called at predefined intervals, 
	 * load the devices (discovered and paired)
	 * and call the <code>connectToServerDevice</code>
	 * method.
	 */
	public void run() {
		Log.i(TAG,"running service cycle");
		Set<BluetoothDevice> devices = null;
		try {
			devices = deviceObtainer.lookupDevices(this, true);
		} catch(Exception e){}
		
		if(devices != null) {
			for(BluetoothDevice device : devices) {
				if(isExiting){return;}
				Log.i(TAG,"trying to connect to possible server device: "+device.getAddress());
				connectToServerDevice(device);
			}
		} else {
			Log.w(TAG,"uh-ops, devices are null!");
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	/**
	 * Return true if the connection has been successfully added
	 * to the set of current connections.
	 * Returns false if the service is exiting or if the 
	 * connection regards a device already connected.
	 * @param connection
	 * @return
	 */
	boolean canConnect( AbstractBlueTweetConnection connection ) {
		if(isExiting){return false;}
		boolean out = false;
		synchronized( connections ) {
			if(!connections.containsKey(connection.remoteDevice.getAddress())) {
				Log.i(TAG,"accepting a connection with "+connection.remoteDevice.getAddress());
				connections.put(connection.remoteDevice.getAddress(),connection);
				out = true;
			} else {
				Log.i(TAG,"device " + connection.remoteDevice.getAddress() + " is already connected!");
			}
		}
		return out;
	}
	
	/**
	 * Called when an exception occur when handling a connection.
	 * The connection is removed from the set of current connections.
	 * @param connection
	 * @param e
	 */
	void onConnectionError( AbstractBlueTweetConnection connection, Exception e ) {
		onConnectionEnd( connection );
		Log.w(TAG,"exception while connecting with device "+connection.remoteDevice.getAddress());
	}
	
	/**
	 * Called when a connection has been closed.
	 * @param connection
	 */
	void onConnectionEnd( AbstractBlueTweetConnection connection ) {
		synchronized(connections) {
			connections.remove(connection.remoteDevice.getAddress());
		}
	}
	
	/**
	 * Close all current connections. Called when closing the service.
	 */
	protected void shutdownConnections() {
		synchronized(connections) {
			for(AbstractBlueTweetConnection conn : connections.values()) {
				conn.cancel();
			}
			connections.clear();
		}
	}
	
	/**
	 * Open the Bluetooth server socket and start to accept
	 * connections from remote devices
	 */
	protected abstract void configureBluetoothServerSocket();
	
	/**
	 * Close the Bluetooth server socket and the thread
	 * managing it
	 */
	protected abstract void shutdownBluetoothServerSocket();
	
	/**
	 * Connect to the device; use the current thread.
	 * @param device
	 */
	protected abstract void connectToServerDevice(BluetoothDevice device);
	
	
	// these methods are required to query/store tweets
	// you can safely skip the private methods,
	// read the javadocs of the public ones.
	private void innerLoadHashes(String remoteDevice,long since, Set<TweetHash> hashes) {
		Log.i(TAG,"open cursor for tweet hash...");
		Cursor c = getContentResolver().query(
				BlueTweet.Tweet.CONTENT_HASH_URI, 
				null, 
				BlueTweet.Tweet.BT_ADDR + "!='"+remoteDevice+"' AND "
				+ BlueTweet.Tweet.TIMESTAMP + ">" + since, 
				null,
				BlueTweet.Tweet.DEFAULT_SORT_ORDER);
		int colIdxTimestamp = c.getColumnIndex(BlueTweet.Tweet.TIMESTAMP);
		int colIdxBtAddr = c.getColumnIndex(BlueTweet.Tweet.BT_ADDR);

		boolean hasNext = c.moveToFirst();
		
		Log.i(TAG,"loading hash not from "+remoteDevice+" since "+since);
		
		while(hasNext) {
			long timestamp = c.getLong(colIdxTimestamp);
			String btaddr = c.getString(colIdxBtAddr);
			Log.i(TAG,"loaded hash: " + timestamp + "/" + btaddr);
			hashes.add(new TweetHash(btaddr,timestamp));
			hasNext = c.moveToNext();
		}
		c.close();
	}
	
	private void innerLoadTweets(Set<TweetHash> receivedHash, long since, List<Tweet> tweets) {
		Log.i(TAG,"innerLoadTweets called...");
		// select only the tweets more recent than the reference timestamp (now - remoteMaxAgeTime)
		String select = BlueTweet.Tweet.TIMESTAMP + ">" + since +
		" AND " + BlueTweet.Tweet.BT_ADDR + " not in (";
		
		List<String> selArgs = new ArrayList<String>();
		String dbg = "";
		boolean first = true;
		// ..as well it doesn't want already sync.ed tweets
		for(TweetHash h : receivedHash) {
			if(first){first=false;}else{select+=",";}
			select += "?";
			selArgs.add(h.originAddress);
			dbg += h.originAddress+", ";
		}
		select += ")";

		Log.i(TAG,"loading tweets with select: "+select+", btaddr list: "+dbg);

		Cursor c = getContentResolver().query(
				BlueTweet.Tweet.CONTENT_URI,
				null,
				select,
				selArgs.toArray(new String[]{}),
				BlueTweet.Tweet.DEFAULT_SORT_ORDER);
		
		tweets.addAll(read(c));
	}
	private void innerLoadTweets(String fromBluetoothAddress, long since, List<Tweet> tweets) {
		Cursor c = getContentResolver().query(
				Uri.withAppendedPath(BlueTweet.Tweet.CONTENT_BTADDR_URI, fromBluetoothAddress),
				null,
				BlueTweet.Tweet.TIMESTAMP + ">" + since,
				null,
				BlueTweet.Tweet.DEFAULT_SORT_ORDER);
		Log.i(TAG,"loading tweets from "+fromBluetoothAddress+" since "+since);
		tweets.addAll(read(c));
	}
	
	/**
	 * Load the latest hash to be transmitted to a remote device.<br/>
	 * The hash does not include the one of the remote device and hash
	 * older than the since parameter.
	 * <br/>
	 * Suppose the DB contains the following rows:
	 * <table>
	 * 	<tr>
	 *   <th>timestamp</th>
	 *   <th>device</th>
	 *  </tr>
	 *  <tr>
	 *   <td>5</td><td>A</td>
	 *  </tr>
	 *  <tr>
	 *   <td>3</td><td>B</td>
	 *  </tr>
	 *  <tr>
	 *   <td>7</td><td>C</td>
	 *  </tr>
	 *  <tr>
	 *   <td>6</td><td>D</td>
	 *  </tr>
	 * </table>
	 * calling this method with parameters C and 4 will returns the set
	 * <table>
	 * 	<tr>
	 *   <th>timestamp</th>
	 *   <th>device</th>
	 *  </tr>
	 *  <tr>
	 *   <td>5</td><td>A</td>
	 *  </tr>
	 *  <tr>
	 *   <td>6</td><td>D</td>
	 *  </tr>
	 * </table>
	 * since the device C is not considered, and the last B hash is older than 4.
	 * @param remoteDevice the connected device to which send the hash
	 * @param since a reference timestamp minus the remote age time
	 * @return
	 */
	public Set<TweetHash> loadLatestsHash(final String remoteDevice, final long since) {
		final Object lock = new Object();
		final Set<TweetHash> hashes = new HashSet<TweetHash>();
		handler.post(new Runnable(){
			public void run() {
				innerLoadHashes(remoteDevice, since, hashes);
				synchronized(lock){
					lock.notify();
				}
			}
		});
		synchronized(lock) {
			try {
				lock.wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		Log.i(TAG, "reading hash, return " + hashes.size() + " hash");
		return hashes;
	}
	
	/**
	 * Load the tweets sent by devices that are not in the set 
	 * of the received hash, and are newer that the since parameter.
	 * <br><br>
	 * You want to call this method after you received and synchronized 
	 * hash with a remote device.
	 * @param receivedHash
	 * @param since
	 * @return
	 */
	public List<Tweet> bulkLoadTweets(final Set<TweetHash> receivedHash, final long since) {
		Log.i(TAG,"BulkLoadingTweets");
		final Object lock = new Object();
		final List<Tweet> tweets = new ArrayList<Tweet>();
		handler.post(new Runnable(){
			public void run() {
				innerLoadTweets(receivedHash, since, tweets);
				synchronized(lock){
					lock.notify();
				}
			}
		});
		synchronized(lock) {
			try {
				lock.wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return tweets;
	}
	
	/**
	 * Load the updates relatives to the passed hash. Note that is the last
	 * timestamp is older than the since parameter, then only the updates
	 * newer than the since parameter will be returned.
	 * @param hash
	 * @param since
	 * @return
	 */
	public List<Tweet> loadTweets(final TweetHash hash, final long since) {
		final Object lock = new Object();
		final List<Tweet> tweets = new ArrayList<Tweet>();
		handler.post(new Runnable(){
			public void run() {
				long ref = (hash.timestamp>since) ? hash.timestamp : since;
				innerLoadTweets(hash.originAddress, ref, tweets);
				synchronized(lock){lock.notify();}
			}
		});
		synchronized(lock) {
			try {
				lock.wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return tweets;
	}
	
	/**
	 * Load the updates relatives to the set of passed hash. For each of the hash, 
	 * if the timestamp is older than the since parameter, then only the updates
	 * newer than the since parameter will be returned.
	 * @param hash
	 * @param since
	 * @return
	 */
	public List<Tweet> loadTweets(final Set<TweetHash> hash, final long since) {
		Log.i(TAG,"LoadTweets");
		final Object lock = new Object();
		final List<Tweet> tweets = new ArrayList<Tweet>();
		handler.post(new Runnable(){
			public void run() {
				for(TweetHash h : hash) {
					long ref = (h.timestamp>since) ? h.timestamp : since;
					innerLoadTweets(h.originAddress,ref,tweets);
				}
				synchronized(lock) {
					lock.notify();
				}
			}
		});
		synchronized(lock) {
			try {
				lock.wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return tweets;
	}
	
	/**
	 * Save the tweet in the database
	 * @param tweet
	 */
	public void store(Tweet tweet) {
		store(tweet.getBluetoothAddress(),tweet.getTimestamp(),tweet.getDeviceName(),tweet.getMessage());
	}
	public void store(final String fromBluetoothAddress, final long timestamp, final String name,
			final String message) {
		
		handler.post(new Runnable(){
			public void run() {
				ContentValues cv = new ContentValues();
				cv.put(BlueTweet.Tweet.BT_ADDR, fromBluetoothAddress);
				cv.put(BlueTweet.Tweet.TIMESTAMP, timestamp);
				cv.put(BlueTweet.Tweet.NAME, name);
				cv.put(BlueTweet.Tweet.MSG, message);
				Log.i(TAG,"creating new tweet: " + timestamp +"/"+ fromBluetoothAddress + " -- " + message);
				getContentResolver().insert(BlueTweet.Tweet.CONTENT_URI, cv);
			}
		});

	}
	
	List<Tweet> read(Cursor c) {
		List<Tweet> out = new ArrayList<Tweet>();
		int colIdxTimestamp = c.getColumnIndex(BlueTweet.Tweet.TIMESTAMP);
		int colIdxBtAddr = c.getColumnIndex(BlueTweet.Tweet.BT_ADDR);
		int colIdxName = c.getColumnIndex(BlueTweet.Tweet.NAME);
		int colIdxMsg = c.getColumnIndex(BlueTweet.Tweet.MSG);
		
		boolean hasNext = c.moveToFirst();
		
		Tweet t;
		while(hasNext) {
			t = new Tweet();
			t.setTimestamp( c.getLong(colIdxTimestamp) );
			t.setBluetoothAddress( c.getString(colIdxBtAddr) );
			t.setDeviceName( c.getString(colIdxName) );
			t.setMessage( c.getString(colIdxMsg) );
			out.add(t);
			Log.i(TAG,"adding tweet "+t.getTimestamp()+"/"+t.getBluetoothAddress()+" - "+t.getMessage());
			hasNext = c.moveToNext();
		}
		c.close();

		Log.i(TAG,"return tweet list of size: "+out.size());
		return out;
	}
}
