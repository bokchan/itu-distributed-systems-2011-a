package dk.itu.spct;

import android.app.Activity; 
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import dk.itu.spct.server.ServerConnect;

public class FluidPhotoGalleryActivity extends Activity {

    private static final String TAG = "Fluidgallery";
    private static final boolean D = true;
    
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fluidphotogallery);
        
        if(D) Log.d(TAG, "FluidPhotoGalleryActivity onCreate called");
        
    }
    
    public void goToGallery(View view) {
    	
    	if(D) Log.d(TAG, "FluidPhotoGalleryActivity goToGallery called");
//    	startActivity(new Intent(this, ActivitySecond.class));
    	startActivity(new Intent(this, GalleryActivity.class));
    	
    }
    
    public void goToTCPSendMessage(View view) {
    	
    	if(D) Log.d(TAG, "FluidPhotoGalleryActivity goToTCPSendMessage called");
    	startActivity(new Intent(this, TCPSendActivity.class));
    	
    }

    
    
    public void goToImageView(View view) {
    	
    	if(D) Log.d(TAG, "FluidPhotoGalleryActivity goToImageView called");
//    	startActivity(new Intent(this, ActivitySecond.class));
    	startActivity(new Intent(this, ImageActivity.class));
    	
    }    
    

    public void goToTCPServer(View view) {
    	
    	if(D) Log.d(TAG, "FluidPhotoGalleryActivity goToTCPServer called");
    	startActivity(new Intent(this, TCPServerActivity.class));
    	
    }    


    public void goToServerConnect(View view) {
    	
    	if(D) Log.d(TAG, "FluidPhotoGalleryActivity goToServerConnect called");
    	startActivity(new Intent(this, ServerConnect.class));
    	
    }        
    
    
}