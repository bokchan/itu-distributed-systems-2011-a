package lab2.aboa;

import java.io.BufferedReader; 
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map.Entry;

import org.eclipse.jetty.util.ajax.JSON;

import dk.itu.infobus.ws.EventBus;
import dk.itu.infobus.ws.Listener;

public class EventBusClient1_2 {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		EventBusClient1_2 eb2 = new EventBusClient1_2();
        
		String zone_id = "itu.zone3.zone3d";
        String deviceAddress = "43:29:B1:55:00:00";
        
        for (Entry<String, String> t : getJSON("http://pit.itu.dk:7331/terminals-in/" + zone_id).entrySet() ) {
        	//Terminal t = new Terminal(deviceAddress);
        }
         
        
        EventBus eb = new EventBus("tiger.itu.dk",8004);
        eb.start();
        
        Listener zone_listener = new DeviceInZoneListener("itu.zone3.zone3d");
        Listener left_listener = new DeviceLeftZoneListener("itu.zone3.zone3d");

        eb.addListener(zone_listener);
        eb.addListener(left_listener);

	}
	
	public static HashMap<String, String> getJSON(String server_address) throws MalformedURLException {
		URL pitlab = new URL(server_address);
		URLConnection uc;
		try {
			uc = pitlab.openConnection();
			BufferedReader in = new BufferedReader(new InputStreamReader(
					uc.getInputStream()));

			StringBuilder sb = new StringBuilder();
			String inputLine = "";
			while ((inputLine = in.readLine()) != null)
				sb.append(inputLine);
			in.close();
			
			
			try {
				HashMap<String, String> obj = (HashMap<String, String>) JSON.parse(sb.toString());
				return obj;
			} catch (Exception e) {
				return new HashMap<String, String>();
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return new HashMap<String, String>();
	}  

}
