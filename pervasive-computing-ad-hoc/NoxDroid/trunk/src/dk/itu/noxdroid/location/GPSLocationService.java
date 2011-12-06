/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package dk.itu.noxdroid.location;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import dk.itu.noxdroid.NoxDroidApp;
import dk.itu.noxdroid.R;
import dk.itu.noxdroid.database.NoxDroidDbAdapter;

// Need the following import to get access to the app resources, since this
// class is in a sub-package.

/**
 * This is an example of implementing an application service that runs locally
 * in the same process as the application. The
 * {@link LocalServiceActivities.Controller} and
 * {@link LocalServiceActivities.Binding} classes show how to interact with the
 * service.
 * 
 * <p>
 * Notice the use of the {@link NotificationManager} when interesting things
 * happen in the service. This is generally how background services should
 * interact with the user, rather than doing something more disruptive such as
 * calling startActivity().
 */

public class GPSLocationService extends Service {

	private String TAG;

	private int countOnLocationChanged = 0;
	private LocationManager lm;
	private LocationListener locListenD;
	private Double latitude;
	private Double longitude;
	private NoxDroidDbAdapter mDbHelper;
	private String locationProvider;

	/**
	 * Class for clients to access. Because we know this service always runs in
	 * the same process as its clients, we don't need to deal with IPC.
	 */
	public class LocalBinder extends Binder {

		public GPSLocationService getService() {
			Log.d(TAG, "LocalBinder called");
			return GPSLocationService.this;
		}

	}

	@Override
	public void onCreate() {

		TAG = getString(R.string.LOGCAT_TAG, getString(R.string.app_name), this
				.getClass().getSimpleName());
		
		//
		// Get handle for LocationManager
		//
		lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		//
		// Get the global database adapter
		// - this approach needs no open commands and such its handled with the
		// adapter
		//
		mDbHelper = ((NoxDroidApp) getApplication()).getDbAdapter();

		Location loc = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		if (loc != null) {
			//
			// connect to the GPS location service - get and print
			//
			locationProvider = loc.getProvider();

			latitude = loc.getLatitude();
			longitude = loc.getLongitude();

			/*
			 * Add to database
			 */
			mDbHelper
					.createLocationPoint(latitude, longitude, locationProvider);

		} else {
			Log.d(TAG,
					"lm.getLastKnownLocation(LocationManager.GPS_PROVIDER) is "
							+ loc);
		}

		// ask the Location Manager to send us location updates
		locListenD = new DispLocListener();
		// bind to location manager - TODO: fine tune the variables
		// 30000L / minTime = the minimum time interval for notifications, in
		// milliseconds.
		// 10.0f / minDistance - the minimum distance interval for notifications
		lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0,
				locListenD);

	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.i(TAG, "Received start id " + startId + ": " + intent);
		// We want this service to continue running until it is explicitly
		// stopped, so return sticky.
		return START_STICKY;
	}

	@Override
	public void onDestroy() {

		Log.d(TAG, "onDestroy called");

		// Location: close down / unsubscribe the location updates
		lm.removeUpdates(locListenD);

		// the NoxDroid database don't need to be closed - are handled globally
		// // close database
		// mDbHelper.close();

	}

	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}

	// This is the object that receives interactions from clients. See
	// RemoteService for a more complete example.
	private final IBinder mBinder = new LocalBinder();

	/*
	 * Location listener
	 * 
	 * - could also have been implemented directly on the LocationService class
	 * but its convenient to split it out.
	 */
	private class DispLocListener implements LocationListener {
		@Override
		public void onLocationChanged(Location location) {

			countOnLocationChanged++;

			Log.d(TAG, "onLocationChanged called");
			Log.d(TAG, "countOnLocationChanged: " + countOnLocationChanged);

			latitude = location.getLatitude();
			longitude = location.getLongitude();

			Log.d(TAG, "latitude: " + latitude + "longitude: " + longitude);
			Log.d(TAG, "locationProvider: " + locationProvider);

			/**
			 * Add to database
			 */
			mDbHelper
					.createLocationPoint(latitude, longitude, locationProvider);

		}

		@Override
		public void onProviderDisabled(String arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onProviderEnabled(String provider) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			// TODO Auto-generated method stub

		}

	}

}