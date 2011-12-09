package dk.itu.noxdroidcloudengine.tests;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.junit.Test;

import com.google.appengine.repackaged.org.json.JSONArray;
import com.google.appengine.repackaged.org.json.JSONException;
import com.google.appengine.repackaged.org.json.JSONObject;

/*
 * Based upon
 * 
 * http://code.google.com/appengine/docs/java/tools/localunittesting.html#Introducing_App_Engine_Utilities
 * 
 */
public class JSONTest {

	private static String json = "{ \"locations\" : [{\"latitude\" : 55.659919, \"longitude\" : 12.591190, \"time_stamp\" : \"2011-12-04 09:12:04\"}, {\"latitude\" : 55.659919, \"longitude\" : 12.691190, \"time_stamp\" : \"2011-12-04 09:12:05\"}]}";

	/*
	 * based upon:
	 * 
	 * http://developer.android.com/reference/org/json/JSONObject.html
	 * http://www.json.org/javadoc/org/json/JSONObject.html
	 */
	@Test
	public void testJSONObject() throws JSONException {

		JSONObject jsonObj = new JSONObject(json);
		// human readable json- nice
		System.out.println("jsonObj: " + jsonObj.toString(4));

		JSONArray locationJSONArray = jsonObj.optJSONArray("locations");
		System.out.println("locations: " + locationJSONArray);

		// note: also rec.keys()
		double longitude = 0.0;
		double latitude = 0.0;

		for (int i = 0; i < locationJSONArray.length(); ++i) {
			JSONObject rec = locationJSONArray.getJSONObject(i);

			// note: also rec.keys()
			longitude = rec.getDouble("longitude");
			latitude = rec.getDouble("latitude");

			System.out.println("longitude: " + longitude + " latitude: "
					+ latitude);
		}

		// just a couple of naive test(s) - elaborate if needed
		assertNotNull(jsonObj);
		assertNotNull(locationJSONArray);

		assertNotSame(longitude, 0.0);
		assertNotSame(latitude, 0.0);

	}

	/*
	 * Build JSONObject with JSONObject's to get / build:
	 * 
	 * {"location": [ { "provider": "gps", "longitude": 12.59119, "latitude":
	 * 55.659919, "time_stamp": "2011-12-04 09:12:04" }, { "provider":
	 * "skyhook", "longitude": 13.59119, "latitude": 56.659919, "time_stamp":
	 * "2011-12-04 09:12:05" } ]}
	 */
	@Test
	public void testJSONObjectMultiple() throws JSONException {

		JSONObject jsonObj1 = new JSONObject();
		// put some real values
		jsonObj1.put("latitude", 55.659919);
		jsonObj1.put("longitude", 12.591190);
		jsonObj1.put("time_stamp", "2011-12-04 09:12:04");
		jsonObj1.put("provider", "gps");

		System.out.println(jsonObj1.toString(4));

		assertNotNull(jsonObj1);

		JSONObject jsonObj2 = new JSONObject();
		// put some real values
		jsonObj2.put("latitude", 56.659919);
		jsonObj2.put("longitude", 13.591190);
		jsonObj2.put("time_stamp", "2011-12-04 09:12:05");
		jsonObj2.put("provider", "skyhook");

		System.out.println(jsonObj2.toString(4));

		assertNotNull(jsonObj2);

		List<JSONObject> list = new ArrayList<JSONObject>();
		list.add(jsonObj1);
		list.add(jsonObj2);

		assertNotNull(list);

		// now we are ready to make the final JSON object including the list of
		// JSON objects
		JSONObject finalJSON = new JSONObject();
		finalJSON.put("location", list);
		//
		// warning! this approach does not work on/with the android/json
		// implementation
		// here you have to put list into an JSONArray and then put the
		// JSONArray into the JSONObject
		// like:
		// JSONArray jsArray = new JSONArray(list);
		// jsonLocationsFinal.put("locations", jsArray);
		// not sure whats *right*
		//

		System.out.println(finalJSON);
		System.out.println(finalJSON.toString(4));

		assertNotNull(finalJSON);

	}

