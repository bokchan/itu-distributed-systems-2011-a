package dk.itu.noxdroidcloudengine.noxdroids;

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
public class NoxDroidsListingServlet extends HttpServlet {

    private static final Logger log =
        Logger.getLogger(NoxDroidsListingServlet.class.getName());
	
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
          throws ServletException, IOException {

        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        Query q = new Query("NoxDroid").addSort("username", Query.SortDirection.DESCENDING);
        // naive: its not possible to sort on the id/name (key) or what ?
        
        PreparedQuery pq = datastore.prepare(q);
        int pageSize = 20;
        
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
        	
//        	log("entity.getProperties().toString(): " + props);
        	System.out.println("entity.getProperties().toString(): " + props);
        	System.out.println("entity.getKey(): " + entity.getKey());
        	
        	
            resp.getWriter().println("<li>"
            		+ entity.getProperty("username") 
            		+ " | "
            		+ entity.getKey() 
            		+ "|"
            		+ " <a href='/noxdroids_tracks_listing?ancestor_key_name=" + entity.getKey().getName() + "'>View tracks</a>"

//            		// uuid
//            		+ entity.getProperty("id")
            		
            		
//            		+ " <a href='/activityrecording.jsp?activityName=" + entity.getProperty("uuid") + "'>HTML</a>"
//            		+ " <a href='/activity_arff?activityName=" + entity.getProperty("uuid") + "'>ARFF</a>"
//            		+ " <a href='/activity_csv?activityName=" + entity.getProperty("uuid") + "'>CSV</a>"
//            		//+ entity.getProperty("uuid") 
//            		+ "|"            		
//            		+ entity.getProperty("t") 
//            		+ "|" 
//            		+ entity.getProperty("time") 
//            		+ "|" 
//            		+ entity.getProperty("x") 
//            		+ "|" 
//            		+ entity.getProperty("y") 
//            		+ "|" 
//            		+ entity.getProperty("z")  
            		+ "</li>");
        }
        if(results.size() < 1)
        	resp.getWriter().println("<li>no results</li>");
        resp.getWriter().println("</ul>");

        String cursor = results.getCursor().toWebSafeString();

        // Assuming this servlet lives at '/noxdroids_listing'
        if(results.size() > 20)
	        resp.getWriter().println(
	            "<a href='/noxdroids_listing?cursor=" + cursor + "'>Next page</a>");
    }
}