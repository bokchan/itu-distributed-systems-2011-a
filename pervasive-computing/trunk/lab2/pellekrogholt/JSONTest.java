package lab2.pellekrogholt;


//import edu.princeton.cs.stdlib.StdOut; 
//import edu.princeton.cs.stdlib.StdRandom;
//import edu.princeton.cs.stdlib.Stopwatch;


import java.util.HashMap;

import org.eclipse.jetty.util.ajax.JSON; 
import org.junit.Test;  
import org.junit.Assert;

public class JSONTest
{  
	
	
	@Test
	public void testJSONParseStringToMap() 
	{ 		
		
		String json_string = "{\"last-event-description\":\"Device Detected\",\"last-event-timestamp\":1315250496330,\"location\":\"itu.zone4.zone4e\",\"major-class-of-device\":\"Not available\",\"terminal-id\":\"7C2F80173FC3\"}";
		
		HashMap<String, String> map;
		// the JSON.parse have to be type casted 
		map = (HashMap<String, String>) JSON.parse(json_string);
		
		Assert.assertEquals(null, map.get("location"), "itu.zone4.zone4e");
		
		// System.out.println(map);
		// prints
		// {last-event-description=Device Detected, location=itu.zone4.zone4e, last-event-timestamp=1315250496330, major-class-of-device=Not available, terminal-id=7C2F80173FC3}
		
		
	}

	
	@Test
	public void testJSONParseErrorString() 
	{ 		
		
		String json_string = "{ 'error' : 'null'}";

		System.out.println(json_string);
		
		String test = "{ 'error' : 'null'}";
		
		Assert.assertTrue(null, json_string.equals(test));
		
				
	}

	@Test
	public void testErrorStringReplace() 
	{ 		
		
		String json_string = "{ 'error' : 'null'}";

		json_string = json_string.replace("'", "\"");
		
		System.out.println(json_string);
		
		String test = "{ \"error\" : \"null\"}";

		Assert.assertTrue(null, json_string.equals(test));		
				
	}


	
	
	
	@Test
	public void testJSONParseList() 
	{ 		
		

//		String json_string = "{\"last-event-description\":\"Device Detected\",\"last-event-timestamp\":1315250496330,\"location\":\"itu.zone4.zone4e\",\"major-class-of-device\":\"Not available\",\"terminal-id\":\"7C2F80173FC3\"}";
		
		String json_string = "[{\"terminal-id\":\"000ea50050c0\"},{\"terminal-id\":\"002608d7c487\"},{\"terminal-id\":\"001167000000\"},{\"terminal-id\":\"60fb42744cc3\"},{\"terminal-id\":\"00236ca909cd\"}]"; 
		
		// todo: is this possible to parse ?
		/*
		 * looked through: http://wiki.eclipse.org/Jetty/Reference/jetty.xml_syntax#.3CArray.3E
		 * http://download.eclipse.org/jetty/stable-7/apidocs/org/eclipse/jetty/util/ajax/JSON.html 
		 *  
		 */
		
		
//		HashMap<String, String> map;
		// the JSON.parse have to be type casted 
//		map = JSON.parseArray((HashMap<String, String>) JSON.parse(json_string));
				
//		Assert.assertEquals(null, map.get("location"), "itu.zone4.zone4e");
		
//		 System.out.println(json_string);
		// prints
		// {last-event-description=Device Detected, location=itu.zone4.zone4e, last-event-timestamp=1315250496330, major-class-of-device=Not available, terminal-id=7C2F80173FC3}
		
	}
	
	
	
	
	
	
	
	
	
	
	
}
