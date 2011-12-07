package dk.itu.noxdroid.tracks;

import android.app.ListActivity;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import dk.itu.noxdroid.NoxDroidApp;
import dk.itu.noxdroid.R;
import dk.itu.noxdroid.cloudservice.NoxDroidAppEngineUtils;
import dk.itu.noxdroid.database.NoxDroidDbAdapter;



//public class NoxDroidPostActivity extends Activity {
public class TracksListActivity extends ListActivity {
	

	private String TAG = this.getClass().getSimpleName();
	private String cloudServiceURL;
	private String userName;
	private String userId;
	private NoxDroidDbAdapter mDbHelper;


	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	
		super.onCreate(savedInstanceState);
//		setContentView(R.layout.main_simple);
		
		setContentView(R.layout.tracks_list);
		
		
		// note: based upon http://goo.gl/y5m4u - also take a look at the *real*
		// api
		PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(this);
		cloudServiceURL = prefs.getString(
				getString(dk.itu.noxdroid.R.string.WEBSERVICE_URL),
				"http://noxdroidcloudengine.appspot.com/add_track");

		
		userId = prefs.getString(getString(dk.itu.noxdroid.R.string.USER_ID),
				"test_user_id");
		userName = prefs
				.getString(getString(dk.itu.noxdroid.R.string.USER_NAME),
						"Test User Name");

		// note: sometimes a bit confused about the approach to get stuff from
		// <package>.R.string.*
		// webservice_url = prefs.getString("WEBSERVICE_URL",
		// "http://10.0.1.7:8888/add_track");
		// String server_url =
		// prefs.getString(dk.itu.noxdroid.R.string.WEBSERVICE_URL,
		// "http://10.0.1.7:8888/add_track");

		//
		// Get the global database adapter
		// - this approach needs no open commands and such its handled with the
		// adapter
		//
		mDbHelper = ((NoxDroidApp) getApplication()).getDbAdapter();

		
		
		// list view specific
        fillData();
//        registerForContextMenu(getListView());
		
		
		
	}
	

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        
        
        // do something post to service
        Log.d(TAG, "onListItemClick: " + position + " " + id);

        
        
        
		// emulator
		String trackUID = "8c3adc99-3e51-4922-a3c9-d127117bb764";

		// post to cloud service
		NoxDroidAppEngineUtils.postForm(cloudServiceURL, trackUID, userId, userName, mDbHelper);
        
        
        
        
        // hook post in here // 
        
//        Intent i = new Intent(this, NoteEdit.class);
//        i.putExtra(NotesDbAdapter.KEY_ROWID, id);
//        startActivityForResult(i, ACTIVITY_EDIT);

    }

// not sure about this one   
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
//        super.onActivityResult(requestCode, resultCode, intent);
//        fillData();
// 
//    }
	
	
    private void fillData() {
        // Get all of the rows from the database and create the item list
    	Cursor mNotesCursor = mDbHelper.fetchAllTracks();
        startManagingCursor(mNotesCursor);

        // Create an array to specify the fields we want to display in the list (only TITLE)
        String[] from = new String[]{mDbHelper.KEY_TRACKUUID};

        // and an array of the fields we want to bind those fields to (in this case just trackItemText)
        int[] to = new int[]{R.id.trackItemText};

        // Now create a simple cursor adapter and set it to display
        SimpleCursorAdapter tracksAdapter = 
            new SimpleCursorAdapter(this, R.layout.tracks_row, mNotesCursor, from, to);
        setListAdapter(tracksAdapter);
        
        
        // try out bsed upon:
        //http://stackoverflow.com/questions/1254074/how-to-call-the-setlistadapter-in-android 
//        myList.setAdapter(tracksAdapter);
        // but that crashed app
        // this one points out the problem
        // http://stackoverflow.com/questions/3033791/the-method-setlistadapterarrayadapter-is-undefined-for-the-type-create
       // "When you call this.setListAdapter this must extend ListActivity probably you class just extends Activity."
    }
	
	
	
	
	
	
	
	
	/* moce all post to an utility package etc...*/
	
	
	/*
	 * Post Static To Cloud
	 * 
	 * Should normally not be done from an activity (UI)
	 */
	public void postStaticToCloud(View view) {

		// just for test
		// String trackUID = "f3d282f3-6f1b-4a5c-bfa3-a0fc33cfc1a5-test";

		// emulator
		String trackUID = "8c3adc99-3e51-4922-a3c9-d127117bb764";

		// post to cloud service
		NoxDroidAppEngineUtils.postForm(cloudServiceURL, trackUID, userId, userName, mDbHelper);

	}


	
	
	
	
	
}