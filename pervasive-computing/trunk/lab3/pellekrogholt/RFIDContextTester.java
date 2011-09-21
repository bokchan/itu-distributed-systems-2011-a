package lab3.pellekrogholt;

import java.rmi.RemoteException; 

import com.alien.enterpriseRFID.reader.AlienReaderConnectionException;
import com.alien.enterpriseRFID.reader.AlienReaderConnectionRefusedException;
import com.alien.enterpriseRFID.reader.AlienReaderNotValidException;
import com.alien.enterpriseRFID.reader.AlienReaderTimeoutException;

import lab3.pellekrogholt.entity.Display;
import lab3.pellekrogholt.entity.Visitor;
//import lab3.pellekrogholt.monitor.DisplayMonitor;
import lab3.pellekrogholt.monitor.RFIDMonitor;
import lab3.pellekrogholt.relationship.Located;
import dk.pervasive.jcaf.ContextEvent;
import dk.pervasive.jcaf.EntityListener;
import dk.pervasive.jcaf.impl.RemoteEntityListenerImpl;
import dk.pervasive.jcaf.util.AbstractContextClient;

public class RFIDContextTester extends AbstractContextClient {

	private RemoteEntityListenerImpl display_rfid_listener;

	// define some visitor with rfid ids

	// E200 9037 8904 0121 0960 B576 is the one attached to the wall of pitLab
	final Visitor visitor1 = new Visitor("E200 9037 8904 0121 0960 B576", "Visitor 1");
	
	// E200 9037 8904 0121 1860 5608 is the rfid 'hold by hand' (pelle)
	final Visitor visitor2 = new Visitor("E200 9037 8904 0121 1860 5608", "Visitor 2");

	// E200 9037 8904 0121 1540 7908 is the rfid 'hold by hand' (sbie)
	final Visitor visitor3 = new Visitor("E200 9037 8904 0121 1540 7908", "Visitor 3");
	
	final Display display = new Display("spisesal@itu.dk", 0, 'S', 10);
    
	// todo: change into located or what ?
    final Located located = new Located(this.getClass().getName());
    
	
	public RFIDContextTester(String serviceUri) throws AlienReaderConnectionRefusedException, AlienReaderNotValidException, AlienReaderTimeoutException, AlienReaderConnectionException {
		super(serviceUri);

		// similar to the VisitorDetector - jcaf tutorial/or demo 
		try {
			RFIDMonitor rfid_monitor = new RFIDMonitor(serviceUri, display, located);
			Thread t = new Thread(rfid_monitor);
			t.start();
			
		} catch (RemoteException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
		
		try {
			display_rfid_listener = new RemoteEntityListenerImpl();
			display_rfid_listener.addEntityListener(new EntityListener() {
				
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
        	
			getContextService().addEntity(visitor1);
			getContextService().addEntity(visitor2);
			getContextService().addEntity(visitor3);
//			getContextService().addEntity(visitor4);
//			getContextService().addEntity(visitor5);
//			getContextService().addEntity(visitor6); 
            getContextService().addEntity(display);
//            getContextService().addEntity(display2);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
    
    private void test() {
        try {
        	
        	// what does this exactly mean
        	getContextService().addEntityListener(display_rfid_listener, Visitor.class);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
	@Override
	public void run() {		
	}

	public static void main(String[] args) throws AlienReaderConnectionRefusedException, AlienReaderNotValidException, AlienReaderTimeoutException, AlienReaderConnectionException {
		new RFIDContextTester("testcustom");
	}
}
