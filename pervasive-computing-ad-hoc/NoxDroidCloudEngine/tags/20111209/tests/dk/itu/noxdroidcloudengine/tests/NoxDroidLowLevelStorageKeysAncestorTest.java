package dk.itu.noxdroidcloudengine.tests;

import static org.junit.Assert.assertNotNull;

import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
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
public class NoxDroidLowLevelStorageKeysAncestorTest {
	
	private final LocalServiceTestHelper helper = new LocalServiceTestHelper(
			new LocalDatastoreServiceTestConfig());

	
	@Before
	public void setUp() {
		helper.setUp();
	}

	@After
	public void tearDown() {
		helper.tearDown();
	}

	
	
	@Test
	public void testAncestorsWithRootSensorKey() {

		
		DatastoreService localdatastore = DatastoreServiceFactory
		.getDatastoreService();
		
		
		long keyLong;
		String keyName;
		
		
		Key sensorKey1 = KeyFactory.createKey("SensorKey", "sensor1");
		Entity sensor1 = new Entity("Sensor", sensorKey1);

		Key sensorKey2 = KeyFactory.createKey("SensorKey", "sensor2");
		Entity sensor2 = new Entity("Sensor", sensorKey2);
		
		// bulk add items		
		List<Entity> sensors = Arrays.asList(sensor1, sensor2);
		localdatastore.put(sensors);
		

		//
		// Add tracks to sensor 1
		//
//		// Entity(type/kind, id/key, parent )
//		locationEntity = new Entity("Location", i + 1,
//				track.getKey());
		// now add some tracks
		Entity track1_1 = new Entity("Track", "track1", sensor1.getKey());
		Entity track1_2 = new Entity("Track", "track2", sensor1.getKey());
		Entity track1_3 = new Entity("Track", "track3", sensor1.getKey());

		// bulk add items
		List<Entity> tracks1 = Arrays.asList(track1_1, track1_2, track1_3);
		localdatastore.put(tracks1);

		System.out.println("track1 key: " + track1_1.getKey());
		System.out.println("track1 prop: " + track1_1.getProperties());
		System.out.println("track1 parent: " + track1_1.getParent());
		
		//
		// Add tracks to sensor 2
		//
		Entity track2_1 = new Entity("Track", "track1", sensor1.getKey());
		Entity track2_2 = new Entity("Track", "track2", sensor1.getKey());

		// bulk add items
		List<Entity> tracks2 = Arrays.asList(track2_1, track2_2);
		localdatastore.put(tracks2);
		
		
		
		// now make a query on tracks with ancestor sensor1
	    Query query = new Query("Track");
	    query.setAncestor(sensor1.getKey());
	    System.out.println("sensor1.getParent(): " + sensor1.getParent());
//	    query.addFilter("age", FilterOperator.GREATER_THAN, 25);

		PreparedQuery pq = localdatastore.prepare(query);

//		pq.countEntities();
		
		for (Entity item : pq.asIterable()) {
			System.out.println("item: " + item);
			System.out.println("key: " + item.getKey());
			System.out.println("parent: " + item.getParent());

			keyLong = item.getKey().getId();			
			System.out.println("key long: " + keyLong);
			
			keyName = item.getKey().getName();
			System.out.println("key name: " + keyName);

			// just a naive test
			assertNotNull(item);			
			
			System.out.println("---------------------------------------");
		}
		
		
		
		// ok lets create some recordings on track1 - locations/nox
		Entity location1_1 = new Entity("Location", "location1_1", track1_1.getKey());
		Entity location1_2 = new Entity("Location", "location1_2", track1_1.getKey());
		Entity location1_3 = new Entity("Location", "location1_3", track1_1.getKey());
		Entity location1_4 = new Entity("Location", "location1_4", track1_1.getKey());
		
		// bulk add items
		List<Entity> locations1 = Arrays.asList(location1_1, location1_2, location1_3, location1_4);
		localdatastore.put(locations1);

		// lets query
		Query queryLocations = new Query("Location");
		query.setAncestor(track1_1.getKey());
//	    query.addFilter("age", FilterOperator.GREATER_THAN, 25);

		PreparedQuery preparedQueryLocations = localdatastore.prepare(queryLocations);

//		pq.countEntities();
		

		for (Entity item : preparedQueryLocations.asIterable()) {
			System.out.println("item: " + item);
			System.out.println("key: " + item.getKey());
			System.out.println("parent: " + item.getParent());

			keyLong = item.getKey().getId();			
			System.out.println("key long: " + keyLong);
			
			keyName = item.getKey().getName();
			System.out.println("key name: " + keyName);

			// just a naive test
			assertNotNull(item);			
			
			System.out.println("---------------------------------------");
		}


		
		
		
		
		
	}
	
	
	
	

}