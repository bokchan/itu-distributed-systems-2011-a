package assignment1.conference.eventbus;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Map;

import assignment1.conference.entity.Room;
import assignment1.conference.relationship.Located;
import dk.itu.infobus.ws.Listener;
import dk.itu.infobus.ws.PatternBuilder;
import dk.itu.infobus.ws.PatternOperator;
import dk.pervasive.jcaf.ContextService;



public class DeviceInZoneListener extends Listener {

	private ContextService service;	
	private Located located = null;
	private Room room = null;	
	
	/**
	 * DeviceInZoneListener constructor
	 * 
	 * 
	 * - so fare it make no sense to use terminals (a terminal is a device)
	 * since we like to make look up on zone (area).
	 * 
	 * @param zone
	 * @throws NotBoundException 
	 * @throws RemoteException 
	 * @throws MalformedURLException 
	 */
	public DeviceInZoneListener(String zone, String service_uri, Located located, Room room) throws MalformedURLException, RemoteException, NotBoundException {
		super(new PatternBuilder().addMatchAll("terminal.btmac")
				.add("type", PatternOperator.EQ, "device.detected")
				.add("zone.current", PatternOperator.EQ, zone).getPattern());
		this.service = (ContextService) Naming.lookup(service_uri);
		this.located = located;
		this.room = room;
		
		System.out.print("DeviceInZoneListener called/started");
		
	}

	public void onStarted() {
	}

	public void cleanUp() {
	}

	public void onMessage(Map<String, Object> message) {
		String terminal = (String) message.get("terminal.btmac");
		if (Terminals.add(terminal)) {
			System.out.println(terminal + " in " + message.get("zone.current"));
		}
		
		try {
			
			// add terminal/device to the JCAF service
			service.addContextItem(terminal, this.located, this.room);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

}
