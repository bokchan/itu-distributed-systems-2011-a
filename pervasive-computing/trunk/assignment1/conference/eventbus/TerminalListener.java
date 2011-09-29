package assignment1.conference.eventbus;

import java.util.Map;

import dk.itu.infobus.ws.Listener;
import dk.itu.infobus.ws.PatternBuilder;
import dk.itu.infobus.ws.PatternOperator;



/**
 * Eventbus TerminalListener 
 * 
 * 
 * can be used like:
 * 
 *     public static void main(String[] args) throws IOException {
 *   	EventBus eb = new EventBus("tiger.itu.dk",8004);
 *   	eb.start();
 *   	Listener listener = new TerminalListener("43:29:B1:55:00:00");
 *   	eb.addListener(listener);
 *   }
 * 
 * @author 
 *
 */
public class TerminalListener extends Listener {
	
	private String deviceaddress;
	private String name;
	private String lastUpdatedZone = "";
	/**
	 * TerminalListener constructor
	 * 
	 * @param deviceAddress
	 */
	public TerminalListener(String deviceAddress, String name) {
        super(new PatternBuilder()
        .add("terminal.btmac", PatternOperator.EQ, deviceAddress)          // get all events related to the given terminal
        .add("type", PatternOperator.EQ, "device.detected","device.moved") // and the property 'type' is either detected or moved
        .addMatchAll("zone.current")                                // and have a 'zone.current' property
        .getPattern()
        );
        this.name = name;
        this.deviceaddress = deviceAddress;
    }

    public void onStarted() { System.out.println("Hello, the listener has been registered!"); }
    public void cleanUp(){ System.out.println("Bye bye!"); }

    
    public void onMessage(Map<String,Object> evt) {
    	if (!lastUpdatedZone.equalsIgnoreCase(evt.get("zone.current").toString())) {
	    	System.out.println(name + " is in zone " +  evt.get("zone.current"));
			lastUpdatedZone = evt.get("zone.current").toString();
    	} 
    }
}