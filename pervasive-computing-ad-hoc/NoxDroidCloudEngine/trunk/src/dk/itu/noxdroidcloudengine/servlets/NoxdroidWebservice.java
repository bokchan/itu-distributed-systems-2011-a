package dk.itu.noxdroidcloudengine.servlets;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.repackaged.org.json.JSONArray;

import dk.itu.noxdroidcloudengine.dataprocessing.KMLManager;
import dk.itu.noxdroidcloudengine.dataprocessing.KMLManager.KMLACTION;
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
		LISTTRACKS, SINGLETRACKKML, DOWNLOADKML, ALLTRACKS, NOXDROIDTRACKS, UNDEFINED
	}
	
	public static Enum<ACTION>[] ACTIONS = ACTION.class.getEnumConstants();

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
		Date date = null;
		Date from = null;
		Date to = null;
		String[] params;
		TimeZone z = TimeZone.getTimeZone("Europe/Copenhagen");
		if (!z.inDaylightTime(new Date())) {
			z.setDefault(TimeZone.getTimeZone("GMT+1"));
		} else {
			z.useDaylightTime();
			}
		
		
		
//		if (z.inDaylightTime(new Date())) {
//			z.setRawOffset(3600000);
//		}
		Calendar cal = GregorianCalendar.getInstance(z);
		
		if (req.getParameter("date") != null & req.getParameter("date").length() > 0){ 
			String[] paramDate = req.getParameter("date").split("/");
			
			cal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(paramDate[0]));
			cal.set(Calendar.MONTH, Integer.parseInt(paramDate[1])-1);
			cal.set(Calendar.YEAR, Integer.parseInt(paramDate[2]));
		} else {
			cal.set(Calendar.HOUR_OF_DAY, 0);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
		}
		
		date = cal.getTime();
		if (req.getParameter("from") != null && req.getParameter("from").length() > 0) {
			params = req.getParameter("from").split(":"); 
			cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(params[0]));
			cal.set(Calendar.MINUTE, Integer.parseInt(params[1]));
		}
		from = cal.getTime();
		
		if (req.getParameter("to") != null && req.getParameter("to").length() > 0) {
			params = req.getParameter("to").split(":"); 
			cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(params[0]));
			cal.set(Calendar.MINUTE, Integer.parseInt(params[1]));
			
		} else {
			cal.add(Calendar.DAY_OF_MONTH, 1);
		}
		to = cal.getTime();

		String trackKeyName;
		Key trackKey;
		String flag;
		String kml;
		KMLManager generator;
		boolean forceCreate = true;
		switch (getActionByString(action)) {
		case SINGLETRACKKML:
			trackKeyName = req.getParameter("track");
			trackKey = KeyFactory.createKey(noxdroidKey, "Track",
					trackKeyName);
			flag = req.getParameter("force_create");
			if (flag != null) {
				forceCreate = Boolean.parseBoolean(flag);
			}
			generator = new KMLManager();
			kml = generator.generateKML(trackKey, noxdroidKey, KMLACTION.SINGLETRACK, forceCreate, from, to);
			
			resp.setHeader("Content-type",
					"application/vnd.google-earth.kml+xml");
			resp.setHeader("Content-Disposition", "attachment; filename=\""
					+ noxdroidKeyName + ".kml\"");
			resp.setStatus(200);
			resp.getWriter().print(kml);
			break;
		case LISTTRACKS:
			// Add date choice
			generator = new KMLManager();
			JSONArray array = generator.getTracksByNoxDROID(noxdroidKey);
			resp.setHeader("Content-type","application/json;charset=UTF-8");
			resp.setHeader("Transfer-Encoding", "chunked");
			resp.setHeader("Access-Control-Allow-Origin", "*"); 
			resp.setCharacterEncoding("UTF-8");	
			resp.setStatus(200);
			resp.getWriter().print(array.toString());
			break;
		case ALLTRACKS :
			// Add date choice 
			generator = new KMLManager();
			kml = generator.generateKML(null, null, KMLACTION.ALLTRACKS, forceCreate, from, to);
			resp.setHeader("Content-type",
					"application/vnd.google-earth.kml+xml");
			resp.setHeader("Content-Disposition", "attachment; filename=\""
					+ noxdroidKeyName + ".kml\"");
			resp.setStatus(200);
			resp.getWriter().print(kml);
			System.out.println("Sent response");
			break;
		case NOXDROIDTRACKS: 
			// Add datechoice
			generator = new KMLManager();
			kml = generator.generateKML(null, noxdroidKey, KMLACTION.NOXDROIDTRACKS, forceCreate, from, to);
			resp.setHeader("Content-type",
					"application/vnd.google-earth.kml+xml");
			resp.setHeader("Content-Disposition", "attachment; filename=\""
					+ noxdroidKeyName + ".kml\"");
			resp.setStatus(200);
			resp.getWriter().print(kml);
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
			generator = new KMLManager();
			kml = generator.generateKML(trackKey, noxdroidKey, KMLACTION.DOWNLOADKML, forceCreate, null, null);
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

	private ACTION getActionByString(String action) {
		
		for (Enum<ACTION> e: ACTIONS) {
			if (e.name().equalsIgnoreCase(action))
				return (ACTION)e;
		}
		
		return ACTION.LISTTRACKS;
	}

	// Serverside
	// Get tracks by noxdroid id
	// Get kml by trackid
	// Get all map data for timespan, default today...
	// Add name to kml, center coordinate for trip
	// Upload alltracks to geocommons
	// make cronjob 

	// Client side
	// Javascript for showing map
	// Ajax get to noxdroidcloudengine
	// Response status on errors 
	// Zoom in more
	//NoxDROID stats : Accordion #tracks #nox links to that days data 
}