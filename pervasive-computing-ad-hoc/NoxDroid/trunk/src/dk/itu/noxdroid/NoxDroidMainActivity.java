package dk.itu.noxdroid;

import android.app.Activity;
import android.content.Context;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import dk.itu.noxdroid.ioio.IOIOConnectedTestThread;
import dk.itu.noxdroid.ioio.IOIOConnectedTestThread.STATUS;

public class NoxDroidMainActivity extends Activity {

	private final int IOIOCONNECTIONTEST = 0;

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
	private ProgressBar pb;
	private RelativeLayout.LayoutParams lp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main2);

		/********** INITIALIZES *************/
		imgBtnStart = (ImageButton) findViewById(R.id.imgBtnStart);
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

		lp = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);
		lp.addRule(RelativeLayout.CENTER_IN_PARENT);
		pb = new ProgressBar(getBaseContext());
		pb.setLayoutParams(lp);

		// showLoaders();
		testDependencies();
	}
	

	private void showLoaders() {
		ImageView[] iv = { imgConn, imgGPS, imgIOIO };
		for (ImageView i : iv) {
			ProgressBar pb = new ProgressBar(this);
			pb.setVisibility(View.VISIBLE);
			RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
					RelativeLayout.LayoutParams.WRAP_CONTENT,
					RelativeLayout.LayoutParams.WRAP_CONTENT);
			lp.addRule(RelativeLayout.CENTER_IN_PARENT);
			pb.setLayoutParams(lp);
			((RelativeLayout) i.getParent()).addView(pb);
			i.setVisibility(View.GONE);
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

	class ConnectivityTest extends AsyncTask<Void, Void, Boolean> {

		@Override
		protected void onPreExecute() {
			imgConn.setVisibility(View.GONE);
			// ProgressBar pb = new ProgressBar(getBaseContext());
			// pb.setVisibility(View.VISIBLE);
			// RelativeLayout.LayoutParams lp = new
			// RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
			// lp.addRule(RelativeLayout.CENTER_IN_PARENT);
			// pb.setLayoutParams(lp);
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
			} else {
				imgBtnConn.setImageResource(R.drawable.circle_red);
			}
			imgConn.setVisibility(View.VISIBLE);
			pb.setVisibility(View.GONE);
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
		}
	}

	class IOIOConnectionTest extends AsyncTask<Void, Void, Boolean> {
		protected void disconnected() throws InterruptedException {

		}

		/**
		 * Subclasses should override this method for performing operations to
		 * be done if an incompatible IOIO firmware is detected. The
		 * {@link #ioio_} member must not be used from within this method. This
		 * method will only be called once, until a compatible IOIO is connected
		 * (i.e. {@link #setup()} gets called).
		 */
		protected void incompatible() {

		}

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

		@Override
		protected void onPostExecute(Boolean result) {
			if (result) {
				imgBtnIOIO.setImageResource(R.drawable.circle_green);

			} else {
				imgBtnIOIO.setImageResource(R.drawable.circle_red);
			}
			this.pb.setVisibility(View.GONE);
			imgIOIO.setVisibility(View.VISIBLE);
		}
	}
}