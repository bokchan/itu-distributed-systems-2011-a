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
package dk.itu.spvc.android.locationservice;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;
import dk.itu.spvc.android.R;
import dk.itu.spvc.android.apidemoexample.LocalServiceActivities;



// Need the following import to get access to the app resources, since this
// class is in a sub-package.


/**
 * This is an example of implementing an application service that runs locally
 * in the same process as the application.  The {@link LocalServiceActivities.Controller}
 * and {@link LocalServiceActivities.Binding} classes show how to interact with the
 * service.
 *
 * <p>Notice the use of the {@link NotificationManager} when interesting things
 * happen in the service.  This is generally how background services should
 * interact with the user, rather than doing something more disruptive such as
 * calling startActivity().
 */

public class LocationServiceDB extends Service {

	private static final String TAG = "LocationService";
	
	
	private NotificationManager mNM;

	// some debug counters
	private int countOnProviderEnabled = 0;
	private int countOnStatusChanged = 0;	
	private int countOnProviderDisabled = 0;
	private int countOnLocationChanged = 0;
	
	private LocationManager lm;
	private LocationListener locListenD;

	private Double latitude;
	private Double longitude;

	
	// TODO: get rid of this hashmap
	// hmm probably better just make a stack since we have to put and then later pop it!
	// dog a fifo queue of course !
	// - key should be a string 
	HashMap<Integer,Double> map = new HashMap<Integer,Double>();

	private LocationDbAdapter mDbHelper;
	long rowId;
	
	// based on unique uuid per track
	String trackId;
    
    /**
     * Class for clients to access.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with
     * IPC.
     */
    public class LocalBinder extends Binder {
    	
    	public LocationServiceDB getService() {
    		Log.d(TAG, "LocalBinder called");
    		return LocationServiceDB.this;
        }
    	
    }
    
    @Override
    public void onCreate() {
    	
    	Log.d(TAG, "onCreate called");
    	
        mNM = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);

        // Display a notification about us starting.  We put an icon in the status bar.
        showNotification();

        
        /**
         *  Set up the location stuff
         *  for a stand alone example with an activity look here xxxx
         *  (TODO:pase url to svn / dk.itu.spvc.androidlocationsimple)
         */

		// get handle for LocationManager
		lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		
		
		// Note: we might wanna enable other then gps? 
		//
		// possible criteria's:
		// power requirement
		// accuracy
		// bearing
		// speed
		// altitude
		//
		// enabledOnly set to true
		Boolean enabledOnly = true;
		List<String> allLocationProviders = lm.getAllProviders();
		Log.d(TAG, "allLocationProviders available: " + allLocationProviders);
		Criteria criteria = new Criteria();
		
		criteria.setPowerRequirement(Criteria.POWER_MEDIUM); 
//		criteria.setAccuracy(accuracy);
		criteria.setAltitudeRequired(false);
		criteria.setSpeedRequired(false);
		String bestProvider = lm.getBestProvider(criteria, enabledOnly);
		Log.d(TAG, "bestProvider - based on criteria given (): " + bestProvider);
		
		// try out end
		
