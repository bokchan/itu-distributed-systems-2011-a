package dk.itu.spct.tryouts;

import dk.itu.spct.R;
import dk.itu.spct.R.layout;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class ActivityFirst extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activityfirst);
    }
    
    public void goToSecondActivity(View view) {
    	startActivity(new Intent(this, ActivitySecond.class));
    }
}