	/*
	 * 
	 * Test json array feature with *real* data
	 */
	@Test
	public void testJSONLarge() throws JSONException {

		String jsonLarge = "{\"locations\": [{\"provider\":\"gps\",\"time_stamp\":\"2011-12-05 22:16:13\",\"longitude\":-112.256315,\"latitude\":36.101083333333335}, {\"provider\":\"gps\",\"time_stamp\":\"2011-12-05 22:16:13\",\"longitude\":-112.26262833333334,\"latitude\":36.10157}, {\"provider\":\"gps\",\"time_stamp\":\"2011-12-05 22:16:14\",\"longitude\":-112.26566166666667,\"latitude\":36.09445166666667}, {\"provider\":\"gps\",\"time_stamp\":\"2011-12-05 22:16:15\",\"longitude\":-112.26522333333332,\"latitude\":36.09520833333333}, {\"provider\":\"gps\",\"time_stamp\":\"2011-12-05 22:16:16\",\"longitude\":-112.26450666666666,\"latitude\":36.09580666666667}, {\"provider\":\"gps\",\"time_stamp\":\"2011-12-05 22:16:17\",\"longitude\":-112.26388166666666,\"latitude\":36.096284999999995}, {\"provider\":\"gps\",\"time_stamp\":\"2011-12-05 22:16:18\",\"longitude\":-112.263575,\"latitude\":36.09679166666667}, {\"provider\":\"gps\",\"time_stamp\":\"2011-12-05 22:16:19\",\"longitude\":-112.26357000000002,\"latitude\":36.0974}, {\"provider\":\"gps\",\"time_stamp\":\"2011-12-05 22:16:20\",\"longitude\":-112.26403,\"latitude\":36.09804833333333}, {\"provider\":\"gps\",\"time_stamp\":\"2011-12-05 22:16:21\",\"longitude\":-112.26432666666666,\"latitude\":36.09880166666667}, {\"provider\":\"gps\",\"time_stamp\":\"2011-12-05 22:16:22\",\"longitude\":-112.26424333333334,\"latitude\":36.099635}, {\"provider\":\"gps\",\"time_stamp\":\"2011-12-05 22:16:23\",\"longitude\":-112.26391333333335,\"latitude\":36.10055333333333}, {\"provider\":\"gps\",\"time_stamp\":\"2011-12-05 22:16:24\",\"longitude\":-112.26268833333333,\"latitude\":36.10149}, {\"provider\":\"gps\",\"time_stamp\":\"2011-12-05 22:16:25\",\"longitude\":-122.08489333333334,\"latitude\":37.42257}, {\"provider\":\"gps\",\"time_stamp\":\"2011-12-05 22:16:26\",\"longitude\":-122.08495666666667,\"latitude\":37.42211833333333}, {\"provider\":\"gps\",\"time_stamp\":\"2011-12-05 22:16:27\",\"longitude\":-122.08474666666667,\"latitude\":37.42207166666667}, {\"provider\":\"gps\",\"time_stamp\":\"2011-12-05 22:16:28\",\"longitude\":-122.08457166666668,\"latitude\":37.422088333333335}, {\"provider\":\"gps\",\"time_stamp\":\"2011-12-05 22:16:29\",\"longitude\":-122.08459333333333,\"latitude\":37.42215833333333}, {\"provider\":\"gps\",\"time_stamp\":\"2011-12-05 22:16:30\",\"longitude\":-122.08385166666667,\"latitude\":37.42227166666667}, {\"provider\":\"gps\",\"time_stamp\":\"2011-12-05 22:16:31\",\"longitude\":-122.08379166666667,\"latitude\":37.422035}, {\"provider\":\"gps\",\"time_stamp\":\"2011-12-05 22:16:32\",\"longitude\":-122.08350666666665,\"latitude\":37.422088333333335}, {\"provider\":\"gps\",\"time_stamp\":\"2011-12-05 22:16:33\",\"longitude\":-122.08347,\"latitude\":37.42201}, {\"provider\":\"gps\",\"time_stamp\":\"2011-12-05 22:16:34\",\"longitude\":-122.08312166666668,\"latitude\":37.422105}, {\"provider\":\"gps\",\"time_stamp\":\"2011-12-05 22:16:35\",\"longitude\":-122.082925,\"latitude\":37.422265}, {\"provider\":\"gps\",\"time_stamp\":\"2011-12-05 22:16:36\",\"longitude\":-122.08293333333334,\"latitude\":37.422311666666666}, {\"provider\":\"gps\",\"time_stamp\":\"2011-12-05 22:16:37\",\"longitude\":-122.08338333333334,\"latitude\":37.42224833333333}, {\"provider\":\"gps\",\"time_stamp\":\"2011-12-05 22:16:38\",\"longitude\":-122.08336,\"latitude\":37.42234166666666}, {\"provider\":\"gps\",\"time_stamp\":\"2011-12-05 22:16:39\",\"longitude\":-122.08341999999999,\"latitude\":37.42237}, {\"provider\":\"gps\",\"time_stamp\":\"2011-12-05 22:16:40\",\"longitude\":-122.08365833333333,\"latitude\":37.422511666666665}, {\"provider\":\"gps\",\"time_stamp\":\"2011-12-05 22:16:41\",\"longitude\":-122.08397500000001,\"latitude\":37.42265833333333}, {\"provider\":\"gps\",\"time_stamp\":\"2011-12-05 22:16:42\",\"longitude\":-122.08423666666667,\"latitude\":37.42265}, {\"provider\":\"gps\",\"time_stamp\":\"2011-12-05 22:16:43\",\"longitude\":-122.08450333333333,\"latitude\":37.42265}, {\"provider\":\"gps\",\"time_stamp\":\"2011-12-05 22:16:44\",\"longitude\":-122.08480166666668,\"latitude\":37.42261}, {\"provider\":\"gps\",\"time_stamp\":\"2011-12-05 22:16:45\",\"longitude\":-122.08478666666666,\"latitude\":37.42256333333333}, {\"provider\":\"gps\",\"time_stamp\":\"2011-12-05 22:16:46\",\"longitude\":-122.08489333333334,\"latitude\":37.42257}, {\"provider\":\"gps\",\"time_stamp\":\"2011-12-05 22:16:47\",\"longitude\":-122.08573999999999,\"latitude\":37.422268333333335}, {\"provider\":\"gps\",\"time_stamp\":\"2011-12-05 22:16:48\",\"longitude\":-122.08581666666666,\"latitude\":37.422313333333335}, {\"provider\":\"gps\",\"time_stamp\":\"2011-12-05 22:16:49\",\"longitude\":-122.08585166666666,\"latitude\":37.42230166666666}, {\"provider\":\"gps\",\"time_stamp\":\"2011-12-05 22:16:50\",\"longitude\":-122.08588,\"latitude\":37.42225666666666}, {\"provider\":\"gps\",\"time_stamp\":\"2011-12-05 22:16:51\",\"longitude\":-122.08588499999999,\"latitude\":37.42223}, {\"provider\":\"gps\",\"time_stamp\":\"2011-12-05 22:16:52\",\"longitude\":-122.08580666666667,\"latitude\":37.422201666666666}, {\"provider\":\"gps\",\"time_stamp\":\"2011-12-05 22:16:53\",\"longitude\":-122.08583666666667,\"latitude\":37.422138333333336}, {\"provider\":\"gps\",\"time_stamp\":\"2011-12-05 22:16:54\",\"longitude\":-122.08567166666666,\"latitude\":37.42208666666667}, {\"provider\":\"gps\",\"time_stamp\":\"2011-12-05 22:16:55\",\"longitude\":-122.08560166666666,\"latitude\":37.42214833333333}, {\"provider\":\"gps\",\"time_stamp\":\"2011-12-05 22:16:56\",\"longitude\":-122.08558833333333,\"latitude\":37.42212666666667}, {\"provider\":\"gps\",\"time_stamp\":\"2011-12-05 22:16:57\",\"longitude\":-122.08558333333333,\"latitude\":37.42208166666667}, {\"provider\":\"gps\",\"time_stamp\":\"2011-12-05 22:16:58\",\"longitude\":-122.085485,\"latitude\":37.422105}, {\"provider\":\"gps\",\"time_stamp\":\"2011-12-05 22:16:59\",\"longitude\":-122.08550666666666,\"latitude\":37.42214166666667}, {\"provider\":\"gps\",\"time_stamp\":\"2011-12-05 22:17:00\",\"longitude\":-122.08544166666667,\"latitude\":37.42212666666667}, {\"provider\":\"gps\",\"time_stamp\":\"2011-12-05 22:17:01\",\"longitude\":-122.08509833333333,\"latitude\":37.422511666666665}, {\"provider\":\"gps\",\"time_stamp\":\"2011-12-05 22:17:02\",\"longitude\":-122.08567666666667,\"latitude\":37.42281666666667}, {\"provider\":\"gps\",\"time_stamp\":\"2011-12-05 22:17:03\",\"longitude\":-122.08601500000002,\"latitude\":37.422448333333335}, {\"provider\":\"gps\",\"time_stamp\":\"2011-12-05 22:17:04\",\"longitude\":-122.085725,\"latitude\":37.422291666666666}, {\"provider\":\"gps\",\"time_stamp\":\"2011-12-05 22:17:05\",\"longitude\":-122.08573999999999,\"latitude\":37.422268333333335}, {\"provider\":\"gps\",\"time_stamp\":\"2011-12-05 22:17:06\",\"longitude\":-122.085785,\"latitude\":37.42136166666667}, {\"provider\":\"gps\",\"time_stamp\":\"2011-12-05 22:17:07\",\"longitude\":-122.08573,\"latitude\":37.421368333333334}, {\"provider\":\"gps\",\"time_stamp\":\"2011-12-05 22:17:08\",\"longitude\":-122.08573,\"latitude\":37.42140833333334}, {\"provider\":\"gps\",\"time_stamp\":\"2011-12-05 22:17:09\",\"longitude\":-122.08560666666666,\"latitude\":37.42138333333333}, {\"provider\":\"gps\",\"time_stamp\":\"2011-12-05 22:17:10\",\"longitude\":-122.08557833333334,\"latitude\":37.421371666666666}, {\"provider\":\"gps\",\"time_stamp\":\"2011-12-05 22:17:11\",\"longitude\":-122.08521833333334,\"latitude\":37.421371666666666}, {\"provider\":\"gps\",\"time_stamp\":\"2011-12-05 22:17:12\",\"longitude\":-122.08522666666667,\"latitude\":37.421616666666665}, {\"provider\":\"gps\",\"time_stamp\":\"2011-12-05 22:17:13\",\"longitude\":-122.08525999999999,\"latitude\":37.421605}, {\"provider\":\"gps\",\"time_stamp\":\"2011-12-05 22:17:14\",\"longitude\":-122.08525999999999,\"latitude\":37.42168166666667}, {\"provider\":\"gps\",\"time_stamp\":\"2011-12-05 22:17:15\",\"longitude\":-122.08523666666666,\"latitude\":37.4217}, {\"provider\":\"gps\",\"time_stamp\":\"2011-12-05 22:17:16\",\"longitude\":-122.08526333333333,\"latitude\":37.42176166666666}, {\"provider\":\"gps\",\"time_stamp\":\"2011-12-05 22:17:17\",\"longitude\":-122.08532333333332,\"latitude\":37.42176166666666}, {\"provider\":\"gps\",\"time_stamp\":\"2011-12-05 22:17:18\",\"longitude\":-122.085355,\"latitude\":37.42185166666666}, {\"provider\":\"gps\",\"time_stamp\":\"2011-12-05 22:17:19\",\"longitude\":-122.08540999999998,\"latitude\":37.42188833333333}, {\"provider\":\"gps\",\"time_stamp\":\"2011-12-05 22:17:20\",\"longitude\":-122.08548,\"latitude\":37.42189166666667}, {\"provider\":\"gps\",\"time_stamp\":\"2011-12-05 22:17:21\",\"longitude\":-122.08554333333335,\"latitude\":37.42188833333333}, {\"provider\":\"gps\",\"time_stamp\":\"2011-12-05 22:17:22\",\"longitude\":-122.08562500000001,\"latitude\":37.42186}, {\"provider\":\"gps\",\"time_stamp\":\"2011-12-05 22:17:23\",\"longitude\":-122.08593666666665,\"latitude\":37.42186}, {\"provider\":\"gps\",\"time_stamp\":\"2011-12-05 22:17:24\",\"longitude\":-122.08594166666666,\"latitude\":37.42160833333333}, {\"provider\":\"gps\",\"time_stamp\":\"2011-12-05 22:17:25\",\"longitude\":-122.085965,\"latitude\":37.42157833333333}, {\"provider\":\"gps\",\"time_stamp\":\"2011-12-05 22:17:26\",\"longitude\":-122.08586333333332,\"latitude\":37.42147}, {\"provider\":\"gps\",\"time_stamp\":\"2011-12-05 22:17:27\",\"longitude\":-122.08585333333333,\"latitude\":37.421405}, {\"provider\":\"gps\",\"time_stamp\":\"2011-12-05 22:17:28\",\"longitude\":-122.08580833333332,\"latitude\":37.421405}, {\"provider\":\"gps\",\"time_stamp\":\"2011-12-05 22:17:29\",\"longitude\":-122.085785,\"latitude\":37.42136166666667}, {\"provider\":\"gps\",\"time_stamp\":\"2011-12-05 22:17:30\",\"longitude\":-122.08443666666666,\"latitude\":37.42177166666667}, {\"provider\":\"gps\",\"time_stamp\":\"2011-12-05 22:17:31\",\"longitude\":-122.08451166666667,\"latitude\":37.421910000000004}, {\"provider\":\"gps\",\"time_stamp\":\"2011-12-05 22:17:32\",\"longitude\":-122.08504666666667,\"latitude\":37.42178666666667}, {\"provider\":\"gps\",\"time_stamp\":\"2011-12-05 22:17:33\",\"longitude\":-122.08507166666668,\"latitude\":37.421436666666665}, {\"provider\":\"gps\",\"time_stamp\":\"2011-12-05 22:17:34\",\"longitude\":-122.084915,\"latitude\":37.421371666666666}, {\"provider\":\"gps\",\"time_stamp\":\"2011-12-05 22:17:35\",\"longitude\":-122.08421833333333,\"latitude\":37.421371666666666}, {\"provider\":\"gps\",\"time_stamp\":\"2011-12-05 22:17:36\",\"longitude\":-122.08421833333333,\"latitude\":37.421475}, {\"provider\":\"gps\",\"time_stamp\":\"2011-12-05 22:17:37\",\"longitude\":-122.08380833333334,\"latitude\":37.42146}, {\"provider\":\"gps\",\"time_stamp\":\"2011-12-05 22:17:38\",\"longitude\":-122.08378833333333,\"latitude\":37.42131166666666}, {\"provider\":\"gps\",\"time_stamp\":\"2011-12-05 22:17:39\",\"longitude\":-122.08328000000002,\"latitude\":37.42129166666667}, {\"provider\":\"gps\",\"time_stamp\":\"2011-12-05 22:17:40\",\"longitude\":-122.08326000000001,\"latitude\":37.421391666666665}, {\"provider\":\"gps\",\"time_stamp\":\"2011-12-05 22:17:41\",\"longitude\":-122.08293666666665,\"latitude\":37.421371666666666}, {\"provider\":\"gps\",\"time_stamp\":\"2011-12-05 22:17:42\",\"longitude\":-122.082905,\"latitude\":37.421515}, {\"provider\":\"gps\",\"time_stamp\":\"2011-12-05 22:17:43\",\"longitude\":-122.08284833333333,\"latitude\":37.42176166666666}, {\"provider\":\"gps\",\"time_stamp\":\"2011-12-05 22:17:44\",\"longitude\":-122.08294333333333,\"latitude\":37.42176666666667}, {\"provider\":\"gps\",\"time_stamp\":\"2011-12-05 22:17:45\",\"longitude\":-122.08321666666667,\"latitude\":37.421791666666664}, {\"provider\":\"gps\",\"time_stamp\":\"2011-12-05 22:17:46\",\"longitude\":-122.08359666666665,\"latitude\":37.421746666666664}, {\"provider\":\"gps\",\"time_stamp\":\"2011-12-05 22:17:47\",\"longitude\":-122.08394500000001,\"latitude\":37.42169333333333}, {\"provider\":\"gps\",\"time_stamp\":\"2011-12-05 22:17:48\",\"longitude\":-122.08400666666667,\"latitude\":37.42176166666666}, {\"provider\":\"gps\",\"time_stamp\":\"2011-12-05 22:17:49\",\"longitude\":-122.08411333333333,\"latitude\":37.421746666666664}, {\"provider\":\"gps\",\"time_stamp\":\"2011-12-05 22:17:50\",\"longitude\":-122.084075,\"latitude\":37.42171166666667}, {\"provider\":\"gps\",\"time_stamp\":\"2011-12-05 22:17:51\",\"longitude\":-122.08414499999999,\"latitude\":37.42167833333333}, {\"provider\":\"gps\",\"time_stamp\":\"2011-12-05 22:17:52\",\"longitude\":-122.08414499999999,\"latitude\":37.42181666666667}, {\"provider\":\"gps\",\"time_stamp\":\"2011-12-05 22:17:53\",\"longitude\":-122.08424833333332,\"latitude\":37.42181666666667}, {\"provider\":\"gps\",\"time_stamp\":\"2011-12-05 22:17:54\",\"longitude\":-122.08443666666666,\"latitude\":37.42177166666667}, {\"provider\":\"gps\",\"time_stamp\":\"2011-12-05 22:17:55\",\"longitude\":-77.05788333333334,\"latitude\":38.87253166666667}, {\"provider\":\"gps\",\"time_stamp\":\"2011-12-05 22:17:56\",\"longitude\":-77.05465833333332,\"latitude\":38.872908333333335}, {\"provider\":\"gps\",\"time_stamp\":\"2011-12-05 22:17:57\",\"longitude\":-77.053155,\"latitude\":38.87053166666667}, {\"provider\":\"gps\",\"time_stamp\":\"2011-12-05 22:17:58\",\"longitude\":-77.055525,\"latitude\":38.86875666666666}, {\"provider\":\"gps\",\"time_stamp\":\"2011-12-05 22:17:59\",\"longitude\":-77.05844,\"latitude\":38.86996166666667}, {\"provider\":\"gps\",\"time_stamp\":\"2011-12-05 22:18:00\",\"longitude\":-77.05788333333334,\"latitude\":38.87253166666667}, {\"provider\":\"gps\",\"time_stamp\":\"2011-12-05 22:18:01\",\"longitude\":-77.05668,\"latitude\":38.871541666666666}, {\"provider\":\"gps\",\"time_stamp\":\"2011-12-05 22:18:02\",\"longitude\":-77.055425,\"latitude\":38.87167833333333}, {\"provider\":\"gps\",\"time_stamp\":\"2011-12-05 22:18:03\",\"longitude\":-77.05485,\"latitude\":38.870763333333336}, {\"provider\":\"gps\",\"time_stamp\":\"2011-12-05 22:18:04\",\"longitude\":-77.05577666666666,\"latitude\":38.870086666666666}, {\"provider\":\"gps\",\"time_stamp\":\"2011-12-05 22:18:05\",\"longitude\":-77.05691166666666,\"latitude\":38.87054333333333}, {\"provider\":\"gps\",\"time_stamp\":\"2011-12-05 22:18:06\",\"longitude\":-77.05668,\"latitude\":38.871541666666666}, {\"provider\":\"gps\",\"time_stamp\":\"2011-12-05 22:18:07\",\"longitude\":-112.33725000000001,\"latitude\":36.14888333333333}, {\"provider\":\"gps\",\"time_stamp\":\"2011-12-05 22:18:08\",\"longitude\":-112.33561166666665,\"latitude\":36.147815}, {\"provider\":\"gps\",\"time_stamp\":\"2011-12-05 22:18:09\",\"longitude\":-112.33681666666668,\"latitude\":36.146586666666664}, {\"provider\":\"gps\",\"time_stamp\":\"2011-12-05 22:18:10\",\"longitude\":-112.33843999999999,\"latitude\":36.14762666666666}, {\"provider\":\"gps\",\"time_stamp\":\"2011-12-05 22:18:11\",\"longitude\":-112.33725000000001,\"latitude\":36.14888333333333}, {\"provider\":\"gps\",\"time_stamp\":\"2011-12-05 22:18:12\",\"longitude\":-112.33965833333332,\"latitude\":36.146375}, {\"provider\":\"gps\",\"time_stamp\":\"2011-12-05 22:18:13\",\"longitude\":-112.33805833333332,\"latitude\":36.145316666666666}, {\"provider\":\"gps\",\"time_stamp\":\"2011-12-05 22:18:14\",\"longitude\":-112.33682500000002,\"latitude\":36.146595000000005}, {\"provider\":\"gps\",\"time_stamp\":\"2011-12-05 22:18:15\",\"longitude\":-112.33845500000001,\"latitude\":36.147625}, {\"provider\":\"gps\",\"time_stamp\":\"2011-12-05 22:18:16\",\"longitude\":-112.33965833333332,\"latitude\":36.146375}, {\"provider\":\"gps\",\"time_stamp\":\"2011-12-05 22:18:17\",\"longitude\":-112.334945,\"latitude\":36.14988666666667}, {\"provider\":\"gps\",\"time_stamp\":\"2011-12-05 22:18:18\",\"longitude\":-112.33540166666668,\"latitude\":36.149409999999996}, {\"provider\":\"gps\",\"time_stamp\":\"2011-12-05 22:18:19\",\"longitude\":-112.33444166666668,\"latitude\":36.148783333333334}, {\"provider\":\"gps\",\"time_stamp\":\"2011-12-05 22:18:20\",\"longitude\":-112.33312833333333,\"latitude\":36.14780666666666}, {\"provider\":\"gps\",\"time_stamp\":\"2011-12-05 22:18:21\",\"longitude\":-112.33170166666666,\"latitude\":36.14680666666666}, {\"provider\":\"gps\",\"time_stamp\":\"2011-12-05 22:18:22\",\"longitude\":-112.33113,\"latitude\":36.147416666666665}, {\"provider\":\"gps\",\"time_stamp\":\"2011-12-05 22:18:23\",\"longitude\":-112.33261499999999,\"latitude\":36.148453333333336}, {\"provider\":\"gps\",\"time_stamp\":\"2011-12-05 22:18:24\",\"longitude\":-112.33398666666666,\"latitude\":36.149265}, {\"provider\":\"gps\",\"time_stamp\":\"2011-12-05 22:18:25\",\"longitude\":-112.334945,\"latitude\":36.14988666666667}, {\"provider\":\"gps\",\"time_stamp\":\"2011-12-05 22:18:26\",\"longitude\":-112.33487666666666,\"latitude\":36.151399999999995}, {\"provider\":\"gps\",\"time_stamp\":\"2011-12-05 22:18:27\",\"longitude\":-112.33725333333332,\"latitude\":36.14888333333333}, {\"provider\":\"gps\",\"time_stamp\":\"2011-12-05 22:18:28\",\"longitude\":-112.33560666666666,\"latitude\":36.147815}, {\"provider\":\"gps\",\"time_stamp\":\"2011-12-05 22:18:29\",\"longitude\":-112.33500166666666,\"latitude\":36.148465}, {\"provider\":\"gps\",\"time_stamp\":\"2011-12-05 22:18:30\",\"longitude\":-112.33583500000002,\"latitude\":36.148961666666665}, {\"provider\":\"gps\",\"time_stamp\":\"2011-12-05 22:18:31\",\"longitude\":-112.33458833333334,\"latitude\":36.150261666666665}, {\"provider\":\"gps\",\"time_stamp\":\"2011-12-05 22:18:32\",\"longitude\":-112.33379333333332,\"latitude\":36.14978}, {\"provider\":\"gps\",\"time_stamp\":\"2011-12-05 22:18:33\",\"longitude\":-112.33317833333334,\"latitude\":36.15044666666667}, {\"provider\":\"gps\",\"time_stamp\":\"2011-12-05 22:18:34\",\"longitude\":-112.33487666666666,\"latitude\":36.151399999999995}]}";

		JSONObject jsonObj = new JSONObject(jsonLarge);
		// human readable json- nice
		System.out.println("jsonObj: " + jsonObj.toString(4));

		JSONArray locationJSONArray = jsonObj.optJSONArray("locations");

		if (locationJSONArray != null) {
			System.out.println("locationJSONArray: " + locationJSONArray);
		} else {
			System.out.println("locationJSONArray: something is wrong");
		}

		assertNotNull(locationJSONArray);

	}

