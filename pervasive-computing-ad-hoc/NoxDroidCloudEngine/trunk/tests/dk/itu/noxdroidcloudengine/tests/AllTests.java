package dk.itu.noxdroid.tests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses( {
	NoxDroidLowLevelStorageIndexesTest.class,
	SimpleTest.class,
	NoxDroidLowLevelStorageTest.class,
	SimpleDataStoreTest.class,	
	}
)
public class AllTests {
}
