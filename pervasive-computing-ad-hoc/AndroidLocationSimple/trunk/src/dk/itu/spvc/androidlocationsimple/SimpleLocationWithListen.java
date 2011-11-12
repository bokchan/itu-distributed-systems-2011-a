package dk.itu.spvc.androidlocationsimple;

//public class SimpleLocationWithListen {
//
//}

import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;


/**
 * 
 * Basic activity with a location listener that updates location in ui
 * for now based on gps.
 * 
 * - counting part seems not to work right when on location change etc... 
 * 
 * @author pellekrogholt
 *
 */
public class SimpleLocationWithListen extends Activity {

	private static final String TAG = "AndroidLocationSimple";
	private static final boolean D = true;
	
	// some debug counters
	private int countOnProviderEnabled = 0;
	private int countOnStatusChanged = 0;	
	private int countOnProviderDisabled = 0;
	private int countOnLocationChanged = 0;
	
	private LocationManager lm;
	private LocationListener locListenD;
	private TextView tvLatitude;
	private TextView tvLongitude;
	private TextView textView;
	  

	// hmm probably better just make a stack since we have to put and then later pop it!
	// dog a fifo queue of course !
	// - key should be a string 
	HashMap<Integer,Double> map = new HashMap<Integer,Double>();
	
	// not sure about this one
	
//	Activity a = this;
	
//	private static final v;
	

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {

		if (D)
			Log.d(TAG, "onCreate called");

		
		super.onCreate(savedInstanceState);
//		setContentView(R.layout.main);
		setContentView(R.layout.simplelocationlistener);
		
		// find the TextViews
		tvLatitude = (TextView) findViewById(R.id.tvLatitude);
		tvLongitude = (TextView) findViewById(R.id.tvLongitude);

		// new approach
		textView = (TextView) findViewById(R.id.text_view); 

		
		// get handle for LocationManager
		lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		// connect to the GPS location service
		Location loc = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);

		if (D)
			Log.d(TAG, "lm: " + lm);
		if (D)
			Log.d(TAG, "loc: " + loc);

		if (loc != null) {

			// fill in the TextViews
			tvLatitude.setText(Double.toString(loc.getLatitude()));
			tvLongitude.setText(Double.toString(loc.getLongitude()));

		} else {
			if (D)
				Log.d(TAG, "lm.getLastKnownLocation(\"gps\") is " + loc);
		}

		// ask the Location Manager to send us location updates
		locListenD = new DispLocListener();
		
		
		/*
		 * 
		 * NB!
		 * 
		 * 30000L / minTime	= the minimum time interval for notifications, in milliseconds. 
		 * This field is only used as a hint to conserve power, and actual time between location updates may be greater or lesser than this value.
		 *
		 * 10.0f / minDistance - the minimum distance interval for notifications, in meters
		 * 
		 */
		lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 30000L, 10.0f, locListenD);

	}

	private class DispLocListener implements LocationListener {
		@Override
		public void onLocationChanged(Location location) {

			countOnLocationChanged++;
			
			if (D) Log.d(TAG, "onLocationChanged called");
			if (D) Log.d(TAG, "countOnLocationChanged: " + countOnLocationChanged);
			
			
			Double latitude = location.getLatitude();
			Double longitude = location.getLongitude();
			
			// update TextViews
			tvLatitude.setText(Double.toString(latitude));
			tvLongitude.setText(Double.toString(longitude));

			// get previous text
			CharSequence text = textView.getText();
			// concatenate with new location 
	        textView.setText("\n\nlongitude: " + location.getLongitude() +
	        				 "\nlatitude: " + location.getLatitude() +  
	        				 text); 

			
			
// note: this simply dosen't work			
			
//			// lest count and bring a toast
//			// - since we don;t have a direct context/view here use getApplicationContext() - based on http://stackoverflow.com/questions/5895283/locationlistener-and-timers
//			// - not sure if its used correct  ?
//			Toast.makeText(getApplicationContext(), "LocationChanged: " + countOnLocationChanged, Toast.LENGTH_SHORT);
////			Toast.makeText(a, "LocationChanged: " + count, Toast.LENGTH_SHORT);
//			if (D) Log.d(TAG, "toast should have been hit");
			
	        // we can't use "this" here 
	        // don't use getApplicationContext()
	        // our context is SimpleLocationWithListen.this 
	       // Toast.makeText(SimpleLocationWithListen.this, "LocationChanged: " + countOnLocationChanged, Toast.LENGTH_SHORT);
	        // - even that didn't work must be doing something completely wrong... p
	        
			int hashCode = hashCode();
			
			if (D) Log.d(TAG, "put data in a map - with hashcode:" + Integer.toString(hashCode));
			map.put(hashCode, latitude);
			
			
		}

		@Override
		public void onProviderDisabled(String provider) {

			countOnProviderDisabled++;
			
			if (D) Log.d(TAG, "onProviderDisabled called");
			if (D) Log.d(TAG, "countOnProviderDisabled: " + countOnProviderDisabled);
			
			
		
		}

		@Override
		public void onProviderEnabled(String provider) {
			
			countOnProviderEnabled++;
			
			
			if (D) Log.d(TAG, "onProviderEnabled called");
			if (D) Log.d(TAG, "countOnProviderEnabled: " + countOnProviderEnabled);
			

		
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {

			countOnStatusChanged++;
			
			if (D) Log.d(TAG, "onStatusChanged called");
			if (D) Log.d(TAG, "countOnStatusChanged: " + countOnStatusChanged);
			

		
		}
	}

	/*
	 * Book note:
	 * 
	 * Finally, we want to add the onPause and onResume code to turn location
	 * updates off when we're not actually displaying on the user's screen, and
	 * turn them back on when we are:
	 */

	/**
	 * Turn off location updates if we're paused
	 */
	@Override
	public void onPause() {
		super.onPause();
		lm.removeUpdates(locListenD);
	}

	/**
	 * Resume location updates when we're resumed
	 */
	@Override
	public void onResume() {
		super.onResume();
		lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 30000L, 10.0f, locListenD);
	}

}
