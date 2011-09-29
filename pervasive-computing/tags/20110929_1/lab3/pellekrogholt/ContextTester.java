/*
 * 
 * use DisplayContextTester + Display2ContextTester
 * 
 */



package lab3.pellekrogholt;

import java.rmi.RemoteException; 

//import dk.itu.jcafdemo.entity.Room;
//import dk.itu.jcafdemo.entity.Visitor;
//import dk.itu.jcafdemo.item.Presentation;
//import dk.itu.jcafdemo.relationship.Arrived;
//import dk.itu.jcafdemo.relationship.Attending;


import lab3.pellekrogholt.entity.Display;
import lab3.pellekrogholt.entity.Visitor;
import lab3.pellekrogholt.relationship.Arrived;
import dk.pervasive.jcaf.ContextEvent;
import dk.pervasive.jcaf.EntityListener;
import dk.pervasive.jcaf.entity.Place;
import dk.pervasive.jcaf.impl.RemoteEntityListenerImpl;
import dk.pervasive.jcaf.item.Location;
import dk.pervasive.jcaf.relationship.Uses;
import dk.pervasive.jcaf.util.AbstractContextClient;

public class ContextTester extends AbstractContextClient {

	private RemoteEntityListenerImpl listener1;
	private RemoteEntityListenerImpl listener2;

    final Visitor visitor1 = new Visitor("visitor1@itu.dk", "Visitor 1");
    final Visitor visitor2 = new Visitor("visitor2@itu.dk", "Visitor 2");
    
    final Display display1 = new Display("attrium@itu.dk", 0, 'N', 10);
    final Display display2 = new Display("attrium@itu.dk", 0, 'S', 10);
    
    final Arrived arrived = new Arrived(this.getClass().getName());
    
    // try out
//    final Uses uses = new Uses(); 
    
    
//	final Arrived arrived = new Arrived(this.getClass().getName());
//	final Attending attending = new Attending();
	
	public ContextTester(String serviceUri) {
		super(serviceUri);

		try {
			listener1 = new RemoteEntityListenerImpl();
			listener1.addEntityListener(new EntityListener() {
				
				@Override
				public void contextChanged(ContextEvent event) {
					System.out.println("Listener1: " + event.toString());
				}
			});
			
			listener2 = new RemoteEntityListenerImpl();
			listener2.addEntityListener(new EntityListener() {
				
				@Override
				public void contextChanged(ContextEvent event) {
					System.out.println("Listener2: " + event.toString());
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
            getContextService().addEntity(display1);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
    
    private void test() {
        try {
        	
        	// todo?: why is Visitor.class passed as argument here and not in the next
        	getContextService().addEntityListener(listener1, Visitor.class);
        	getContextService().addEntityListener(listener2, "visitor2@itu.dk");

        	
        	
//        	System.out.println(getContextService().getContext(display1.getId()).toXML());
        	
        	
//            System.out.println(getContextService().getContext(visitor1.getId()).toXML());
            
            
            getContextService().addContextItem(visitor1.getId(), arrived, display1);
            
            
            getContextService().addContextItem(visitor2.getId(), arrived, display1);
//            
//            getContextService().addContextItem(visitor2.getId(), arrived, display2);
//            
            // naive try out with uses but didn't play well	
//          getContextService().addContextItem(visitor2.getId(), uses, display2);
            
            
            // lets try and remove some vistor - wrong approach 
//            getContextService().removeContextItem(service_uri, visitor1);
//            getContextService().removeContextItem(visitor2.getId(), display1);
            
//            addContextClient
            // public void addContextClient(int type, Class relation_type, RemoteContextClient client); 
            
//            getContextService().addContextItem(visitor2.getId(), attending, new Presentation("presentation1@itu.dk", "JCAF Demo"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
	@Override
	public void run() {		
	}

	public static void main(String[] args) {
		new ContextTester("testcustom");
	}
}
