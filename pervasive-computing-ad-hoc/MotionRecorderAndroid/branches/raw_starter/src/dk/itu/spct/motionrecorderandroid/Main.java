package dk.itu.spct.motionrecorderandroid;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Toast;

public class Main extends Activity {

	private static final String TAG = "MotionRecorderMain";

	public static final String MOTION_TYPE = "motionType";

	public static final String MOTION_TYPE_WALKING = "Walking";
	public static final String MOTION_TYPE_SITTING = "Sitting";
	public static final String MOTION_TYPE_STAIRS = "Stairs";

	private Button buttonStartRecording, buttonStopRecording; // , buttonPostRecordingToWebservice;
	
	
	private Intent intent;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		buttonStartRecording = (Button) findViewById(R.id.buttonStartRecording);
		buttonStopRecording = (Button) findViewById(R.id.buttonStopRecording);
		
		
		// TODO: can this be set in the main.xml as default value ?
		// does this also be loaded then on orientation shifts...
		buttonStopRecording.setEnabled(false);
		

	}

//	/**
//	 * Start service w. motion type set
//	 * 
//	 * @param motion
//	 */
//	private void startService(CharSequence motion) {
//		Intent intent = new Intent(Main.this, MotionRecorderService.class);
//		intent.putExtra(MOTION_TYPE, motion);
//		startService(intent);
//	}

	
	public void stopRecording(View view) {
		
		Log.d(TAG, "buttonStopRecording clicked");

//		Intent intent = new Intent(Main.this,
//				MotionRecorderService.class);
		stopService(intent);
		
		
		// make stop button click able again
		buttonStartRecording.setEnabled(true);
		buttonStopRecording.setEnabled(false);

		
		
		
	}
	
	
	/**
	 * 
	 * Start the dialog
	 * 
	 * @param view
	 */
	public void startRecordingDialog(View view) {

		Log.d(TAG, "buttonStartRecording/startRecordingDialog clicked");
		
		
		showDialog(10);
	}

	@Override
	protected Dialog onCreateDialog(int id) {

		switch (id) {
		case 10:
			// final CharSequence[]
			final String[] motions = { MOTION_TYPE_SITTING,
					MOTION_TYPE_WALKING, MOTION_TYPE_STAIRS };

			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("Pick a motion");
			builder.setSingleChoiceItems(motions, -1,
					new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface dialog, int item) {
							// Toast.makeText(getApplicationContext(),
							// items[item], Toast.LENGTH_SHORT).show();

							// close doalog box
							dialog.cancel();
							
							
							buttonStartRecording.setEnabled(false);
							buttonStopRecording.setEnabled(true);
							

							// start the motion recording service
//							startService(items[item]);
							
							intent = new Intent(Main.this, MotionRecorderService.class);
							intent.putExtra(MOTION_TYPE, motions[item]);
							startService(intent);

							Toast.makeText(getApplicationContext(), "Motion recording started", Toast.LENGTH_SHORT).show();
							
							
						}
					});
			AlertDialog dialog = builder.create();
			dialog.show();
		}
		return super.onCreateDialog(id);

	}

}