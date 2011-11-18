package dk.itu.noxdroid;

import java.util.Hashtable;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;
import dk.itu.noxdroid.ioio.IOIOConnectedTestThread;
import dk.itu.noxdroid.ioio.IOIOConnectedTestThread.STATUS;
import dk.itu.noxdroid.service.NoxDroidService;

public class NoxDroidMainActivity extends Activity{
	
	private String TAG;

	/********** DECLARES *************/
	
		
	private RelativeLayout layoutGPS;
	private RelativeLayout layoutIOIO;
	private RelativeLayout layoutConn;
	private ImageButton imgBtnStart;
	private ImageButton imgBtnGPS;
	private ImageView imgGPS;
	private ImageButton imgBtnIOIO;
	private ImageView imgIOIO;
	private ImageButton imgBtnConn;
	private ImageView imgConn;
	private ImageButton imgBtnStop;

	private RelativeLayout.LayoutParams lp;
	private Hashtable<Class<?>, Boolean> tests;
	private boolean isBound;
	private Messenger msg_service;
	private ServiceConnection mConnection = new ServiceConnection() {

		@Override
		public void onServiceDisconnected(ComponentName name) {
			msg_service = null;
		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder binder) {
			// service = ((NoxDroidService.ServiceBinder) binder).getService();
			Log.i(TAG, "Connected to NoxDroidService");

			// service.addMessenger(messenger);

			try {
				msg_service = new Messenger(binder);
				Message msg = Message.obtain(null,
						NoxDroidService.MSG_REGISTER_CLIENT);
				msg.replyTo = messenger;
				msg_service.send(msg);
				Log.i(TAG, "Registered messenger to NoxDroidService");
			} catch (RemoteException e) {

			}
		}
	};

