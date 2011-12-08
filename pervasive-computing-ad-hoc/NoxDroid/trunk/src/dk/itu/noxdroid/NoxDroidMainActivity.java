package dk.itu.noxdroid;

import java.util.Hashtable;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import dk.itu.noxdroid.service.NoxDroidService;
import dk.itu.noxdroid.setup.PreferencesActivity;
import dk.itu.noxdroid.tracks.TracksListActivity;
import dk.itu.noxdroid.util.Line;


public class NoxDroidMainActivity extends Activity {

	private String TAG;

	/********** DECLARES *************/

	private RelativeLayout layoutGPS;
	private RelativeLayout layoutIOIO;
	private RelativeLayout layoutConn;
	private RelativeLayout layoutWrapper;
	private RelativeLayout wrapper;
	private RelativeLayout parentWrapper;
	private ImageButton imgBtnStart;
	private ImageButton imgBtnGPS;
	private ImageView imgGPS;
	private ImageButton imgBtnIOIO;
	private ImageView imgIOIO;
	private ImageButton imgBtnConn;
	private ImageView imgConn;
	private ImageButton imgBtnStop;
	private ImageButton imgBtnRecord;
	
	private RelativeLayout.LayoutParams lp;
	private Hashtable<Class<?>, Boolean> tests;
	private boolean isBound;
	private Messenger msg_service;
	private NoxDroidApp app; 
	private ServiceConnection mConnection = new ServiceConnection() {

		@Override
		public void onServiceDisconnected(ComponentName name) {
			msg_service = null;
		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder binder) {
			// service = ((NoxDroidService.ServiceBinder) binder).getService();
			Log.i(TAG, "Connected to NoxDroidService");

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
				
		app = (NoxDroidApp)getApplication();

		/********** INITIALIZES *************/
		imgBtnStart = (ImageButton) findViewById(R.id.imgBtnStart);
		imgBtnStart.setEnabled(false);
		imgBtnStop = (ImageButton) findViewById(R.id.imgBtnStop);
		imgBtnStop.setEnabled(false);
		imgBtnRecord = (ImageButton) findViewById(R.id.imgBtnStartRecording);
		imgBtnRecord.setEnabled(false);		

		imgBtnGPS = (ImageButton) findViewById(R.id.imgBtnGPS);
		imgBtnGPS.setEnabled(false);
		imgGPS = (ImageView) findViewById(R.id.imgGPS);
		imgBtnIOIO = (ImageButton) findViewById(R.id.imgBtnIOIO);
		imgBtnIOIO.setEnabled(false);
		imgIOIO = (ImageView) findViewById(R.id.imgIOIO);
		imgBtnConn = (ImageButton) findViewById(R.id.imgBtnConn);
		imgBtnConn.setEnabled(false);
		imgConn = (ImageView) findViewById(R.id.imgConn);
		layoutConn = (RelativeLayout) findViewById(R.id.relLayoutConnection);
		layoutGPS = (RelativeLayout) findViewById(R.id.relLayoutGPS);
		layoutIOIO = (RelativeLayout) findViewById(R.id.relLayoutIOIO);
		layoutWrapper = (RelativeLayout) findViewById(R.id.relLayoutWrapper);
		wrapper = (RelativeLayout) findViewById(R.id.wrapper);
		parentWrapper = (RelativeLayout) findViewById(R.id.parentWrapper);
		
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
		
		DisplayMetrics metrics = new DisplayMetrics(); 
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		
		// X, y of left is 60, (height - 360) + 180
		float heightDP = (metrics.heightPixels - (60 * metrics.density)) / metrics.density ;
		float[] points = {metrics.widthPixels / 2, heightDP, 60 * metrics.density, 360 * metrics.density / 2};
		float[] points2 = {metrics.widthPixels / 2, heightDP, metrics.widthPixels - ( 60 * metrics.density), 360 * metrics.density / 2};
		Line l = new Line(this, points);
		Line l2 = new Line(this, points2);
		layoutWrapper.addView(l,0);
		layoutWrapper.addView(l2,0);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		Log.d(TAG, "onDestroy");
		doUnbindService();
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (!isServiceRunning(NoxDroidService.class)) {
			// Start service
			doBindService();
		} else if (app.getCurrentTrack() != null){
			updateGUI(NoxDroidService.STATUS_RECORDING);
		} else {
			updateGUI(NoxDroidService.STATUS_SERVICE_READY);
		}
	}

	void doBindService() {
		Intent intent = new Intent(this, NoxDroidService.class);
		intent.putExtra("Main Activity", messenger);		
		bindService(new Intent(this, NoxDroidService.class), mConnection,
				Context.BIND_AUTO_CREATE);
		isBound = true;

	}

	void doUnbindService() {
		Log.d(TAG, "doUnbindService");
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
	
	/*
	 * Start track
	 * - send message(s) to the underlying service(s) 
	 */
	public void startTrack(View view) {
		updateGUI(NoxDroidService.ACTION_START_TRACK);
		Message msg = Message.obtain(null,
				NoxDroidService.ACTION_START_TRACK);
		msg.replyTo = messenger;
		try {
			msg_service.send(msg);
		} catch (RemoteException e) {
			Log.e(TAG, e.getMessage());
		}
		Log.i(TAG, "ACTION_START_TRACK sent to NoxDroidService");
		
	}

	/*
	 * Stop track
	 * - send message(s) to the underlying service(s) 
	 */
	public void endTrack(View view) {
		updateGUI(NoxDroidService.ACTION_STOP_TRACK);
		Toast.makeText(this, "stopping service", Toast.LENGTH_SHORT);
		Message msg = Message.obtain(null,
				NoxDroidService.ACTION_STOP_TRACK);
		msg.replyTo = messenger;
		try {
			msg_service.send(msg);
		} catch (RemoteException e) {
			Log.e(TAG, e.getMessage());
		}
		Log.i(TAG, "ACTION_STOP_TRACK sent to NoxDroidService");
		
	}

	public void changeGPS(View view) {
		Intent gpsOptionsIntent = new Intent(
				android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
		startActivity(gpsOptionsIntent);
	}

	public void startIOIO(View view) {
		Toast.makeText(this, "You click start IOIO", Toast.LENGTH_SHORT).show();
	}

	public void changeConnectivity(View view) {
		Toast.makeText(this, "You clicked change connectivity",
				Toast.LENGTH_SHORT).show();
	}

	private synchronized void update(Class<?> c, boolean flag) {
		tests.put(c, flag);
		if (tests.size() == 3) {
			if (passedTests()) {
				if (!isServiceRunning(NoxDroidService.class)) {
					// Start service
					doBindService();
				} else {
					updateGUI(NoxDroidService.STATUS_SERVICE_STARTED);
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

	/**
	 * Section
	 */

	class IncomingHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			Log.d(TAG, "Handling incoming message from " + msg.describeContents()) ;
			updateGUI(msg.what);
		}
	}

	final Messenger messenger = new Messenger(new IncomingHandler());

	private void updateGUI(int status) {
		switch (status) {
		case NoxDroidService.STATUS_IOIO_CONNECTED:
			imgBtnIOIO.setEnabled(true);
			imgBtnIOIO.setImageResource(R.drawable.circle_green);
			Toast.makeText(getBaseContext(), "IOIO Connected",
					Toast.LENGTH_LONG);
			Log.i(TAG, "IOIO Connected");
			break;
		case NoxDroidService.ERROR_IOIO_ABORTED : 
		case NoxDroidService.ERROR_IOIO_CONNECTION_LOST:
			imgBtnIOIO.setEnabled(false);
			imgBtnIOIO.setImageResource(R.drawable.circle_red);
			Toast.makeText(getBaseContext(), "IOIO Lost connection",
					Toast.LENGTH_LONG);
			Log.e(TAG, "IOIO Not Connected");
			break;
		case NoxDroidService.ACTION_START_TRACK :
			imgBtnStart.setVisibility(View.GONE);
			imgBtnStop.setVisibility(View.VISIBLE);
			imgBtnStop.setEnabled(true);
			break;
		case NoxDroidService.ACTION_STOP_TRACK :
			imgBtnStop.setEnabled(false);
			imgBtnStart.setVisibility(View.VISIBLE);
			imgBtnStop.setVisibility(View.GONE);
			imgBtnStart.setEnabled(true);
			Log.e(TAG, "Stop track");
			
			break;
		case NoxDroidService.STATUS_SERVICE_READY :
			imgBtnConn.setImageResource(R.drawable.circle_green);
			imgBtnIOIO.setImageResource(R.drawable.circle_green);
			imgBtnGPS.setImageResource(R.drawable.circle_green);
			imgBtnStart.setImageResource(R.drawable.play);
			imgBtnStart.setEnabled(true);
			break;
		case NoxDroidService.STATUS_RECORDING :
			imgBtnStart.setVisibility(View.GONE);
			imgBtnStop.setVisibility(View.VISIBLE);
			break;
		case NoxDroidService.STATUS_CONNECTIVITY_OK:
			imgBtnConn.setImageResource(R.drawable.circle_green);
			imgConn.setVisibility(View.VISIBLE);
			break;
		case NoxDroidService.ERROR_NO_CONNECTIVITY:
			imgBtnConn.setImageResource(R.drawable.circle_red);
			imgConn.setVisibility(View.VISIBLE);
			break;
		case NoxDroidService.STATUS_LOCATION_OK:
			imgBtnGPS.setImageResource(R.drawable.circle_green);
			imgGPS.setVisibility(View.VISIBLE);
			break;
		case NoxDroidService.ERROR_NO_LOCATION : 
			imgBtnGPS.setImageResource(R.drawable.circle_red);
			imgGPS.setVisibility(View.VISIBLE);
		default:
			break;
		}
	}
	
	/*
	 * Menu
	 * 
	 * 
	 * */
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.mainmenu, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.preferences:
			startActivity(new Intent(NoxDroidMainActivity.this,
					PreferencesActivity.class));
			Toast.makeText(this, "Just a test", Toast.LENGTH_SHORT).show();
			break;
			
		case R.id.post_to_cloud:
			startActivity(new Intent(NoxDroidMainActivity.this,
					TracksListActivity.class));
			break;
			
		case R.id.exitapp :
			stopService(new Intent(this,NoxDroidService.class));
			finish();
			break;
		}		
		return true;
	}	
}