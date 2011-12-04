package dk.itu.noxdroid.simple;

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
		// TODO: not 100% sure what will happen if its already created - 
		// i we remember right its just overriding the properties of teh exixiting one
		Entity sensor = new Entity("Sensor");
		sensor.setProperty("id", sensorId);
		sensor.setProperty("username", sensorUserName);
		datastore.put(sensor);
		
		// add track
		Entity track = new Entity("Track", trackId, sensor.getKey());
		track.setProperty("id", trackId);
		track.setProperty("start_time", trackStartTime);
		track.setProperty("end_time", trackEndTime);
		datastore.put(track);
		
		
		
//		} // end of parameter checks
		
//		
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