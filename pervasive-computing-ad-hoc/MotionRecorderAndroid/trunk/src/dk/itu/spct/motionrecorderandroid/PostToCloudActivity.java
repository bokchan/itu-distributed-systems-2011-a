package dk.itu.spct.motionrecorderandroid;

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
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.ProgressBar;

public class PostToCloudActivity extends Activity {

	private static final String TAG = "PostToCloudActivity";
	private static final int DIALOG_YES_NO_MESSAGE = 1;
	private DbAdapter mDbHelper;
	private String motionUUID;	
	private Handler handler;
	private ProgressBar progress;

	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.posttocloud);
		
		// get MOTION_UUID from Intent from service 
		Intent previousActivity = getIntent();
		motionUUID = (String) previousActivity.getExtras().get(dk.itu.spct.motionrecorderandroid.Main.MOTION_UUID);		
		
		// start up dialog about yes/no to upload
		showDialog(DIALOG_YES_NO_MESSAGE);
		
		// for the progress bar
		progress = (ProgressBar) findViewById(R.id.progressBar1);
		handler = new Handler();
		
	}

	
	/*
	 * 
	 * yes/no dialog based upon:
	 * android-8/ApiDemos/src/com/example/android/apis/app/AlertDialogSamples.java
	 * 
	 * (non-Javadoc)
	 * @see android.app.Activity#onCreateDialog(int)
	 */
	@Override
	protected Dialog onCreateDialog(int id) {

		switch (id) {
		
        case DIALOG_YES_NO_MESSAGE:
            return new AlertDialog.Builder(this)
                .setIcon(R.drawable.alert_dialog_icon)
                .setTitle(R.string.alert_dialog_two_buttons_title)
                .setPositiveButton(R.string.alert_dialog_ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                        /* User clicked OK so do some stuff */
                    	startProgress();
                    }
                })
                .setNegativeButton(R.string.alert_dialog_cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                    	// back to the main activity
                    	startActivity(new Intent(PostToCloudActivity.this, Main.class));
                    	
                        /* User clicked Cancel so do some stuff */
                    }
                })
                .create();
         // end case DIALOG_YES_NO_MESSAGE
			
			
		}
		return null;
//		previously returned 
//		return super.onCreateDialog(id);
//		that crashed app use return null based upon AlertDialogSamples.java		

	}

	
	/**
	 * 
	 * Start progress bar and upload/post motion recording to service 
	 * 
	 * We are using the handle approach based upon the ...
	 * The right(TM) approach would probably have been to set up a async task 
	 * 
	 */
	public void startProgress() {
		// Do something long
		Runnable runnable = new Runnable() {
			@Override
			public void run() {

				String motionType;
				String x;
				String y;
				String z;
				String timeStamp;
				
				// Set up data base 
		        mDbHelper = new DbAdapter(PostToCloudActivity.this);
		        mDbHelper.open();
		        Cursor mCursor = mDbHelper.fetchMotion(motionUUID);
		        mDbHelper.close();
		        
		        int size = mCursor.getCount();
		        Log.d(TAG, "size: " + size);
		        
		        // set to upper size of the progress bar
		        progress.setMax(size-1);
		        
		        // motions.motion_type, motions.uuid, accelerometer.x, accelerometer.y, accelerometer.z
		        String[] columnNames = mCursor.getColumnNames();
		        Log.d(TAG, "columnNames: " + columnNames);

				// TODO:
		        // not 100% sure if it should be
		        // i < size or i <= size
				for (int i = 0; i < size; i++) {
					final int value = i;

					Log.d(TAG, "mCursor: " + mCursor);

					motionType = mCursor.getString(0);
					x = mCursor.getString(2);
					y = mCursor.getString(3);
					z = mCursor.getString(4);
					timeStamp = mCursor.getString(5);

					Log.d(TAG, motionType + " " + x + " " + y + " " + z + " " + timeStamp);
				

					// post to cloud service
					postForm(motionType, x, y, z, timeStamp, motionUUID);
					
//					if postings to cloud get over heated then sleep a bit					
//					try {
//						// post to cloud service
//						postForm(motionType, x, y, z, timeStamp, motionUUID);
//						
//						// remainder from the progress bar setup just used for consuming time
//					    // we keep it to ensure the cloud dosn't get to many posts at once (clearly a hack - just remove when/if implemented as one 'post')
//						Thread.sleep(5);
//					} catch (InterruptedException e) {
//						e.printStackTrace();
//					}
					
					// move to next item
					mCursor.moveToNext();
					
					// 
					handler.post(new Runnable() {
						@Override
						public void run() {
							progress.setProgress(value);
						}
					});
				}
				

            	// back to the main activity
            	startActivity(new Intent(PostToCloudActivity.this, Main.class));				
			}
		};
		new Thread(runnable).start();
	}
	
	
	
	/**
	 * 
	 * 
	 * 
	 * @param motionType
	 * @param x
	 * @param y
	 * @param z
	 * @param timeStamp
	 */
    public void postForm(String motionType, String x, String y, String z, String timeStamp, String motionUUID){
        
    	String url = getString(R.string.default_url_post);
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost(url);

        /*
        we are miming this form:
        
        http://localhost:8888/activityrecording.jsp?activityName=default
        http://localhost:8888/add_recording
        
    <form action="/add_recording" method="post">

      <div>
          <label>Activity Type</label>
          <select name="activityType">
              <option value="Running">Running</option>
              <option value="Walking">Walking</option>
              <option value="Stairs">Stairs</option>
              <option value="None">None</option>
          </select>
      </div>
      
      <div><label>x</label><input type="text" name="activityX" size="6" /></div>
      <div><label>y</label><input type="text" name="activityY" size="6" /></div>
      <div><label>z</label><input type="text" name="activityZ" size="6" /></div>      
      <div><label>time (unix time sample)</label><input type="text" name="activityTime" size="8" /></div>      
      <div><label>username</label><input type="text" name="activityUserName" size="20" default="ttw" /></div>  
	  
      <div>
          <input type="submit" value="Post Activity Node" />
          <input type="hidden" name="activityName" value="<%= activityName %>"/>
      </div>
		*/
        
        try {
            // Add your data to the form
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(8);
            nameValuePairs.add(new BasicNameValuePair("activityType", motionType));
            nameValuePairs.add(new BasicNameValuePair("activityX", x));
            nameValuePairs.add(new BasicNameValuePair("activityY", y));
            nameValuePairs.add(new BasicNameValuePair("activityZ", z));
            
           
            nameValuePairs.add(new BasicNameValuePair("activityTime", timeStamp));
            // TODO: get android id etc...
            nameValuePairs.add(new BasicNameValuePair("activityUserName", "testuser"));
            // TODO: change to uuid
            nameValuePairs.add(new BasicNameValuePair("activityName", motionUUID));
            
            nameValuePairs.add(new BasicNameValuePair("activityDevice", "android"));
            
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            // Execute HTTP Post Request
            HttpResponse response = httpclient.execute(httppost);

            
            try {
				// if the response is a html page then print out with the helper
				// String the_string_response = HttpHelper.request(response);
            	// we just have a short on - usually: HTTP/1.1 200 OK
            	StatusLine status = response.getStatusLine();
            	Log.d(TAG, "status code: " + status.getStatusCode() );
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