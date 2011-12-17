package dk.itu.noxdroidcloudengine.dataprocessing;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.boehn.kmlframework.kml.AltitudeModeEnum;
import org.boehn.kmlframework.kml.ColorModeEnum;
import org.boehn.kmlframework.kml.Document;
import org.boehn.kmlframework.kml.Feature;
import org.boehn.kmlframework.kml.Kml;
import org.boehn.kmlframework.kml.LineString;
import org.boehn.kmlframework.kml.LineStyle;
import org.boehn.kmlframework.kml.Placemark;
import org.boehn.kmlframework.kml.Point;
import org.boehn.kmlframework.kml.Style;
import org.boehn.kmlframework.kml.StyleSelector;
import org.boehn.kmlframework.kml.TimeStamp;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.GeoPt;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.QueryResultList;
import com.google.appengine.api.datastore.Text;

import dk.itu.noxdroidcloudengine.noxdroids.NoxDroidsListingServlet;

public class KMLManager_old {
	private double green_upperbound;
	private double yellow_upperbound;
	private String styleurl_green = "transGreenLine";
	private String styleurl_yellow = "transYellowLine";
	private String styleurl_red = "transRedLine";
	private ArrayList<Placemark> placemarks;

	private static final Logger log = Logger
			.getLogger(NoxDroidsListingServlet.class.getName());

	private static double nox_delta = Double.parseDouble(System
			.getProperty("noxdroidcloudengine.noxdelta"));

	private static SimpleDateFormat formatter = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");
	private static SimpleDateFormat date_formatter = new SimpleDateFormat(
			"dd-MM-yyyy");
	private static SimpleDateFormat time_formatter = new SimpleDateFormat(
			"HH:mm:ss");
	private DatastoreService datastore;
	public static enum NOXLEVEL {
		GREEN, YELLOW, RED
	}

	public static enum KMLACTION {
		ALLTRACKS, SINGLETRACK, NOXDROIDTRACKS, DOWNLOADKML
	}

	public static enum NOX {
		GREEN("5000ff00", NOXLEVEL.GREEN), YELLOW("5000ffff", NOXLEVEL.YELLOW), RED(
				"500000ff", NOXLEVEL.RED);
		String COLOR;
		NOXLEVEL LEVEL;

		NOX(String color, NOXLEVEL level) {
			this.COLOR = color;
			this.LEVEL = level;
		}
	};

	public KMLManager_old() {
		green_upperbound = Double.parseDouble(System
				.getProperty("noxdroid.green_uppperbound"));
		yellow_upperbound = Double.parseDouble(System
				.getProperty("noxdroid.yellow_uppperbound"));
		System.err.println("Green: " + green_upperbound);
		System.err.println("Yellow: " + yellow_upperbound);
	}

	@SuppressWarnings("unchecked")
	public String generateKML(Key trackKey, Key NoxDroidKey, KMLACTION action,
			boolean forceCreate) {
		datastore = DatastoreServiceFactory
				.getDatastoreService();
		switch (action) {
		case DOWNLOADKML:
		case SINGLETRACK:
			try {
				Entity track = datastore.get(trackKey);
				Object[] response = generateTrackKML(track.getKey());
				if (response.length == 2) {
					Kml kml = new Kml();
					Document kmldoc = createDocument(track.getParent().getName(), getTrackDescription(track));
					kml.setFeature(kmldoc);
					kmldoc.setFeatures((List<Feature>) response[0]);					
					return kml.toString();
				}
				return "";
				
			} catch (EntityNotFoundException e) {
				e.printStackTrace();
				return "";
			}
		case ALLTRACKS:
			return generateAllTracks(null, null, null, null);
		case NOXDROIDTRACKS:
			return generateAllTracks(null, null, NoxDroidKey, null);
		}
		return "";
	}
	

