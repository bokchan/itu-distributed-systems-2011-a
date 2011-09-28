package assignment1.conference;

import java.rmi.RemoteException; 

import assignment1.conference.entity.Display;
import assignment1.conference.entity.Participant;
import assignment1.conference.monitor.BLIPMonitor;
import assignment1.conference.monitor.RFIDMonitor;
import assignment1.conference.relationship.Located;

import com.alien.enterpriseRFID.reader.AlienReaderConnectionException;
import com.alien.enterpriseRFID.reader.AlienReaderConnectionRefusedException;
import com.alien.enterpriseRFID.reader.AlienReaderNotValidException;
import com.alien.enterpriseRFID.reader.AlienReaderTimeoutException;

import dk.pervasive.jcaf.ContextEvent;
import dk.pervasive.jcaf.EntityListener;
import dk.pervasive.jcaf.impl.RemoteEntityListenerImpl;
import dk.pervasive.jcaf.util.AbstractContextClient;

public class BLIPContextTester extends AbstractContextClient {

	private RemoteEntityListenerImpl listener;

	// define some participant with rfid ids

	// E200 9037 8904 0121 1620 7040
	
	// E200 9037 8904 0121 0960 B576 is the one attached to the wall of pitLab
	// or
	// E200 9037 8904 0121 1620 7040 is the one attached to the wall of pitLab - mac address is the one by book
	final Participant participant1 = new Participant("E200 9037 8904 0121 1620 7040", "Participant 1", "4329b1550000");
	
	// E200 9037 8904 0121 1860 5608 is the rfid 'hold by hand' (pelle) - mac address is taken randomly from http://tiger.itu.dk:8000/ITUitter/ 
	final Participant participant2 = new Participant("E200 9037 8904 0121 1860 5608", "Participant 2", "E4CE8F3C480D");

	// E200 9037 8904 0121 1540 7908 is the rfid 'hold by hand' (sbie) - mac address is taken randomly from http://tiger.itu.dk:8000/ITUitter/
	final Participant participant3 = new Participant("E200 9037 8904 0121 1540 7908", "Participant 3", "E4CE8F3C480D");
	
	// todo: clean up not sure about this constructor...
//	final Display display = new Display("spisesal@itu.dk", 0, 'S', 10, "Display in spisesal", "Spisesal");
    
//	final Display display = new Display("Doer syd@itu.dk", 0, 'S', 10, "Display at door sounth", "Dør syd");
	
	final Display display = new Display("Læsesal@itu.dk", 1, 'S', 10, "Display at Læsesal", "itu.zone1.zonelaesesal");
	
	// zone ids look here: http://pit.itu.dk:7331/locations/
	
