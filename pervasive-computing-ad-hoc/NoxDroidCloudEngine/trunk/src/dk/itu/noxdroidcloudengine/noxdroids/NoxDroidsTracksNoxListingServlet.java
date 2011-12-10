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
public class NoxDroidsTracksNoxListingServlet extends HttpServlet {

    private static final Logger log =
        Logger.getLogger(NoxDroidsTracksNoxListingServlet.class.getName());
	
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
          throws ServletException, IOException {

    	
    	String ancestorParentKeyName = req.getParameter("ancestor_parent_key_name");
    	Key parentAncestorKey = KeyFactory.createKey("NoxDroid", ancestorParentKeyName);
    	// prints: NoxDroid()
    	
    	// Get ancestorKeyName from request
    	// and create the key for the query
    	String ancestorKeyName = req.getParameter("ancestor_key_name");
    	Key ancestorKey = KeyFactory.createKey(parentAncestorKey, "Track", ancestorKeyName);
    	System.out.println("ancestorKey: " + ancestorKey);
    	System.out.println("ancestorKey.getParent(): " + ancestorKey.getParent());    	
    	
    	
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        Query q = new Query("Nox");
        
        q.setAncestor(ancestorKey);
//        q.addSort("start_time", Query.SortDirection.DESCENDING);
        
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
        	
//        	String props = entity.getProperties().toString(); 
//        	log("entity.getProperties().toString(): " + props);
//        	System.out.println("entity.getProperties().toString(): " + props);

            resp.getWriter().println("<li>"
            		+ entity.getProperty("nox") 
            		+ " | "
            		+ entity.getProperty("time_stamp") 
            		+ "</li>");
        }
        //no results
        if(results.size() < 1)
        	resp.getWriter().println("<li>no results</li>");
        resp.getWriter().println("</ul>");

        String cursor = results.getCursor().toWebSafeString();

        
        // Assuming this servlet lives at '/noxdroids_tracks_nox_listing'
        if(results.size() >= pageSize)
	        resp.getWriter().println(
	            "<a href='/noxdroids_tracks_nox_listing?ancestor_parent_key_name=" 
	        		+ ancestorParentKeyName + "&ancestor_key_name=" + ancestorKeyName
	        		+"&cursor=" + cursor + "'>Next page</a>");
        
        
    }
}