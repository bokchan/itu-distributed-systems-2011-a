package dk.itu.noxdroid.setup;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import dk.itu.noxdroid.R;

public class PreferencesActivity extends PreferenceActivity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
	
		super.onCreate(savedInstanceState);
		
		addPreferencesFromResource(R.xml.preferences);
		
		//PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
	}
}
