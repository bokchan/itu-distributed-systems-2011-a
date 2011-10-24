package dk.itu.spct;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Gallery;
import android.widget.Toast;

public class GalleryActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gallery);
        
        Gallery g = (Gallery) findViewById(R.id.gallery);
        g.setAdapter(new ImageAdapter(this));
        
        g.setOnItemClickListener(new OnItemClickListener() {
        	@Override
        	public void onItemClick(AdapterView<?> parent, View v, int position,
        			long id) {
        		Toast.makeText(GalleryActivity.this, "" + position, Toast.LENGTH_SHORT).show();
        	} 
		});
        
    }
}