	// TODO : Listener on usb not plugged in
	// TODO : Listener on connectivity changed
	// TODO : LIstener on GPS status changed

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main2);

		/********** INITIALIZES *************/
		imgBtnStart = (ImageButton) findViewById(R.id.imgBtnStart);
		imgBtnStop = (ImageButton) findViewById(R.id.imgBtnStop);

		// imgBtnStart.setEnabled(false);

		imgBtnGPS = (ImageButton) findViewById(R.id.imgBtnGPS);
		imgGPS = (ImageView) findViewById(R.id.imgGPS);
		imgBtnIOIO = (ImageButton) findViewById(R.id.imgBtnIOIO);
		imgIOIO = (ImageView) findViewById(R.id.imgIOIO);
		imgBtnConn = (ImageButton) findViewById(R.id.imgBtnConn);
		imgConn = (ImageView) findViewById(R.id.imgConn);
		layoutConn = (RelativeLayout) findViewById(R.id.relLayoutConnection);
		layoutGPS = (RelativeLayout) findViewById(R.id.relLayoutGPS);
		layoutIOIO = (RelativeLayout) findViewById(R.id.relLayoutIOIO);

		/* Please visit http://www.ryangmattison.com for updates */
		((ImageView) findViewById(R.id.imgIOIO)).setAlpha(80);
		((ImageView) findViewById(R.id.imgGPS)).setAlpha(80);
		((ImageView) findViewById(R.id.imgConn)).setAlpha(80);

		tests = new Hashtable<Class<?>, Boolean>();

		lp = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);
		lp.addRule(RelativeLayout.CENTER_IN_PARENT);

		TAG = getString(R.string.LOGCAT_TAG, getString(R.string.app_name), this
				.getClass().getSimpleName());
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		doUnbindService();
	}

	@Override
	protected void onResume() {
		super.onResume();
		testDependencies();
		updateGUI(NoxDroidService.STATUS_SERVICE_STARTED);
	}

	void doBindService() {
		// Intent i = new Intent(this, NoxDroidService.class);
		// startService(i);
		// bindService(i, mConnection,
		// Context.BIND_AUTO_CREATE);
		bindService(new Intent(this, NoxDroidService.class), mConnection,
				Context.BIND_AUTO_CREATE);
		isBound = true;

	}

	void doUnbindService() {
		if (isBound) {
			if (msg_service != null) {
				try {
					Message msg = Message.obtain(null,
							NoxDroidService.MSG_UNREGISTER_CLIENT);
					msg.replyTo = messenger;
					msg_service.send(msg);
				} catch (RemoteException e) {
					// There is nothing special we need to do if the service
					// has crashed.
				}
			}

			// Detach our existing connection.
			unbindService(mConnection);
			isBound = false;
		}
	}

	private void testDependencies() {

		GPSConnectionTest gpstest = new GPSConnectionTest();
		gpstest.execute(new Void[] {});

		ConnectivityTest connTest = new ConnectivityTest();
		connTest.execute(new Void[] {});

		IOIOConnectionTest ioiotest = new IOIOConnectionTest();
		ioiotest.execute(new Void[] {});
	}

	public void startNoxDroidService(View view) {
		// startService(new Intent(this, NoxDroidService.class));
		doBindService();
		imgBtnStart.setVisibility(View.GONE);
		imgBtnStop.setVisibility(View.VISIBLE);
	}

	public void changeGPS(View view) {
		Intent gpsOptionsIntent = new Intent(
				android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
		startActivity(gpsOptionsIntent);
	}

	public void startIOIO(View view) {
		Toast.makeText(this, "YOu click start IOIO", Toast.LENGTH_SHORT).show();
	}

	public void changeConnectivity(View view) {
		Toast.makeText(this, "You clicked change connectivity",
				Toast.LENGTH_SHORT).show();
	}

	class ConnectivityTest extends AsyncTask<Void, Void, Boolean> {
		ProgressBar pb;

		@Override
		protected void onPreExecute() {
			imgBtnConn.setImageResource(R.drawable.circle_grey);
			imgConn.setVisibility(View.GONE);
			pb = new ProgressBar(getBaseContext());
			pb.setLayoutParams(lp);
			layoutConn.addView(pb);
		}

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
			if (result) {
				imgBtnConn.setImageResource(R.drawable.circle_green);
				imgConn.setVisibility(View.VISIBLE);
				update(this.getClass(), true);
			} else {
				imgBtnConn.setImageResource(R.drawable.circle_red);
				imgConn.setVisibility(View.VISIBLE);
				update(this.getClass(), false);
			}
			imgConn.setVisibility(View.VISIBLE);
			pb.setVisibility(View.GONE);
			update(this.getClass(), result);
			
		}
	}

	class GPSConnectionTest extends AsyncTask<Void, Void, Boolean> {
		ProgressBar pb;

		@Override
		protected void onPreExecute() {
			pb = new ProgressBar(getBaseContext());
			pb.setLayoutParams(lp);
			layoutGPS.addView(pb);
			imgGPS.setVisibility(View.GONE);
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
			return locationManager
					.isProviderEnabled(LocationManager.GPS_PROVIDER);
		}

		@Override
		protected void onPostExecute(Boolean result) {
			if (result) {
				imgBtnGPS.setImageResource(R.drawable.circle_green);

			} else {
				imgBtnGPS.setImageResource(R.drawable.circle_red);
			}
			this.pb.setVisibility(View.GONE);
			imgGPS.setVisibility(View.VISIBLE);
			update(this.getClass(), result);
		}
	}

	class IOIOConnectionTest extends AsyncTask<Void, Void, Boolean> {
		ProgressBar pb;

		@Override
		protected void onPreExecute() {
			pb = new ProgressBar(getBaseContext());
			pb.setLayoutParams(lp);
			layoutIOIO.addView(pb);
			// Later.. stop the animation
			imgIOIO.setVisibility(View.GONE);
		}

		@Override
		protected Boolean doInBackground(Void... params) {

			if (isServiceRunning(NoxDroidService.class)) {
				return true;
			} else {
				IOIOConnectedTestThread t = new IOIOConnectedTestThread();

				t.start();
				try {
					t.join(2000);
				} catch (InterruptedException e) {

				}
				if (t.isAlive()) {
					t.abort();
				}

				return (t.getStatus() == STATUS.CONNECTED);
			}
		}

		@Override
		protected void onPostExecute(Boolean result) {
			if (result) {
				imgBtnIOIO.setImageResource(R.drawable.circle_green);

			} else {
				imgBtnIOIO.setImageResource(R.drawable.circle_red);
			}
			this.pb.setVisibility(View.GONE);
			imgIOIO.setVisibility(View.VISIBLE);
			update(this.getClass(), result);
		}
	}

	private synchronized void update(Class<?> c, boolean flag) {
		tests.put(c, flag);
		if (tests.size() == 3) {
			if (passedTests()) {
				if (!isServiceRunning(NoxDroidService.class)) {
					imgBtnStart.setImageResource(R.drawable.play);
					imgBtnStart.setEnabled(true);
					imgBtnStop.setVisibility(View.GONE);

				} else {
					imgBtnStop.setVisibility(View.VISIBLE);

					imgBtnStart.setVisibility(View.GONE);

				}
			}
			tests.clear();
		}
	}

	private synchronized boolean passedTests() {
		for (Boolean val : tests.values()) {
			if (!val && true)
				return false;
		}
		return true;
	}

	private boolean isServiceRunning(Class<?> service) {
		ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		for (RunningServiceInfo rs : manager
				.getRunningServices(Integer.MAX_VALUE)) {
			if (service.getName().equals(rs.service.getClassName())) {

				Log.i(TAG, "Noxdroid Service running");
				return true;
			}
		}
		return false;
	}

	public void stopNoxDroidService(View view) {
		stopService(new Intent(this, NoxDroidService.class));
		Toast.makeText(this, "stopping service", Toast.LENGTH_SHORT);
		imgBtnStop.setVisibility(View.GONE);
		imgBtnStart.setImageResource(R.drawable.play_disabled);
		imgBtnStart.setVisibility(View.VISIBLE);
		imgBtnStart.setEnabled(false);
		onResume();
	}

	private void IOIO_Change_Status(int status) {
		
	}

	/**
	 * Section
	 */

	class IncomingHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case NoxDroidService.ERROR_IOIO_CONNECTION_LOST:
				imgBtnIOIO.setEnabled(false);
				imgBtnIOIO.setImageResource(R.drawable.circle_red);
				Toast.makeText(getBaseContext(), "IOIO Lost connection",
						Toast.LENGTH_LONG);
				break;
			case NoxDroidService.STATUS_IOIO_STATUS_GREEN:
				break;
			case NoxDroidService.STATUS_IOIO_YELLOW:
				break;
			case NoxDroidService.STATUS_IOIO_RED:
				break;
			case NoxDroidService.ERROR_IOIO_ABORTED: 
				imgBtnIOIO.setEnabled(false);
				imgBtnIOIO.setImageResource(R.drawable.circle_red);
				break;
			default:
				super.handleMessage(msg);
				break;
			}
		}
	}

	final Messenger messenger = new Messenger(new IncomingHandler());

	
	private void updateGUI(int status) {
		switch (status)  { 
			case NoxDroidService.STATUS_SERVICE_STARTED :
				
				break;
			
			case NoxDroidService.STATUS_CONNECTIVITY_SUCCESS :
				imgBtnConn.setImageResource(R.drawable.circle_green);
				imgConn.setVisibility(View.VISIBLE);
				update(this.getClass(), true);
			case NoxDroidService.STATUS_CONNECTIVITY_FAILURE:
				imgBtnConn.setImageResource(R.drawable.circle_red);
				imgConn.setVisibility(View.VISIBLE);
				update(this.getClass(), false);
				default :
					break;
		}
							
	}
}