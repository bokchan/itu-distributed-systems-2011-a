package dk.itu.noxdroid.service;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;
import dk.itu.noxdroid.NoxDroidApp;
import dk.itu.noxdroid.NoxDroidMainActivity;
import dk.itu.noxdroid.R;
import dk.itu.noxdroid.database.NoxDroidDbAdapter;
import dk.itu.noxdroid.ioio.IOIOConnectedTestThread;
import dk.itu.noxdroid.ioio.IOIOConnectedTestThread.STATUS;
import dk.itu.noxdroid.ioio.IOIOEventListener;
import dk.itu.noxdroid.ioio.NoxDroidIOIOThread;
import dk.itu.noxdroid.location.GPSLocationService;
import dk.itu.noxdroid.location.SkyHookLocationService;

public class NoxDroidService extends Service implements IOIOEventListener {

	public final static int MSG_REGISTER_CLIENT = 1;
	public final static int MSG_UNREGISTER_CLIENT = 2;

	public final static int STATUS_IOIO_GREEN = 3;
	public final static int STATUS_IOIO_YELLOW = 4;
	public final static int STATUS_IOIO_RED = 5;
	public final static int STATUS_IOIO_STOPPED_RECORDING = 21;
	public final static int STATUS_IOIO_CONNECTED = 8;
	public final static int ERROR_IOIO_CONNECTION_LOST = 0;
	public final static int ERROR_IOIO_INTERRUPTED = 9;
	public final static int ERROR_IOIO_ABORTED = 10;
	public final static int ERROR_IOIO_INCOMPATIBLE = 11;

	public final static int STATUS_SERVICE_STARTED = 6;
	public final static int STATUS_SERVICE_STOPPED = 7;
	public final static int STATUS_SERVICE_READY = 18;

	public final static int STATUS_RECORDING = 20;

	public final static int STATUS_CONNECTIVITY_WAITING = 12;
	public final static int STATUS_CONNECTIVITY_OK = 13;
	public final static int ERROR_NO_CONNECTIVITY = 14;

	public final static int STATUS_SKYHOOK_OK = 15;
	public final static int ERROR_NO_SKYHOOK = 19;

	public final static int ACTION_START_TRACK = 16;
	public final static int ACTION_STOP_TRACK = 17;

	public static final int ERROR_NO_GPS = 22;
	public static final int STATUS_GPS_OK = 23;

	public static final int ERROR_NO_LOCATION = 24;
	public static final int STATUS_LOCATION_OK = 25;

	public static final int GET_SENSOR_STATES = 26;
	public static final int ACTION_SKYHOOK_DOTEST = 27;

	private boolean isTrackOpen = false;

	public Map<String, ?> APP_PREFS = null;
	//
	private NoxDroidIOIOThread ioio_thread_;
	private NotificationManager nman;
	private Messenger messengerSkyhook;
	private Messenger messengerGPS;
	private boolean locServiceIsBound = false;
	private String TAG;
	private int NOTIFICATION = R.string.noxdroid_service_started;

	private SharedPreferences prefs;
	private NoxDroidDbAdapter dbAdapter;
	private Hashtable<Class<?>, Boolean> tests = new Hashtable<Class<?>, Boolean>();
	boolean connectToIOIO;

	private ConnectivityTest connTest;
	private IOIOConnectionTest ioiotest;

	public class ServiceBinder extends Binder {
		public NoxDroidService getService() {
			return NoxDroidService.this;
		}
	}

	private ArrayList<Integer> msgQueue;

	private ArrayList<Messenger> clients = new ArrayList<Messenger>();

	private ServiceConnection connSkyhookService = new ServiceConnection() {

		@Override
		public void onServiceDisconnected(ComponentName name) {
			messengerSkyhook = null;
		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			Log.i(TAG, "Connected to SkyhookLocationService");

			try {
				messengerSkyhook = new Messenger(service);
				Message msg = Message.obtain(null,
						NoxDroidService.MSG_REGISTER_CLIENT);
				msg.replyTo = messenger;
				messengerSkyhook.send(msg);

			} catch (RemoteException e) {
				Log.e(TAG, e.getMessage());
			}
		}
	};

