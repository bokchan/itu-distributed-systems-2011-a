package lab2;

import java.util.Map;

import dk.itu.infobus.ws.Listener;
import dk.itu.infobus.ws.PatternBuilder;
import dk.itu.infobus.ws.PatternOperator;

public class DeviceInZoneListener extends Listener {

	//Terminal terminals;

    public DeviceInZoneListener(String zone, Terminal terminals) {
        super(new PatternBuilder()
            .addMatchAll("terminal.btmac")
            .add("type", PatternOperator.EQ, "device.detected")
            .add("zone.current", PatternOperator.EQ, zone)
            .getPattern());
        //this.terminals = terminals;
    }

    public void onStarted(){}
    public void cleanUp(){}

    public void onMessage(Map<String,Object> message) {
        String terminal = (String)message.get("terminal.btmac");
        if(Terminal_old.add(terminal)) {
            System.out.println(terminal + " in " + message.get("zone.current"));
        }
    }


}
