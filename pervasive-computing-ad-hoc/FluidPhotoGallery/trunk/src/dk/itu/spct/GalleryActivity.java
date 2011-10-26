package dk.itu.spct;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Gallery;
import android.widget.Toast;
import dk.itu.spct.R;
import dk.itu.spct.server.TCPClient;

 
/**
 * 
 * based upon 
 * http://developer.android.com/resources/tutorials/views/hello-gallery.html
 * http://developer.android.com/reference/android/widget/Gallery.html
 *
 */
public class GalleryActivity extends Activity {
	
	
    private static final String TAG = "Fluidgallery";
    private static final boolean D = true;
	
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gallery);
        
        if(D) Log.e(TAG, "GalleryActivity onCreate called");
        
        Gallery g = (Gallery) findViewById(R.id.gallery);
        g.setAdapter(new ImageAdapter(this));

        if(D) Log.e(TAG, "GalleryActivity onCreate called - adapter set");
        
        g.setOnItemClickListener(new OnItemClickListener() {
        	@Override
        	public void onItemClick(AdapterView<?> parent, View v, int position,
        			long id) {
        		Toast.makeText(GalleryActivity.this, "" + position, Toast.LENGTH_SHORT).show();

        		
        		if(D) Log.e(TAG, "long id: "+id);
        		
        		// todo: find out how to get the 'real image'
        		// niecetohave: send part should probably be done in a async
        		String message = "image " +  position + " shold be send";
        		TCPClient.send(message);
        		
        	} 
		});
        
    }
}