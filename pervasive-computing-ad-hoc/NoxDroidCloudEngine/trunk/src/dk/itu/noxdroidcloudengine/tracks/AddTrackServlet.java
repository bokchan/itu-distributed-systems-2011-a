package dk.itu.noxdroidcloudengine.tracks;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mortbay.util.ajax.JSON;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.GeoPt;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.repackaged.org.json.JSONArray;
import com.google.appengine.repackaged.org.json.JSONException;
import com.google.appengine.repackaged.org.json.JSONObject;

public class AddTrackServlet extends HttpServlet {
	private static final Logger log = Logger.getLogger(AddTrackServlet.class
			.getName());

	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {

		// setup datastor
		DatastoreService datastore = DatastoreServiceFactory
				.getDatastoreService();

		// print request to log
		// log(req.toString());

		/*
		 * raw sketch:
		 * 
		 * fields: userid / name track id track start track end
		 * 
		 * locations: as json nox: as json
		 */

		// sensor
		String sensorId = req.getParameter("sensor_id");
		String sensorUserName = req.getParameter("sensor_user_name");

		// track
		String trackId = req.getParameter("track_id");
		String trackStartTime = req.getParameter("track_start_time");
		String trackEndTime = req.getParameter("track_end_time");
		String trackLocationsJSON = req.getParameter("track_locations");

		//
		// add sensor
		//
		// but first check if sensor is already created ?
		//
		Query q = new Query("Sensor");
		q.addFilter("id", Query.FilterOperator.EQUAL, sensorId);

		// PreparedQuery contains the methods for fetching query results from
		// the datastore
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
		// parse json
		if (trackLocationsJSON != null) {
			JSONObject jsonObj;
			try {
				jsonObj = new JSONObject(trackLocationsJSON);
				// human readable json- nice
				System.out.println("jsonObj: " + jsonObj.toString(4));

				JSONArray locationJSONArray = jsonObj.optJSONArray("locations");

				// prepare for the datastore
				List<Entity> locations = new ArrayList<Entity>();
				Entity locationEntity = null;
				double longitude = 0.0;
				double latitude = 0.0;
				String time_stamp;

				for (int i = 0; i < locationJSONArray.length(); ++i) {
					JSONObject rec = locationJSONArray.getJSONObject(i);

					// note: also rec.keys()
					longitude = rec.getDouble("longitude");
					latitude = rec.getDouble("latitude");
					time_stamp = rec.getString("time_stamp");

					
					// TODO: look up issue on set geopt 
					// GeoPt geoPoint = new GeoPt(Float.parseFloat("2.0"),Float.parseFloat("1.0"));
					// read more in NoxDroidLowLevelStorageTest.testNewEntitiesWithAncestorChildren() 
					locationEntity = new Entity("Location", 2, track.getKey());
					locationEntity.setProperty("latitude", latitude);
					locationEntity.setProperty("longitude", longitude);
					locationEntity.setProperty("longitude", longitude);
					
					locations.add(locationEntity);
					

					System.out.println("longitude: " + longitude
							+ " latitude: " + latitude + " time_stamp: " + time_stamp);
				}
				
				
				// batch add locations
				datastore.put(locations);
				

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		// add nox (from a json file)

		// } // end of parameter checks

		// if (activityDevice != null && activityDevice.equals("android")) {
		// // note: for now we have skipped to get the response
		// // // ok
		// // resp.setStatus(200);
		//
		// } else {
		//
		// resp.sendRedirect("/activityrecording.jsp?activityName="
		// + activityName);
		//
		// }

		resp.sendRedirect("/add_track_form.html");

	}

	/*
	 * 
	 * http://jetty.codehaus.org/jetty/jetty-6/apidocs/org/mortbay/util/ajax/JSON
	 * .html
	 * 
	 * watch out for the static is that needed ? was set when we tried to unit
	 * test
	 */
	public static HashMap<String, String> parseJSONLocation(String json) {

		try {
			HashMap<String, String> obj = (HashMap<String, String>) JSON
					.parse(json.toString());
			return obj;
		} catch (Exception e) {
			return new HashMap<String, String>();
		}

	}

}