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
package dk.itu.spct.motionrecorderandroid;


import java.text.SimpleDateFormat;
import java.util.List;
import java.util.UUID;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

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


public class MotionRecorderService extends Service {
    
    private static final String TAG = "LocalService";
    
    private NotificationManager mNM;
    private SensorManager mngr;
    
    private String motionType;

    private DbAdapter mDbHelper;
    private String motionUUID;
    
    
    private Thread motionRecorderThread;
    
    private float axisX;
    private float axisY;
    private float axisZ;
    
    
    
    
    /**
     * Class for clients to access.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with
     * IPC.
     */
    public class LocalBinder extends Binder {
    	
    	MotionRecorderService getService() {
    		
    		Log.d(TAG, "LocalBinder called");

    		return MotionRecorderService.this;
        }
    }
    
    @Override
    public void onCreate() {
    	
    	Log.d(TAG, "onCreate called");
    	
        mNM = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);

        // Display a notification about us starting.  We put an icon in the status bar.
        showNotification();

        
        
//        SensorManager mngr_test = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
//        Sensor sensor = mngr_test.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
//        Log.d(TAG, "mngr_test:" + mngr_test);
//        Log.d(TAG, "mngr_test.getDefaultSensor:" + sensor);
//        
        
        
        /* progarmming_android.pdf 
         * 
        To access a sensor or set of sensors, Android provides a convenient system service called 
        the SensorManager. This can be accessed via the getSystemService() method of the 
        Context with the argument of Context.SENSOR_SERVICE. With the SensorManager you 
        then can get a specific sensor via the getDefaultSensor() method. 
        However, a composite sensor may sometimes be returned, so if you wish to get access 
        to the raw sensor and its associated data, you should use getSensorList():
        */ 

        Log.d(TAG, "book approach to get sensor:");
        
        
        
//        public int getMinDelay ()
//
//        Since: API Level 9
//        Returns
//
//        the minimum delay allowed between two events in microsecond or zero if this sensor only returns a value when the data it's measuring changes.
        
        
//        // Note: not 100% sure about 'this' set it instead of context - which we don't have here.. 
//		mngr = (SensorManager) this
//				.getSystemService(Context.SENSOR_SERVICE);
//		// getting the default accelerometer
//		Sensor accelerometer = mngr.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
//		// getting the raw accelerometer
//		List<Sensor> list = mngr.getSensorList(Sensor.TYPE_ACCELEROMETER);
//        

		
		
		
//		mngr.SENSOR_DELAY_NORMAL
		
		
////		Log.d(TAG, "accelerometer.getMinDelay:" + accelerometer.getMinDelay());
//		Log.d(TAG, "accelerometer:" + accelerometer);
//		Log.d(TAG, "sensor list:" + list);
		

		
		
		
		
		
		
		
		/*
D/LocalService(  404): mngr_test:android.hardware.SensorManager@44f52218
D/LocalService(  404): mngr_test.getDefaultSensor:android.hardware.Sensor@44f527b8
D/LocalService(  404): book approach to get sensor:
D/LocalService(  404): accel:android.hardware.Sensor@44f527b8
D/LocalService(  404): sensor list:[android.hardware.Sensor@44f527b8]
I/LocalService(  404): Received start id 1: Intent { cmp=dk.itu.spct.motionrecorderandroid/.LocalService }
		 */
        
	    // registering a listener 
		// for 
		
		
//		TODO: solve this one as welll
		// seems it didn't like  - SensorManager.SENSOR_DELAY_GAME
		// try briefly with 20 
		// 1/1000 - 50 
		
