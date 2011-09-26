package dk.itu.pervasive;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.skyhookwireless.wps.WPS;
import com.skyhookwireless.wps.WPSAuthentication;
import com.skyhookwireless.wps.WPSContinuation;
import com.skyhookwireless.wps.WPSLocation;
import com.skyhookwireless.wps.WPSLocationCallback;
import com.skyhookwireless.wps.WPSReturnCode;
import com.skyhookwireless.wps.WPSStreetAddressLookup;


public class SkyhookLocationActivity extends Activity{
    /** Called when the activity is first created. */
	MyLocationCallBack callback = new MyLocationCallBack();
	WPS wps;
	TextView tv;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        wps = new WPS(this);
        tv = (TextView) findViewById(R.id.textView1);
        
        setContentView(R.layout.main);
        
    }
	
	private class MyLocationCallBack implements WPSLocationCallback {
		@Override
		public WPSContinuation handleError(WPSReturnCode error) {
			handleError(error);
			return WPSContinuation.WPS_CONTINUE.WPS_STOP;
		}
		
		@Override
		public void done() {
			
		}
		
		@Override
		public void handleWPSLocation(WPSLocation location) {
			Log.i(LOCATION_SERVICE, "Got Location");
			printLocation(location);
		}
	} 
    
    
    public void printLocation(WPSLocation location) {
    	
    	Log.i(LOCATION_SERVICE,String.valueOf(location.getLatitude()));
    	tv.setText("lat: " + location.getLatitude() + " lon: " + location.getLongitude());
    	
    }

    public void getLocation(View view) {
    	
        WPSAuthentication auth = new WPSAuthentication("bokchan", "itu.dk");
    	wps.getLocation(auth, WPSStreetAddressLookup.WPS_NO_STREET_ADDRESS_LOOKUP, callback);
    } 
	
}