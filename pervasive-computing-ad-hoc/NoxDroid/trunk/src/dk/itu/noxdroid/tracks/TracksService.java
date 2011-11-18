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
package dk.itu.noxdroid.tracks;

import java.util.UUID;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;
import dk.itu.noxdroid.R;
import dk.itu.noxdroid.database.DbAdapter;


/**
 * 
 * Stand alone service to keep hold of each track
 *
 */
public class TracksService extends Service {

	private static final String TAG = "TracksService";

	private DbAdapter mDbHelper;
	
	private String trackUUID;
			
	
	
    /**
     * Class for clients to access.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with
     * IPC.
     */
    public class LocalBinder extends Binder {
    	
    	public TracksService getService() {
    		Log.d(TAG, "LocalBinder called");
    		return TracksService.this;
        }
    	
    }
    
    @Override
    public void onCreate() {
    	
    	Log.d(TAG, "onCreate called");
            	
    	Toast.makeText(this, R.string.tracks_service_started, Toast.LENGTH_SHORT).show();
    	
		
		// Set up data base 
        mDbHelper = new DbAdapter(this);
        mDbHelper.open();
	
        trackUUID = UUID.randomUUID().toString();
        
        // Save track in db with current time stamp (handled by sqlite)
        mDbHelper.createTrack(trackUUID);
        
        // lets close database since we are not expecting to do much write within this service
        mDbHelper.close();
        
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
        
    	// open again
    	mDbHelper.open();
    	
    	// end track
    	if(mDbHelper.endTrack(trackUUID)){
    		Log.i(TAG, "track end succesfully stored in database");
    	} else {
    		Log.i(TAG, "track end not stored in database");
    	}
    	
        // close database
        mDbHelper.close();        
        
        // Tell the user we stopped
        Toast.makeText(this, R.string.tracks_service_stopped, Toast.LENGTH_SHORT).show();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    // This is the object that receives interactions from clients.  See
    // RemoteService for a more complete example.
    private final IBinder mBinder = new LocalBinder();
    
    
}