		/*
		 * from the api about the rate variable
		 * 
		 * rate 
		 * ----	
		 * The rate sensor events are delivered at. This is only a hint to the system. 
		 * Events may be received faster or slower than the specified rate. Usually events are received faster. 
		 * The value must be one of SENSOR_DELAY_NORMAL, SENSOR_DELAY_UI, SENSOR_DELAY_GAME, or 
		 * SENSOR_DELAY_FASTEST or, the desired delay between events in microsecond.
		 * 
		 * 
		 * 
		 * A microsecond is an SI unit of time equal to one millionth (10−6) of a second. Its symbol is µs.
		 * A microsecond is equal to 1000 nanoseconds or 1/1000 millisecond. Because the next SI unit is 1000 
		 * times larger, measurements of 10−5 and 10−4 seconds are typically expressed as tens or hundreds 
		 * of microseconds.
		 * 
		 * A millisecond (from milli- and second; abbreviation: ms) is a thousandth (1/1,000) of a second
		 * 
		 */
		
		
		// 1000000 / 1 million mili secs for one second
		
		// SensorManager.SENSOR_DELAY_NORMAL - seems to run with a frequenzy of 200 mili seconds
		
		
		// 1000000 / 20 = 50 000
		
		// working
//		mngr.registerListener(listener, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
		// try out 50000
//		mngr.registerListener(listener, accelerometer, 50000);
		
		// others writes:

//... SENSOR_DELAY_NORMAL .. and 235 As. So the first
//comment is that they do not allways app..
		
//		Os and As varied among the tests.  Typical sample
//		period is 20 msec for fast and 40 msec for GAMES. 
// https://groups.google.com/group/android-developers/browse_thread/thread/2e14272d72b7ab4f/9b2f9afb37d59044?hl=en&lnk=gst&q=SENSOR_DELAY_NORMAL#9b2f9afb37d59044					
		
		//mngr.registerListener(listener, accelerometer, SensorManager.SENSOR_DELAY_GAME);
        // registerListener (SensorEventListener listener, Sensor sensor, int rate)
		// You can choose from a variety of rates enumerated by SensorManager class:
		// SENSOR_DELAY_NORMAL, SENSOR_DELAY_UI, SENSOR_DELAY_GAME, or SENSOR_DELAY_FASTEST

		
        
		// TODO: figure out to implement to 20 data recordings per secound
		// solutions:
		// - make a thread that samples 20 samples / 1 sec   
		// - then don't use listener apporach right ?
		
		// start sampler - delay it 
		
		// note:
		// it should be ok to safe in sqlite even with 20 samples per second
		// "Actually, SQLite will easily do 50,000 or more INSERT statements per second on an average desktop computer. But it will only do a few dozen transactions per second. Transaction speed is limited by the rotational speed of your disk drive. A transaction normally requires two complete rotations of the disk platter, which on a 7200RPM disk drive limits you to about 60 transactions per second."
		// source: http://www.sqlite.org/faq.html#q19
        
        
        
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        
    	
    	
    	Log.d(TAG, "Received start id " + startId + ": " + intent);
        

    	
        Log.d(TAG, "intent.getExtras():" + intent.getExtras());
        
        Log.d(TAG, "Main.MOTION_TYPE:" + intent.getExtras().get(dk.itu.spct.motionrecorderandroid.Main.MOTION_TYPE));
                
        motionType = (String) intent.getExtras().get(dk.itu.spct.motionrecorderandroid.Main.MOTION_TYPE);
   
        
        // String msg = R.string.motion_recorder_service_started + " " + motionType;
        // note tried passing msg but that one wrote number  + motionType ...
        
        
        
        
		// Set up data base 
        mDbHelper = new DbAdapter(this);
        mDbHelper.open();
        
        
        motionUUID = UUID.randomUUID().toString();
        
        // Save motion in db with current time stamp (handled by sqlite)
        mDbHelper.createMotion(motionUUID, motionType);
    	
        
        
        
        
        // Note: not 100% sure about 'this' set it instead of context - which we don't have here.. 
		mngr = (SensorManager) this
				.getSystemService(Context.SENSOR_SERVICE);		
		// getting the default accelerometer
		Sensor accelerometer = mngr.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		// getting the raw accelerometer
//		List<Sensor> list = mngr.getSensorList(Sensor.TYPE_ACCELEROMETER);
        
