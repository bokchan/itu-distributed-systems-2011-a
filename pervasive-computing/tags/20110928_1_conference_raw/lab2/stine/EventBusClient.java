package lab2.stine;

import dk.itu.infobus.ws.EventBus;
import dk.itu.infobus.ws.Listener;

//import dk.itu.infobus.comet.EventBus.*;

/**
 * 
 * EventBus Client 1.2
 *
 */
public class EventBusClient {


	public static void main (String args[]) throws Exception{
		
//		EventBus eb = new EventBus("tiger.itu.dk",8004);
		
		String deviceAddress = "38E7D820836E";
		
		EventBus eb = new EventBus("tiger.itu.dk",8004);
		eb.start();

		Listener listener = new EventBusMyListener(deviceAddress);

		eb.addListener(listener);	
		
		
		// print out example:
//		received clientid: 48419bfc-dce2-4f85-8358-427cb0db93bf
//		client id set 48419bfc-dce2-4f85-8358-427cb0db93bf
//		Hello, the listener has been registered!
//		itu.zone3.zone3d
//		itu.zone3.zone3e
//		itu.zone0.zonedorsyd
		
		
	}

}


