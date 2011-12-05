package dk.itu.noxdroidcloudengine.tests;

import static com.google.appengine.api.datastore.FetchOptions.Builder.withLimit;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.GeoPt;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.QueryResultList;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;

/*
 * Based upon
 * 
 * http://code.google.com/appengine/docs/java/tools/localunittesting.html#Introducing_App_Engine_Utilities
 * 
 * be 100% sure to have all the libs in place!
 * - ${SDK_ROOT}/lib/impl/appengine-api.jar
 * - ${SDK_ROOT}/lib/impl/appengine-api-labs.jar
 * - ${SDK_ROOT}/lib/impl/appengine-api-stubs.jar (this one was tricky for us)
 * 
 * - ${SDK_ROOT}/lib/testing/appengine-testing.jar
 * 
 */
public class NoxDroidLowLevelStorageIndexesTest {

	private final LocalServiceTestHelper helper = new LocalServiceTestHelper(
			new LocalDatastoreServiceTestConfig());

	private final DatastoreService datastore = DatastoreServiceFactory
	.getDatastoreService();
	
	@Before
	public void setUp() {
		helper.setUp();
		setUpEntities();
	}

	@After
	public void tearDown() {
		helper.tearDown();
	}

	/*
	 * Based upon:
	 * 
	 * http://code.google.com/appengine/docs/java/datastore/entities.html
	 * 
	 */
	public void setUpEntities() {

		// add a sensor(s)
		Entity sensor = new Entity("Sensor");
		sensor.setProperty("id", "sensor1");
		sensor.setProperty("title", "Sensor 1");		
		
		Entity sensor2 = new Entity("Sensor");
		Entity sensor3 = new Entity("Sensor");
		
		// bulk add sensor
		List<Entity> sensors = Arrays.asList(sensor, sensor2, sensor3);
		datastore.put(sensors);

		

		System.out.println("print sensor key: " + sensor.getKey());
				
		// add a track - now with the second argument as keyname 
		// of the track entity - hope we get/got this wrong
		Entity track = new Entity("Track", "track1", sensor.getKey());
		track.setProperty("id", "track1");
		track.setProperty("title", "title1");
		
		System.out.println("print track 1 key: " + track.getKey());
		

		// add another track
		Entity track2 = new Entity("Track", "track2", sensor.getKey());
		track2.setProperty("id", "track2");
		track2.setProperty("title", "title2");
		
		System.out.println("print track key 2: " + track2.getKey());
		
		// batch add tracks
		List<Entity> tracks =  Arrays.asList(track, track2);
		datastore.put(tracks);		

		// add locations and nos to track 1
		
		// add a location(s)
		Entity location = new Entity("Location", 1, track.getKey());
		location.setProperty("longitude", 89.99999);
		location.setProperty("latitude", 60.99999);
		
		System.out.println("print location 1 key: " + location.getKey());
		
		Entity location2 = new Entity("Location", 2, track.getKey());
		location2.setProperty("latitude", -122.084075);
		location2.setProperty("longitude", 37.4220033612141);
		
		// batch add locations
		List<Entity> locations =  Arrays.asList(location, location2);
		datastore.put(locations);		

		// add a nox(s)
		// TODO: can they have same keys as location ? 
		Entity nox = new Entity("Nox", 1, track.getKey());
		nox.setProperty("value", 8989889);
		
		System.out.println("print nox 1 key: " + nox.getKey());
		
		Entity nox2 = new Entity("Nox", 2, track.getKey());
		nox2.setProperty("value", 8989889);
		
		// batch add nox
		List<Entity> noxs =  Arrays.asList(nox, nox2);
		datastore.put(noxs);
	}
	

	@Test
	public void testNewEntitiesKeys() {
		
		
		
		Query q = new Query("Sensor"); // SELECT * FROM Sensor
		PreparedQuery pQuery = datastore.prepare(q);
		
		// There should be 3 sensors
		assertEquals(3, pQuery.countEntities(withLimit(10)));
		
	}	
	

	
	/*
	 * Based upon:
	 * 
	 * http://code.google.com/appengine/docs/java/datastore/queries.html
	 * 
	 * The filter operator can be any of the following:
	 * 
		Query.FilterOperator.LESS_THAN
		Query.FilterOperator.LESS_THAN_OR_EQUAL
		Query.FilterOperator.EQUAL
		Query.FilterOperator.GREATER_THAN
		Query.FilterOperator.GREATER_THAN_OR_EQUAL
		Query.FilterOperator.NOT_EQUAL
		Query.FilterOperator.IN (equal to any of the values in the provided list)
	 *
	 */
	@Test
	public void testQueriesBasic() {
	
		// The Query interface assembles a query
		Query q = new Query("Sensor");
		q.addFilter("title", Query.FilterOperator.EQUAL, "Sensor 1");
//		q.addFilter("height", Query.FilterOperator.LESS_THAN, maxHeightParam);

		// PreparedQuery contains the methods for fetching query results
		// from the datastore
		PreparedQuery pq = datastore.prepare(q);

		for (Entity result : pq.asIterable()) {
		  String id = (String) result.getProperty("id");
		  String title = (String) result.getProperty("title");
//		  Long height = (Long) result.getProperty("height");
		  System.out.println(id + " " + title);
		  //+ ", " + height.toString() + " inches tall"
		}
		
	}
	
	
	/*
	 * example of getting keys only of a specific entity - Sensor
	 * 
	 * probably also handsome because that will limit the read counts ?
	 * 
	 */
	@Test
	public void testQueriesBasicListKeys() {
				
		Query q = new Query("Sensor").setKeysOnly();
		PreparedQuery pq = datastore.prepare(q);
		
		for (Entity result : pq.asIterable()) {
			System.out.println(result.getKey());

			// just a naive test
			assertNotNull(result);
		}
		
	}

	
	
	/**
	 * 
	 * a sort could be
	 * .addSort("time", Query.SortDirection.DESCENDING);
	 * at least worked on the date from sqlite
	 * 
	 */
	@Test
	public void testQueriesBasicTrack() {
		
		Query q = new Query("Track");
		PreparedQuery pq = datastore.prepare(q);
		
		for (Entity result : pq.asIterable()) {
			System.out.println(result);

			// just a naive test
			assertNotNull(result);
		}
		
	}
	

	@Test
	public void testQueriesBasicNox() {
		
		Query q = new Query("Nox");
		PreparedQuery pq = datastore.prepare(q);
		
		for (Entity result : pq.asIterable()) {
			System.out.println(result);

			// just a naive test
			assertNotNull(result);
		}
		
	}
	
	
	@Test
	public void testQueriesBasicLocation() {
		
		Query q = new Query("Location");
		PreparedQuery pq = datastore.prepare(q);
		
		for (Entity result : pq.asIterable()) {
			System.out.println(result);

			// just a naive test
			assertNotNull(result);
		}
		
	}
	
	
	
	
	
	
	
	

}