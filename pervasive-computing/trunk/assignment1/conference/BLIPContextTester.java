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
	final Display display = new Display("spisesal@itu.dk", 0, 'S', 10, "Display in spisesal");
    
    final Located located = new Located(this.getClass().getName());
    
	
	public BLIPContextTester(String serviceUri) throws AlienReaderConnectionRefusedException, AlienReaderNotValidException, AlienReaderTimeoutException, AlienReaderConnectionException {
		super(serviceUri);

		// similar to the ParticipantDetector - jcaf tutorial/or demo 
		try {
			
			// probably needs a room to ? so we can bind room and user through 
			// the blip ?
			BLIPMonitor monitor = new BLIPMonitor(serviceUri, located);
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