	@SuppressWarnings("unchecked")
	private String generateAllTracks(Date from, Date to, Key noxdroidKey, Key trackKey) {
		List<Entity> tracks = new ArrayList<Entity>();
		ArrayList<Point> centerpoints = new ArrayList<Point>();
		DatastoreService datastore = DatastoreServiceFactory
				.getDatastoreService();
		
		Query q = new Query("Track");
		if (noxdroidKey != null)
			q.setAncestor(noxdroidKey);

		System.err.println("Generating all tracks");
		if (from != null)
			q.addFilter("start_time_date",
					FilterOperator.GREATER_THAN_OR_EQUAL, from);

		if (to != null)
			q.addFilter("end_time_date", FilterOperator.LESS_THAN_OR_EQUAL, to);
		
		//q.addSort("start_time_date", SortDirection.ASCENDING);

		PreparedQuery pq = datastore.prepare(q);
		FetchOptions fetchOptions = FetchOptions.Builder.withDefaults();
		tracks = pq.asList(fetchOptions);
		
		List<Feature> _placemarks = new ArrayList<Feature>();
		
		for (Entity track : tracks) {
			Object[] response = generateTrackKML(track.getKey());
			if (response.length == 2) {
				_placemarks.addAll((List<Placemark>) response[0]);
				centerpoints.add((Point) response[1]);
			}
		}
		Kml kml = new Kml();
		Document kmldoc = createDocument(null, null);

		kml.setFeature(kmldoc);
		kmldoc.setFeatures(_placemarks);

		return kml.toString();
	}

	private Object[] generateTrackKML(Key trackKey) {
		DatastoreService datastore = DatastoreServiceFactory
				.getDatastoreService();

		System.err.println("Trackkey " + trackKey.getName());
		Query q = new Query("Location");
		FetchOptions fetchOptions = FetchOptions.Builder.withDefaults();
		q.setAncestor(trackKey);
		PreparedQuery pq = datastore.prepare(q);
		pq = datastore.prepare(q);
		
		List<Entity> locations = pq.asList(fetchOptions);
		System.out.println("#locations: " + locations.size());

		if (placemarks == null) {
			placemarks = new ArrayList<Placemark>();
		}
		placemarks.clear();

		if (locations.size() == 0)
			return new Object[0];
		Entity loc1 = locations.get(0);

		Entity track = null;
		try {
			track = datastore.get(trackKey);
		} catch (EntityNotFoundException e) {
			System.err.println("fucked up");
			e.printStackTrace();
		}

		// KML
		// Kml kml = new Kml();
		// Document kmldoc = createDocument(trackKey.getParent().getName(),
		// getTrackDescription(track));
		//
		// kml.setFeature(kmldoc);

		Object[] response = new Object[2];

		for (Entity loc2 : locations.subList(1, locations.size())) {
			Date t1 = null;
			if (( t1 = cast(loc1.getProperty("time_stamp_date"), Date.class)) == null && (t1 = tryParse((String)loc1.getProperty("time_stamp")))== null) {
				loc1 = loc2 ;
				continue;
			}
			Date t2 = null;
			if ((t2 = cast(loc2.getProperty("time_stamp_date"), Date.class)) == null && (t2 = tryParse((String)loc2.getProperty("time_stamp")))== null) {
				continue;
			}

			Query qNox = new Query("Nox");
			qNox.setAncestor(trackKey);
			PreparedQuery pqNox = datastore.prepare(qNox);
			qNox.addFilter("time_stamp_date",
					Query.FilterOperator.GREATER_THAN_OR_EQUAL, t1);
			qNox.addFilter("time_stamp_date",
					Query.FilterOperator.LESS_THAN_OR_EQUAL, t2);
			qNox.addSort("time_stamp_date", Query.SortDirection.ASCENDING);
			pqNox = datastore.prepare(qNox);
			QueryResultList<Entity> nox = pqNox.asQueryResultList(fetchOptions);
			System.out.println("#nox" + nox.size());

			// ArrayList<Entity> processed_nox = processNox(nox);

			/**
			 * GREEN: 0,255,0 YELLOW: 255,255,0 RED: 255,0,0 0.20 0.40
			 * 
			 * 
			 * 255/20
			 * 
			 */
			if (nox.size() == 0)
				continue;
			Double[][] points = extrapolateGPS(
					cast(loc1.getProperty("latitude"), Double.class),
					cast(loc1.getProperty("longitude"), Double.class),
					cast(loc2.getProperty("latitude"), Double.class),
					cast(loc2.getProperty("longitude"), Double.class),
					nox.size());

			Date timeStampDate = null;
			Placemark mark = null;
			LineString lineString = null;
			List<Point> coordinates = null;
			NOXLEVEL CURRENT_NOX_VAL = null;
			Entity centerPoint = locations.get((int) Math.floor(locations
					.size() / 2));

			Point center = new Point(cast(centerPoint.getProperty("longitude"),
					Double.class), cast(centerPoint.getProperty("latitude"),
					Double.class));
			response[1] = center;

			for (int i = 0; i < points.length; i++) {

				Entity _nox = nox.get(i);

				double nox_val = cast(_nox.getProperty("nox"), Double.class);
				timeStampDate = null;
				if ((timeStampDate = cast(_nox.getProperty("time_stamp_date"), Date.class)) == null && (timeStampDate = tryParse((String)_nox.getProperty("time_stamp")))== null) {
					continue;
				}
				String timeStamp = DateFormatUtils.formatUTC(timeStampDate,
						DateFormatUtils.ISO_DATETIME_TIME_ZONE_FORMAT
								.getPattern());

				/**
				 * If ni && nj are in same category add gps coord to placemark
				 * else add Placemark to doc and start new Placemark
				 */

				if (i == 0) {
					coordinates = new ArrayList<Point>();
					CURRENT_NOX_VAL = getNOXLEVEL(nox_val);
					mark = createPlacemark(getStyleURL(CURRENT_NOX_VAL),
							timeStamp);
					coordinates
							.add(new Point(points[i][1], points[i][0], 100d));
				} else if (!getNOXLEVEL(nox_val).equals(CURRENT_NOX_VAL)) {
					coordinates
							.add(new Point(points[i][1], points[i][0], 100d));
					lineString = createLineString(coordinates);
					mark.setGeometry(lineString);
					// kmldoc.addFeature(mark);
					placemarks.add(mark);

					if (i < points.length - 1) {
						// Create new
						CURRENT_NOX_VAL = getNOXLEVEL(nox_val);
						coordinates = new ArrayList<Point>();
						coordinates.add(new Point(points[i][1], points[i][0],
								100d));
						mark = createPlacemark(getStyleURL(CURRENT_NOX_VAL),
								timeStamp);
					}
					// add placemark
				} else {
					coordinates
							.add(new Point(points[i][1], points[i][0], 100d));
					if (i == points.length - 1) {
						lineString = createLineString(coordinates);
						mark.setGeometry(lineString);
						// kmldoc.addFeature(mark);
						placemarks.add(mark);
					}
				}
			}
			loc1 = loc2;
		}

		// track.setProperty("kml", new Text(kml.toString()));
		// datastore.put(track);
		// System.err.println(track.toString());
		// response[0] = kmldoc.getFeatures();
		
		response[0] = placemarks;
		return response;
	}

