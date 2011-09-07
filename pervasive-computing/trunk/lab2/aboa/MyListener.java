package lab2;

import java.io.IOException;
import java.util.Map;

import dk.itu.infobus.ws.EventBus;
import dk.itu.infobus.ws.Listener;
import dk.itu.infobus.ws.PatternBuilder;
import dk.itu.infobus.ws.PatternOperator;

public class MyListener extends Listener {
    public MyListener(String deviceAddress) {
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
    
    public static void main(String[] args) throws IOException {
    	EventBus eb = new EventBus("tiger.itu.dk",8004);
    	eb.start();

    	Listener listener = new MyListener("43:29:B1:55:00:00");

    	eb.addListener(listener);
    }
}