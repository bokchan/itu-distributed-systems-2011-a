package dk.itu.noxdroidcloudengine.tracks;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.repackaged.org.json.JSONArray;
import com.google.appengine.repackaged.org.json.JSONException;
import com.google.appengine.repackaged.org.json.JSONObject;

public class AddTrackServlet extends HttpServlet {

	private static final Logger log = Logger.getLogger(AddTrackServlet.class
			.getName());

	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {

		// get / print parameters from request
		Map map = req.getParameterMap();
		log(map.toString());
		System.out.println(map);

		// setup datastore
		DatastoreService datastore = DatastoreServiceFactory
				.getDatastoreService();

		// print request to log
		// log(req.toString());

		// read json string from request
		String trackJSON = req.getParameter("track_json");
		String isTestForm = req.getParameter("is_test_form");

		
		// Set up variables
		//
		// we are pragmatic here if the basic extract of the json fails
		// we just return immediately without any writes to the datastore
		JSONObject jsonObj;
		String noxDroidId = null;
		String noxDroidUserName = null;
		String trackId = null;
		String trackStartTime = null;
		String trackEndTime = null;
		JSONArray locationsJSONArray = null;
		JSONArray noxJSONArray = null;
		try {

			// load json string into jsonobject
			jsonObj = new JSONObject(trackJSON);

			// nox droid sensor
			noxDroidId = jsonObj.getString("nox_droid_id");
			noxDroidUserName = jsonObj.getString("nox_droid_user_name");

			// track
			trackId = jsonObj.getString("track_id");
			trackStartTime = jsonObj.getString("track_start_time");
			trackEndTime = jsonObj.getString("track_end_time");

			// track nox and locations are json array's
			locationsJSONArray = jsonObj.optJSONArray("locations");
			noxJSONArray = jsonObj.optJSONArray("nox");

		} catch (JSONException e) {

			if (isTestForm != null) {
				// TODO: perhaps redirect to an error page but absolutely
				// nicetohave
				// the current form is just plain html
				resp.sendRedirect("/add_track_form.html");
			} else {
				// NOT OK
				resp.setStatus(409);
			}
			log("AddTrackServlet - the json seemed currupted" + e.getMessage());

		}
		
		
		
		//
		// add nox droid sensor
		//

		
		// Entity - type/kind | key/id | optional parent - this one has no
		// parent
		// if an entity with the same id exists - data is stored into that
		// one or a new is created
		Entity noxDroid = new Entity("NoxDroid", noxDroidId);
		// not strictly needed use the key name/id - entity.getKey().getName()
		// noxDroid.setProperty("id", noxDroidId);
		noxDroid.setProperty("username", noxDroidUserName);
		datastore.put(noxDroid);

		//
		// add track
		//
		Entity track = new Entity("Track", trackId, noxDroid.getKey());
		
		// not strictly needed use the key name/id - entity.getKey().getName()
		// track.setProperty("id", trackId);
		track.setProperty("start_time", trackStartTime);
		track.setProperty("end_time", trackEndTime);
		datastore.put(track);

		// add locations and nox
		addLocationsToDatastore(locationsJSONArray, track, datastore);
		addNoxToDatastore(noxJSONArray, track, datastore);

		if (isTestForm != null) {
			resp.sendRedirect("/add_track_form.html");
		} else {
			// OK - we return 201 in a restful like approach
			// - which is the normal for success on put/post
			// - http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html
			resp.setStatus(201);
		}

		
		
		
		

	}

	/**
	 * 
	 * Add locations to datastore based on an json array
	 * 
	 * 
	 * @param jsonArray
	 * @param track
	 * @param datastore
	 */
	private void addLocationsToDatastore(JSONArray jsonArray, Entity track,
			DatastoreService datastore) {

		// prepare for the datastore
		List<Entity> locations = new ArrayList<Entity>();
		Entity entity = null;
		double longitude = 0.0;
		double latitude = 0.0;
		String time_stamp;
		String provider;

		if (jsonArray != null) {

			try {
				System.out.println(jsonArray.toString(4));

				for (int i = 0; i < jsonArray.length(); ++i) {
					JSONObject location = jsonArray.getJSONObject(i);

					// note: also rec.keys()
					longitude = location.getDouble("longitude");
					latitude = location.getDouble("latitude");
					time_stamp = location.getString("time_stamp");
					provider = location.getString("provider");

					//
					// TODO: look up issue on set geopt
					//
					// GeoPt geoPoint = new
					// GeoPt(Float.parseFloat("2.0"),Float.parseFloat("1.0"));
					// read more in
					// NoxDroidLowLevelStorageTest.testNewEntitiesWithAncestorChildren()

					// Entity(type/kind, id/key, parent )
					entity = new Entity("Location", i + 1,
							track.getKey());
					
					// not strictly needed use the key name/id - entity.getKey().getName()
					// entity.setProperty("id", i + 1);
					entity.setProperty("latitude", latitude);
					entity.setProperty("longitude", longitude);
					entity.setProperty("time_stamp", time_stamp);
					entity.setProperty("provider", provider);

					locations.add(entity);

					System.out.println("longitude: " + longitude
							+ " latitude: " + latitude + " time_stamp: "
							+ time_stamp + " provider: " + provider);
				}

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			// batch add locations
			datastore.put(locations);

		}
	}

	/**
	 * 
	 * Add nox to datastore based on an json array
	 * 
	 * 
	 * @param jsonArray
	 * @param track
	 * @param datastore
	 */
	private void addNoxToDatastore(JSONArray jsonArray, Entity track,
			DatastoreService datastore) {

		// prepare for the datastore
		List<Entity> noxList = new ArrayList<Entity>();
		Entity entity = null;
		double nox = 0.0;
		double temperature = 0.0;
		String time_stamp;

		if (jsonArray != null) {

			try {
				System.out.println(jsonArray.toString(4));

				for (int i = 0; i < jsonArray.length(); ++i) {
					JSONObject obj = jsonArray.getJSONObject(i);

					// note: also rec.keys()
					nox = obj.getDouble("nox");
					temperature = obj.getDouble("temperature");
					time_stamp = obj.getString("time_stamp");

					// Entity(type/kind, id/key, parent )
					entity = new Entity("Nox", i + 1, track.getKey());
					// not strictly needed use the key name/id - entity.getKey().getName()
					// entity.setProperty("id", i + 1);
					entity.setProperty("temperature", temperature);
					entity.setProperty("nox", nox);
					entity.setProperty("time_stamp", time_stamp);
					noxList.add(entity);

					System.out.println("nox: " + nox + " temperature: "
							+ temperature + " time_stamp: " + time_stamp);
				}

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			// batch add locations
			datastore.put(noxList);

		}

	}

}