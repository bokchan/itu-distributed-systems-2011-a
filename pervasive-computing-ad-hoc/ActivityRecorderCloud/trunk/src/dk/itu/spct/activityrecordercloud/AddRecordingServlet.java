package dk.itu.spct.activityrecordercloud;

import java.io.IOException;
import java.util.Map;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Index;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;

public class AddRecordingServlet extends HttpServlet {
	private static final Logger log = Logger
			.getLogger(AddRecordingServlet.class.getName());

	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		// UserService userService = UserServiceFactory.getUserService();
		// User user = userService.getCurrentUser();

		// We have one entity group per Guestbook with all Greetings residing
		// in the same entity group as the Guestbook to which they belong.
		// This lets us run an ancestor query to retrieve all Greetings for a
		// given Guestbook. However, the write rate to each Guestbook should be
		// limited to ~1/second.
		String activityName = req.getParameter("activityName");

		Key activityKey = KeyFactory.createKey("Activity", activityName);

		// print request to log
		// log(req.toString());

		// get vars from request
		String activityType = req.getParameter("activityType");
		// long - but since its from a form its string
		String activityTime = req.getParameter("activityTime");
		// int(s) - but since its from a form its string
		String activityX = req.getParameter("activityX");
		String activityY = req.getParameter("activityY");
		String activityZ = req.getParameter("activityZ");

		String activityDevice = req.getParameter("activityDevice");

		String activityUserName = req.getParameter("activityUserName");

		// Date date = new Date();

		Entity activityNode = new Entity("ActivityNode", activityKey);
		// greeting.setProperty("user", user);
		// greeting.setProperty("date", date);
		activityNode.setProperty("type", activityType);

		// work around to get an id on each entity ...
		activityNode.setProperty("uuid", activityName);
		activityNode.setProperty("username", activityUserName);
		activityNode.setProperty("time", activityTime);
		activityNode.setProperty("x", activityX);
		activityNode.setProperty("y", activityY);
		activityNode.setProperty("z", activityZ);

		DatastoreService datastore = DatastoreServiceFactory
				.getDatastoreService();
		datastore.put(activityNode);

		// note: try outs with datastore and key
		//
		// Map<Index,Index.IndexState> map = datastore.getIndexes();
		// log("map: " + map);
		// datastore.getIndexes();
		// log("\n\n==============================================\n\n");
		// //
		// Query q = new Query("ActivityNode");
		//
		// q.setKeysOnly();
		// PreparedQuery pq = datastore.prepare(q);
		//
		// log("pq.countEntities(): " +pq.countEntities());
		//
		// // pq.asIterable()
		// // pq.asQueryResultIterator()
		//
		// for (Entity result : pq.asIterable()) {
		// Key k = result.getKey();
		// log("key: " + k.toString());
		// log("key (parent): " + result.getParent().toString());
		// }
		//
		// log("\n\n==============================================\n\n");

		if (activityDevice != null && activityDevice.equals("android")) {
			// note: for now we have skipped to get the response
			// // ok
			// resp.setStatus(200);

		} else {

			resp.sendRedirect("/activityrecording.jsp?activityName="
					+ activityName);

		}
	}
}