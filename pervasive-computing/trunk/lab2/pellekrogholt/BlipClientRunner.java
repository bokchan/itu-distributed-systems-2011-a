package lab2.pellekrogholt;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;


/**
 * 
 * Blip Client 1.1
 *
 */
public class BlipClientRunner {

	public static void main (String args[]) throws Exception{

		
		BlipClient client = new BlipClient("38E7D820836E");
		
		BufferedReader in = client.device();
		
		
		String inputLine;
		while ((inputLine = in.readLine()) != null)
			System.out.println(inputLine);
		in.close();
		
		
//		example of out put {"last-event-description":"Device Detected","last-event-timestamp":1315225542558,"location":"itu.zone3.zone3d","major-class-of-device":"Not available","terminal-id":"38E7D820836E"}
		
		
	}
	

}