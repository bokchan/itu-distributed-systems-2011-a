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

package dk.itu.spvc.android;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import dk.itu.spvc.android.apidemoexample.LocalServiceActivities.Binding;
import dk.itu.spvc.android.apidemoexample.LocalServiceActivities.Controller;
import dk.itu.spvc.android.locationservice.LocationService;
import dk.itu.spvc.android.locationservice.LocationServiceDB;
import dk.itu.spvc.android.standaloneservice.StandAloneLocalService;

/**
 * 
 * Intended for links to other activities - start services etc... 
 *
 */
public class Main extends Activity {

	// needed for the options menu
    private static final int START_SERVICE_ID = Menu.FIRST;
    private static final int STOP_SERVICE_ID = Menu.FIRST + 1;
    private static final int START_LOCATION_SERVICE_ID = Menu.FIRST + 2;
    private static final int STOP_LOCATION_SERVICE_ID = Menu.FIRST + 3;
    private static final int START_LOCATION_SERVICE_DB_ID = Menu.FIRST + 4;
    private static final int STOP_LOCATION_SERVICE_DB_ID = Menu.FIRST + 5;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
        setContentView(R.layout.main);

        // Watch for button clicks.
        Button button = (Button)findViewById(R.id.start);
        button.setOnClickListener(mStartListener);
        button = (Button)findViewById(R.id.stop);
        button.setOnClickListener(mStopListener);
    }

    private OnClickListener mStartListener = new OnClickListener() {
        
    	
    	public void onClick(View v) {
    		
    		Log.d("Main", "OnClickListener mStartListener onClick");
    		
            // Make sure the service is started.  It will continue running
            // until someone calls stopService().  The Intent we use to find
            // the service explicitly specifies our service component, because
            // we want it running in our own process and don't want other
            // applications to replace it.
        	
        	// NB! this here "this" is not the Main class but the listener
        	// so refere to the Main class like Main.this
        	startService(new Intent(Main.this, StandAloneLocalService.class));
        	// is a short cut for writing:
            // Intent startIntent = new Intent();
            // startIntent.setClass(Main.this, StandAloneLocalService.class);
            // startService(startIntent); 

        }
    };

    private OnClickListener mStopListener = new OnClickListener() {
        public void onClick(View v) {
            
        	Log.d("Main", "OnClickListener mStopListener onClick");
        	
        	// Cancel a previous call to startService().  Note that the
            // service will not actually stop at this point if there are
            // still bound clients.
        	stopService(new Intent(Main.this, StandAloneLocalService.class));
        }
    };		
		
	// go to another activity directly for that reason we don't use a onclick litenener etc..
    public void goToLocalServiceActivities(View view) {
    	Log.d("Main", "goToLocalServiceActivities called");
    	
    	// note: don't do LocalServiceActivities.class its the innerclass we need
    	startActivity(new Intent(this, Controller.class));
    	
    };
    
    public void goToLocalServiceActivitiesBinding(View view) {
    	Log.d("Main", "goToLocalServiceActivitiesBinding called");
    	
    	// note: don't do LocalServiceActivities.class its the innerclass we need
    	startActivity(new Intent(this, Binding.class));
    	
    };
    
    
    
    
    
    /**
     * create a options menu
     * 
     * based on the notepad android tutorial: 
     * http://developer.android.com/resources/tutorials/notepad
     * 
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        // group_id, item_id, order_id
        menu.add(0, START_SERVICE_ID,0, R.string.start_service);
        menu.add(0, STOP_SERVICE_ID,1, R.string.stop_service);
        menu.add(0, START_LOCATION_SERVICE_ID,2, R.string.start_location_service);
        menu.add(0, STOP_LOCATION_SERVICE_ID,3, R.string.stop_location_service);
        menu.add(0, START_LOCATION_SERVICE_DB_ID,4, R.string.start_location_service_db);
        menu.add(0, STOP_LOCATION_SERVICE_DB_ID,5, R.string.stop_location_service_db);
        
        return true;
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        switch(item.getItemId()) {
        
        // the standalone service
        case START_SERVICE_ID:
            // start stand-a-lone service
        	startService(new Intent(Main.this, StandAloneLocalService.class));
            return true;
    	case STOP_SERVICE_ID:
            // start stand-a-lone service
    		stopService(new Intent(Main.this, StandAloneLocalService.class));
            return true;        
        // the location service            
        case START_LOCATION_SERVICE_ID:
            // start stand-a-lone service
        	startService(new Intent(Main.this, LocationService.class));
            return true;
    	case STOP_LOCATION_SERVICE_ID:
            // start stand-a-lone service
    		stopService(new Intent(Main.this, LocationService.class));
            return true;            
        // the location service w. db            
        case START_LOCATION_SERVICE_DB_ID:
            // start stand-a-lone service
        	startService(new Intent(Main.this, LocationServiceDB.class));
            return true;
    	case STOP_LOCATION_SERVICE_DB_ID:
            // start stand-a-lone service
    		stopService(new Intent(Main.this, LocationServiceDB.class));
            return true;            
        }
        
        return super.onMenuItemSelected(featureId, item);
    }
	
}
