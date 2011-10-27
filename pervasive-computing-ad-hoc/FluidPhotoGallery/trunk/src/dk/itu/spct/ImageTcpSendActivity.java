package dk.itu.spct;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import dk.itu.spct.server.TCPImageClient;

//public class ImageActivity extends Activity implements OnClickListener {


/*
 * client tcp
 * 
 */

//public class ImageTcpSendActivity extends Activity {
public class ImageTcpSendActivity extends Activity implements OnTouchListener, OnGestureListener {


	private static final String TAG = "Fluidgallery";
	private static final boolean D = true;

	private GestureDetector gestureScanner;
	private View v;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.imagesendtcpactivity);

		if (D)
			Log.d(TAG, "ImageActivity onCreate called");

		gestureScanner = new GestureDetector(this);
		
		// quick fix for button listener - couldn't get the more elegant solution bellow to work
		// could it be api level related ?
		final Button btnFetch = (Button) findViewById(R.id.send_image_to_tcp_server);
		// final TextView txtResult = (TextView)findViewById(R.id.content);

		
		
//		ImageView image = R.layout.imagesendtcpactivity.imageView1; 
		
		
		
		
//		ImageView image = (ImageView) findViewById(R.id.send_image_to_tcp_server);
		
//		ImageView imageView = findViewById(R.id.send_image_to_tcp_server);
		
//		View v = findViewById(R.layout.imagesendtcpactivity);
		
		v = findViewById(R.id.send_image_to_tcp_server_image);
//		View v = findViewById(R.id.textView1);
		
		v.setOnTouchListener(this);
		
		
		
		
		
//		OnTouchListener touchImage =  image.OnTouchListener;
		
//		
//		
//		TouchListener l ;
//		image.setOnTouchListener(l);
		
/*


MotionEvent.getPressure(), which will return a 
float between 0.0 for no pressure and 1.0 for maximum pressure. 

 */
		
		
		
		
		
		
		
		btnFetch.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				// do something
				
				
				//imageView.setImageResource(R.drawable.sample_0);
				
//				// get the image
//				R.drawable.tiny_12_k

//				final Bitmap bitmap = BitmapFactory.decodeResource(res, id);
				
//				final Bitmap bitmap = BitmapFactory.decodeResource(R.drawable, sample_0);
				
				
//				Bitmap bitmapImage = BitmapFactory.decodeResource(v.getResources(),
//                        R.drawable.tiny_12_k);
				
				
// bit map try out 1
				Bitmap bitmapImage = BitmapFactory.decodeResource(v.getResources(),
                        R.drawable.medium_145_k);
				TCPImageClient.send(bitmapImage);				
				
				
//					Edit: Here a version where the image gets downloaded.
//					
//					String name = c.getString(str_url);
//					URL url_value = new URL(name);
//					ImageView profile = (ImageView)v.findViewById(R.id.vdo_icon);
//					if (profile != null) {
//					Bitmap mIcon1 =
//					BitmapFactory.decodeStream(url_value.openConnection().getInputStream());
//					profile.setImageBitmap(mIcon1);
//					}
				
				
				
				
				

				
				if (D) Log.d(TAG, "ImageActivity onClick called");
				String message = "ImageActivity button hit - shold be send";

				
				// alternative image send try out -
//				TCPImageClient.send(v);

				

				
				
				
				
			}
		});

		
		
	}
	
	
	
	
	
	
    @Override
    public void onLongPress(MotionEvent e)
    {
//        Toast mToast =  Toast.makeText(getApplicationContext(), "Long Press", Toast.LENGTH_SHORT);
//        mToast.show();
        
        Log.d(TAG, "gesture onLongPress");        

        // even another hack couldn't get the scrool to work
        
		Bitmap bitmapImage = BitmapFactory.decodeResource(v.getResources(),
                R.drawable.medium_145_k);
		TCPImageClient.send(bitmapImage);		
        
        
        }
      @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY)
    {
    	  
//          Toast mToast =  Toast.makeText(getApplicationContext(), "On scroll", Toast.LENGTH_SHORT);
//          mToast.show();
    	
          Log.d(TAG, "gesture onScroll");
          
//     viewA.setText("-" + "SCROLL" + "-");
     return true;
    }
	
	
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		Log.d(TAG, "onTouch fired off");
		
		 return gestureScanner.onTouchEvent(event);
		
//		if (event.getAction() == MotionEvent.ACTION_DOWN) {
//		
//			Log.d(TAG, " MotionEvent.ACTION_DOWN");
//
//			
//		}
//		
//		if (event.getAction() == MotionEvent.ACTION_POINTER_DOWN) {
//			
//			Log.d(TAG, " event.getAction() == MotionEvent.ACTION_POINTER_DOWN");
//
//		}

		
		
//		if (event.getActionMasked() == MotionEvent.ACTION_POINTER_DOWN) {
//			Log.d(TAG, "event.getActionMasked() == MotionEvent.ACTION_POINTER_DOWN");
//		}
		
//		if (event.getAction() == MotionEvent.ACTION_DOWN) {
//
//			Log.d(TAG, " MotionEvent.ACTION_DOWN");
//			
//			//			// Getting the user sound settings
////			AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
////			float actualVolume = (float) audioManager
////					.getStreamVolume(AudioManager.STREAM_MUSIC);
////			float maxVolume = (float) audioManager
////					.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
////			float volume = actualVolume / maxVolume;
////			// Is the sound loaded already?
////			if (loaded) {
////				soundPool.play(soundID, volume, volume, 1, 0, 1f);
////				Log.e("Test", "Played sound");
////			}
//		}
//		return false;
	}






	@Override
	public boolean onDown(MotionEvent arg0) {
		// TODO Auto-generated method stub
		Log.d(TAG, "gesture onDown");
		
		return false;
	}


	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		// TODO Auto-generated method stub
		
		Log.d(TAG, "gesture onFling");
		return false;
	}


	@Override
	public void onShowPress(MotionEvent e) {
		// TODO Auto-generated method stub
		Log.d(TAG, "gesture onShowPress");
		
	}


	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		// TODO Auto-generated method stub
		
		Log.d(TAG, "gesture onSingleTapUp");
		return false;
	}
	

//    public boolean onTouch(View view, MotionEvent event) {
//        // if(event.getAction() != MotionEvent.ACTION_DOWN)
//        // return super.onTouchEvent(event);
////        Point point = new Point();
////        point.x = event.getX();
////        point.y = event.getY();
////        points.add(point);
////        invalidate();
////        Log.d(TAG, "point: " + point);
//        Log.d(TAG, "onTouch fired off");
//        return true;
//    }
	
	
//	public void onTouch(View, MotionEvent) {
//		
//	}
	
	// note: didn't work - no hit 
	// @Override
	// public void onClick(View v) {
	// // TODO Auto-generated method stub
	//
	// if (D) Log.d(TAG, "ImageActivity onClick called");
	//
	// if (D) Log.d(TAG, "v.getId(): " + v.getId());
	//
	// switch (v.getId()) {
	//
	//
	//
	// case R.id.send_image_to_tcp_server: {
	//
	// if (D) Log.d(TAG, "case R.id.send_image_to_tcp_server");
	//
	// // String message = "ImageActivity button hit - shold be send";
	// // TCPClient.send(message);
	//
	// }
	// // Start whatever it is the start button starts...
	// // ...
	// // case R.id.some_other_button:
	//
	// }
	//
	// }

}