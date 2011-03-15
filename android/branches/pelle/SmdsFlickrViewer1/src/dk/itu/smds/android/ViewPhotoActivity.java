package dk.itu.smds.android;

import java.net.HttpURLConnection;
import java.net.URL;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

public class ViewPhotoActivity extends Activity implements View.OnClickListener {
	
	String photoUri;
	String pageUri;
	static final int DIALOG_DOWNLOADING = 0;
	
	
//	public void onClick( android.view.View view ) {
//		Intent intent = new Intent(android.content.Intent.ACTION_VIEW,Uri.parse(pageUri));
//		startActivity(intent);
//	}

	
	// intent can be understood as message
	public void onClick(View view) {
		Intent intent = new Intent(android.content.Intent.ACTION_VIEW,Uri.parse(pageUri));
		startActivity(intent);		
	}
	
	
	
//	@Override - hwy shouldn't it be here 
	protected void onCreate(Bundle savedInstanceState) {
        // important android cnvention 
		super.onCreate(savedInstanceState);

        // custom 
        setContentView( R.layout.photo );
        
        
        Intent startIntent = getIntent();
        photoUri = startIntent.getStringExtra(ViewThumbsActivity.EXTRA_PHOTO_URI);
        pageUri = startIntent.getStringExtra(ViewThumbsActivity.EXTRA_PHOTO_PAGE_URI);
        
        new PhotoDownloadAsyncTask().execute(photoUri);
        
        
    }
	
	@Override
	protected Dialog onCreateDialog(int id) {
		if(DIALOG_DOWNLOADING == id) {
		    return ProgressDialog.show(this, "", "Downloading photo, please wait...", true);
		}
		return super.onCreateDialog(id);
	}
	
	// download image asynchronously
	class PhotoDownloadAsyncTask extends AsyncTask<String,Void,Bitmap> {
	   
		@Override
		protected Bitmap doInBackground(String... params) {
	    	
	    	Bitmap out = null;
	    	
	    	try {
	    	    // like for the thumbnails, download the image in a Bitmap object
	    	    URL url = new URL( params[0] );
	    	    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
	    	    out = BitmapFactory.decodeStream(urlConnection.getInputStream());
	    	} catch(Exception e) {
	    	    Log.e("ViewPhotoActivity", "can't download the photo!", e);
	    	}
	    	
	        return out;
	    }

		@Override
		protected void onPreExecute() {
			// while search, show a dialog with infinite progress
			showDialog(DIALOG_DOWNLOADING);
		}
		
		
//		@Override - some java versions dont like this one
//		protected void onPostExecute(List<PhotoInfo> result) {
		protected void onPostExecute(Bitmap result){

			// dismiss the dialog
			dismissDialog(DIALOG_DOWNLOADING);
			if(result == null) {
			    // an error occurred
			    Toast.makeText(ViewPhotoActivity.this, "Can't download the photo!", 10);
			} else {
			    // set the photo
			    setPhoto(result);
			}
			
			//			if(result==null) {
//				// I guess show some dialog to the user
//				Toast.makeText(ViewThumbsActivity.this, "Sorry, an error occurred!", 10);
//			}
		}
		
	}


	protected void setPhoto(Bitmap bitmap) {
		//	HorizontalScrollView parentView = (HorizontalScrollView)findViewById( R.id.PhotoScrollView );			
		// another approach suggested: 
		// Both ScrollView and HorizontalScrollView (in our case) inherit from ViewGroup, so we can safely write:

		ViewGroup parentView = (ViewGroup)findViewById( R.id.PhotoScrollView );
		
		// In this way we are kind-of “future-proofing” our application.

		// create the image button
		ImageButton img = new ImageButton(this);
		img.setImageBitmap(bitmap);
		img.setOnClickListener(this);

		// add the image button to the parent group
		parentView.addView(img, bitmap.getWidth(), bitmap.getHeight());
		
	}


	
}
