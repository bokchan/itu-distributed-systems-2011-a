package dk.itu.noxdroid;

import java.util.Hashtable;
import java.util.UUID;

import android.app.Application;
import android.preference.PreferenceManager;
import android.util.Log;
import dk.itu.noxdroid.database.NoxDroidDbAdapter;

public class NoxDroidApp extends Application {
	//private DbAdapter dbAdapter;
	private NoxDroidDbAdapter dbAdapter;
	private String TAG;
	private UUID currentTrack = null;
	private Hashtable<Class<?>, Boolean> sensorStates = new Hashtable<Class<?>, Boolean>();
	
	@Override
	public void onCreate() {
		super.onCreate();
		TAG = getString(R.string.LOGCAT_TAG, getString(R.string.app_name), this
				.getClass().getSimpleName());
		Log.i(TAG, "Created NoxdroidApp");
		
		PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
	}
	
	public NoxDroidDbAdapter getDbAdapter() {
		if (dbAdapter == null) {
			NoxDroidDbAdapter.initInstance(this);
			dbAdapter = NoxDroidDbAdapter.getInstance();
			Log.i(TAG, "Init DbAdapter");
		}
		
		return dbAdapter;
	}
	
	
	@Override
	public void onTerminate() {
		super.onTerminate();
		Log.d(TAG, "onTerminate called");
		if (dbAdapter != null)  {
			dbAdapter.close();
		}
	}
	
	public void setCurrentTrack(UUID uuid) {
		this.currentTrack = uuid;
	}
	public UUID getCurrentTrack() {
		return this.currentTrack;
	}
	
	public void updateState(Class<?> c, boolean state) {
		sensorStates.put(c, state);
	}
	
	public boolean getState(Class<?> c) 
	{
		return sensorStates.containsKey(c) && sensorStates.get(c); 
	}
	
}
