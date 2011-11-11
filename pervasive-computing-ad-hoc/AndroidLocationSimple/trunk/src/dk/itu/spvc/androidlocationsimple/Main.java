package dk.itu.spvc.androidlocationsimple;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class Main extends Activity { 

//    private static final String TAG = R.string.app_name;
	private static final String TAG = "AndroidLocationSimple";
    private static final boolean D = true;
		
	/** Called when the activity is first created. */ 
    @Override 
    public void onCreate(Bundle savedInstanceState) { 
        super.onCreate(savedInstanceState); 
        setContentView(R.layout.main);
        
        if(D) Log.d(TAG, "onCreate called");
        
        // find the TextViews 
        TextView tvLatitude = (TextView)findViewById(R.id.tvLatitude); 
        TextView tvLongitude = (TextView)findViewById(R.id.tvLongitude); 
        // get handle for LocationManager 
        LocationManager lm = (LocationManager) 
            getSystemService(Context.LOCATION_SERVICE); 
        // connect to the GPS location service 
        Location loc = lm.getLastKnownLocation("gps");
        
        if(D) Log.d(TAG, "lm: " + lm);
        if(D) Log.d(TAG, "loc: " + loc);
        
        if (loc != null) {
        
	        // fill in the TextViews 
	        tvLatitude.setText(Double.toString(loc.getLatitude())); 
	        tvLongitude.setText(Double.toString(loc.getLongitude()));
        
        } else {
        	if(D) Log.d(TAG, "lm.getLastKnownLocation(\"gps\") is " + loc);
        }
    } 
}