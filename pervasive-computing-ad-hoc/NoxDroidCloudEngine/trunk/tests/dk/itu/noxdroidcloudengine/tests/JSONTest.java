package dk.itu.noxdroidcloudengine.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;

import java.util.HashMap;

import org.junit.Test;

import com.google.appengine.repackaged.org.json.JSONArray;
import com.google.appengine.repackaged.org.json.JSONException;
import com.google.appengine.repackaged.org.json.JSONObject;

import dk.itu.noxdroidcloudengine.tracks.AddTrackServlet;

/*
 * Based upon
 * 
 * http://code.google.com/appengine/docs/java/tools/localunittesting.html#Introducing_App_Engine_Utilities
 * 
 */
public class JSONTest {

	private static String json = "{ \"locations\" : [{\"latitude\" : \"55.659919\", \"longitude\" : \"12.591190\", \"time_stamp\" : \"2011-12-04 09:12:04\"}, {\"latitude\" : \"55.659919\", \"longitude\" : \"12.691190\", \"time_stamp\" : \"2011-12-04 09:12:05\"}]}";

//	@Test
	public void testAddition() {

		HashMap<String, String> map = AddTrackServlet.parseJSONLocation(json);
		System.out.println(map);

		// System.out.println(locations);

		assertEquals(4, 2 + 2);
	}

	
	/*
	 * based upon:
	 * 
	 * http://developer.android.com/reference/org/json/JSONObject.html
	 * 
	 * TODO: look up the right api for java/app engine
	 * 
	 * 
	 */
	 @Test
	 public void testJSONObject() throws JSONException {	
		 
		 JSONObject jsonObj = new JSONObject(json);
		 // human readable json- nice
		 System.out.println( "jsonObj: " + jsonObj.toString(4) );

		 JSONArray locationJSONArray = jsonObj.optJSONArray("locations");
		 System.out.println( "locations: " +  locationJSONArray);
		 

	    // note: also rec.keys()
	    double longitude = 0.0;
	    double latitude = 0.0;
		 
		 for (int i = 0; i < locationJSONArray.length(); ++i) {
			    JSONObject rec = locationJSONArray.getJSONObject(i);
			    
			    // note: also rec.keys()
			    longitude = rec.getDouble("longitude");
			    latitude = rec.getDouble("latitude");
			    
			    System.out.println( "longitude: " +  longitude + " latitude: " + latitude);	    
			}
	
		 // just a couple of naive test(s) - elaborate if needed
		 assertNotNull(jsonObj);
		 assertNotNull(locationJSONArray);
		 
		 assertNotSame(longitude, 0.0);
		 assertNotSame(latitude, 0.0);
		 
	 }

/* bellow is garbage just a reminder of the json 'detour' */	
	
	
//	/*
//	 * based upon:
//	 * 
//	 * http://answers.oreilly.com/topic/257-how-to-parse-json-in-java/
//	 */
//	 @Test
//	 public void testJSON() {
//	
//	
//	 // HashMap<String, String> map = AddTrackServlet.parseJSONLocation(json);
//	 // System.out.println(map);
//	
//	 String jsonTxt =
//	 "{'foo':'bar','coolness':2.0,'altitude':39000,'pilot':{'firstName':'Buzz','lastName':'Aldrin'},'mission':'apollo 11'}";
//	
//	 // JSONObject json = (JSONObject) JSONSerializer.toJSON( jsonTxt );
//	 // JSONObject json = (JSONObject) JSON.parse( jsonTxt );
////	 JSONObject json = (JSONObject) JSON.parse( jsonTxt );
//	 JSONObject json = (JSONObject) JSONSerializer.toJSON( jsonTxt );
//	 
//	 
//	 double coolness = json.getDouble( "coolness" );
//	 int altitude = json.getInt( "altitude" );
//	 JSONObject pilot = json.getJSONObject("pilot");
//	 String firstName = pilot.getString("firstName");
//	 String lastName = pilot.getString("lastName");
//	
//	 System.out.println( "Coolness: " + coolness );
//	 System.out.println( "Altitude: " + altitude );
//	 System.out.println( "Pilot: " + lastName );
//	
//	
//	 // System.out.println(locations);
//	
//	
//	 assertEquals(4, 2 + 2);
//	 }

	
	
	/*
	 * based upon https://sites.google.com/site/gson/gson-user-guide
	 */
//	@Test
//	public void testJSONMore() {
//
////		(Serialization)
//		Gson gson = new Gson();
//		gson.toJson(1);            //==> prints 1
//		gson.toJson("abcd");       //==> prints "abcd"
//		gson.toJson(new Long(10)); //==> prints 10
//		int[] values = { 1 };
//		gson.toJson(values);       //==> prints [1]
//
////		(Deserialization)
//		int one = gson.fromJson("1", int.class);
//		Integer one = gson.fromJson("1", Integer.class);
//		Long one = gson.fromJson("1", Long.class);
//		Boolean false = gson.fromJson("false", Boolean.class);
//		String str = gson.fromJson("\"abc\"", String.class);
//		String anotherStr = gson.fromJson("[\"abc\"]", String.class);
//		
//		assertEquals(4, 2 + 2);
//	}

}

