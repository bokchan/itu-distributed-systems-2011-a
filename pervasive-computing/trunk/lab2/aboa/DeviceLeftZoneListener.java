package lab2.aboa;

import java.util.Map;

import dk.itu.infobus.ws.Listener;
import dk.itu.infobus.ws.PatternBuilder;
import dk.itu.infobus.ws.PatternOperator;

public class DeviceLeftZoneListener extends Listener {
	
		/**
		 * DeviceLeftZoneListener constructor
		 * 
		 * - so fare it make no sense to use terminals (a terminal is a device)
		 * since we like to make look up on zone (area). 
		 * 
		 * @param zone
		 */
		public DeviceLeftZoneListener(String zone) {
	
	        super(new PatternBuilder()
	        .addMatchAll("terminal.btmac")
	        .add("type", PatternOperator.EQ, "device.moved")
	        .add("zone.previous", PatternOperator.EQ, zone)
	        .getPattern());
	    }

	    public void onStarted(){}
	    public void cleanUp(){}

	    public void onMessage(Map<String,Object> message) {
	        String terminal = (String)message.get("terminal.btmac");
	        if(Terminal_old.remove(terminal)) {
	            System.out.println(terminal + " left " + message.get("zone.previous"));
	        }
	    }
	
	}
