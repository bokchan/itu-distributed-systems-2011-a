/*
 * based upon ApiDemos/tests/src/com/example/android/apis/app/LocalServiceTest.java
 */
package dk.itu.spvc.android.test;

import junit.framework.Assert;
import android.content.Intent;
import android.os.IBinder;
import android.test.ServiceTestCase;
import android.test.suitebuilder.annotation.MediumTest;
import android.test.suitebuilder.annotation.SmallTest;
import dk.itu.spvc.android.apidemoexample.LocalService;

/**
 * This is a simple framework for a test of a Service.  See {@link android.test.ServiceTestCase
 * ServiceTestCase} for more information on how to write and extend service tests.
 * 
 * To run this test, you can type:
 * adb shell am instrument -w \
 *   -e class com.example.android.apis.app.LocalServiceTest \
 *   com.example.android.apis.tests/android.test.InstrumentationTestRunner
 */
public class LocalServiceTest extends ServiceTestCase<LocalService> {

    public LocalServiceTest() {
      super(LocalService.class);
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
        startIntent.setClass(getContext(), LocalService.class);
        startService(startIntent); 
    }
    
    /**
     * Test binding to service
     */
    @MediumTest
    public void testBindable() {
        Intent startIntent = new Intent();
        startIntent.setClass(getContext(), LocalService.class);
        IBinder service = bindService(startIntent);

        //is service alive?
        Assert.assertTrue(service.isBinderAlive());
        
        Assert.assertTrue(service.pingBinder());
       
    }
    
}
