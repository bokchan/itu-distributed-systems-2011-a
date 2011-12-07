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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import dk.itu.noxdroid.database.NoxDroidDbAdapter;

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

		// note: based upon http://goo.gl/y5m4u - also take a look at the *real*
		// api
		PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(this);
		webservice_url = prefs.getString(
				getString(dk.itu.noxdroid.R.string.WEBSERVICE_URL),
				"http://10.0.1.7:8888/add_track");

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

	}

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
		postForm(trackUID);

	}

	public void postForm(String trackUID) {

		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost(webservice_url);

		/*
		 * 
		 * We have to build a json similar to
		 * 
		 * { "nox_droid_id" : "test_user_id", "nox_droid_user_name" :
		 * "Default user name", "track_id" :
		 * "eeb445dc-d2eb-494f-a4af-78c20b5d181c", "track_start_time" :
		 * "2011-12-04 09:10:04", "track_end_time" : "2011-12-04 09:20:04",
		 * "locations" : [ {"latitude" : 55.659919, "longitude" : 12.591190,
		 * "time_stamp" : "2011-12-04 09:12:04", "provider" : "gps"},
		 * {"latitude" : 55.659919, "longitude" : 12.691190, "time_stamp" :
		 * "2011-12-04 09:12:05", "provider" : "skyhook"} ], "nox" : [ {"nox" :
		 * 55.65, "temperature" : 0.0, "time_stamp" : "2011-12-04 09:12:04"},
		 * {"nox" : 65.65, "temperature" : 0.0, "time_stamp" :
		 * "2011-12-04 09:13:04"} ] }
		 * 
		 * 
		 * - the json lists in locations/nox can be set to be empty but all keys
		 * are required - we send it similar to a html form (for reasons of
		 * simplicity - other wise make a direct post/put)
		 */
		
		
		//
		// test json string - usefull for a simple try out
		//
		// String trackStaticJSONString = "{\"nox_droid_id\" : \"test_user_id\", \"nox_droid_user_name\" : \"Default user name\", \"track_id\" : \"eeb445dc-d2eb-494f-a4af-78c20b5d181c\", \"track_start_time\" : \"2011-12-04 09:10:04\", \"track_end_time\" : \"2011-12-04 09:20:04\", \"locations\" : [ {\"latitude\" : 55.659919, \"longitude\" : 12.591190, \"time_stamp\" : \"2011-12-04 09:12:04\", \"provider\" : \"gps\"},  {\"latitude\" : 55.659919, \"longitude\" : 12.691190, \"time_stamp\" : \"2011-12-04 09:12:05\", \"provider\" : \"skyhook\"} ], \"nox\" : [ {\"nox\" : 55.65, \"temperature\" : 0.0, \"time_stamp\" : \"2011-12-04 09:12:04\"},  {\"nox\" : 65.65, \"temperature\" : 0.0, \"time_stamp\" : \"2011-12-04 09:13:04\"} ]}";

		JSONObject trackAsJSON = new JSONObject();
		try {
			trackAsJSON.put("nox_droid_id", userId);
			trackAsJSON.put("nox_droid_user_name", userName);

			// TODO: get from database
			trackAsJSON.put("track_id", trackUID);
			trackAsJSON.put("track_start_time", "2011-12-04 09:10:04");
			trackAsJSON.put("track_end_time", "2011-12-04 09:20:04");

			
			JSONArray jsonListLocationsAsJSONArray = getLocations(trackUID);
			trackAsJSON.put("locations", jsonListLocationsAsJSONArray);

//			List<JSONObject> jsonListNox = new ArrayList<JSONObject>();
			JSONArray jsonListNoxAsJSONArray = getNox(trackUID);
			trackAsJSON.put("nox", jsonListNoxAsJSONArray);

			Log.d(TAG, "trackAsJSON: " + trackAsJSON.toString(4));

		} catch (JSONException e1) {
			Log.e(TAG, "JSONObject build/puts failed: " + e1.getMessage());
		}

		try {
			// Add data to the form
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(8);
			nameValuePairs.add(new BasicNameValuePair("track_json", trackAsJSON
					.toString()));

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
				Log.d(TAG, "status code: " + status.getStatusCode());

				Toast.makeText(NoxDroidPostActivity.this,
						"Post to cloud was successful", Toast.LENGTH_SHORT)
						.show();

				// TODO:
				// set track flag in database to be sync'ed

			} catch (Exception e) {
				Log.e(TAG,
						"problems with posting to the cloud server - the respone failed: "
								+ e.getMessage());
			}

		} catch (ClientProtocolException e) {
			Log.e(TAG,
					"ClientProtocolException	in case of an http protocol error - "
							+ e.getMessage());
		} catch (IllegalStateException e) {
			Log.e(TAG, "IllegalStateException - " + e.getMessage());
		} catch (IOException e) {
			Log.e(TAG,
					"IOException	in case of a problem or the connection was aborted - "
							+ e.getMessage());
		}

	}
	
	/**
	 * Create json array of locations from database query
	 *  
	 * @param trackUID
	 * @return
	 */
	private JSONArray getLocations(String trackUID) {

		// first build list json objects
		List<JSONObject> jsonList = new ArrayList<JSONObject>();
		Cursor mCursor = mDbHelper.fetchLocations(trackUID);

		double latitude;
		double longitude;
		String timeStamp;
		String provider;
		int size = mCursor.getCount();

		for (int i = 0; i < size; i++) {
			// We build a new json object for each iteration
			JSONObject jsonObj = new JSONObject();
			latitude = mCursor.getDouble(0);
			longitude = mCursor.getDouble(1);
			timeStamp = mCursor.getString(2);
			provider = mCursor.getString(3);

			try {
				jsonObj.put("latitude", latitude);
				jsonObj.put("longitude", longitude);
				jsonObj.put("time_stamp", timeStamp);
				jsonObj.put("provider", provider);

				// add json object to the final json locations list
				jsonList.add(jsonObj);

			} catch (JSONException e) {
				Log.e(TAG, "JSONObject put location failed: " + e.getMessage());
			}

			mCursor.moveToNext();
		}

		// remember to close the cursor
		// otherwise it raises: http://pastebin.com/P7AsncRc
		// read more http://goo.gl/gC8fa | api
		mCursor.close();
		
		// Secondly turn list into an json array
		return new JSONArray(jsonList);

	}

	
	
	/**
	 * Create json array of locations from database query
	 *  
	 * @param trackUID
	 * @return
	 */
	private JSONArray getNox(String trackUID) {

		// first build list json objects
		List<JSONObject> jsonList = new ArrayList<JSONObject>();
		Cursor mCursor = mDbHelper.fetchNox(trackUID);

		//nox, temperature, time_stamp
		double nox;
		double temperature;
		String timeStamp;
		int size = mCursor.getCount();

		for (int i = 0; i < size; i++) {
			// We build a new json object for each iteration
			JSONObject jsonObj = new JSONObject();
			nox = mCursor.getDouble(0);
			temperature = mCursor.getDouble(1);
			timeStamp = mCursor.getString(2);

			try {
				jsonObj.put("nox", nox);
				jsonObj.put("temperature", temperature);
				jsonObj.put("time_stamp", timeStamp);

				// add json object to the final json locations list
				jsonList.add(jsonObj);

			} catch (JSONException e) {
				Log.e(TAG, "JSONObject put location failed: " + e.getMessage());
			}

			mCursor.moveToNext();
		}

		// remember to close the cursor
		// otherwise it raises: http://pastebin.com/P7AsncRc
		// read more http://goo.gl/gC8fa | api
		mCursor.close();
		
		// Secondly turn list into an json array
		return new JSONArray(jsonList);

	}	
	
	
	
	
	
}