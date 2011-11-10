package dk.itu.noxdroid.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;
import dk.itu.noxdroid.IOIOSensorActivity;
import dk.itu.noxdroid.R;

public class NoxDroidService extends Service {
	private IOIOSensorActivity sensorActivity;

	NotificationManager nman;

	private String TAG = "NoxDroidService";
	private int NOTIFICATION = R.string.noxdroid_service_started;

	public class ServiceBinder extends Binder {
		public NoxDroidService getService() {
			return NoxDroidService.this;
		}
	}

	@Override
	public void onCreate() {
		nman = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		// Display a notification about us starting. We put an icon in the
		// status bar.
		showNotification();
		 Intent intent = new Intent (getBaseContext(), IOIOSensorActivity.class);
		 intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		 getApplication().startActivity(intent);
		 
		 
//		 Log.i(TAG, "started IOIOSensorActivity");
	}

	@Override
	public void onDestroy() {
		// Cancel the persistent notification.
		nman.cancel(NOTIFICATION);

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
		return mBinder;
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
}