package lab2.stine;
import java.util.Map;

import dk.itu.infobus.ws.Listener;
import dk.itu.infobus.ws.PatternBuilder;
import dk.itu.infobus.ws.PatternOperator;

//import dk.itu.infobus.ws.*;

public class EventBusMyListener extends Listener {
    
	public EventBusMyListener(String deviceAddress) {
        super(new PatternBuilder()
        .add("terminal.btmac", PatternOperator.EQ, deviceAddress)          // get all events related to the given terminal
        .add("type", PatternOperator.EQ, "device.detected","device.moved") // and the property 'type' is either detected or moved
        .addMatchAll("zone.current")                                // and have a 'zone.current' property
        .getPattern()
        );
    }

    public void onStarted() { System.out.println("Hello, the listener has been registered!"); }
    public void cleanUp(){ System.out.println("Bye bye!"); }

    public void onMessage(Map<String,Object> evt) {
        System.out.println( evt.get("zone.current") );
    }
}