	/*
	 * Android json api approach
	 */
	@Test
	public void testJSONArrayAppliedToJSONObject() throws JSONException {

		JSONObject jsonObj1 = new JSONObject();
		// put some real values
		jsonObj1.put("latitude", 55.659919);
		jsonObj1.put("longitude", 12.591190);
		jsonObj1.put("time_stamp", "2011-12-04 09:12:04");
		jsonObj1.put("provider", "gps");

		System.out.println(jsonObj1.toString(4));

		assertNotNull(jsonObj1);

		JSONObject jsonObj2 = new JSONObject();
		// put some real values
		jsonObj2.put("latitude", 56.659919);
		jsonObj2.put("longitude", 13.591190);
		jsonObj2.put("time_stamp", "2011-12-04 09:12:05");
		jsonObj2.put("provider", "skyhook");

		System.out.println(jsonObj2.toString(4));

		assertNotNull(jsonObj2);

		List<JSONObject> list = new ArrayList<JSONObject>();
		list.add(jsonObj1);
		list.add(jsonObj2);

		assertNotNull(list);

		JSONArray jsArray = new JSONArray(list);

		assertNotNull(jsArray);

		// now we are ready to make the final JSON object including the list of
		// JSON objects
		JSONObject finalJSON = new JSONObject();
		// finalJSON.put("location", list);
		finalJSON.put("locations", jsArray);

		System.out.println(finalJSON);
		System.out.println(finalJSON.toString(4));

		assertNotNull(finalJSON);

	}