	public String generateKML(Key trackKey) {
		List<Entity> kml_points = new ArrayList<Entity>();
		DatastoreService datastore = DatastoreServiceFactory
				.getDatastoreService();
		System.err.println("Trackkey " + trackKey.getName());
		Query q = new Query("Location");
		q.setAncestor(trackKey);
		// q.addSort("time_stamp_date", Query.SortDirection.ASCENDING);

		PreparedQuery pq = datastore.prepare(q);
		FetchOptions fetchOptions = FetchOptions.Builder.withDefaults();

		QueryResultList<Entity> locations = pq.asQueryResultList(fetchOptions);
		System.err.println("Fetched locations");
		if (locations.size() == 0)
			return "ERROR";
		Entity loc1 = locations.get(0);

		Entity track = null;
		try {
			track = datastore.get(trackKey);
		} catch (EntityNotFoundException e) {
			System.err.println("fucked up");
			e.printStackTrace();
		}
		System.err.println("Fetched track");

		// KML
		Kml kml = new Kml();
		Document kmldoc = new Document();
		kml.setFeature(kmldoc);

		LineStyle lineStyle;
		Style green = new Style();
		lineStyle = new LineStyle(NOX.GREEN.COLOR, ColorModeEnum.normal, 5d);
		green.setLineStyle(lineStyle);
		green.setId(styleurl_green);

		Style yellow = new Style();
		lineStyle = new LineStyle(NOX.YELLOW.COLOR, ColorModeEnum.normal, 5d);
		yellow.setLineStyle(lineStyle);
		yellow.setId(styleurl_yellow);

		Style red = new Style();
		lineStyle = new LineStyle(NOX.RED.COLOR, ColorModeEnum.normal, 5d);
		red.setLineStyle(lineStyle);
		red.setId(styleurl_red);

		StyleSelector[] styles = { green, yellow, red };
		List<StyleSelector> styleSelectors = new ArrayList<StyleSelector>(
				Arrays.asList(styles));
		kmldoc.setStyleSelectors(styleSelectors);
		System.err.println("Prepared KML DOC");

		kmldoc.setName(trackKey.getParent().getName());
		kmldoc.setDescription(getTrackDescription(track));

		Entity centerPoint = locations
				.get((int) Math.floor(locations.size() / 2));

		Point center = new Point(cast(centerPoint.getProperty("longitude"),
				Double.class), cast(centerPoint.getProperty("latitude"),
				Double.class));
		kmldoc.setAddress(center.getLongitudeLatitudeAltitudeString());

		for (Entity loc2 : locations.subList(1, locations.size())) {
			Date t1 = (Date) loc1.getProperty("time_stamp_date");
			Date t2 = (Date) loc2.getProperty("time_stamp_date");

			Query qNox = new Query("Nox");
			qNox.setAncestor(trackKey);
			qNox.addFilter("time_stamp_date",
					Query.FilterOperator.GREATER_THAN_OR_EQUAL, t1);
			qNox.addFilter("time_stamp_date",
					Query.FilterOperator.LESS_THAN_OR_EQUAL, t2);
			qNox.addSort("time_stamp_date", Query.SortDirection.ASCENDING);
			PreparedQuery pqNox = datastore.prepare(qNox);
			QueryResultList<Entity> nox = pqNox.asQueryResultList(fetchOptions);

			// ArrayList<Entity> processed_nox = processNox(nox);

			/**
			 * GREEN: 0,255,0 YELLOW: 255,255,0 RED: 255,0,0 0.20 0.40
			 * 
			 * 
			 * 255/20
			 * 
			 */
			System.err.println("Fetched Nox");
			if (nox.size() == 0)
				continue;
			Double[][] points = extrapolateGPS(
					cast(loc1.getProperty("latitude"), Double.class),
					cast(loc1.getProperty("longitude"), Double.class),
					cast(loc2.getProperty("latitude"), Double.class),
					cast(loc2.getProperty("longitude"), Double.class),
					nox.size());

			System.err.println("Extrapolated GPS");
			Date timeStampDate = null;
			Placemark mark = null;
			LineString lineString = null;
			List<Point> coordinates = null;
			NOXLEVEL CURRENT_NOX_VAL = null;

			for (int i = 0; i < points.length; i++) {

				Entity _nox = nox.get(i);

				double nox_val = cast(_nox.getProperty("nox"), Double.class);
				Entity kml_point = new Entity("KMLPoint", trackKey);
				timeStampDate = cast(_nox.getProperty("time_stamp_date"),
						Date.class);
				String timeStamp = DateFormatUtils.formatUTC(timeStampDate,
						DateFormatUtils.ISO_DATETIME_TIME_ZONE_FORMAT
								.getPattern());

				kml_point.setProperty("time_stamp", timeStampDate);
				kml_point.setProperty("nox", _nox.getProperty("nox"));
				kml_point.setProperty("latitude", points[i][0]);
				kml_point.setProperty("longitude", points[i][1]);

				kml_point.setProperty("geo_point",
						new GeoPt(Float.parseFloat(points[i][0].toString()),
								Float.parseFloat(points[i][1].toString())));
				kml_points.add(kml_point);

				/**
				 * If ni && nj are in same category add gps coord to placemark
				 * else add Placemark to doc and start new Placemark
				 */

				if (i == 0) {
					coordinates = new ArrayList<Point>();
					CURRENT_NOX_VAL = getNOXLEVEL(nox_val);
					mark = createPlacemark(getStyleURL(CURRENT_NOX_VAL),
							timeStamp);
					coordinates
							.add(new Point(points[i][1], points[i][0], 100d));
				} else if (!getNOXLEVEL(nox_val).equals(CURRENT_NOX_VAL)) {
					coordinates
							.add(new Point(points[i][1], points[i][0], 100d));
					lineString = createLineString(coordinates);
					mark.setGeometry(lineString);
					kmldoc.addFeature(mark);

					if (i < points.length - 1) {
						// Create new
						CURRENT_NOX_VAL = getNOXLEVEL(nox_val);
						coordinates = new ArrayList<Point>();
						coordinates.add(new Point(points[i][1], points[i][0],
								100d));
						mark = createPlacemark(getStyleURL(CURRENT_NOX_VAL),
								timeStamp);
					}
					// add placemark
				} else {
					coordinates
							.add(new Point(points[i][1], points[i][0], 100d));
					if (i == points.length - 1) {
						lineString = createLineString(coordinates);
						mark.setGeometry(lineString);
						kmldoc.addFeature(mark);
					}
				}
			}
			loc1 = loc2;
		}

		System.err.println(kml_points);

		track.setProperty("kml", new Text(kml.toString()));
		track.setProperty("centerpoint", new GeoPt(center.getLatitude()
				.floatValue(), center.getLongitude().floatValue()));

		System.err.println(track.toString());
		datastore.put(track);
		System.err.println(kml.toString());

		return kml.toString();

		// Remove to write to db
		// datastore.put(kml_points);
	}