		mngr.registerListener(listener, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);

    	
    	
// Note: dosn't work well here to spin oof a thread
// at least it dosn't work well when we have to close it form onDestroy
// even if we have close thread there etc...
//        
//    	// TODO: implement  
//    	// 
//    	final Handler handler = new Handler();
//    	motionRecorderThread = new Thread(new Runnable() {
//    		
//    		public void run() {
//    			handler.post(new Runnable() {
//    				
//    				int count;
//    				
//    				public void run() {
//    					// textView is a UI control created on the main thread
//    
////    					Log.d(TAG, "thread_try_out thread is running - run called");
//    					while(true) {
//    						count++; 
//    						Log.d(TAG, "thread_try_out thread is running - run called: " + count);
//    						
//    						
////    						mDbHelper.createMotionPoint(axisX, axisY, axisZ);
//    						
//    						// Time in mili secounds - a millisecond is one thousandth of a second 
//    						// 
//    						SystemClock.sleep(50); 
//    					}
//    
//    				}					
//    
//    			});
//    		}
//    	}, "Motion recorder thread");
//
//		motionRecorderThread.start();
        
        
        
        
        // We want this service to continue running until it is explicitly
        // stopped, so return sticky.
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        
    	Log.d(TAG, "onDestroy called");
    	
    	
    	Log.d(TAG, "motionUUID: " + motionUUID);
    	
    	// Cancel the persistent notification.
        mNM.cancel(R.string.local_service_started);
        
        // remove event listener
        mngr.unregisterListener(listener);        
        
    	// end motion
    	mDbHelper.endMotion(motionUUID);
        
        // close database
        mDbHelper.close();
        
        // Tell the user we stopped.
        Toast.makeText(this, R.string.motion_recorder_service_stopped, Toast.LENGTH_SHORT).show();

    
    
     // NB! this one didn't play well
//      // close motion recorder thread
//      motionRecorderThread.stop();
    
    
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
        CharSequence text = getText(R.string.local_service_started);

        // Set the icon, scrolling text and timestamp
        Notification notification = new Notification(R.drawable.stat_sample, text,
                System.currentTimeMillis());