	// [{"location-name":"itu.zone3.zone3c","location-id":"000ea50050bd"},{"location-name":"itu.zone2.zone2e","location-id":"000ea50050e6"},{"location-name":"itu.zone3.zone3e","location-id":"000ea50050b2"},{"location-name":"itu.zone0.zonedorsyd","location-id":"00a096091c36"},{"location-name":"itu.zone0.zoneanalog","location-id":"000ea5005dd5"},{"location-name":"itu.zone4.zone4d","location-id":"000ea50050aa"},{"location-name":"itu.zone-1.zonekaeldernord","location-id":"00a096091bef"},{"location-name":"itu.zone3.zone3b","location-id":"000ea50050ba"},{"location-name":"itu.zone1.zone1c","location-id":"000ea50050d8"},{"location-name":"itu.zone2.zone2e","location-id":"000ea50050e4"},{"location-name":"itu.zone4.zone4b","location-id":"000ea50050ed"},{"location-name":"itu.zone4.zone4e","location-id":"000ea50050cd"},{"location-name":"itu.zone4.zone4b","location-id":"000ea50050ee"},{"location-name":"itu.zone2.zone2m31","location-id":"000ea5005dcd"},{"location-name":"itu.zone1.zonelaesesal","location-id":"000ea50050e9"},{"location-name":"itu.zone2.zone2b","location-id":"000ea50050d2"},{"location-name":"itu.zone3.zone3d","location-id":"000ea50050c1"},{"location-name":"itu.zone4.zone4c1","location-id":"000ea5005dda"},{"location-name":"itu.zone5.zone5c","location-id":"000ea50050b6"},{"location-name":"itu.zone2.zone2m52","location-id":"000ea5005dc9"},{"location-name":"itu.zone2.zone2c","location-id":"000ea50050e0"},{"location-name":"itu.zone2.zone2m28","location-id":"000ea50050a1"},{"location-name":"itu.zone4.zone4c","location-id":"000ea50050ad"},{"location-name":"itu.zone5.zone5b","location-id":"000ea50050c4"},{"location-name":"itu.zone4.zone4c","location-id":"000ea50050ae"},{"location-name":"itu.zone0.zoneanalog","location-id":"000ea5005dd6"},{"location-name":"itu.zone2.zone2m18","location-id":"000ea5005dd1"},{"location-name":"itu.zone4.zone4d","location-id":"000ea50050a8"},{"location-name":"itu.zone3.zone3e","location-id":"000ea50050b1"},{"location-name":"itu.zone1.zonelaesesal","location-id":"000ea50050e8"},{"location-name":"itu.zone5.zone5e","location-id":"00a096091be2"},{"location-name":"itu.zone2.zone2m18","location-id":"000ea5005dd2"},{"location-name":"itu.zone3.zone3e","location-id":"000ea50050b0"},{"location-name":"itu.zone5.zone5c","location-id":"000ea50050b5"},{"location-name":"itu.zone2.zone2e","location-id":"000ea50050e5"},{"location-name":"itu.zone4.zone4e","location-id":"000ea50050cc"},{"location-name":"itu.zone3.zone3c","location-id":"000ea50050be"},{"location-name":"itu.zone1.zone1c","location-id":"000ea50050da"},{"location-name":"itu.zone2.zone2d","location-id":"000ea50050d6"},{"location-name":"itu.zone5.zone5b","location-id":"000ea50050c6"},{"location-name":"itu.zone3.zone3d","location-id":"000ea50050c0"},{"location-name":"itu.zone0.zoneanalog","location-id":"000ea5005dd4"},{"location-name":"itu.zone5.zone5b","location-id":"000ea50050c5"},{"location-name":"itu.zone2.zone2m52","location-id":"000ea5005dc8"},{"location-name":"itu.zone2.zone2m28","location-id":"000ea50050a0"},{"location-name":"itu.zone0.zoneaud2","location-id":"000ea50050a6"},{"location-name":"itu.zone2.zone2c","location-id":"000ea50050e1"},{"location-name":"itu.zone2.zone2m18","location-id":"000ea5005dd0"},{"location-name":"itu.zone0.zone0c","location-id":"00a096091cb1"},{"location-name":"itu.zone5.zone5c","location-id":"000ea50050b4"},{"location-name":"itu.zone0.zonekanspisesal","location-id":"000ea50050ca"},{"location-name":"itu.zone4.zone4b","location-id":"000ea50050ec"},{"location-name":"itu.zone0.zonekanspisesal","location-id":"000ea50050c9"},{"location-name":"itu.zone0.zonekanspisesal","location-id":"000ea50050c8"},{"location-name":"itu.zone0.zoneaud2","location-id":"000ea50050a4"},{"location-name":"itu.zone3.zone3b","location-id":"000ea50050b9"},{"location-name":"itu.zone1.zone1c","location-id":"000ea50050d9"},{"location-name":"itu.zone2.zone2m52","location-id":"000ea5005dca"},{"location-name":"itu.zone3.zone3c","location-id":"000ea50050bc"},{"location-name":"itu.zone0.zonekanvindfang","location-id":"00a096091eed"},{"location-name":"itu.zone2.zone2c","location-id":"000ea50050e2"},{"location-name":"itu.zone0.zonekandisk","location-id":"00a096091ce7"},{"location-name":"itu.zone4.zone4d","location-id":"000ea50050a9"},{"location-name":"itu.zone3.zone3d","location-id":"000ea50050c2"},{"location-name":"itu.zone4.zone4e","location-id":"000ea50050ce"},{"location-name":"itu.zone2.zone2b","location-id":"000ea50050d0"},{"location-name":"itu.zone4.zone4c1","location-id":"000ea5005dd9"},{"location-name":"itu.zone2.zone2m31","location-id":"000ea5005dce"},{"location-name":"itu.zone0.zoneaud1","location-id":"000ea50050f0"},{"location-name":"itu.zone2.zone2d","location-id":"000ea50050d5"},{"location-name":"itu.zone0.zoneaud1","location-id":"000ea50050f2"},{"location-name":"itu.zone0.zonescroll","location-id":"00a096091e98"},{"location-name":"itu.zone0.zonedornord","location-id":"00a096091c83"},{"location-name":"itu.zone5.zone5d","location-id":"00a096091f19"},{"location-name":"itu.zone0.zoneaud2","location-id":"000ea50050a5"},{"location-name":"itu.zone2.zone2m31","location-id":"000ea5005dcc"},{"location-name":"itu.zone-1.zonekaeldersyd","location-id":"00a096091c5d"},{"location-name":"itu.zone2.zone2b","location-id":"000ea50050d1"},{"location-name":"itu.zone3.zone3b","location-id":"000ea50050b8"},{"location-name":"itu.zone2.zone2m28","location-id":"000ea50050a2"},{"location-name":"itu.zone0.zoneaud1","location-id":"000ea50050f1"},{"location-name":"itu.zone4.zone4c1","location-id":"000ea5005dd8"},{"location-name":"itu.zone2.zone2d","location-id":"000ea50050d4"},{"location-name":"itu.zone4.zone4c","location-id":"000ea50050ac"},{"location-name":"itu.zone1.zonelaesesal","location-id":"000ea50050ea"}]	
	
