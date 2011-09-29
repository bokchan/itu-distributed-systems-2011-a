package assignment1.conference;

import java.rmi.RemoteException;

import assignment1.conference.entity.Display;
import assignment1.conference.entity.Participant;
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

public class RFIDContextTester extends AbstractContextClient {

	private RemoteEntityListenerImpl listener;

	// define some participant with rfid ids

	// E200 9037 8904 0121 1620 7040
	
	// E200 9037 8904 0121 0960 B576 is the one attached to the wall of pitLab
	// or
	// E200 9037 8904 0121 1620 7040 is the one attached to the wall of pitLab - mac address taken from a.book
	final Participant participant1 = new Participant("E200 9037 8904 0121 1620 7040", "Participant 1", "4329b1550000");
	
	// E200 9037 8904 0121 1860 5608 is the rfid 'hold by hand' (pelle) - mac address randomly picked from http://tiger.itu.dk:8000/ITUitter/ 
	final Participant participant2 = new Participant("E200 9037 8904 0121 1860 5608", "Participant 2", "0C6076A90D8D");

	// E200 9037 8904 0121 1540 7908 is the rfid 'hold by hand' (sbie) - mac address randomly picked from http://tiger.itu.dk:8000/ITUitter/
	final Participant participant3 = new Participant("E200 9037 8904 0121 1540 7908", "Participant 3", "0C6076A90D8D");
	
	// todo: clean up not sure about this constructor...
	final Display display = new Display("spisesal@itu.dk", 0, 'S', 10, "Display in spisesal", "Spisesal");
    
    final Located located = new Located(this.getClass().getName());
    
	
	public RFIDContextTester(String serviceUri) throws AlienReaderConnectionRefusedException, AlienReaderNotValidException, AlienReaderTimeoutException, AlienReaderConnectionException {
		super(serviceUri);

		// similar to the ParticipantDetector - jcaf tutorial/or demo 
		try {
			
            RFIDMonitor rfid_monitor = new RFIDMonitor(serviceUri, display, located);
            Thread t = new Thread(rfid_monitor);
            t.start();

			
		} catch (RemoteException e1) {
			
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
		new RFIDContextTester("conference");
	}
}