	private ArrayList<Entity> processNox(QueryResultList<Entity> nox) {
		ArrayList<Entity> nox_processed = new ArrayList<Entity>();
		Entity nox0 = nox.get(0);
		nox_processed.add(nox0);
		for (Entity nox1 : nox.subList(1, nox.size())) {
			Double x = (Double) nox0.getProperty("nox");
			Double y = (Double) nox1.getProperty("nox");
			if (Math.abs(x - y) < nox_delta) {
				continue;
			} else {
				nox_processed.add(nox1);
				nox0 = nox1;
			}
		}
		return nox_processed;
	}

	private Double[][] extrapolateGPS(double lat1, double lon1, double lat2,
			double lon2, int factor) {
		Double[][] points = new Double[factor][2];
		double deltaLat = (lat2 - lat1) / (double) factor;
		double deltaLong = (lon2 - lon1) / (double) factor;

		// Use first GPS point
		points[0][0] = lat1;
		points[0][1] = lon1;
		for (int i = 1; i < factor; i++) {
			points[i][0] = (i * deltaLat) + lat1;
			points[i][1] = (i * deltaLong) + lon1;
		}
		return points;
	}

	public static <T> T cast(Object o, Class<? extends T> c) {
		T t = null;
		try {
			t = (T) c.cast(o);
		} catch (IllegalArgumentException e) {

		}
		return t;
	}

