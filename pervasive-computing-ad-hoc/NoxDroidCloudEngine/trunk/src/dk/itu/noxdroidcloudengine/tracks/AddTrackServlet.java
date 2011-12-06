package dk.itu.noxdroidcloudengine.tracks;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mortbay.util.ajax.JSON;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
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

		
		
		Map map = req.getParameterMap();		
		log(map.toString());
		System.out.println(map);
		
		
		
		
		
		// setup datastore
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

		// do we get it from an Android NoxDroid or form a html form

		String sensorDevice = req.getParameter("sensorDevice");

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
				
				// here we have the full / whole json map
				// {"locations":[{<item>},{<item>}]}
				jsonObj = new JSONObject(trackLocationsJSON);
				// human readable json- nice
				log("jsonObj: " + jsonObj.toString(4));

				// here we have the json list / array
				// [{<item>},{<item>}]
				JSONArray locationJSONArray = jsonObj.optJSONArray("locations");
				log("locationJSONArray: " + locationJSONArray);
				
				System.out.println(locationJSONArray.toString(4));
				
//				locationJSONArray = jsonObj.toJSONArray(null);
				
				
				// prepare for the datastore
				List<Entity> locations = new ArrayList<Entity>();
				Entity locationEntity = null;
				double longitude = 0.0;
				double latitude = 0.0;
				String time_stamp;
				String provider;

				if(locationJSONArray!=null) {
					for (int i = 0; i < locationJSONArray.length(); ++i) {
						JSONObject rec = locationJSONArray.getJSONObject(i);
	
						// note: also rec.keys()
						longitude = rec.getDouble("longitude");
						latitude = rec.getDouble("latitude");
						time_stamp = rec.getString("time_stamp");
						provider = rec.getString("provider");
	
						// TODO: look up issue on set geopt
						// GeoPt geoPoint = new
						// GeoPt(Float.parseFloat("2.0"),Float.parseFloat("1.0"));
						// read more in
						// NoxDroidLowLevelStorageTest.testNewEntitiesWithAncestorChildren()
						
						// Entity(type/kind, id/key, parent )
						locationEntity = new Entity("Location", i+1, track.getKey());
						locationEntity.setProperty("latitude", latitude);
						locationEntity.setProperty("longitude", longitude);
						locationEntity.setProperty("time_stamp", time_stamp);
						locationEntity.setProperty("provider", provider);
	
						locations.add(locationEntity);
	
						System.out.println("longitude: " + longitude
								+ " latitude: " + latitude + " time_stamp: "
								+ time_stamp + " provider: " + provider);
					}
				}
				
				// batch add locations
				datastore.put(locations);

				if (sensorDevice != null && sensorDevice.equals("android")) {
					// OK - we return 201 in a restful like approach
					// - which is the normal for success on put/post
					// - http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html
					resp.setStatus(201);
				} else {
					resp.sendRedirect("/add_track_form.html");
				}

			} catch (JSONException e) {
				if (sensorDevice != null && sensorDevice.equals("android")) {
					// NOT OK
					resp.setStatus(409);
				} else {
					// TODO: perhaps redirect to an error page but absolutely nicetohave 
					// the current form is just plain html 
					resp.sendRedirect("/add_track_form.html");
				}				
				log("AddTrackServlet - the json seemed currupted" + e.getMessage());
			}
		}

	}

}