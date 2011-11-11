package dk.itu.spvc.android.test;

import android.test.ActivityInstrumentationTestCase2;
import android.widget.Button;
import android.widget.Spinner;
import dk.itu.spvc.android.Main;

/**
 * 
 * Based upon
 * http://developer.android.com/resources/tutorials/testing/activity_test.html
 * 
 * 
 * @author pelle
 * 
 */
public class MainTest extends ActivityInstrumentationTestCase2<Main> {

	private Main mActivity;
	private Button mButton;

	// private SpinnerAdapter mPlanetData;

	/**
	 * Constructor
	 */
	public MainTest() {
		super("dk.itu.spvc.android", Main.class);
	} // end of MainTest constructor definition

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		setActivityInitialTouchMode(false);

		mActivity = getActivity();
		mButton = (Button) mActivity
				.findViewById(dk.itu.spvc.android.R.id.start);

	} // end of setUp() method definition

	public void testPreConditions() {

		// TODO: had expected to do something like this:
		// assertTrue(mButton.getOnClickListener() != null);
		// to ensure having a OnClickListener
		// but it won't work we might not have the button but the view...
		assertTrue(mButton != null);

	} // end of testPreConditions() method definition

	
	 public void testMainUI() {

		    mActivity.runOnUiThread(
		      new Runnable() {
		        public void run() {
		          mButton.requestFocus();
		          // TODO: figure out som button on clcick etc... 
//		          mButton.onKeyDown(keyCode, event);
//		          mSpinner.setSelection(INITIAL_POSITION);
		        } // end of run() method definition
		      } // end of anonymous Runnable object instantiation
		    ); // end of invocation of runOnUiThread
	 }
	
}
