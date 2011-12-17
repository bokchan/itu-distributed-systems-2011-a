package dk.itu.noxdroidcloudengine.servlets;

import java.io.IOException;
import java.net.SocketException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.net.ftp.FTPClient;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.QueryResultList;

public class AdministrationServlet extends HttpServlet {
	private static SimpleDateFormat formatter = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");
	
	public static enum ACTION {
		MISSINGDATES, MISSINGNOXORLOC, DOWNLOADKML, UNDEFINED, FTP
	}
	public static Enum<ACTION>[] ACTIONS = ACTION.class.getEnumConstants();
	
	ACTION action = null;
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		
		action = getActionByString(req.getParameter("action"));
		System.out.println("action: " +action.name());
		
		switch (action) {
		case MISSINGNOXORLOC:
			checkMissingNOXORLOC();
			break;
		case DOWNLOADKML:
			break;
		case MISSINGDATES :
			checkForMissingDates();
			break;
		case FTP: 
			ftp();
			break;
		case UNDEFINED :
			
			default :
				break;
		}

		
		resp.getWriter().print("DONE");
		
	}
	
	private void checkMissingNOXORLOC() {
		
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		Query q = new Query("Track");
		q.setKeysOnly();
		PreparedQuery pq = datastore.prepare(q);
		
		FetchOptions fetchOptions = FetchOptions.Builder.withDefaults();
		QueryResultList<Entity> results = pq.asQueryResultList(fetchOptions);
		
		System.out.println("Results: " + results.size());
		System.out.println("Noxdroid\tTrack\t\t\tLocCount\tNoxCount");
		for (Entity track :results ) {
			Query qSub = new Query("Nox");
			qSub.setAncestor(track.getKey());
			qSub.setKeysOnly();
			PreparedQuery pqSub = datastore.prepare(q);
			int countNox = pqSub.countEntities(fetchOptions);
			
			qSub = new Query("Location");
			qSub.setAncestor(track.getKey());
			qSub.setKeysOnly();
			pqSub = datastore.prepare(q);
			int countLoc =  pqSub.countEntities(fetchOptions);
			if (countNox == 0 || countLoc == 0) {				
				System.out.println(String.format("%s\t%s\t%s", track.getKey(), countLoc, countNox ));
			}
			
		}
		
		
	}
	
	
	private void checkForMissingDates() {
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		Query q = new Query("Track");
		PreparedQuery pq = datastore.prepare(q);
		
		FetchOptions fetchOptions = FetchOptions.Builder.withDefaults();
		QueryResultList<Entity> results = pq.asQueryResultList(fetchOptions);
		
		 
		for (Entity track : results ) {
			
			if (track.hasProperty("start_time") && track.getProperty("start_time") != null) {
				Date dateStart = tryParse(cast(track.getProperty("start_time"), String.class));
				if (dateStart != null)
					track.setProperty("start_time_date", dateStart);
			}
			
			if (track.hasProperty("end_time") && track.getProperty("end_time") != null) {
				Date dateEnd = tryParse(cast(track.getProperty("end_time"), String.class));
				if (dateEnd != null)
					track.setProperty("end_time_date", dateEnd);
			}
		}
		
		datastore.put(results);
		
	}
	
	
	
	private <T> T cast(Object o, Class<? extends T> c) {
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
	
	private ACTION getActionByString(String action) {
		
		for (Enum<ACTION> e: ACTIONS) {
			if (e.name().equalsIgnoreCase(action))
				return (ACTION)e;
		}
		return ACTION.UNDEFINED;
	}
	
	private void ftp() {
		FTPClient ftpclient = new FTPClient();
		
		
		try {
			ftpclient.connect("ftp.noxdroid.org");
			ftpclient.login("noxdroid", "Yaz39m83Bq");
			System.out.print(ftpclient.getReplyCode());
			System.out.print(ftpclient.getReplyString());
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		
	}
}
