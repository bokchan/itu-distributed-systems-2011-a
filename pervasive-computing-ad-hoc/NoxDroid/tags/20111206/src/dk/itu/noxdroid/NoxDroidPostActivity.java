package dk.itu.noxdroid;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import dk.itu.noxdroid.database.NoxDroidDbAdapter;

import android.app.Activity;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

public class NoxDroidPostActivity extends Activity {

	private String TAG = this.getClass().getSimpleName();
	private String webservice_url;
	private String userName;
	private String userId;
	private NoxDroidDbAdapter mDbHelper;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_simple);
		
		
		// note: based upon http://goo.gl/y5m4u - also take a look at the *real* api
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        webservice_url = prefs.getString(getString(dk.itu.noxdroid.R.string.WEBSERVICE_URL), "http://10.0.1.7:8888/add_track");
        
        
        userId = prefs.getString(getString(dk.itu.noxdroid.R.string.USER_ID), "test_user_id");
        userName = prefs.getString(getString(dk.itu.noxdroid.R.string.USER_NAME), "Test User Name");	
        
        
// note: sometimes a bit confused about the approach to get stuff from <package>.R.string.* 
//        webservice_url = prefs.getString("WEBSERVICE_URL", "http://10.0.1.7:8888/add_track"); 
//        String server_url = prefs.getString(dk.itu.noxdroid.R.string.WEBSERVICE_URL, "http://10.0.1.7:8888/add_track");
        
        
        
        
		//
		// Get the global database adapter
		// - this approach needs no open commands and such its handled with the adapter
		//        
		mDbHelper = ((NoxDroidApp) getApplication()).getDbAdapter();
        
	}
	
	/*
	 * Post Static To Cloud
	 * 
	 * Should normally not be done from an activity (UI)
	 * 
	 */
	public void postStaticToCloud(View view) {
	
		// just for test
//        String trackUID = "f3d282f3-6f1b-4a5c-bfa3-a0fc33cfc1a5-test";
		
		// emulator
        String trackUID = "8c3adc99-3e51-4922-a3c9-d127117bb764";
        
        
		// post to cloud service
		postForm(trackUID);

	}

	
    public void postForm(String trackUID){
        
    	
    	// TODO: how do we get hand on the service - where the preferences are crated globally
//    	service.APP_PREFS.get(dk.itu.noxdroid.R.string.server_url);
    	

    	
        
        
    	
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost(webservice_url);

        /*
        we are mime'ing this form:
        
        http://noxdroidcloudengine.appspot.com/add_track_form.html
        http://noxdroidcloudengine.appspot.com/add_track
        
	<form method="post" action="/add_track">

      <div>
          <label>sensor id/name (e.g. default)</label>
          <input type="text" size="30" name="sensor_id">
      </div>
      <div><label>sensor user name (e.g. default) - (might be the same as sensor id/name)</label><input type="text" size="30" name="sensor_user_name"></div>

      <div><label>track id (UUID e.g. eeb445dc-d2eb-494f-a4af-78c20b5d181c)</label><input type="text" size="30" name="track_id"></div>
      <div><label>track start time (e.g. 2011-12-02 09:12:04)</label><input type="text" size="30" name="track_start_time"></div>
      <div><label>track end time (e.g. 2011-12-02 10:12:04)</label><input type="text" size="30" name="track_end_time"></div>
      
      
      <div><label>location(s) (as json)</label><textarea cols="60" rows="3" name="track_locations">{ "locations" : [{"latitude" : "55.659919", "longitude" : "12.591190", "time_stamp" : "2011-12-04 09:12:04"}, {"latitude" : "55.659919", "longitude" : "12.691190", "time_stamp" : "2011-12-04 09:12:05"}]}
</textarea></div>

      <div><label>nox(s) (as json)</label><textarea cols="60" rows="3" name="track_nox"></textarea></div>      
      
      <div>
          <input type="submit" value="Add">
      </div>
      
    </form>
    		*/
        
        // test vars/data should be loaded from database
        String trackStartTime = "2011-12-02 09:12:04";
        String trackEndTime = "2011-12-02 2011-12-02 10:12:04";
        String trackJSONString = "{ \"locations\" : [{\"latitude\" : \"55.659919\", \"longitude\" : \"12.591190\", \"time_stamp\" : \"2011-12-04 09:12:04\"}, {\"latitude\" : \"55.659919\", \"longitude\" : \"12.691190\", \"time_stamp\" : \"2011-12-04 09:12:05\"}]}";
        
        // create json from databse query
        
        Cursor mCursor = mDbHelper.fetchLocations(trackUID);
        
        
        int size = mCursor.getCount();
        for (int i = 0; i < size; i++) {
        	Log.d(TAG, "mCursor: " + mCursor );
        	mCursor.moveToNext();
        }
        	
        
        try {
            // Add your data to the form
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(8);
            
            // TODO: sensor_id will change to noxdroid_id
            nameValuePairs.add(new BasicNameValuePair("sensor_id", userId));
            //sensor_user_name
            nameValuePairs.add(new BasicNameValuePair("sensor_user_name", userName));

              
            nameValuePairs.add(new BasicNameValuePair("track_id", trackUID));
            nameValuePairs.add(new BasicNameValuePair("track_start_time", trackStartTime));
            nameValuePairs.add(new BasicNameValuePair("track_end_time", trackEndTime));
            
            // locations - 
            nameValuePairs.add(new BasicNameValuePair("track_locations", trackJSONString));
            
            // Finalize the form
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            // Execute HTTP Post Request
            HttpResponse response = httpclient.execute(httppost);

            // note: for now we have skipped to get the response
            try {
				// if the response is a html page then print out with the helper
				// String the_string_response = HttpHelper.request(response);
            	// we just have a short on - usually: HTTP/1.1 201 OK
            	StatusLine status = response.getStatusLine();
            	Log.d(TAG, "status code: " + status.getStatusCode() );
            	            	
    			Toast.makeText(NoxDroidPostActivity.this, "Post to cloud was successful", Toast.LENGTH_SHORT).show();
            	
            	// TODO:
            	// set track flag in database to be sync'ed
            	
            } catch(Exception e) {
            	Log.e(TAG, "problems with posting to the cloud server - the respone failed: " 
            			+ e.getMessage());
            }
            
        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
        	Log.e(TAG, "ClientProtocolException	in case of an http protocol error - " +
        			e.getMessage());
        } catch (IllegalStateException e) {
            // TODO Auto-generated catch block
        	Log.e(TAG, "IllegalStateException - " 
        			+ e.getMessage());
        } catch (IOException e) {
            // TODO Auto-generated catch block
        	Log.e(TAG, "IOException	in case of a problem or the connection was aborted - " 
        			+ e.getMessage());
        } 
//        catch(Exception e){
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        	Log.e(TAG, e.getMessage());        	
//        }
        
        
    }    
	

}