        // The PendingIntent to launch our activity if the user selects this notification
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, Main.class), 0);

        // Set the info for the views that show in the notification panel.
        notification.setLatestEventInfo(this, getText(R.string.local_service_label),
                       text, contentIntent);

        // Send the notification.
        // We use a layout id because it is a unique number.  We use it later to cancel.
        mNM.notify(R.string.local_service_started, notification);
    }
    
    
    
    /**
     * 
     * Accelerometer event listener
     * 
     * Based upon: http://developer.android.com/reference/android/hardware/SensorEvent.html#values
     * 
     */
    SensorEventListener listener = new SensorEventListener() { 
    	
    	int countAccuracyChanged;
    	int countSensorChanged;
    	SimpleDateFormat sdf = new SimpleDateFormat("MMMM d, yyyy 'at' h:mm a");
    	long timeStamp;
    	String date;
    	
    	long lastTime;
    	
//    	Calendar cal = Calendar.getInstance();
//    	cal.setTimeInMillis(timeInMillis);
//    	Date dateText = new Date(cal.get(Calendar.YEAR)-1900,
//    	    cal.get(Calendar.MONTH),
//    	    cal.get(Calendar.DAY_OF_MONTH),
//    	    cal.get(Calendar.HOUR_OF_DAY),
//    	    cal.get(Calendar.MINUTE));
//    	String dateTime = android.text.format.DateFormat.format("MM/dd/yyyy hh:mm", dateText);
    	
    	

//    	private static final float NS2S = 1.0f / 1000000000.0f;
//    	private float timestamp;
//
//    	// final float alpha = 0.8;
//    	final float alpha = (float) 0.8;
//    	
//    	private float[] gravity = new float[3];
//    	private float[] linear_acceleration = new float[3];
    	
    	
        @Override 
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        	
//        	Log.d(TAG, "onAccuracyChanged called: " + countAccuracyChanged);
//        	countAccuracyChanged++;

        } 
        @Override 
        public void onSensorChanged(SensorEvent event) {
        	
//        	timeStamp = event.timestamp;
//        	String timeStampAsSting = "test: " +  timeStamp;
//        	Log.d(TAG, timeStampAsSting);        	
        	
        	
////        	event.timestamp();
//        	
//        	
////        	Log.d(TAG, "onSensorChanged called: " + countSensorChanged);
////        	countSensorChanged++;
//        	
////        	Log.d(TAG, );
//        	
//        	timeStamp = event.timestamp;
//        	
////        	(String) timeStamp;
//        	
////        	String test = "timestamp: " + timeStamp;
//        	date = sdf.format(timeStamp); // prints weird stuff like LocalService(  307): May 22, 2045 at 1:59 PM
//        	Log.d(TAG, date);

        	

//            Log.v("SensorEvent", "Time="+(System.currentTimeMillis()-lastTime));
//            lastTime = System.currentTimeMillis(); 
        	// based upon https://groups.google.com/group/android-developers/browse_thread/thread/d286eaf21b2767dc/e2c4f22ceade7492?hl=en&lnk=gst&q=SENSOR_DELAY_NORMAL#e2c4f22ceade7492

    		
    		
        	
        	
        	
        	/* progarmming_android.pdf
Linear acceleration 
Another sensor type is supported by Android 2.3 (API level 9) to simplify a common 
calculation with the use of the accelerometer. The value sent is a three-dimensional 
vector indicating acceleration along each device axis, not including gravity. This means 
the values are the result of linear acceleration on each axis minus the effects of gravity 
along that axis. This makes it easier to filter out gravity’s constant effects for those of 
us using the phone while on Earth. All values have units of m/s2. 
Gravity 
The values resulting from this sensor make up a three-dimensional vector indicating 
the direction and magnitude of gravity. This too is an Android 2.3 (API level 9) sensor 
that provides a common calculation. Units are m/s2. 
        	 */
        	
        	
//            gravity[0] = alpha * gravity[0] + (1 - alpha) * event.values[0];
//            gravity[1] = alpha * gravity[1] + (1 - alpha) * event.values[1];
//            gravity[2] = alpha * gravity[2] + (1 - alpha) * event.values[2];
//
//            linear_acceleration[0] = event.values[0] - gravity[0];
//            linear_acceleration[1] = event.values[1] - gravity[1];
//            linear_acceleration[2] = event.values[2] - gravity[2];        	
        	
//        	Log.d(TAG, "event.values[0]: "+event.values[0]+" event.values[1]: "+event.values[1]+ " event.values[2]: "+event.values[2]);
        	
        	// gyroscope code
//            final float dT = (event.timestamp - timestamp) * NS2S;
            // Axis of the rotation sample, not normalized yet.
            axisX = event.values[0];
            axisY = event.values[1];
            axisZ = event.values[2];
        	
//            Log.d(TAG, "axisX: "+axisX+" axisY: "+axisY+ " axisZ: "+axisZ);
//            Log.d(TAG, "dT = (event.timestamp - timestamp) * NS2S" + dT);
            
//            TODO: save to database
            
            /* out put could look like:
			D/LocalService(  932): onAccuracyChanged called: 1011
			D/LocalService(  932): onSensorChanged called: 1011
			D/LocalService(  932): axisX: 0.667397 axisY: 8.825985 axisZ: 3.4731886
			D/LocalService(  932): onAccuracyChanged called: 1012
			D/LocalService(  932): onSensorChanged called: 1012
			D/LocalService(  932): axisX: 0.4903325 axisY: 8.934948 axisZ: 3.5821514
             */
                        
            //TODO:
            // look into this one perhpas pass event.timestamp(); as well ? 
            // hmm not now it seems not to be unix time returning stuff like: 
            mDbHelper.createMotionPoint(axisX, axisY, axisZ);
            
        	
        } 
    }; 
    
}

