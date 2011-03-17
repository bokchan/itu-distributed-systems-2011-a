package dk.itu.smds.android.bluetweet;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import dk.itu.android.bluetooth.BluetoothAdapter;

public class BlueTweetActivity extends Activity {
	static String TAG = BlueTweetActivity.class.getSimpleName();
	
	static int REQUEST_ENABLE_BT = 0;
	static int REQUEST_ENABLE_DISCOVERABLE = 1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		BluetoothAdapter.SetContext(this);
		requestWindowFeature( Window.FEATURE_NO_TITLE );
		
		setContentView( R.layout.main );
		ListView tweets = (ListView)findViewById(R.id.TweetList);
		
		String[] proj = new String[]{
			BlueTweet.Tweet._ID,
			BlueTweet.Tweet.MSG,
			BlueTweet.Tweet.NAME,
			BlueTweet.Tweet.TIMESTAMP
		};
		
		Cursor c = managedQuery(BlueTweet.Tweet.CONTENT_URI, proj, null, null, BlueTweet.Tweet.DEFAULT_SORT_ORDER);
		
		SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, R.layout.tweet_item, c, 
				new String[]{ BlueTweet.Tweet.MSG,BlueTweet.Tweet.NAME,BlueTweet.Tweet.TIMESTAMP }, 
				new int[]{ R.id.TweetMessage,R.id.TweetName,R.id.TweetTimestamp });
		tweets.setAdapter(adapter);
		
		BluetoothAdapter bta = BluetoothAdapter.getDefaultAdapter();
		if(bta == null) {
			Log.w(TAG,"uops..no Bluetooth adapter found!");
			finish();
			return;
		}
		if(!bta.isEnabled()) {
    		Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
    		startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
		} else {
			startDiscoverable();
		}
	}
	
	private void startDiscoverable() {
		Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
		startActivityForResult(enableBtIntent, REQUEST_ENABLE_DISCOVERABLE);
	}
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if( REQUEST_ENABLE_BT == requestCode ) {
			if( resultCode == Activity.RESULT_OK ) {
				startDiscoverable();
			} else {
				Log.e(TAG, "uops, user don't want to enable bluetooth!");
			}
		} else if( REQUEST_ENABLE_DISCOVERABLE == requestCode ) {
			startService(new Intent(AbstractBlueTweetService.ACTION));
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	
	@Override
	protected void onStop() {
		super.onStop();
		
		stopService(new Intent(AbstractBlueTweetService.ACTION));
	}
	public void quitBlueTweet( View view ) {
		stopService(new Intent(AbstractBlueTweetService.ACTION));
		this.setResult(Activity.RESULT_OK);
		this.finish();
	}
	public void updateTweet( View view ) {
		EditText et = (EditText)findViewById(R.id.TweetEditText);
		String status = et.getText().toString();
		long ts = System.currentTimeMillis();
		String btaddr = BluetoothAdapter.getDefaultAdapter().getAddress();
		String name = BluetoothAdapter.getDefaultAdapter().getName();
//		if(name==null||name.length()==0||name.equals("local")) {
//			name = btaddr;
//		}
		createTweet(btaddr,name,status,ts);
	}
	private void createTweet( String btaddr, String name, String status, long ts ) {
		ContentValues cv = new ContentValues();
		cv.put(BlueTweet.Tweet.BT_ADDR, btaddr);
		cv.put(BlueTweet.Tweet.MSG, status);
		cv.put(BlueTweet.Tweet.NAME, name);
		cv.put(BlueTweet.Tweet.TIMESTAMP, ts);
		
		Uri newUri = getContentResolver().insert(BlueTweet.Tweet.CONTENT_URI, cv);
		String msg = null;
		if(newUri==null) {
			msg = "cannot create tweet!";
		} else {
			msg = "tweet uri: " + newUri;
		}
		Log.i(TAG, msg);
	}
}
