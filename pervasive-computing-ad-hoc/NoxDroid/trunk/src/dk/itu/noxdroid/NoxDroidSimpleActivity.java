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

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

public class NoxDroidSimpleActivity extends Activity {

	private String TAG = this.getClass().getSimpleName();
	


	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_simple);
	}

	
	/*
	 * Post Static To Cloud
	 * 
	 * Should normally not be done from an activity (UI)
	 * 
	 */
	public void postStaticToCloud(View view) {
	
		// just for test
        String trackUID = "f3d282f3-6f1b-4a5c-bfa3-a0fc33cfc1a5-test";
		
		// post to cloud service
		postForm(trackUID);

	}

	
    public void postForm(String trackUID){
        
    	String url = getString(R.string.service_default_url_post);
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost(url);

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
        
        try {
            // Add your data to the form
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(8);
            
            // TODO: sensor_id will change to noxdroid_id
            nameValuePairs.add(new BasicNameValuePair("sensor_id", "noxdroid_test"));
            //sensor_user_name
            nameValuePairs.add(new BasicNameValuePair("sensor_user_name", "noxdroid_user_name"));

              
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
            	            	
    			Toast.makeText(NoxDroidSimpleActivity.this, "Post to cloud was successful", Toast.LENGTH_SHORT).show();
            	
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