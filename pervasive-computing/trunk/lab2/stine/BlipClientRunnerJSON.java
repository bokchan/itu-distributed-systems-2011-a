package lab2.stine;

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

			// build string
			StringBuilder sb = new StringBuilder();
			String inputLine = "";
			while ((inputLine = in.readLine()) != null)
				sb.append(inputLine);
			in.close();
			
//			TODO: only parse sb with json if not sb = { 'error' : 'null'}  
			
			HashMap<String, String> obj; 
			if ((obj = (HashMap<String, String>) JSON.parse(sb.toString())) != null) {
				
				String location = obj.get("location");
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