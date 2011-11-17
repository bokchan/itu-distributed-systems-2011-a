package dk.itu.noxdroid.service;

import ioio.lib.api.exception.ConnectionLostException;

import java.util.ArrayList;
import java.util.Map;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;
import dk.itu.noxdroid.R;
import dk.itu.noxdroid.ioio.IOIOEventListener;
import dk.itu.noxdroid.ioio.NoxDroidIOIOThread;

public class NoxDroidService extends Service implements IOIOEventListener {
	
	public static int MSG_UNREGISTER_CLIENT = 3; 

	public Map<String, ?> APP_PREFS;
	//
	private NoxDroidIOIOThread ioio_thread_;
	NotificationManager nman;

	private String TAG;
	private int NOTIFICATION = R.string.noxdroid_service_started;

	public class ServiceBinder extends Binder {
		public NoxDroidService getService() {
			return NoxDroidService.this;
		}
	}
	

	private ArrayList<Messenger> clients = new ArrayList<Messenger>();

	@Override
	public void onCreate() {

		TAG = getString(R.string.LOGCAT_TAG, getString(R.string.app_name), this
				.getClass().getSimpleName());

		nman = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		// Display a notification about us starting. We put an icon in the
		// status bar.

		showNotification();
		try {
			// TODO : Make preferences accessible from Service
			APP_PREFS = PreferenceManager.getDefaultSharedPreferences(
					this).getAll();
			Log.i(TAG, "Got SharedPreferences " + APP_PREFS);
		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
		}

		ioio_thread_ = new NoxDroidIOIOThread(this);
		ioio_thread_.start();

	}

	public synchronized Map<String, ?> getPrefs() {
		return this.APP_PREFS;
	}

	private void doReading() {
		while (true) {
			try {
				Log.i(TAG, "Received reading : " + ioio_thread_.getReading());
				Thread.sleep(2000);
			} catch (ConnectionLostException e) {

				e.printStackTrace();
			} catch (InterruptedException e) {

				e.printStackTrace();
			}
		}
	}

	@Override
	public void onDestroy() {
		// Cancel the persistent notification.
		nman.cancel(NOTIFICATION);

		if (ioio_thread_ != null && ioio_thread_.isAlive()) {
			ioio_thread_.abort();
		}

		// Tell the user we stopped.
		Toast.makeText(this, R.string.noxdroid_service_stopped,
				Toast.LENGTH_SHORT).show();
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
		//return mBinder;
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

		// The PendingIntent to launch our activity if the user selects this
		// notification
		PendingIntent contentIntent = PendingIntent
				.getActivity(this, 0, new Intent(this,
						NoxDroidServiceActivities.Controller.class), 0);
		

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
	
	public void addMessenger(Messenger m) {
		clients.add(m);
		Log.i(TAG, "Added Messenger to NoxDroidService");
	}

	class IncomingHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case R.string.MSG_REGISTER_CLIENT:
				Log.i(TAG, "Added client: " + msg.replyTo);
				//clients.add(msg.replyTo);
			default:
				super.handleMessage(msg);
				break;
			}

		}
	}
	
	public final Messenger messenger = new Messenger(new IncomingHandler());

	private void notifyClients(int msg) {
		for (int i = 0; i < clients.size(); i++) {
			try {
				clients.get(i).send(Message.obtain(null,msg));
			} catch (RemoteException e) {
				// If we get here, the client is dead, and we should remove it
				// from the list
				Log.e(TAG, "Removing client: " + clients.get(i));
				clients.remove(i);
			}
		}
	}

	@Override
	public void notify(int msg) {
		switch (msg) {
		case (R.string.ERROR_IOIO_CONNECTION_LOST):
			notifyClients(msg);
			break;
		default:
			break;
		}
	}
}