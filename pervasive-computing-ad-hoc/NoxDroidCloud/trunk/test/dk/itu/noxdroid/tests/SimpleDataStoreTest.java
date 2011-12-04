package dk.itu.noxdroid.tests;

import static com.google.appengine.api.datastore.FetchOptions.Builder.withLimit;
import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
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
public class SimpleDataStoreTest {

    private final LocalServiceTestHelper helper =
        new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());


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
}