	private ServiceConnection connGPSService = new ServiceConnection() {

		@Override
		public void onServiceDisconnected(ComponentName name) {
			messengerGPS = null;
		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			Log.i(TAG, "Connected to GPSLocationService");
			try {
				messengerGPS = new Messenger(service);
				Message msg = Message.obtain(null,
						NoxDroidService.MSG_REGISTER_CLIENT);
				msg.replyTo = messenger;
				messengerGPS.send(msg);

			} catch (RemoteException e) {
				Log.e(TAG, e.getMessage());
			}
		}
	};

	@Override
	public void onCreate() {
		// TODO: Start both GPS and skyhook. But check that at least one of them
		// is running
		super.onCreate();

		TAG = getString(R.string.LOGCAT_TAG, getString(R.string.app_name), this
				.getClass().getSimpleName());

		nman = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		// Display a notification about us starting. We put an icon in the
		// status bar.
		showNotification();

		msgQueue = new ArrayList<Integer>();

		// Get dbAdapter;
		dbAdapter = ((NoxDroidApp) getApplication()).getDbAdapter();
		
		registerReceiver(networkStateReceiver, filter);

		try {
			APP_PREFS = PreferenceManager.getDefaultSharedPreferences(this)
					.getAll();
			Log.i(TAG, "Got SharedPreferences " + APP_PREFS);
		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
		}

		//
		// Start skyhook & GPS services
		doBindService();

		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		connectToIOIO = prefs.getBoolean(
				getString(dk.itu.noxdroid.R.string.IOIO_ENABLED), true);

		if (connectToIOIO) {
			// Check for IOIO connection
			ioiotest = new IOIOConnectionTest();
			ioiotest.execute(new Void[] {});
		} else {
			updateTest(IOIOConnectionTest.class, true);
		}
		connTest = new ConnectivityTest();
		connTest.execute(new Void[] {});

	}

	public synchronized Map<String, ?> getPrefs() {
		return this.APP_PREFS;
	}

	@Override
	public void onDestroy() {
		// Set endtime equal to time of exit
		stopTrack();
		// Cancel the persistent notification.
		nman.cancel(NOTIFICATION);

		if (ioio_thread_ != null && ioio_thread_.isAlive()) {
			ioio_thread_.abort();
		}

		// Tell the user we stopped.
		Toast.makeText(this, R.string.noxdroid_service_stopped,
				Toast.LENGTH_SHORT).show();

		if (ioiotest != null)
			ioiotest.cancel(true);
		if (connTest != null)
			connTest.cancel(true);

		unbindService(connSkyhookService);
		unbindService(connGPSService);

		// stop additional services
		stopService(new Intent(this, GPSLocationService.class));
		stopService(new Intent(this, SkyHookLocationService.class));
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.i(TAG, "Received start id " + startId + ": " + intent);
		Toast.makeText(this, R.string.noxdroid_service_started,
				Toast.LENGTH_SHORT).show();

		// We want this service to continue running until it is explicitly
		// stopped, so return sticky.
		return START_STICKY;
	}

	// This is the object that receives interactions from clients. See
	// RemoteService for a more complete example.
	private final IBinder mBinder = new ServiceBinder();

	@Override
	public IBinder onBind(Intent intent) {
		// return mBinder;
		return messenger.getBinder();
	}

	/**
	 * Show a notification while this service is running.
	 */
	private void showNotification() {

		// In this sample, we'll use the same text for the ticker and the
		// expanded notification
		CharSequence text = getText(R.string.noxdroid_service_started);

		// Set the icon, scrolling text and timestamp
		Notification notification = new Notification(R.drawable.no2_molecule,
				text, System.currentTimeMillis());

		PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
				new Intent(this, NoxDroidMainActivity.class), 0);

		// Set the info for the views that show in the notification panel.

