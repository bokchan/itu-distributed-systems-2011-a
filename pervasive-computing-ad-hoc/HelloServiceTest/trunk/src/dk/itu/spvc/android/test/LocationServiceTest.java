/*
 * based upon ApiDemos/tests/src/com/example/android/apis/app/NotePadTest.java
 */
package dk.itu.spvc.android.test;

import junit.framework.Assert;
import android.content.Intent;
import android.os.IBinder;
import android.test.ServiceTestCase;
import android.test.suitebuilder.annotation.MediumTest;
import android.test.suitebuilder.annotation.SmallTest;
import android.util.Log;
import dk.itu.spvc.android.locationservice.LocationService;

/**
 * This is a simple framework for a test of a Service.  See {@link android.test.ServiceTestCase
 * ServiceTestCase} for more information on how to write and extend service tests.
 * 
 * To run this test, you can type:
 * adb shell am instrument -w \
 *   -e class com.example.android.apis.app.LocationServiceTest \
 *   com.example.android.apis.tests/android.test.InstrumentationTestRunner
 */
public class LocationServiceTest extends ServiceTestCase<LocationService> {

	private static final String TAG = "LocationServiceTest";
	
    public LocationServiceTest() {
      super(LocationService.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    /**
     * The name 'test preconditions' is a convention to signal that if this
     * test doesn't pass, the test case was not set up properly and it might
     * explain any and all failures in other tests.  This is not guaranteed
     * to run before other tests, as junit uses reflection to find the tests.
     */
    @SmallTest
    public void testPreconditions() {
    }
    
    /**
     * Test basic startup/shutdown of Service
     */
    @SmallTest
    public void testStartable() {
        Intent startIntent = new Intent();
        startIntent.setClass(getContext(), LocationService.class);
        startService(startIntent); 
    }
    
    /**
     * Test binding to service
     */
    @MediumTest
    public void testBindable() {
        Intent startIntent = new Intent();
        startIntent.setClass(getContext(), LocationService.class);
        IBinder service = bindService(startIntent);

        // is service still alive?
        
        Assert.assertTrue(service.isBinderAlive());
        
        Assert.assertTrue(service.pingBinder());
        
// hmm is it possible to run logcat from a test - feels odd but tried with the old fahioned Sysmet...print and that didn't work        
//        
//        Log.d(TAG, "service.pingBinder(): " + service.pingBinder());        
               
    }
    
//    @MediumTest
//    public void testInteractionWithService() {
//    	
//    
//    
//    }
    
    
    
}
