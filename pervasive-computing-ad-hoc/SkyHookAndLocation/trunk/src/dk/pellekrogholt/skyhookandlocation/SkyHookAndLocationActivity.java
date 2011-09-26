/*
 * 
 * The location part is based on this tutorial: http://www.vogella.de/articles/AndroidLocationAPI
 * 
 */
package dk.pellekrogholt.skyhookandlocation;

import android.app.Activity;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

public class SkyHookAndLocationActivity extends Activity implements LocationListener {
	private TextView latituteField;
	private TextView longitudeField;
	private TextView skyhookLatituteField;
//	private TextView skyhookLongitudeField;
	private LocationManager locationManager;
	private String provider;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		
		latituteField = (TextView) findViewById(R.id.TextView02);
		longitudeField = (TextView) findViewById(R.id.TextView04);

		// Get the location manager
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		
		
		Log.d("----------------------------------------------------", null , null);
		Log.d("SkyHookAndLocationActivity", "onCreate called", null);
		Log.d("----------------------------------------------------", null , null);
				
		
		// Define the criteria how to select the locatioin provider -> use
		// default
		Criteria criteria = new Criteria();
		provider = locationManager.getBestProvider(criteria, false);
		Location location = locationManager.getLastKnownLocation(provider);

		// Initialize the location fields
		if (location != null) {
			System.out.println("Provider " + provider + " has been selected.");
			int lat = (int) (location.getLatitude());
			int lng = (int) (location.getLongitude());
			latituteField.setText(String.valueOf(lat));
			longitudeField.setText(String.valueOf(lng));
		} else {
			latituteField.setText("Provider not available");
			longitudeField.setText("Provider not available");
		}
		

		//skyhook stuff
		skyhookLatituteField = (TextView) findViewById(R.id.TextView06);
//		skyhookLongitudeField = (TextView) findViewById(R.id.TextView08);
		
		
		Log.d("----------------------------------------------------", null , null);
		Log.d("SkyHookAndLocationActivity", "onCreate called after location stuff now try out skyhook", null);
		Log.d("----------------------------------------------------", null , null);

		skyhookLatituteField.setText(SENSOR_SERVICE);
//		skyhookLatituteField.setText("Skyhook not available");
//		skyhookLongitudeField.setText("Skyhook  not available");
		
		
//		// Initialize the location fields
//		if (location != null) {
////			System.out.println("Provider " + provider + " has been selected.");
////			int lat = (int) (location.getLatitude());
////			int lng = (int) (location.getLongitude());
////			skyhookLatituteField.setText(String.valueOf(lat));
////			skyhookLongitudeField.setText(String.valueOf(lng));
//			skyhookLatituteField.setText("Skyhook todo");
//			skyhookLongitudeField.setText("Skyhook todo");
//		
//		} else {
//			skyhookLatituteField.setText("Skyhook not available");
//			skyhookLongitudeField.setText("Skyhook  not available");
//		}		
		
		
		
	}

	/* Request updates at startup */
	@Override
	protected void onResume() {
		super.onResume();
		locationManager.requestLocationUpdates(provider, 400, 1, this);
	}

	/* Remove the locationlistener updates when Activity is paused */
	@Override
	protected void onPause() {
		super.onPause();
		locationManager.removeUpdates(this);
	}

	@Override
	public void onLocationChanged(Location location) {
		int lat = (int) (location.getLatitude());
		int lng = (int) (location.getLongitude());
		latituteField.setText(String.valueOf(lat));
		longitudeField.setText(String.valueOf(lng));
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderEnabled(String provider) {
		Toast.makeText(this, "Enabled new provider " + provider,
				Toast.LENGTH_SHORT).show();

	}

	@Override
	public void onProviderDisabled(String provider) {
		Toast.makeText(this, "Disenabled provider " + provider,
				Toast.LENGTH_SHORT).show();
	}
}