    final Located located = new Located(this.getClass().getName());
    
	
	public BLIPContextTester(String serviceUri) throws AlienReaderConnectionRefusedException, AlienReaderNotValidException, AlienReaderTimeoutException, AlienReaderConnectionException {
		super(serviceUri);

		// similar to the ParticipantDetector - jcaf tutorial/or demo 
		try {
			
			// probably needs a room to ? so we can bind room and user through 
			// the blip ?
			BLIPMonitor monitor = new BLIPMonitor(serviceUri, display, located);
			Thread t = new Thread(monitor);
			t.start();
			
		} catch (RemoteException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
		
		try {
			listener = new RemoteEntityListenerImpl();
			listener.addEntityListener(new EntityListener() {
				
				@Override
				public void contextChanged(ContextEvent event) {
	
					// todo: make logic here
					
					System.out.println("Listener1: " + event.toString());
				}
			});
			
		} catch (RemoteException e) {
			e.printStackTrace();
		}

		ping();
		load();
		test();
	}

    private void ping() {
        try {
            System.out.println("Server info: \n   " + getContextService().getServerInfo());

        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Adding entities to the ContextService
     */
    private void load() {
        try {
        	
			getContextService().addEntity(participant1);
			getContextService().addEntity(participant2);
			getContextService().addEntity(participant3);
//			getContextService().addEntity(participant4);
//			getContextService().addEntity(participant5);
//			getContextService().addEntity(participant6); 
            getContextService().addEntity(display);
//            getContextService().addEntity(display2);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
    
    private void test() {
        try {
        	
        	// what does this exactly mean
        	getContextService().addEntityListener(listener, Participant.class);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
	@Override
	public void run() {		
	}

	public static void main(String[] args) throws AlienReaderConnectionRefusedException, AlienReaderNotValidException, AlienReaderTimeoutException, AlienReaderConnectionException {
		new BLIPContextTester("testcustom");
	}
}
