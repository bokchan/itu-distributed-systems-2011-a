package assignment1.conference.monitor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.rmi.RemoteException;
import java.util.HashMap;

import lab2.pellekrogholt.BlipClient;

import org.eclipse.jetty.util.ajax.JSON;

import assignment1.conference.relationship.Located;
import dk.pervasive.jcaf.util.AbstractMonitor;

public class BLIPMonitor extends AbstractMonitor {

	private Located located = null;
	
	
	public BLIPMonitor(String service_uri, Located located) throws RemoteException {
		super(service_uri);
		this.located = located;
		
	}
	

	@Override
	public void monitor(String arg0) throws RemoteException {

	}

	@Override
	public void run() {

		
		System.out.println("BLIPMonitor: thread in blip monitor startet and reading done");
		
		
//		// setup thread that keeps listing to blip system for a specific user / mac address
//		while(true) {
//			Thread t = new Thread();
//			t.start();
//			try {
//				System.out.println("BLIPMonitor: thread in blip monitor startet and reading done");
//
//				
//				
//				String point = "E4CE8F3C480D";
//				client = new BlipClient(point);
//				
//				getBlipLocation(client, point);
//				t.sleep(5000);
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}  

		
		
	}

//	/**
//	 * 
//	 */
//	private void getBlipLocation(BlipClient client, String mac_id) {
//		// TODO Auto-generated method stub
//		
//		
//		System.out.println("=======================");
//		
//		System.out.println("client");
//		
//		System.out.println("=======================");
//		
//		
////		HashMap<String, String> map; 
////		if ((map = (HashMap<String, String>) JSON.parse(client.toString())) != null) {
////			
////			String location = map.get("location");
////			if (!location.equals(previous_locations.get(mac_id))) {
////				System.out.println(location);
////			}
////			previous_locations.put(mac_id, location);
////			
////		} else {
////			System.out.println("Device has not turned on bluetooth");
////		}
//		
//		
////		E4CE8F3C480D
//		
//		
//	}
	
}
