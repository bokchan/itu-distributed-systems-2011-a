package dk.itu.noxdroid;

import Pachube.Feed;
import Pachube.Pachube;
import Pachube.PachubeException;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import dk.itu.noxdroid.service.NoxDroidService;

public class NoxDroidActivity extends Activity {
	
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		//startActivity(new Intent(this,IOIOSensorActivity.class));
		//startActivity(new Intent(this,NoxDroidGPSActivity.class));
	}

	public void updateFeed(View view) {
		// <script type="text/javascript"
		// src="http://www.google.com/jsapi"></script><script
		// language="JavaScript"
		// src="http://apps.pachube.com/google_viz/viz.js"></script><script
		// language="JavaScript">createViz(35611,1,600,200,"1DAB07");</script>
		Pachube p = new Pachube("lFw8Nl2AhdRz2wKSXMvavSuxfjhjQBhl3efwXrmiTPk");
		try {
			TextView info = (TextView) findViewById(R.id.txtInfo);

			Feed f = p.getFeed(38346);

			info.setText(String.format("%s\n%s\n%s\n%s\n", f.getTitle(),
					f.getEmail(), f.getWebsite(), f.getStatus()));

			info.append("Connecting to Pachube Feed\n");
			f.updateDatastream(1, 230.0);
			info.append("Feed Updated\n");
		} catch (PachubeException e) {
			e.printStackTrace();
		}

	}

	public void goIOIO(View view) {
		//startActivity(new Intent(this, IOIOSensorActivity.class));
	}
	
	public void startService(View view) {
		startService(new Intent(this, NoxDroidService.class));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.mainmenu, menu);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.preferences:
			Intent intent = new Intent(NoxDroidActivity.this,
					PreferencesActivity.class);
			startActivity(intent);
			Toast.makeText(this, "Just a test", Toast.LENGTH_SHORT).show();
			break;
		}
		return true;
	}
	
	@Override
	public void onBackPressed() {
		
		moveTaskToBack (true);
	}	
}