		notification
				.setLatestEventInfo(this,
						getText(R.string.noxdroid_service_started), text,
						contentIntent);
		// Send the notification.
		nman.notify(NOTIFICATION, notification);
	}

	public void update(Class<?> sender, Object data) {
		Log.i(TAG,
				"Notified from " + sender.getName() + " : "
						+ String.valueOf(data));
	}

	private void updateTest(Class<?> c, boolean status) {
		Log.d(TAG, "updating test for: " + c.getSimpleName() + " " + status);
		tests.put(c, status);
		if (c.equals(GPSLocationService.class)
				|| c.equals(SkyHookLocationService.class)) {
			if ((tests.containsKey(GPSLocationService.class) && tests
					.containsKey(SkyHookLocationService.class))) {
				if ((tests.get(GPSLocationService.class) || tests
						.get(SkyHookLocationService.class))) {
					notifyClients(STATUS_LOCATION_OK);
				} else {
					notifyClients(ERROR_NO_LOCATION);
				}
			}
		} else {
			notifyClients(getTestStatus(c, status));
		}
		if (isReady()) {
			notifyClients(STATUS_SERVICE_READY);
		}

	}

	private boolean isReady() {
		return ((tests.containsKey(GPSLocationService.class) && tests
				.get(GPSLocationService.class)) || (tests
				.containsKey(SkyHookLocationService.class) && tests
				.get(SkyHookLocationService.class)))
				&& (tests.containsKey(IOIOConnectionTest.class) && tests
						.get(IOIOConnectionTest.class))
				&& (tests.containsKey(ConnectivityTest.class) && tests
						.get(ConnectivityTest.class));

	}

	private int getTestStatus(Class<?> c, boolean flag) {
		if (c.equals(IOIOConnectionTest.class)) {
			if (flag)
				return STATUS_IOIO_CONNECTED;
			else
				return ERROR_IOIO_CONNECTION_LOST;

		} else if (c.equals(ConnectivityTest.class)) {
			if (flag)
				return STATUS_CONNECTIVITY_OK;
			else
				return ERROR_NO_CONNECTIVITY;
		} else if (c.equals(GPSLocationService.class)) {
			if (flag)
				return STATUS_GPS_OK;
			else
				return ERROR_NO_GPS;
		} else if (flag) {
			return STATUS_SKYHOOK_OK;
		} else
			return ERROR_NO_SKYHOOK;
	}

	public void removeMessenger(Messenger m) {
		clients.remove(m);
		Log.i(TAG, "Removed messenger from NoxDroidService");
	}

	public void addMessenger(Messenger m) {
		clients.add(m);
		Log.i(TAG, "Added Messenger to NoxDroidService");
	}

	class IncomingHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			Log.i(TAG, "Handling incoming message");
			switch (msg.what) {
			case MSG_REGISTER_CLIENT:
				Log.i(TAG, "Added client: " + msg.replyTo);
				clients.add(msg.replyTo);
				break;
			case STATUS_SKYHOOK_OK:
				// TODO: if SkyHook is not depending on GPS but network
				// - then it might make sense to be moved to
				// STATUS_CONNECTIVITY_SUCCESS...?
				updateTest(SkyHookLocationService.class, true);
				break;
			case ERROR_NO_SKYHOOK:
				updateTest(SkyHookLocationService.class, false);
				break;
			case ACTION_START_TRACK:
				startTrack();
				break;
			case ACTION_STOP_TRACK:
				stopTrack();
				break;
			case ERROR_NO_GPS:
				Log.i(TAG, "STATUS_GPS_DISABLED");
				updateTest(GPSLocationService.class, false);
				break;
			case STATUS_GPS_OK:
				Log.i(TAG, "STATUS_GPS_ENABLED");
				updateTest(GPSLocationService.class, true);
				break;
			case GET_SENSOR_STATES:
				for (Entry<Class<?>, Boolean> e : tests.entrySet()) {
					updateTest(e.getKey(), e.getValue());
				}
			default:
				super.handleMessage(msg);
				break;
			}
		}
	}

	public final Messenger messenger = new Messenger(new IncomingHandler());

	private void notifyClients(int msg) {
		Log.i(TAG, "Notifying clients # " + clients.size() + " msg " + msg);
		if (clients.size() > 0) {
			for (int i = 0; i < clients.size(); i++) {
				try {
					Log.i(TAG, "Sent message to : " + clients.get(i));
					showNotification();

					clients.get(i).send(Message.obtain(null, msg));
					for (Integer m : msgQueue) {
						clients.get(i).send(Message.obtain(null, m));
					}
					msgQueue.clear();
				} catch (RemoteException e) {
					// If we get here, the client is dead, and we should remove
					// it
					// from the list
					Log.e(TAG, "Removing client: " + clients.get(i));
					clients.remove(i);
				}
			}
		} else {
			msgQueue.add(msg);
		}
	}

	@Override
	public void notify(int msg) {
		switch (msg) {
		case (ERROR_IOIO_CONNECTION_LOST):
		case ERROR_IOIO_ABORTED:
		case ERROR_IOIO_INTERRUPTED:
			updateTest(IOIOConnectionTest.class, false);
		case STATUS_IOIO_CONNECTED:
			updateTest(IOIOConnectionTest.class, true);
			break;
		case STATUS_IOIO_STOPPED_RECORDING:
			break;
		default:
			break;
		}
	}

	void doBindService() {
		Intent intent = new Intent(this, SkyHookLocationService.class);
		bindService(intent, connSkyhookService, Context.BIND_AUTO_CREATE);

		bindService(new Intent(this, GPSLocationService.class), connGPSService,
				Context.BIND_AUTO_CREATE);

		Log.i(TAG, "doBindService");
		locServiceIsBound = true;
	}

	void doUnbindService() {
		if (locServiceIsBound) {
			if (connSkyhookService != null) {
				try {
					Message msg = Message.obtain(null,
							NoxDroidService.MSG_UNREGISTER_CLIENT);
					msg.replyTo = messenger;
					messengerSkyhook.send(msg);
				} catch (RemoteException e) {
					// There is nothing special we need to do if the service
					// has crashed.
				}
			}
			// Detach our existing connection.
			unbindService(connSkyhookService);
			locServiceIsBound = false;
		}
	}

	BroadcastReceiver networkStateReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
				boolean isDisConnected = intent.getBooleanExtra(
						android.net.ConnectivityManager.EXTRA_NO_CONNECTIVITY,
						false);
				if (isDisConnected) {
					updateTest(ConnectivityTest.class, false);
					Message msg = Message.obtain(null, ACTION_SKYHOOK_DOTEST);
					msg.replyTo = messenger;
					try {
						messengerSkyhook.send(msg);
					} catch (RemoteException e) {
						e.printStackTrace();
					}
				} 
				updateTest(ConnectivityTest.class, true);

				NetworkInfo mNetworkInfo = (NetworkInfo) intent
						.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
				NetworkInfo mOtherNetworkInfo = (NetworkInfo) intent
						.getParcelableExtra(ConnectivityManager.EXTRA_OTHER_NETWORK_INFO);

				String mReason = intent
						.getStringExtra(ConnectivityManager.EXTRA_REASON);
				boolean mIsFailover = intent.getBooleanExtra(
						ConnectivityManager.EXTRA_IS_FAILOVER, false);

				Log.d(TAG, "onReceive(): mNetworkInfo="
						+ mNetworkInfo
						+ " mOtherNetworkInfo = "
						+ (mOtherNetworkInfo == null ? "[none]"
								: mOtherNetworkInfo + " noConn="
										+ isDisConnected) + " reason=" + mReason + " isFailOver=" + mIsFailover);
			}

			Log.w("Network Listener", "Network Type Changed");
		}
	};

	IntentFilter filter = new IntentFilter(
			ConnectivityManager.CONNECTIVITY_ACTION);

	/**
	 * Tracks
	 */

	private void startTrack() {
		// TODO A check should be made for LocationService and IOIO before
		// recording

		Log.i(TAG, "Starting track: " + isTrackOpen);

		if (((NoxDroidApp) getApplication()).getCurrentTrack() != null) {
			Log.i(TAG, "Something is wrong");
			dbAdapter.endTrack(((NoxDroidApp) getApplication())
					.getCurrentTrack().toString());
			if (ioio_thread_ != null || ioio_thread_.isAlive()) {
				ioio_thread_.abort();
			}
		}

		Log.d(TAG, "Checked ioio_thread.isAlive()");
		// Location
		UUID uuid = UUID.randomUUID();
		dbAdapter.createTrack(uuid.toString());
		Log.d(TAG, "Saved new track ");
		((NoxDroidApp) getApplication()).setCurrentTrack(uuid);
		Log.d(TAG, "Set new track uuid in app context");

		if (tests.containsKey(SkyHookLocationService.class)
				&& tests.get(SkyHookLocationService.class)) {

			Message msg = Message.obtain(null,
					NoxDroidService.ACTION_START_TRACK);
			msg.replyTo = messenger;
			try {
				messengerSkyhook.send(msg);
			} catch (RemoteException e1) {
				Log.e(TAG, e1.getMessage());
			}

			Log.d(TAG, "Sent msg to SkyhookService");
		}

		if (tests.containsKey(GPSLocationService.class)
				&& tests.get(GPSLocationService.class)) {
			Message msg2 = Message.obtain(null,
					NoxDroidService.ACTION_START_TRACK);
			msg2.replyTo = messenger;

			try {
				Log.i(TAG, messengerGPS.toString());
				messengerGPS.send(msg2);
			} catch (RemoteException e1) {
				Log.e(TAG, e1.getMessage());
			}

			Log.d(TAG, "Sent msg to GPSService");
		}

		// IOIO
		ioio_thread_ = new NoxDroidIOIOThread(this);
		ioio_thread_.start();
		try {
			ioio_thread_.join(5000);
		} catch (InterruptedException e) {
			Log.e(TAG, e.getMessage());
		}

		isTrackOpen = true;

		Log.i(TAG, "Started track: ");
	}

	/*
	 * 
	 * Get message from main ui - and stop
	 */
	private void stopTrack() {
		Log.i(TAG, "stopping track: ");
		if (((NoxDroidApp) getApplication()).getCurrentTrack() != null) {
			if (ioio_thread_ != null || ioio_thread_.isAlive()) {
				// ioio_thread_.stopRecording();
				ioio_thread_.abort();
			}

			Message msg = Message.obtain(null,
					NoxDroidService.ACTION_STOP_TRACK);
			msg.replyTo = messenger;
			try {
				messengerSkyhook.send(msg);
			} catch (RemoteException e1) {
				e1.printStackTrace();
			}

			dbAdapter.endTrack(((NoxDroidApp) getApplication())
					.getCurrentTrack().toString());
		}
		isTrackOpen = false;
		((NoxDroidApp) getApplication()).setCurrentTrack(null);
		Log.i(TAG, "Stopped track: ");

		if (connectToIOIO) {
			// Check for IOIO connection
			IOIOConnectionTest ioiotest = new IOIOConnectionTest();
			ioiotest.execute(new Void[] {});
		} else {
			updateTest(IOIOConnectionTest.class, true);
		}
	}

	class ConnectivityTest extends AsyncTask<Void, Void, Boolean> {

		@Override
		protected Boolean doInBackground(Void... params) {
			final ConnectivityManager connMan = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
			final NetworkInfo wifiInfo = connMan
					.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
			final NetworkInfo mobileInfo = connMan
					.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
			return wifiInfo.isAvailable() || mobileInfo.isAvailable();
		}

		@Override
		protected void onPostExecute(Boolean result) {
			updateTest(this.getClass(), result);
		}
	}

	class IOIOConnectionTest extends AsyncTask<Void, Void, Boolean> {
		@Override
		protected Boolean doInBackground(Void... params) {
			Log.i("NoxDroid_IOIOTest", "Testing");
			IOIOConnectedTestThread t = new IOIOConnectedTestThread();

			t.start();
			try {
				t.join(2000);
			} catch (InterruptedException e) {

			}
			if (t.isAlive()) {
				t.abort();
			}
			Log.i("NoxDroid_IOIOTest", "tested: " + t.getStatus());
			return (t.getStatus() == STATUS.CONNECTED);
		}

		@Override
		protected void onPostExecute(Boolean result) {
			updateTest(this.getClass(), result);
		}
	}
}