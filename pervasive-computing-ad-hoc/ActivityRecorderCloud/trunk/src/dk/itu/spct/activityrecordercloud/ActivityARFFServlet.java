package dk.itu.spct.activityrecordercloud;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.QueryResultList;


/**
 * 
 * Based upon http://code.google.com/appengine/docs/java/datastore/queries.html
 *
 */
public class ActivityARFFServlet extends HttpServlet {

    private static final Logger log =
        Logger.getLogger(ActivityARFFServlet.class.getName());
	
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
          throws ServletException, IOException {

    	
        String activityName = req.getParameter("activityName");
        if (activityName == null) {
            activityName = "default";
        }
    	
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        Key activityKey = KeyFactory.createKey("Activity", activityName);
        
        Query q = new Query("ActivityNode", activityKey).addSort("time", Query.SortDirection.ASCENDING);
//        Query q = new Query("ActivityNode").addSort("time", Query.SortDirection.DESCENDING);
        PreparedQuery pq = datastore.prepare(q);

        // note(s):
        // resp.setContentType("text/plain")
        // - didn't work so we set the header 
        // 
        // and its very important to set utf-8 to get weka to read the file/url
        // based on this tip http://goo.gl/ajfxt
        resp.setHeader("Content-Type", "text/plain; charset=utf-8");


        
        FetchOptions fetchOptions = FetchOptions.Builder.withDefaults();
        String startCursor = req.getParameter("cursor");
        
        // If this servlet is passed a cursor parameter, let's use it
        if (startCursor != null) {
            fetchOptions.startCursor(Cursor.fromWebSafeString(startCursor));
        }

        /*
		@relation <theaction>
		
		@attribute action {sitting, walking, stairs}
		@date time_stamp date  // shouldn't it be ? @attribute time_stamp date  
		@attribute x numeric
		@attribute y numeric
		@attribute z numeric
		
		@data
		walking,'2011-11-30 10:45:20',4.137180328369141,3.44765043258667,-3.3710360527038574
		NB! date have to be surrounded with signle quotes ''
		
         */
        

        
        boolean firstrunFlag = true;
        
        // now print eact activity node linie by line    
        QueryResultList<Entity> results = pq.asQueryResultList(fetchOptions);
        for (Entity entity : results) {
        	
        	if (firstrunFlag) {

                resp.getWriter().println("@relation " + entity.getProperty("type").toString().toLowerCase());
                resp.getWriter().println("@attribute action {sitting, walking, stairs}");
                // note: date turned off didn't play well within weka 
                // resp.getWriter().println("@date time_stamp date");
                resp.getWriter().println("");
                resp.getWriter().println("@attribute x numeric");
                resp.getWriter().println("@attribute y numeric");
                resp.getWriter().println("@attribute z numeric");
                resp.getWriter().println("");
                resp.getWriter().println("@data");
        		
        	}

//        	String props = entity.getProperties().toString(); 
//        	log("entity.getProperties().toString(): " + props);
 
            resp.getWriter().println(
            		entity.getProperty("type").toString().toLowerCase()
// note: date turned off didn't play well within weka
//            		+ ","
//            		+ "'"
//            		+ entity.getProperty("time")
//            		+ "'"
            		+ ","
            		+ entity.getProperty("x")
            		+ ","
            		+ entity.getProperty("y")
            		+ ","
            		+ entity.getProperty("z")
            		);	

        
            firstrunFlag = false;
        }


    }
}