package dk.itu.noxdroid;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import dk.itu.noxdroid.location.LocationService;
import dk.itu.noxdroid.service.NoxDroidService;
import dk.itu.noxdroid.tracks.TracksService;

public class NoxDroidSimpleActivity extends Activity {

	private String TAG = this.getClass().getSimpleName();
	
	private String login = "noxdroid";
    private String password = "noxdroid";

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_simple);
	}

	
	/*
	 * Starts various services
	 * - nox
	 * - location
	 * - more to come
	 * 
	 */
	public void startService(View view) {
// dont call/start NoxDroidService since it also tries to start the LocationService service
//		startService(new Intent(this, NoxDroidService.class));

		startService(new Intent(this, LocationService.class));

//		startService(new Intent(this, TracksService.class));
	
	}

	/*
	 * Stop service(s)
	 */
	public void stopService(View view) {
// dont call/start NoxDroidService since it also tries to start the LocationService service
//		stopService(new Intent(this, NoxDroidService.class));
		
		stopService(new Intent(this, LocationService.class));
		
//		stopService(new Intent(this, TracksService.class));
	}

}