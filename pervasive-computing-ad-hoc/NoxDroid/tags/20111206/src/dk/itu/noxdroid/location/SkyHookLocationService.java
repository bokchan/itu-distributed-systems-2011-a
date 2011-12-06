package dk.itu.noxdroid.location;

import java.util.ArrayList;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import com.skyhookwireless.wps.WPSAuthentication;
import com.skyhookwireless.wps.WPSContinuation;
import com.skyhookwireless.wps.WPSLocation;
import com.skyhookwireless.wps.WPSLocationCallback;
import com.skyhookwireless.wps.WPSPeriodicLocationCallback;
import com.skyhookwireless.wps.WPSReturnCode;
import com.skyhookwireless.wps.WPSStreetAddressLookup;
import com.skyhookwireless.wps.XPS;

import dk.itu.noxdroid.NoxDroidApp;
import dk.itu.noxdroid.R;
import dk.itu.noxdroid.database.NoxDroidDbAdapter;
import dk.itu.noxdroid.service.NoxDroidService;


/*
 * NoxDroidLocationService
 * 
 * 
 */
public class SkyHookLocationService extends Service {

	private XPS _xps;
	private NoxDroidDbAdapter mDbHelper;
	private SkyhookLocationCallBack _callback = new SkyhookLocationCallBack();

	private String TAG;
	private ArrayList<Messenger> clients = new ArrayList<Messenger>();

	private int updateinterval = 10000;

	final WPSAuthentication auth = new WPSAuthentication("noxdroid",
			"dk.itu.noxdroid");

	// our Handler understands three messages:
	// a location, an error, or a finished request
	private static final int LOCATION_MESSAGE = 1;
	private static final int ERROR_MESSAGE = 2;
	private static final int DONE_MESSAGE = 3;
	private boolean doCheck = true;

	public final Messenger _handler = new Messenger(new IncomingHandler());

	@Override
	public IBinder onBind(Intent arg0) {
		return _handler.getBinder();
	}

	public class ServiceBinder extends Binder {
		public SkyHookLocationService getService() {
			return SkyHookLocationService.this;
		}
	}

	@Override
	public void onCreate() {
		super.onCreate();
		TAG = getString(R.string.LOGCAT_TAG, getString(R.string.app_name), this
				.getClass().getSimpleName());
		_xps = new XPS(this);

		mDbHelper = ((NoxDroidApp) getApplication()).getDbAdapter();
		doCheck = true;
		
		_xps.getLocation(auth,
				WPSStreetAddressLookup.WPS_NO_STREET_ADDRESS_LOOKUP, _callback);
	}

	class IncomingHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			Log.i(TAG, "Handling incoming message");
			switch (msg.what) {
			case NoxDroidService.MSG_REGISTER_CLIENT:
				Log.i(TAG, "Added client: " + msg.replyTo
						+ " NoxDroidLocationService");
				clients.add(msg.replyTo);
				break;
			case NoxDroidService.ACTION_START_TRACK :
				startRecording();
				break;
			case NoxDroidService.ACTION_STOP_TRACK:
				stopRecording();
				break;
			default:
				break;
			}
		}
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.i(TAG, "Received start id " + startId + ": " + intent);

		// We want this service to continue running until it is explicitly
		// stopped, so return sticky.

		return START_STICKY;

	}

	private void notifyClients(int msg) {
		Log.i(TAG, "Notifying clients # " + clients.size());
		for (int i = 0; i < clients.size(); i++) {
			try {
				Log.i(TAG, "Sent message to : " + clients.get(i));
				clients.get(i).send(Message.obtain(null, msg));
			} catch (RemoteException e) {
				// If we get here, the client is dead, and we should remove it
				// from the list
				Log.e(TAG, "Removing client: " + clients.get(i));
				clients.remove(i);
			}
		}
	}

	private class SkyhookLocationCallBack implements
			WPSPeriodicLocationCallback, WPSLocationCallback {
		@Override
		public WPSContinuation handleError(WPSReturnCode error) {
			Message m = Message.obtain(null, ERROR_MESSAGE);
			Log.e(TAG, error.toString());
			// try {
			// Log.e(TAG, error.toString());
			// _handler.send(m);
			// } catch (RemoteException e) {
			// Log.e(TAG, e.getMessage());
			// }
			return WPSContinuation.WPS_CONTINUE;
		}

		@Override
		public void done() {
			// tell the UI thread to re-enable the buttons
			Message m = Message.obtain(null, DONE_MESSAGE);
			try {
				Log.i(TAG, "Skyhook done");
				_handler.send(m);
			} catch (RemoteException e) {
				Log.e(TAG, e.getMessage());
				e.printStackTrace();
			}
		}

		@Override
		public WPSContinuation handleWPSPeriodicLocation(WPSLocation location) {

			Log.i(TAG, "Got PeriodicLocation");
			Log.i(TAG, String.format("lat: %s - long: %s - acc: %s",
					location.getLatitude(), location.getLongitude(),
					location.getSpeed()));

			mDbHelper.createLocationPoint(location.getLatitude(),
					location.getLongitude(), "skyhook");
			Log.i(TAG, "Saved Location to database");

			return WPSContinuation.WPS_CONTINUE;
		}

		@Override
		public void handleWPSLocation(WPSLocation location) {
			Log.i(TAG, "Got Location");
			Log.i(TAG, String.format("lat: %s - long: %s",
					location.getLatitude(), location.getLongitude()));

			if (doCheck) {
				notifyClients(NoxDroidService.STATUS_LOCATION_SERVICE_STARTED);
				doCheck = false;
			} else {

				mDbHelper.createLocationPoint(location.getLatitude(),
						location.getLongitude(), "skyhook");
				Log.i(TAG, "Saved Location to database");
				try {
					Thread.sleep(updateinterval);
				} catch (InterruptedException e) {
					Log.e(TAG, e.getMessage());
				}
			}
		}
	}

	public void startRecording() {
		_xps.getLocation(auth,
				WPSStreetAddressLookup.WPS_NO_STREET_ADDRESS_LOOKUP, _callback);
	}

	public void stopRecording() {
		_xps.abort();
	}

	private void startPeriodicLocation() {
		Log.i(TAG, "Starting SKYHOOK");
		_xps.getPeriodicLocation(auth,
				WPSStreetAddressLookup.WPS_NO_STREET_ADDRESS_LOOKUP, 50000, 0,
				_callback);
	}
}