	@Test
	public void testJSONExtend() throws JSONException {

		String extendJSON = "{\"nox_droid_device\" : \"android\", \"nox_droid_id\" : \"test_user_id\", \"nox_droid_user_name\" : \"Default user name\", \"track_id\" : \"eeb445dc-d2eb-494f-a4af-78c20b5d181c\", \"track_start_time\" : \"2011-12-04 09:10:04\", \"track_end_time\" : \"2011-12-04 09:20:04\", \"locations\" : [ {\"latitude\" : 55.659919, \"longitude\" : 12.591190, \"time_stamp\" : \"2011-12-04 09:12:04\", \"provider\" : \"gps\"},  {\"latitude\" : 55.659919, \"longitude\" : 12.691190, \"time_stamp\" : \"2011-12-04 09:12:05\", \"provider\" : \"skyhook\"} ], \"nox\" : [ {\"nox\" : 55.65, \"temperature\" : 0.0, \"time_stamp\" : \"2011-12-04 09:12:04\"},  {\"nox\" : 65.65, \"temperature\" : 0.0, \"time_stamp\" : \"2011-12-04 09:13:04\"}, ]}";

		JSONObject jsonObj = new JSONObject(extendJSON);

		// lets iterate over the keys
		Iterator<?> it = jsonObj.keys(); // gets all the keys

		while (it.hasNext()) {
			String key = it.next().toString(); // get key
			// Object o = jObj.get(key); // get value
			// session.putValue(key, o); // store in session

			System.out.println("key name: " + key);

			if (key.contentEquals("locations") || key.contentEquals("nox")) {
				// Android approach
				JSONArray jSONArray = jsonObj.optJSONArray(key);
				System.out.println(jSONArray.toString(4));
				assertNotNull(jSONArray);
			} else {

				String value = jsonObj.getString(key);
				System.out.println(value);
				assertNotNull(value);

			}

		}

		assertNotNull(jsonObj);

	}

}
