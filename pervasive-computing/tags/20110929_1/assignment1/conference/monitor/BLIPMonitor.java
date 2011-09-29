package assignment1.conference.monitor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Map.Entry;

import org.eclipse.jetty.util.ajax.JSON;

import assignment1.conference.entity.Room;
import assignment1.conference.eventbus.DeviceInZoneListener;
import assignment1.conference.eventbus.DeviceLeftZoneListener;
import assignment1.conference.relationship.Located;
import dk.itu.infobus.ws.EventBus;
import dk.itu.infobus.ws.Listener;
import dk.pervasive.jcaf.util.AbstractMonitor;

// note wild shot:
// public class BLIPMonitor implements ContextService 
// and then extends Listeners

public class BLIPMonitor extends AbstractMonitor{

	private Located located = null;
	private Room room = null;

	public BLIPMonitor(String service_uri, Room room, Located located)
			throws RemoteException {
		super(service_uri);
		this.located = located;
		this.room = room;
	}

	@Override
	public void monitor(String arg0) throws RemoteException {
		
	}

	@Override
	public void run() {

		String zone_id = room.getBlipZoneId();

//		System.out.println("BLIPMonitor: Eventbus started listening to zone: "
//				+ zone_id);
		try {
			for (Entry<String, String> t : getJSON(
					"http://pit.itu.dk:7331/terminals-in/" + zone_id)
					.entrySet()) {
				// Terminal t = new Terminal(deviceAddress);
			}

			EventBus eb = new EventBus("tiger.itu.dk", 8004);
			eb.start();

			Listener zone_listener = new DeviceInZoneListener(zone_id,
					super.getURI(), located, room);
			Listener left_listener = new DeviceLeftZoneListener(zone_id,
					super.getURI(), located, room);

			eb.addListener(zone_listener);
			eb.addListener(left_listener);

			// getContextService().addContextItem(tag.toString(), rfid_located,
			// display);

		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NotBoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * getJSON
	 * 
	 * - split/insert to map a json request like:
	 * [{"terminal-id":"000ea50050c0"}
	 * ,{"terminal-id":"002608d7c487"},{"terminal-id"
	 * :"001167000000"},{"terminal-id"
	 * :"60fb42744cc3"},{"terminal-id":"00236ca909cd"}]
	 * 
	 * 
	 * @param server_address
	 * @return
	 * @throws MalformedURLException
	 */
	private static HashMap<String, String> getJSON(String server_address)
			throws MalformedURLException {
		URL blip_url = new URL(server_address);
		URLConnection uc;
		try {
			uc = blip_url.openConnection();
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
