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

       
        // resp.setContentType("text/plain")
        // - didn't work so we set the header 
        resp.setHeader("Content-Type", "text/plain");

        FetchOptions fetchOptions = FetchOptions.Builder.withDefaults();
        String startCursor = req.getParameter("cursor");
        
        // If this servlet is passed a cursor parameter, let's use it
        if (startCursor != null) {
            fetchOptions.startCursor(Cursor.fromWebSafeString(startCursor));
        }

        /*
    @relation activity
    @attribute type {Walking, Sitting, Chairs, None} 
    @attribute x float
    @attribute y float
    @attribute z float

    @data 
    Walking,0.9,8.9,7.3
    Walking,1.9,9.9,0.3

         */
        
        resp.getWriter().println("@relation activity");
        resp.getWriter().println("@attribute type {Walking, Sitting, Chairs, None}");
        resp.getWriter().println("@attribute x float");
        resp.getWriter().println("@attribute y float");
        resp.getWriter().println("@attribute z float");
        resp.getWriter().println("");
        resp.getWriter().println("@data");
        
        // now print eact activity node linie by line    
        QueryResultList<Entity> results = pq.asQueryResultList(fetchOptions);
        for (Entity entity : results) {
        	
        	String props = entity.getProperties().toString(); 
        	
        	log("entity.getProperties().toString(): " + props);
 
            resp.getWriter().println(
            		entity.getProperty("type")
            		+ " "
            		+ entity.getProperty("x")
            		+ " "
            		+ entity.getProperty("y")
            		+ " "
            		+ entity.getProperty("z")
            		);	
        }



    }
}