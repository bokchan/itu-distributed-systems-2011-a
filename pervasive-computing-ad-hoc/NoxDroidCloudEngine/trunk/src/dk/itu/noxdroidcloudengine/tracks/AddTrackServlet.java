package dk.itu.noxdroidcloudengine.tracks;

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

public class AddTrackServlet extends HttpServlet {
	private static final Logger log = Logger
			.getLogger(AddTrackServlet.class.getName());

	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {

		// setup datastor
        DatastoreService datastore =
            DatastoreServiceFactory.getDatastoreService();
		
		// print request to log
		// log(req.toString());
		
		/*
		raw sketch:

		fields:
		userid / name
		track id
		track start
		track end
		
		locations: as json
		nox: as json
		 */
				
		// sensor 
		String sensorId = req.getParameter("sensor_id");
		String sensorUserName = req.getParameter("sensor_user_name");
		
		// track
		String trackId = req.getParameter("track_id");
		String trackStartTime = req.getParameter("track_start_time");
		String trackEndTime = req.getParameter("track_end_time");
		
		// add sensor 
		// 
		// but first check if sensor is already created ?
		//
		Query q = new Query("Sensor");
		q.addFilter("id", Query.FilterOperator.EQUAL, sensorId);
		
		// PreparedQuery contains the methods for fetching query results from the datastore
		PreparedQuery pq = datastore.prepare(q);
		
		Entity sensor = pq.asSingleEntity();

		if (sensor == null) {
			// no sensor was found in datastore lets create a new one
			sensor = new Entity("Sensor");			
		}


		sensor.setProperty("id", sensorId);
		sensor.setProperty("username", sensorUserName);
		datastore.put(sensor);
		
		// add track 
		// we assume its a new track so no datastore lookup/query is done
		Entity track = new Entity("Track", trackId, sensor.getKey());
		track.setProperty("id", trackId);
		track.setProperty("start_time", trackStartTime);
		track.setProperty("end_time", trackEndTime);
		datastore.put(track);
		
		// add locations (from a json file)
		
		
		// add nox (from a json file)

		
		
		
		
		
		
		
		
		
//		} // end of parameter checks
		
//		if (activityDevice != null && activityDevice.equals("android")) {
//			// note: for now we have skipped to get the response
//			// // ok
//			// resp.setStatus(200);
//
//		} else {
//
//			resp.sendRedirect("/activityrecording.jsp?activityName="
//					+ activityName);
//
//		}

		resp.sendRedirect("/add_track_form.html");		
		
	}
}