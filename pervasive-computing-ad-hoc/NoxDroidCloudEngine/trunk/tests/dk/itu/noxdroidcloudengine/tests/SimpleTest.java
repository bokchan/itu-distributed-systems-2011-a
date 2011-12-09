package dk.itu.noxdroidcloudengine.tests;

import static org.junit.Assert.assertEquals;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.SimpleTimeZone;

import org.junit.Test;

/*
 * Based upon
 * 
 * http://code.google.com/appengine/docs/java/tools/localunittesting.html#Introducing_App_Engine_Utilities
 * 
 */
public class SimpleTest {
    @Test
    public void testAddition() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void testDateTime() {

    	// from book Developing the Application | 43
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSSSSS"); 
        fmt.setTimeZone(new SimpleTimeZone(0, "")); 
        
        
        System.out.println(fmt.format(new Date()));
        
    	
    	//        
    	String timeStamp = "2011-12-04 09:12:04";
    	
//    	Date date = new Date(timeStamp);
    	
    	
		// create cet time
		Date time = new Date();
		System.out.println(time);
		
		//create unix time
		long unixTime = System.currentTimeMillis() / 1000L;
		System.out.println(unixTime);
		
		
		
		

    	
//    	
//    	assertEquals(4, 2 + 2);
    }

}









//
//import java.io.IOException;
//import javax.servlet.http.*;
//
//@SuppressWarnings("serial")
//public class NoxDroidCloudServlet extends HttpServlet {
//	public void doGet(HttpServletRequest req, HttpServletResponse resp)
//			throws IOException {
//		resp.setContentType("text/plain");
//		resp.getWriter().println("Hello, world");
//	}
//}
