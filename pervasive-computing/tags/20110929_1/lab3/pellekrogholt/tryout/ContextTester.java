package lab3.pellekrogholt.tryout;

import java.io.BufferedReader; 
import java.io.InputStreamReader;
import java.rmi.RemoteException;

import dk.itu.jcafdemo.entity.Room;
import dk.itu.jcafdemo.entity.Visitor;
import dk.itu.jcafdemo.item.Presentation;
import dk.itu.jcafdemo.relationship.Arrived;
import dk.itu.jcafdemo.relationship.Attending;
import dk.pervasive.jcaf.ContextEvent;
import dk.pervasive.jcaf.EntityListener;
import dk.pervasive.jcaf.entity.Place;
import dk.pervasive.jcaf.impl.RemoteEntityListenerImpl;
import dk.pervasive.jcaf.item.Location;
import dk.pervasive.jcaf.relationship.Located;
import dk.pervasive.jcaf.util.AbstractContextClient;

public class ContextTester extends AbstractContextClient {

	private RemoteEntityListenerImpl listener1;
	

//	final Visitor visitor1 = new Visitor("Poul", "Visitor 1");
	
	final Visitor visitor1 = new Visitor("q", "Visitor 1");
	final Visitor visitor2 = new Visitor("w", "Visitor 2");
	final Visitor visitor3 = new Visitor("e", "Visitor 3");
	final Visitor visitor4 = new Visitor("r", "Visitor 4");
	final Visitor visitor5 = new Visitor("t", "Visitor 5");
	final Visitor visitor6 = new Visitor("y", "Visitor 6");
	
	
	final Room hall = new Room("hall", 2, 'C', 10);	    
	final Arrived arrived = new Arrived(this.getClass().getName());
	
	public ContextTester(String serviceUri) {
		super(serviceUri);
		try {
			
			VisitorDetector detect = new VisitorDetector(serviceUri, hall, arrived);
			Thread t = new Thread(detect);
			t.start();
			
		} catch (RemoteException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		try {
			listener1 = new RemoteEntityListenerImpl();
			listener1.addEntityListener(new EntityListener() {
				
				@Override
				public void contextChanged(ContextEvent event) {
					System.out.println("Listener1: " + event.toString());
				}
			});
		} catch (RemoteException e) {
			e.printStackTrace();
		}

		ping();
		load();
		test();
		
	};

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
			getContextService().addEntity(visitor4);
			getContextService().addEntity(visitor5);
			getContextService().addEntity(visitor6);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
    }
    
    private void test() {
        try {
        	getContextService().addEntityListener(listener1, Visitor.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
	@Override
	public void run() {		
	}

	public static void main(String[] args) {
//		new ContextTester("rmi://10.25.237.227/spvc");
		new ContextTester("testcustom");
	}
}
