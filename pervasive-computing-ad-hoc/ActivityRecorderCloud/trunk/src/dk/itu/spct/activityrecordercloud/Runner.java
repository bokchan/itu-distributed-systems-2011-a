package dk.itu.spct.activityrecordercloud;

import java.util.List;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;

public class Runner {

	
	
	
	// TODO: move to a junit based test
	public static void main(String[] args) {

		System.out.println("Simulating the jsp file");
		
//		
//		// previously: guestbookName
//        String activityName = req.getParameter("activityName");
//        
//        // previously: guestbookKey
//        Key activityKey = KeyFactory.createKey("Activity", activityName);
//        // previously: content
//        String activityType = req.getParameter("activityType");
//        
//        String date = req.getParameter("date");
//        String xAxis = req.getParameter("xAxis");
//        String yAxis = req.getParameter("yAxis");
//        String zAxis = req.getParameter("zAxis");
//        // ? should probably be casted into some long / etc.. types ?

		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		String activityName = "default";
		
		
	    Key activityKey = KeyFactory.createKey("Activity", activityName);		
	    Query query = new Query("Activity", activityKey).addSort("date", Query.SortDirection.DESCENDING);
	    List<Entity> greetings = datastore.prepare(query).asList(FetchOptions.Builder.withLimit(5));
		
	    
	    
	    System.out.println("greetings: " + greetings);
		
	}	
	







}
