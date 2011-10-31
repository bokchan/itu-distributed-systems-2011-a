package dk.itu.spct;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class SimpleBarcodeResolverActivity extends Activity {

	// Debugging
	private static final String TAG = "SimpleBarcodeResolver";
	private static final boolean D = true;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		final Button btnFetch = (Button) findViewById(R.id.button);
		btnFetch.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {

				Intent intent = new Intent("com.google.zxing.client.android.SCAN");								
				intent.setPackage("com.google.zxing.client.android");

				// note: this part is essential - look up more examples here http://goo.gl/GYzbE
				intent.putExtra("SCAN_MODE", "ONE_D_MODE"); // Decode only 1D barcodes.

				// could also have been 
				// intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
				// for qr codes
				
				startActivityForResult(intent, 0);
			}
		});

	}

	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		
		if (requestCode == 0) {
			if (resultCode == RESULT_OK) {
				
				
				String contents = intent.getStringExtra("SCAN_RESULT");
				String format = intent.getStringExtra("SCAN_RESULT_FORMAT");
				// Handle successful scan
				
				if (D)
					Log.d(TAG, "scan contents: " + contents.toString());

				if (D)
					Log.d(TAG, "scan format: " + format.toString());

				try {
					
					// probably make contents.toString() more fine graded ? has to be a isbn number etc...
					// isbn can either 10 or 13 length
					// || format.toString().equals("ISBN") 
					if (format.toString().equals("EAN_13") || format.toString().equals("EAN_11")) {
					
						String uri = "http://www.lookupbyisbn.com/Search/Book/" + contents.toString() + "/1";
						
						Log.d(TAG, "uri: " + uri);
						
					Intent openUriInBrowser = new Intent(Intent.ACTION_VIEW,
							Uri.parse(uri));
					
					
					startActivity(openUriInBrowser);
					} else {
						Toast.makeText(SimpleBarcodeResolverActivity.this,
								"Error: it was not a qr code", Toast.LENGTH_LONG);						
					}
					
				} catch (Exception e) {
					Toast.makeText(SimpleBarcodeResolverActivity.this,
							"Error: " + e, Toast.LENGTH_LONG);
					Log.e(TAG, e.toString());
				}

			} else if (resultCode == RESULT_CANCELED) {
				// Handle cancel

				Toast.makeText(SimpleBarcodeResolverActivity.this,
						"Bar code scan was unfortunately cancled please try again", Toast.LENGTH_LONG);				
				
				if (D)
					Log.d(TAG,
							"scan was not a succes handle result is canceled");

			}
		}
	}

}