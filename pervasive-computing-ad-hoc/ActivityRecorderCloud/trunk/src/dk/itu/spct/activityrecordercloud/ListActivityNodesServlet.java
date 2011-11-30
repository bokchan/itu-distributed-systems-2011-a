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
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.QueryResultList;


/**
 * 
 * Based upon http://code.google.com/appengine/docs/java/datastore/queries.html
 *
 */
public class ListActivityNodesServlet extends HttpServlet {

    private static final Logger log =
        Logger.getLogger(ListActivityNodesServlet.class.getName());
	
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
          throws ServletException, IOException {

        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        Query q = new Query("ActivityNode").addSort("time", Query.SortDirection.DESCENDING);
        PreparedQuery pq = datastore.prepare(q);
        int pageSize = 20000;
        
        resp.setContentType("text/html");
        resp.getWriter().println("<ul>");

        FetchOptions fetchOptions = FetchOptions.Builder.withLimit(pageSize);
        String startCursor = req.getParameter("cursor");
        
        // If this servlet is passed a cursor parameter, let's use it
        if (startCursor != null) {
            fetchOptions.startCursor(Cursor.fromWebSafeString(startCursor));
        }
              
        QueryResultList<Entity> results = pq.asQueryResultList(fetchOptions);
        for (Entity entity : results) {
        	
        	String props = entity.getProperties().toString(); 
        	
        	log("entity.getProperties().toString(): " + props);

            resp.getWriter().println("<li>"
            		+ entity.getProperty("username") 
            		+ "|"
            		+ entity.getProperty("uuid")
            		+ " <a href='/activityrecording.jsp?activityName=" + entity.getProperty("uuid") + "'>HTML</a>"
            		+ " <a href='/activity_arff?activityName=" + entity.getProperty("uuid") + "'>ARFF</a>"
            		//+ entity.getProperty("uuid") 
            		+ "|"            		
            		+ entity.getProperty("type") 
            		+ "|" 
            		+ entity.getProperty("time") 
            		+ "|" 
            		+ entity.getProperty("x") 
            		+ "|" 
            		+ entity.getProperty("y") 
            		+ "|" 
            		+ entity.getProperty("z")  
            		+ "</li>");
        }
        resp.getWriter().println("</ul>");

        String cursor = results.getCursor().toWebSafeString();

        // Assuming this servlet lives at '/people'
        resp.getWriter().println(
            "<a href='/activitynodes?cursor=" + cursor + "'>Next page</a>");
    }
}