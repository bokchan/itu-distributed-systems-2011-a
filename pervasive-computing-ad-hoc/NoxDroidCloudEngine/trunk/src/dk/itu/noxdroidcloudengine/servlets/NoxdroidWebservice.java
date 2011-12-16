package dk.itu.noxdroidcloudengine.servlets;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.QueryResultList;
import com.google.appengine.repackaged.org.json.JSONArray;

import dk.itu.noxdroidcloudengine.dataprocessing.KMLManager;
import dk.itu.noxdroidcloudengine.noxdroids.NoxDroidsTracksListingServlet;

public class NoxdroidWebservice extends HttpServlet {
	
	private static enum STATUSCODE {
		STATUS_200(200, ""), STATUS_404(404,""), STATUS_403(403, "");
		
		public String MSG;
		STATUSCODE(int status, String message) {
			MSG = message;
		}
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static enum ACTION {
		TRACKSBYNOXDROID, SINGLETRACKKML, DOWNLOADKML, UNDEFINED
	}

	private static final Logger log = Logger
			.getLogger(NoxDroidsTracksListingServlet.class.getName());

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		String noxdroidKeyName = req.getParameter("noxdroid");
		Key noxdroidKey = KeyFactory.createKey("NoxDroid", noxdroidKeyName);
		String action = req.getParameter("action");
		System.out.println(noxdroidKeyName);
		System.out.println(action);

		String trackKeyName;
		Key trackKey;
		String flag;
		String kml;
		boolean forceCreate;
		switch (getActionByString(action)) {
		case SINGLETRACKKML:
			trackKeyName = req.getParameter("track");
			trackKey = KeyFactory.createKey(noxdroidKey, "Track",
					trackKeyName);
			flag = req.getParameter("force_create");
			forceCreate = false;
			if (flag != null) {
				forceCreate = Boolean.parseBoolean(flag);
			}
			kml = getKMLForTrack(trackKey, forceCreate);
			resp.setHeader("Content-type",
					"application/vnd.google-earth.kml+xml");
			resp.setHeader("Content-Disposition", "attachment; filename=\""
					+ noxdroidKeyName + ".kml\"");
			resp.setStatus(200);
			resp.getWriter().print(kml);
			break;
		case TRACKSBYNOXDROID:
			JSONArray array = getTracksByNoxDROID(noxdroidKey);
			resp.setHeader("Content-type","application/json;charset=UTF-8");
			resp.setHeader("Transfer-Encoding", "chunked");
			resp.setHeader("Access-Control-Allow-Origin", "*"); 
			resp.setCharacterEncoding("UTF-8");	
			resp.setStatus(200);
			resp.getWriter().print(array.toString());
			break;
		case DOWNLOADKML : 
			trackKeyName = req.getParameter("track");
			trackKey = KeyFactory.createKey(noxdroidKey, "Track",
					trackKeyName);
			flag = req.getParameter("force_create");
			forceCreate = false;
			if (flag != null) {
				forceCreate = Boolean.parseBoolean(flag);
			}
			kml = getKMLForTrack(trackKey, forceCreate);
			resp.setHeader("Content-type",
					"application/vnd.google-earth.kml+xml");
			resp.setHeader("Content-Disposition",
					"attachment; filename=\"" + noxdroidKeyName +  ".kml\"");

			resp.setStatus(200);
			resp.getWriter().print(kml);
			break;
		case UNDEFINED:
			resp.setStatus(404);
			// TODO Write some better error codes
			resp.getWriter().print("An error happened");
		default:
			break;
		}
	}

	private JSONArray getTracksByNoxDROID(Key noxdroidKey) {
		DatastoreService datastore = DatastoreServiceFactory
				.getDatastoreService();
		Query q = new Query("Track");

		q.setAncestor(noxdroidKey);
		q.addSort("start_time", Query.SortDirection.DESCENDING);

		PreparedQuery pq = datastore.prepare(q);

		FetchOptions fetchOptions = FetchOptions.Builder.withDefaults();

		QueryResultList<Entity> results = pq.asQueryResultList(fetchOptions);

		JSONArray tracks = new JSONArray();
		for (Entity track : results) {
			Map<String, Object> properties = new HashMap<String, Object>();

			for (Entry<String, Object> property : track.getProperties()
					.entrySet()) {
				if (!property.getKey().equalsIgnoreCase("kml")) {
					properties.put(property.getKey(), property.getValue());
				}
			}
			properties.put("trackid", track.getKey().getName());
			properties.put("noxdroidid", track.getParent().getName());
			tracks.put(properties);

		}
		log.log(Level.ALL, tracks.toString());
		System.out.println(tracks.toString());
		return tracks;
	}

	private String getKMLForTrack(Key trackkey, boolean flag) {
		KMLManager generator = new KMLManager();
		return generator.getKML(trackkey, flag);
	}

	private ACTION getActionByString(String action) {
		System.out.println(ACTION.SINGLETRACKKML.name());
		System.out.println(ACTION.TRACKSBYNOXDROID.name());
		if (action.equalsIgnoreCase(ACTION.SINGLETRACKKML.name())) {
			
			return ACTION.SINGLETRACKKML;
		} else if (action.equalsIgnoreCase(ACTION.TRACKSBYNOXDROID.name())) {
			return ACTION.TRACKSBYNOXDROID;
		} else if (action.equalsIgnoreCase(ACTION.DOWNLOADKML.name())) {
			return ACTION.DOWNLOADKML;
		}
		return ACTION.TRACKSBYNOXDROID;
	}

	// Serverside
	// Get tracks by noxdroid id
	// Get kml by trackid
	// Get all map data for timespan, default today...
	//

	// Client side
	// Javascript for showing map
	// Ajax get to noxdroidcloudengine

}