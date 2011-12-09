package dk.itu.noxdroidcloudengine.tests;

import static com.google.appengine.api.datastore.FetchOptions.Builder.withLimit;
import static org.junit.Assert.assertEquals;

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
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.QueryResultList;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
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
public class NoxDroidLowLevelStorageTest {

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

	// run this test twice to prove we're not leaking any state across tests
	private void doTest() {
		DatastoreService ds = DatastoreServiceFactory.getDatastoreService();

		Query q = new Query("yam"); // SELECT * FROM yam

		PreparedQuery pQuery = ds.prepare(q);
		int pQueryCount = pQuery.countEntities(withLimit(10));

		assertEquals(0, pQueryCount);
		ds.put(new Entity("yam"));
		ds.put(new Entity("yam"));
		assertEquals(2,
				ds.prepare(new Query("yam")).countEntities(withLimit(10)));
	}

	@Test
	public void testInsert1() {
		doTest();
	}

	@Test
	public void testInsert2() {
		doTest();
	}
	
	/*
	 * 
	 * For now without keys
	 * 
	 */
	@Test
	public void testNewEntities() {
		DatastoreService datastore = DatastoreServiceFactory
				.getDatastoreService();

		// Key sensorKey = KeyFactory.createKey("Activity", activityName);
		// Entity sensor = new Entity("Sensor", activityKey);

		// Entity new Entity("yam")
		Entity sensor = new Entity("Sensor");
		sensor.setProperty("id", "sensor1");
		sensor.setProperty("title", "Sensor 1");

		datastore.put(sensor);

		Query q = new Query("Sensor"); // SELECT * FROM sensor

		PreparedQuery pQuery = datastore.prepare(q);

		int pQueryCount = pQuery.countEntities(withLimit(10));
		assertEquals(1, pQueryCount);		
		
	}
	
	
	@Test
	public void testNewEntitiesWithAncestorChildren() {
		DatastoreService datastore = DatastoreServiceFactory
				.getDatastoreService();

// not sure about the key approach
//		 Key sensorKey = KeyFactory.createKey("SensorKey", "sensor1");
//			// add a sensor
//			Entity sensor = new Entity("Sensor", sensorKey);
		 
		 
		// add a sensor
		Entity sensor = new Entity("Sensor");
		sensor.setProperty("id", "sensor1");
		sensor.setProperty("title", "Sensor 1");
		datastore.put(sensor);
		
		System.out.println("print sensor key: " + sensor.getKey());
		
		
		// add a track
		Entity track = new Entity("Track", sensor.getKey());
		track.setProperty("id", "track1");
		track.setProperty("title", "title1");
		datastore.put(track);

		System.out.println("print track key: " + track.getKey());
		
		// add a location(s)
		Entity location = new Entity("Location", track.getKey());
		location.setProperty("longitude", 89.99999);
		location.setProperty("latitude", 60.99999);
		datastore.put(location);
		
//		double latitude = -122.084075;
//		double longitude = 37.4220033612141;

		Entity location2 = new Entity("Location", track.getKey());

		
		double latitude = -55.659919;
		double longitude = 12.59119;
		
		location2.setProperty("latitude", latitude);
		location2.setProperty("longitude", longitude);
		
		// note: 
		// additional try out to set the location using the GeoPt type 
		// http://code.google.com/appengine/docs/java/javadoc/com/google/appengine/api/datastore/GeoPt.html
		// tried:
		// location2.setProperty("geo", GeoPt(-122.0822035425683, 37.42228990140251));
		// but that didn't play well so based upon http://goo.gl/T8ws1
		// we tried:
		// GeoPt geoPoint = new GeoPt(Float.parseFloat("-122.0822035425683"),Float.parseFloat("37.42228990140251"));
		// but didn't play well so for now we simply do
		// GeoPt geoPoint = new GeoPt(Float.parseFloat("2.0"),Float.parseFloat("1.0"));
		//
		// solution
		// 
		// simply cast double to float - geo point only takes floats (less precise than double's)

		GeoPt geoPoint = new GeoPt((float) latitude, (float) longitude);
		
		
		location2.setProperty("geo", geoPoint);
		datastore.put(location2);		

		// add a nox(s)
		Entity nox = new Entity("Nox", track.getKey());
		nox.setProperty("value", 8989889);
		datastore.put(nox);		
		
		Entity nox2 = new Entity("Nox", track.getKey());
		nox2.setProperty("value", 8989889);
		datastore.put(nox2);		
		

		Query q = new Query("Sensor"); // SELECT * FROM sensor
		
		PreparedQuery pQuery = datastore.prepare(q);
				
		int pQueryCount = pQuery.countEntities(withLimit(10));
		assertEquals(1, pQueryCount);		
		
		// list the sensor(s) like we know from a servlet
		FetchOptions fetchOptions = FetchOptions.Builder.withLimit(10);
        QueryResultList<Entity> results = pQuery.asQueryResultList(fetchOptions);
        for (Entity entity : results) {
        	System.out.print(entity);
        }
		
        // how about the children ? - nox - location etc...
        
//		assertEquals(2,
//		datastore.prepare(new Query("Location")).countEntities(withLimit(10)));
//
//assertEquals(2,
//		datastore.prepare(new Query("Nox")).countEntities(withLimit(10)));
        
		
		
		
	}	
	

	
	/*
	 * Based upon
	 * 
	 * http://code.google.com/appengine/docs/java/tools/localunittesting.html#Introducing_App_Engine_Utilities
	
	To specify that an entity be created in an existing entity group, provide the Key of 
	the parent entity as an argument to the Entity constructor of the new entity. You can 
	get a Key object by calling the getKey() method on the parent Entity.
	
	Entity employee = new Entity("Employee");
	datastore.put(employee);
	
	Entity address = new Entity("Address", employee.getKey());
	
	If the entity with the parent also has a key name, provide the key name (a String) as 
	the second argument, and the Key of the parent entity as the third argument:
	
	Entity address = new Entity("Address", "addr1", employee.getKey());
	The complete key of an entity is the key of the parent, if any, followed by the kind 
	of the entity, followed by the key name or system ID. The key of the parent may also 
	include a parent, so the complete key represents the complete path of ancestors from 
	the root entity for the group to the entity itself. In this example, if the key for 
	the Employee entity is Employee:8261, the key for the address may be:
	Employee:8261 / Address:1
	 */
	@Test
	public void testNewEntitiesKeys() {
		DatastoreService datastore = DatastoreServiceFactory
				.getDatastoreService();
		
		// note: 
		// add a sensor with specified key
		//
		// if no key is specified it will get one automatically 
		// for that reason its possible to do:
		// Entity sensor1 = new Entity("Sensor");
		// Entity sensor2 = new Entity("Sensor");
		// Entity sensor3 = new Entity("Sensor");
		//
		// but we like a way to reference the sensor with a key
		//
		
		Key sensorKey = KeyFactory.createKey("SensorKey", "sensor1");
		
		System.out.println("print sensorKey: " + sensorKey);		
		
		Entity sensor = new Entity("Sensor", sensorKey);
		
		sensor.setProperty("id", "sensor1");
		sensor.setProperty("title", "Sensor 1");
		datastore.put(sensor);		

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
		
		Entity nox2 = new Entity("Nox", 2, track.getKey());
		nox2.setProperty("value", 8989889);
		
		// batch add nox
		List<Entity> noxs =  Arrays.asList(nox, nox2);
		datastore.put(noxs);		
		
		Query q = new Query("Sensor"); // SELECT * FROM Sensor
		PreparedQuery pQuery = datastore.prepare(q);
		
		// There should be 1 sensors
		assertEquals(1, pQuery.countEntities(withLimit(10)));
		
	}	
	
	
	/*
	Batch Operations
	The put(), get(), and delete() methods accept java.lang.Iterable Entity objects (for put()) and Key objects (for get() and delete()). This performs the action on multiple entities in a single datastore call. The batch operation groups all the entities/keys by entity group, then performs operations on each entity group in parallel.
	A batch call to the datastore is faster than making separate calls for each entity because it only incurs the overhead of one service call, and, if there are multiple entity groups involved, the work for each entity group is performed on the server side in parallel.
	import java.util.Arrays;
	import java.util.List;

	// ...
	Entity employee1 = new Entity("Employee");
	Entity employee2 = new Entity("Employee");
	Entity employee3 = new Entity("Employee");
	// ...

	List<Entity> employees = Arrays.asList(employee1, employee2, employee3);
	datastore.put(employees);
	A batch call to put() or delete() may succeed for some entities but not others. If it is important that the call succeed completely or fail completely, you must use a transaction, and all affected entities must be in the same entity group. Attempting a batch operation inside a transaction with entities or keys that belong to multiple entity groups results in an IllegalArgumentException.
	*/
	@Test
	public void testNewEntitiesBatch() {
		DatastoreService datastore = DatastoreServiceFactory
				.getDatastoreService();	
		
		Entity sensor1 = new Entity("Sensor");
		Entity sensor2 = new Entity("Sensor");
		Entity sensor3 = new Entity("Sensor");
		
		List<Entity> sensors = Arrays.asList(sensor1, sensor2, sensor3);
		datastore.put(sensors);
		
		Query q = new Query("Sensor"); // SELECT * FROM Sensor
		PreparedQuery pQuery = datastore.prepare(q);
		
		// There should be 3 sensors
		assertEquals(3, pQuery.countEntities(withLimit(10)));		
		
	
	}
	
	

}