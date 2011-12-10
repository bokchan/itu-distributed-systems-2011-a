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
public class NoxDroidsTracksLocationsCSVListingServlet extends HttpServlet {

    private static final Logger log =
        Logger.getLogger(NoxDroidsTracksLocationsCSVListingServlet.class.getName());
	
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
          throws ServletException, IOException {

    	
    	// provider from request
    	String providerSetting = req.getParameter("provider");
    	boolean setProviderFiler = true;
    	if (providerSetting == null)
    		setProviderFiler = false;
    	
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
        Query q = new Query("Location");
        
        if (setProviderFiler)
        	q.addFilter("provider", Query.FilterOperator.EQUAL, providerSetting);
        
        q.setAncestor(ancestorKey);
//        q.addSort("start_time", Query.SortDirection.DESCENDING);
        
        PreparedQuery pq = datastore.prepare(q);

        
//        resp.setContentType("text/html");
        // note(s):
        // resp.setContentType("text/plain")
        // - didn't work so we set the header 
        // 
        // and its very important to set utf-8 to get weka to read the file/url
        // based on this tip http://goo.gl/ajfxt
        resp.setHeader("Content-Type", "text/plain; charset=utf-8");        
        
        
       
        resp.getWriter().println("name, desc, latitude, longitude");

        FetchOptions fetchOptions = FetchOptions.Builder.withDefaults();
        int pageSize = 20;
        boolean usePaging = false;
        // note: use this one pager should be used
        // 
        // FetchOptions fetchOptions = FetchOptions.Builder.withLimit(pageSize);
        
        String startCursor = req.getParameter("cursor");
        
        // If this servlet is passed a cursor parameter, let's use it
        if (startCursor != null) {
            fetchOptions.startCursor(Cursor.fromWebSafeString(startCursor));
        }
              
        QueryResultList<Entity> results = pq.asQueryResultList(fetchOptions);
        for (Entity entity : results) {

        	// example get child from entity:
        	// entity.getKey().getChild("Location", id)
        	
            resp.getWriter().println(            		
            		entity.getProperty("latitude") 
            		+ ","
            		+ entity.getProperty("latitude")
            		+ ","
            		+ entity.getProperty("latitude") 
            		+ ","
            		+ entity.getProperty("longitude")
            		);
        }
        //no results
        if(results.size() < 1)
        	resp.getWriter().println("no results");

        String cursor = results.getCursor().toWebSafeString();

        
        // Assuming this servlet lives at '/noxdroids_tracks_nox_listing'
        if(usePaging && results.size() >= pageSize)
	        resp.getWriter().println(
	            "<a href='/noxdroids_tracks_nox_listing?ancestor_parent_key_name=" 
	        		+ ancestorParentKeyName + "&ancestor_key_name=" + ancestorKeyName
	        		+"&cursor=" + cursor + "'>Next page</a>");
        
        
    }
}