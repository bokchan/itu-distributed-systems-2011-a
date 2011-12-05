package dk.itu.noxdroid.tests;

import org.junit.Test;
// note: import static - currious about that one - 
import static org.junit.Assert.*;

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