	private Date tryParse(String dateStr) {
		Date dt = null;
		try {
			dt = formatter.parse(dateStr);
		} catch (ParseException e) {
			dt = null;
		}
		return dt;
	}

	private String getStyleURL(NOXLEVEL level) {
		switch (level) {
		case GREEN:
			return "#" + styleurl_green;
		case YELLOW:
			return "#" + styleurl_yellow;
		case RED:
			return "#" + styleurl_red;
		default:
			return "#" + styleurl_yellow;
		}
	}

	private NOXLEVEL getNOXLEVEL(double value) {
		if (value <= green_upperbound) {
			return NOXLEVEL.GREEN;
		} else if (value <= yellow_upperbound) {
			return NOXLEVEL.YELLOW;
		} else {
			return NOXLEVEL.RED;
		}
	}

	private LineString createLineString(List<Point> coordinates) {
		return new LineString(false, true, AltitudeModeEnum.relativeToGround,
				coordinates);
	}

	private Placemark createPlacemark(String style, String time_stamp) {

		Placemark p = new Placemark();
		p.setStyleUrl(style);
		p.setTimePrimitive(new TimeStamp(time_stamp));
		p.setVisibility(true);

		return p;
	}

	private Document createDocument(String name, String description) {

		StyleSelector[] styles = { Util.KML_STYLE.GREEN.STYLE,
				Util.KML_STYLE.YELLOW.STYLE, Util.KML_STYLE.RED.STYLE };
		List<StyleSelector> styleSelectors = new ArrayList<StyleSelector>(
				Arrays.asList(styles));
		Document kmldoc = new Document();
		kmldoc.setStyleSelectors(styleSelectors);
		if (name != null)
			kmldoc.setName(name);
		if (description != null)
			kmldoc.setDescription(description);
		return kmldoc;

	}

	public String getKML(Key trackid, boolean forceCreate) {
		DatastoreService datastore = DatastoreServiceFactory
				.getDatastoreService();
		Entity track = null;
		String kml = "";
		System.err.println("trackud " + trackid.getName());
		try {
			track = datastore.get(trackid);
			if (!track.hasProperty("kml") || forceCreate) {
				System.err.println("HasNot kml");
				kml = generateKML(trackid);
			} else {
				System.err.println("Has kml");
				kml = ((Text) track.getProperty("kml")).getValue();
			}
		} catch (EntityNotFoundException e) {
			e.printStackTrace();
		}
		return kml;
	}

	private String getTrackDescription(Entity track) {
		String description = "";
		Date start = null;
		if (( start = cast(track.getProperty("start_time_date"), Date.class)) == null && (start = tryParse((String)track.getProperty("start_time")))== null) {
			description += "No starttime found";
		}
		description += String.format("%s\t%s",
				date_formatter.format(start), time_formatter.format(start));
		Date end = null;
		if ((end = cast(track.getProperty("end_time_date"), Date.class)) == null && (end = tryParse((String)track.getProperty("end_time")))== null) {
			description += "\tNo endtime found";
		}
		description += String.format(" - %s", time_formatter.format(end));
		
		return description;
	}
	/**
	 * Make GEOPoints work Try out GEO ½½
	 */
}