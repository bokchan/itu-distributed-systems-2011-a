package dk.itu.noxdroidcloudengine.dataprocessing;

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
import com.google.appengine.api.datastore.QueryResultList;
import com.google.appengine.api.datastore.Text;

import dk.itu.noxdroidcloudengine.noxdroids.NoxDroidsListingServlet;


public class KMLManager {
	private double green_upperbound;
	private double yellow_upperbound;
	private String styleurl_green = "transGreenLine";
	private String styleurl_yellow = "transYellowLine";
	private String styleurl_red = "transRedLine";
	

	private static final Logger log = Logger
			.getLogger(NoxDroidsListingServlet.class.getName());

	private static double nox_delta = Double.parseDouble(System
			.getProperty("noxdroidcloudengine.noxdelta"));

	private static SimpleDateFormat formatter = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");

	public static enum NOXLEVEL {
		GREEN, YELLOW, RED
	}
	
	public static enum NOX {
		GREEN("5000ff00", NOXLEVEL.GREEN), YELLOW("5000ffff", NOXLEVEL.YELLOW), RED("500000ff", NOXLEVEL.RED);
		String COLOR;
		NOXLEVEL LEVEL;
		NOX(String color, NOXLEVEL level) {
			this.COLOR = color;
			this.LEVEL = level;
		}
	};

	public KMLManager() {
		green_upperbound = Double.parseDouble(System
				.getProperty("noxdroid.green_uppperbound"));
		yellow_upperbound = Double.parseDouble(System
				.getProperty("noxdroid.yellow_uppperbound"));
		System.out.println("Green: " + green_upperbound);
		System.out.println("Yellow: " + yellow_upperbound);
	}

	public String generateKML(Key trackKey) {
		List<Entity> kml_points = new ArrayList<Entity>();
		DatastoreService datastore = DatastoreServiceFactory
				.getDatastoreService();
		System.out.println("Trackkey " +  trackKey.getName());
		Query q = new Query("Location");
		q.setAncestor(trackKey);
		//q.addSort("time_stamp_date", Query.SortDirection.ASCENDING);

		PreparedQuery pq = datastore.prepare(q);
		FetchOptions fetchOptions = FetchOptions.Builder.withDefaults();

		QueryResultList<Entity> locations = pq.asQueryResultList(fetchOptions);
		System.out.println("Fetched locations");
		if (locations.size()==0) return null;
		Entity loc1 = locations.get(0);
		
		Entity track = null;
		try {
			track = datastore.get(trackKey);
		} catch (EntityNotFoundException e) {
			System.out.println("fucked up");
			e.printStackTrace();
		}
		System.out.println("Fetched track");
		
		// KML
		Kml kml = new Kml();
		Document kmldoc = new Document();
		kml.setFeature(kmldoc);
		
		LineStyle lineStyle;
		Style green = new Style();
		lineStyle= new LineStyle(NOX.GREEN.COLOR, ColorModeEnum.normal, 5d);
		green.setLineStyle(lineStyle);
		green.setId(styleurl_green);
		
		Style yellow =  new Style();
		lineStyle = new LineStyle(NOX.YELLOW.COLOR, ColorModeEnum.normal, 5d);
		yellow.setLineStyle(lineStyle);
		yellow.setId(styleurl_yellow);
		
		Style red = new Style();
		lineStyle = new LineStyle(NOX.RED.COLOR, ColorModeEnum.normal, 5d);
		red.setLineStyle(lineStyle);
		red.setId(styleurl_red);
		
		StyleSelector[] styles = {green,yellow, red}; 
		List<StyleSelector> styleSelectors = new ArrayList<StyleSelector>(Arrays.asList(styles));
		kmldoc.setStyleSelectors(styleSelectors);
		System.err.println("Prepared KML DOC");
		
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
			if (nox.size() == 0) continue; 
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
				System.out.println("nox_val " + nox_val);
				Entity kml_point = new Entity("KMLPoint", trackKey);
				timeStampDate = cast(_nox.getProperty("time_stamp_date"),
						Date.class);
				String timeStamp = DateFormatUtils.formatUTC(timeStampDate, DateFormatUtils.ISO_DATETIME_TIME_ZONE_FORMAT.getPattern());
				
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
					mark = createPlacemark(getStyleURL(CURRENT_NOX_VAL), timeStamp);
					coordinates.add(new Point(points[i][1], points[i][0], 100d));
				} else if (!getNOXLEVEL(nox_val).equals(CURRENT_NOX_VAL)) {
					coordinates.add(new Point(points[i][1], points[i][0], 100d));
					lineString = createLineString(coordinates);
					mark.setGeometry(lineString);
					kmldoc.addFeature(mark);
					
					if (i < points.length-1) {
					// Create new 
						CURRENT_NOX_VAL = getNOXLEVEL(nox_val);
						coordinates = new ArrayList<Point>();						
						coordinates.add(new Point(points[i][1], points[i][0], 100d));
						mark = createPlacemark(getStyleURL(CURRENT_NOX_VAL), timeStamp);
					}
					// add placemark
				} else {
					coordinates.add(new Point(points[i][1], points[i][0], 100d));
					if (i == points.length-1) {
						lineString = createLineString(coordinates);
						mark.setGeometry(lineString);
						kmldoc.addFeature(mark);
					}
				}
			}
		}
		
		System.out.println(kml_points);
		
		track.setProperty("kml", new Text(kml.toString()));
		
		System.out.println(track.toString());
		datastore.put(track);
		System.out.println(kml.toString());
		
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
		System.out.println(String.format("(%s,%s)(%s,%s)", lat1, lon1, lat2, lon2 ));
		double deltaLat = (lat2 - lat1) / (double) factor;
		double deltaLong =  (lon2 - lon1) / (double) factor;
		System.out.println("extrapolate: " + deltaLat + " " + deltaLong);
		// Use first GPS point
		points[0][0] = lat1;
		points[0][1] = lon1;
		for (int i = 1; i < factor; i++) {
			points[i][0] = (i * deltaLat) + lat1;
			points[i][1] = (i * deltaLong) + lon1;
		}
		System.out.println(points.toString());
		return points;
	}

	private <T> T cast(Object o, Class<? extends T> c) {
		return (T) c.cast(o);
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
		return new LineString(false, true, AltitudeModeEnum.relativeToGround, coordinates);
	}

	private Placemark createPlacemark(String style, String time_stamp) {

		Placemark p = new Placemark();
		p.setStyleUrl(style);
		p.setTimePrimitive(new TimeStamp(time_stamp));
		p.setVisibility(true);
		
		return p;
	}
	
	public String getKML(Key trackid, boolean forceCreate) {
		DatastoreService datastore = DatastoreServiceFactory
				.getDatastoreService();
		Entity track = null;
		String kml = "";
		System.out.println("trackud " + trackid.getName());
		try {
			track = datastore.get(trackid);
			if (!track.hasProperty("kml") || forceCreate) {
				System.out.println("HasNot kml");
				kml = generateKML(trackid);
			}  else {
				System.out.println("Has kml");
				kml = ((Text)track.getProperty("kml")).getValue();
			}
		} catch (EntityNotFoundException e) {
			e.printStackTrace();
		}
		return kml;
	}
	
	/**
	 * Make GEOPoints work 
	 * Try out GEO ½½
	 */
}