		//
		// connect to the GPS location service  - get and print 
		// 
		Location loc = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);

		// Set up data base 
        mDbHelper = new LocationDbAdapter(this);
        mDbHelper.open();
		
		Log.d(TAG, "lm: " + lm);
		Log.d(TAG, "loc: " + loc);

		
		
		// Set a current track id
		// for now 
		//trackId = mDbHelper.createTrackId();
		//
		trackId = UUID.randomUUID().toString();
		
		
		
		
		
		// TODO: figure out to set a track id thats unique only for one track (start / stop)
				
		if (loc != null) {
			Log.d(TAG, "Latitude is " + Double.toString(loc.getLatitude()));
			Log.d(TAG, "Longitude is " + Double.toString(loc.getLongitude()));
			
			rowId = mDbHelper.createTrack(trackId, loc.getLatitude(), loc.getLongitude());
		} else {
			// no last known location - set latitude and longitude to 0
			Log.d(TAG, "lm.getLastKnownLocation(\"gps\") is " + loc);
			rowId = mDbHelper.createTrack(trackId, 0.0, 0.0);
			// or trackId = mDbHelper.createTrack(0, 0);
		}


		
		// ask the Location Manager to send us location updates
		locListenD = new DispLocListener();        
		// bind to location manager
		// 30000L / minTime	= the minimum time interval for notifications, in milliseconds.
		// 10.0f / minDistance - the minimum distance interval for notifications
		lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 30000L, 10.0f, locListenD);
		
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
    	
    	// Cancel the persistent notification.
        mNM.cancel(R.string.local_service_started);

        // Location: close down / unsubscribe  the location updates
        lm.removeUpdates(locListenD);
        
        // close database
        mDbHelper.close();
        
        // Tell the user we stopped.
        Toast.makeText(this, R.string.location_service_db_stopped, Toast.LENGTH_SHORT).show();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    // This is the object that receives interactions from clients.  See
    // RemoteService for a more complete example.
    private final IBinder mBinder = new LocalBinder();

    /**
     * Show a notification while this service is running.
     */
    private void showNotification() {
        // In this sample, we'll use the same text for the ticker and the expanded notification
        CharSequence text = getText(R.string.location_service_db_started);

        // Set the icon, scrolling text and timestamp
        Notification notification = new Notification(R.drawable.stat_sample, text,
                System.currentTimeMillis());

        // The PendingIntent to launch our activity if the user selects this notification
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, LocalServiceActivities.Controller.class), 0);

        // Set the info for the views that show in the notification panel.
        notification.setLatestEventInfo(this, getText(R.string.local_service_label),
                       text, contentIntent);

        // Send the notification.
        // We use a layout id because it is a unique number.  We use it later to cancel.
        mNM.notify(R.string.local_service_started, notification);
    }
    
        
	private class DispLocListener implements LocationListener {
		@Override
		public void onLocationChanged(Location location) {

			countOnLocationChanged++;
			
			Log.d(TAG, "onLocationChanged called");
			Log.d(TAG, "countOnLocationChanged: " + countOnLocationChanged);
			
			
			latitude = location.getLatitude();
			longitude = location.getLongitude();
			
//			// update TextViews
//			tvLatitude.setText(Double.toString(latitude));
//			tvLongitude.setText(Double.toString(longitude));

// note: this simply dosen't work			
			
//			// lest count and bring a toast
//			// - since we don;t have a direct context/view here use getApplicationContext() - based on http://stackoverflow.com/questions/5895283/locationlistener-and-timers
//			// - not sure if its used correct  ?
//			Toast.makeText(getApplicationContext(), "LocationChanged: " + countOnLocationChanged, Toast.LENGTH_SHORT);
////			Toast.makeText(a, "LocationChanged: " + count, Toast.LENGTH_SHORT);
//			Log.d(TAG, "toast should have been hit");
			
			int hashCode = hashCode();
			
			Log.d(TAG, "put data in a map - with hashcode:" + Integer.toString(hashCode));
			map.put(hashCode, latitude);
						
			// save to database
//			mDbHelper.
//			updateNote(trackId, latitude, longitude);
			
//			mDbHelper.updateNote(trackId, latitude, longitude);
			
			
			rowId = mDbHelper.createTrack(trackId, latitude, longitude);
			
			
			
		}


		@Override
		public void onProviderDisabled(String provider) {

			countOnProviderDisabled++;
			
			Log.d(TAG, "onProviderDisabled called");
			Log.d(TAG, "countOnProviderDisabled: " + countOnProviderDisabled);
			
			
		
		}

		@Override
		public void onProviderEnabled(String provider) {
			
			countOnProviderEnabled++;
			
			Log.d(TAG, "onProviderEnabled called");
			Log.d(TAG, "countOnProviderEnabled: " + countOnProviderEnabled);
		
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {

			countOnStatusChanged++;
			
			Log.d(TAG, "onStatusChanged called");
			Log.d(TAG, "countOnStatusChanged: " + countOnStatusChanged);
			
		}	
	
	}
    
}

