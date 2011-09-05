package lab2.pellekrogholt;

import java.io.BufferedReader; 
import java.io.IOException;
import java.util.HashMap;

import org.eclipse.jetty.util.ajax.JSON;


/**
 * 
 * Blip Client 1.1
 *
 */
public class BlipClientRunnerJSON implements Runnable {


	static String previous_location = null;
	static BlipClient client;
	
//	static BufferedReader in;
	
	public static void main (String args[]) throws Exception{
		 
		String point = "38E7D820836E"; // pellekrogholt droid		
//		String point = "A4670684B501"; // PS IPAD

		
//		point = "7C2F80173FC3";
		
		client = new BlipClient(point);
		
		while(true) {

			// note: special thing have to pass class also into the thread since
			// it a main method
			Thread t = new Thread(new BlipClientRunnerJSON());
			t.start();
			t.sleep(5000);
			
		}  

		

	}
	


	@Override
	public void run() {
		// TODO Auto-generated method stub
		System.out.println("-------- thread call up start --------");
		
		try {

			BufferedReader in = client.device();

			// build string from buffer - similar to a browser request
			StringBuilder sb = new StringBuilder();
			String inputLine = "";
			while ((inputLine = in.readLine()) != null) {
//				System.out.println(inputLine);
				sb.append(inputLine);
			}
			in.close();
			
//			TODO: only parse sb with json if not sb = { 'error' : 'null'}  
			
//			Note the blip webservice unfortunately provides unfriendly json using ' and not "
//			tried something like if (!sb.equals("{ 'error' : 'null'}")) but didn't work well
			
//			String json_string;
//			
//			json_string = (String) sb.replace("'", "\"");
//
//			
			HashMap<String, String> map; 
			if ((map = (HashMap<String, String>) JSON.parse(sb.toString())) != null) {
				
				String location = map.get("location");
				if (!location.equals(previous_location)) {
					System.out.println(location);
				}
				previous_location = location;
				
			} else {
				System.out.println("Device has not truned on bluetooth");
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

}