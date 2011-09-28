package assignment1.conference.eventbus;

import java.util.Map;

import dk.itu.infobus.ws.Listener;
import dk.itu.infobus.ws.PatternBuilder;
import dk.itu.infobus.ws.PatternOperator;

public class DeviceInZoneListener extends Listener {

	/**
	 * DeviceInZoneListener constructor
	 * 
	 * 
	 * - so fare it make no sense to use terminals (a terminal is a device)
	 * since we like to make look up on zone (area).
	 * 
	 * @param zone
	 */
	public DeviceInZoneListener(String zone) {
		super(new PatternBuilder().addMatchAll("terminal.btmac")
				.add("type", PatternOperator.EQ, "device.detected")
				.add("zone.current", PatternOperator.EQ, zone).getPattern());
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
		
		// todo: set up with jcaf
		
		
	}

}
