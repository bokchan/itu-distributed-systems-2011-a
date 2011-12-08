package dk.itu.noxdroidcloudengine.tests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses( {
	NoxDroidLowLevelStorageIndexesTest.class,
	SimpleTest.class,
	NoxDroidLowLevelStorageTest.class,
	SimpleDataStoreTest.class,	
	NoxDroidLowLevelStorageKeysAncestorTest.class
	}
)
public class AllTests {
}
