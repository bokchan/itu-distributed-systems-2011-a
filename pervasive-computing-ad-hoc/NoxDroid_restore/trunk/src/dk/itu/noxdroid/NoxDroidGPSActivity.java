package dk.itu.noxdroid;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.Toast;

public class NoxDroidGPSActivity extends Activity {

	LocationManager locationManager;
	LocationListener locationListener;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

		if (!locationManager.isProviderEnabled(LOCATION_SERVICE)) {
			Intent gpsOptionsIntent = new Intent(
			 android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
			 startActivity(gpsOptionsIntent);
		} else {
			locationListener = new NoxDroidGPSListener();
			locationManager.requestLocationUpdates(
					LocationManager.GPS_PROVIDER, 0, 0, locationListener);
		}
	}

	public class NoxDroidGPSListener implements LocationListener {

		@Override
		public void onProviderDisabled(String provider) {
			Toast.makeText(getApplicationContext(), "Gps Disabled",
					Toast.LENGTH_SHORT).show();
		}

		@Override
		public void onProviderEnabled(String provider) {
			Toast.makeText(getApplicationContext(), "Gps Enabled",
					Toast.LENGTH_SHORT).show();
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {

		}

		@Override
		public void onLocationChanged(Location location) {
			location.getLatitude();
			location.getLongitude();

			String Text = "My current location is: " + "Latitud = "
					+ location.getLatitude() + "Longitud = "
					+ location.getLongitude();
			Toast.makeText(getApplicationContext(), Text, Toast.LENGTH_SHORT)
					.show();
		}
	}
}