package assignment1.ubicompconference;

import java.rmi.RemoteException;

import assignment1.ubicompconference.entity.Display;
import assignment1.ubicompconference.entity.Visitor;
import assignment1.ubicompconference.monitor.DisplayMonitor;
import assignment1.ubicompconference.relationship.Arrived;
import dk.pervasive.jcaf.ContextEvent;
import dk.pervasive.jcaf.EntityListener;
import dk.pervasive.jcaf.impl.RemoteEntityListenerImpl;
import dk.pervasive.jcaf.util.AbstractContextClient;

public class Display1ContextTester extends AbstractContextClient {

	private RemoteEntityListenerImpl listener1;
//	private RemoteEntityListenerImpl listener2;

//    final Visitor visitor1 = new Visitor("visitor1@itu.dk", "Visitor 1");
//    final Visitor visitor2 = new Visitor("visitor2@itu.dk", "Visitor 2");
    
	final Visitor visitor1 = new Visitor("q", "Visitor 1");
	final Visitor visitor2 = new Visitor("w", "Visitor 2");
	final Visitor visitor3 = new Visitor("e", "Visitor 3");
	final Visitor visitor4 = new Visitor("r", "Visitor 4");
	final Visitor visitor5 = new Visitor("t", "Visitor 5");
	final Visitor visitor6 = new Visitor("y", "Visitor 6");
	
    final Display display1 = new Display("attrium@itu.dk", 0, 'N', 10);
//    final Display display2 = new Display("attrium@itu.dk", 0, 'S', 10);
    
    final Arrived arrived = new Arrived(this.getClass().getName());
    
    // try out
//    final Uses uses = new Uses(); 
    
    
//	final Arrived arrived = new Arrived(this.getClass().getName());
//	final Attending attending = new Attending();
	
	public Display1ContextTester(String serviceUri) {
		super(serviceUri);

		// similar to the VisitorDetector 
		try {
			// todo: figure out the arrive drelation ? should there be two ?
			DisplayMonitor display_monitor = new DisplayMonitor(serviceUri, display1, arrived);
			Thread t = new Thread(display_monitor);
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
	
					// todo: make logic here
					
					System.out.println("Listener1: " + event.toString());
				}
			});
			
			// todo: figure out this one
			
//			listener2 = new RemoteEntityListenerImpl();
//			listener2.addEntityListener(new EntityListener() {
//				
//				@Override
//				public void contextChanged(ContextEvent event) {
//					System.out.println("Listener2: " + event.toString());
//				}
//			});
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
			getContextService().addEntity(visitor4);
			getContextService().addEntity(visitor5);
			getContextService().addEntity(visitor6); 
            getContextService().addEntity(display1);
//            getContextService().addEntity(display2);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
    
    private void test() {
        try {
        	
        	// what does this exactly mean
        	getContextService().addEntityListener(listener1, Visitor.class);
        	
        	
//        	// todo?: why is Visitor.class passed as argument here and not in the next
//        	getContextService().addEntityListener(listener1, Visitor.class);
//        	getContextService().addEntityListener(listener2, "visitor2@itu.dk");
//
//        	
//        	
////        	System.out.println(getContextService().getContext(display1.getId()).toXML());
//        	
//        	
////            System.out.println(getContextService().getContext(visitor1.getId()).toXML());
//            
//            
//            getContextService().addContextItem(visitor1.getId(), arrived, display1);
//            
//            
//            getContextService().addContextItem(visitor2.getId(), arrived, display1);
////            
////            getContextService().addContextItem(visitor2.getId(), arrived, display2);
////            
//            // naive try out with uses but didn't play well	
////          getContextService().addContextItem(visitor2.getId(), uses, display2);
//            
//            
//            // lets try and remove some vistor - wrong approach 
////            getContextService().removeContextItem(service_uri, visitor1);
////            getContextService().removeContextItem(visitor2.getId(), display1);
//            
////            addContextClient
//            // public void addContextClient(int type, Class relation_type, RemoteContextClient client); 
//            
////            getContextService().addContextItem(visitor2.getId(), attending, new Presentation("presentation1@itu.dk", "JCAF Demo"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
	@Override
	public void run() {		
	}

	public static void main(String[] args) {
		new Display1ContextTester("testcustom");
	}
}
