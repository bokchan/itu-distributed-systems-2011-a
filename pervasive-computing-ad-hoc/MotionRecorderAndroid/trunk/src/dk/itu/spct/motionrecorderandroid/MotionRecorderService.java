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
import android.os.IBinder;
import android.util.Log;

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
    
    private static final String TAG = "MotionRecorderService";
    
    private NotificationManager mNM;
    private SensorManager mngr;
    
    private String motionType;

    private DbAdapter mDbHelper;
    private String motionUUID;
    
    
    private Thread motionRecorderThread;
    
    private float axisX;
    private float axisY;
    private float axisZ;
    private long eventTimeStamp;
    
    
    private boolean recordMotion = true;
    
    
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
        
		
		
		// SensorManager.SENSOR_DELAY_NORMAL | SENSOR_DELAY_UI | SENSOR_DELAY_GAME
		mngr.registerListener(listener, accelerometer, SensorManager.SENSOR_DELAY_UI);
        
		
		
		// Do something long
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				
				int motionRecorderThread = 1;
				
				while(recordMotion) {
					try {
						Log.d(TAG, "motionRecorderThread - run called: " + motionRecorderThread + " xyz: " + axisX + " " + axisY + " " + axisZ);
	
						// save motion ponit(s)
						mDbHelper.createMotionPoint(axisX, axisY, axisZ, eventTimeStamp);
						
						// The time to sleep in milliseconds. 
						// requirement = sampling rate 20 per second
						// 50 milliseconds = 0.05 second
						// x20 = 1 second
						Thread.sleep(50);	
						motionRecorderThread++;
					} catch (InterruptedException e) {
						e.printStackTrace();
					}			
				}
			}
		};
		motionRecorderThread = new Thread(runnable);
		motionRecorderThread.start();
        
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
                
        // start new activity 
        Intent dialogIntent = new Intent(getBaseContext(), PostToCloudActivity.class);
        dialogIntent.putExtra(dk.itu.spct.motionrecorderandroid.Main.MOTION_UUID, motionUUID);

        dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getApplication().startActivity(dialogIntent);

        
//        // Tell the user we stopped.
//        Toast.makeText(PostToCloudActivity.class, R.string.motion_recorder_service_stopped, Toast.LENGTH_SHORT).show();
    
        // Note:
        // first stop the while loop  then stop thread
        // - stop() is marked deprecated what's the right (TM) way to stop / kill the thread ? = interrupt()
        recordMotion = false;
        motionRecorderThread.interrupt();
    
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
    	
        @Override 
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        	
        	Log.d(TAG, "onAccuracyChanged called: " + countAccuracyChanged);
        	countAccuracyChanged++;

        } 
        @Override 
        public void onSensorChanged(SensorEvent event) {
        	
        	Log.d(TAG, "onSensorChanged called: " + countSensorChanged);
        	countSensorChanged++;

            axisX = event.values[0];
            axisY = event.values[1];
            axisZ = event.values[2];
            eventTimeStamp = event.timestamp;
            
            //TODO:
            // look into this one perhpas pass event.timestamp(); as well ? 
            // hmm not now it seems not to be unix time returning stuff like: 
//            mDbHelper.createMotionPoint(axisX, axisY, axisZ);
            
        	
        } 
    }; 
    
}

