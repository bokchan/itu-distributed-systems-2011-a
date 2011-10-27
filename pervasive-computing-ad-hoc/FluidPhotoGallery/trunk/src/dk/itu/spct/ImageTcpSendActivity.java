package dk.itu.spct;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import dk.itu.spct.server.TCPImageClient;

//public class ImageActivity extends Activity implements OnClickListener {

public class ImageTcpSendActivity extends Activity {

	private static final String TAG = "Fluidgallery";
	private static final boolean D = true;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.imageactivity);

		if (D)
			Log.d(TAG, "ImageActivity onCreate called");

		// quick fix for button listener - couldn't get the more elegant solution bellow to work
		// could it be api level related ?
		final Button btnFetch = (Button) findViewById(R.id.send_image_to_tcp_server);
		// final TextView txtResult = (TextView)findViewById(R.id.content);

		btnFetch.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				// do something
				
				
				//imageView.setImageResource(R.drawable.sample_0);
				
//				// get the image
//				R.drawable.tiny_12_k

//				final Bitmap bitmap = BitmapFactory.decodeResource(res, id);
				
//				final Bitmap bitmap = BitmapFactory.decodeResource(R.drawable, sample_0);
				
				
				Bitmap bitmapImage = BitmapFactory.decodeResource(v.getResources(),
                        R.drawable.tiny_12_k);
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
				TCPImageClient.send(bitmapImage);

				
				
				
			}
		});

	}

	
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