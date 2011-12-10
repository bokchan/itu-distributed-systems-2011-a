package dk.itu.noxdroidcloudengine.tracks;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.GeoPt;
import com.google.appengine.repackaged.org.json.JSONArray;
import com.google.appengine.repackaged.org.json.JSONException;
import com.google.appengine.repackaged.org.json.JSONObject;

public class AddTrackServlet extends HttpServlet {

	private static final Logger log = Logger.getLogger(AddTrackServlet.class
			.getName());

	private static SimpleDateFormat formatter = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");

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
		// format start / time into java date object
		//
		Date trackStartTimeDate = null;
		Date trackEndTimeDate = null;
		try {
			trackStartTimeDate = (Date) formatter.parse(trackStartTime);
			trackEndTimeDate = (Date) formatter.parse(trackEndTime);
		} catch (ParseException e) {
			log("AddTrackServlet - track start / end time stamp was currupted"
					+ e.getMessage());
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
		track.setProperty("start_time_date", trackStartTimeDate);
		track.setProperty("end_time_date", trackEndTimeDate);

		datastore.put(track);

		// add locations and nox
		addLocationsToDatastore(locationsJSONArray, track, datastore);
		addNoxToDatastore(noxJSONArray, track, datastore);

		if (isTestForm != null) {
			resp.sendRedirect("/noxdroids_tracks_listing?ancestor_key_name="
					+ noxDroidId);

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
		GeoPt geoPoint;
		String timeStamp;
		Date timeStampDate = null;
		String provider;
		// prepare time format
		if (jsonArray != null) {

			try {
				System.out.println(jsonArray.toString(4));

				for (int i = 0; i < jsonArray.length(); ++i) {
					JSONObject obj = jsonArray.getJSONObject(i);

					longitude = obj.getDouble("longitude");
					latitude = obj.getDouble("latitude");
					timeStamp = obj.getString("time_stamp");
					provider = obj.getString("provider");

					//
					// format time_stamp to java date object
					//
					try {
						timeStampDate = (Date) formatter.parse(timeStamp);
					} catch (ParseException e) {
						log("AddTrackServlet - location time stamp was currupted"
								+ e.getMessage());
					}

					//
					// Prepare geo point
					// - but also store latitude / longitude
					// - we play safe but might overblow datastore a bit
					//
					// GEOPT(lat, long) GEOPT(37.4219, -122.0846)
					// - takes only floats not double
					geoPoint = new GeoPt((float) latitude, (float) longitude);

					// Entity(type/kind, id/key, parent )
					entity = new Entity("Location", i + 1, track.getKey());

					// not strictly needed use the key name/id -
					// entity.getKey().getName()
					// entity.setProperty("id", i + 1);
					entity.setProperty("latitude", latitude);
					entity.setProperty("longitude", longitude);
					entity.setProperty("time_stamp", timeStamp);
					entity.setProperty("time_stamp_date", timeStampDate);
					entity.setProperty("provider", provider);
					entity.setProperty("geo_point", geoPoint);

					locations.add(entity);

					System.out.println("longitude: " + longitude
							+ " latitude: " + latitude + " time_stamp: "
							+ timeStamp + "time as date obj: " + timeStampDate
							+ " provider: " + provider);
				}

			} catch (JSONException e) {
				log("AddTrackServlet - location json was currupted"
						+ e.getMessage());

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
		String timeStamp;
		Date timeStampDate = null;

		if (jsonArray != null) {

			try {
				System.out.println(jsonArray.toString(4));

				for (int i = 0; i < jsonArray.length(); ++i) {
					JSONObject obj = jsonArray.getJSONObject(i);

					nox = obj.getDouble("nox");
					temperature = obj.getDouble("temperature");
					timeStamp = obj.getString("time_stamp");

					//
					// format time_stamp to java date object
					//
					try {
						timeStampDate = (Date) formatter.parse(timeStamp);
					} catch (ParseException e) {
						log("AddTrackServlet - location time stamp was currupted"
								+ e.getMessage());
					}

					// Entity(type/kind, id/key, parent )
					entity = new Entity("Nox", i + 1, track.getKey());
					// not strictly needed use the key name/id -
					// entity.getKey().getName()
					// entity.setProperty("id", i + 1);
					entity.setProperty("temperature", temperature);
					entity.setProperty("nox", nox);
					entity.setProperty("time_stamp", timeStamp);
					entity.setProperty("time_stamp_date", timeStampDate);

					noxList.add(entity);

					System.out.println("nox: " + nox + " temperature: "
							+ temperature + " time_stamp: " + timeStamp
							+ "time as date obj: " + timeStampDate);
				}

			} catch (JSONException e) {
				log("AddTrackServlet - location json was currupted"
						+ e.getMessage());
			}

			// batch add locations
			datastore.put(noxList);

		}

	}

}