package lab3.pellekrogholt;

import java.rmi.RemoteException;

//import dk.itu.jcafdemo.entity.Room;
//import dk.itu.jcafdemo.entity.Visitor;
//import dk.itu.jcafdemo.item.Presentation;
//import dk.itu.jcafdemo.relationship.Arrived;
//import dk.itu.jcafdemo.relationship.Attending;


import lab3.pellekrogholt.Display;
import lab3.pellekrogholt.Visitor;
import dk.pervasive.jcaf.ContextEvent;
import dk.pervasive.jcaf.EntityListener;
import dk.pervasive.jcaf.entity.Place;
import dk.pervasive.jcaf.impl.RemoteEntityListenerImpl;
import dk.pervasive.jcaf.item.Location;
import dk.pervasive.jcaf.relationship.Located;
import dk.pervasive.jcaf.util.AbstractContextClient;

public class ContextTester extends AbstractContextClient {

	private RemoteEntityListenerImpl listener1;
	private RemoteEntityListenerImpl listener2;

    final Visitor visitor1 = new Visitor("visitor1@itu.dk", "Visitor 1");
    final Visitor visitor2 = new Visitor("visitor2@itu.dk", "Visitor 2");
    
//    final Room room1 = new Room("room1@itu.dk", 2, 'C', 10);
    
    final Display display1 = new Display("room1@itu.dk", 2, 'C', 10);
    
    final ContextMonitor arrived = new ContextMonitor(this.getClass().getName());
    
    
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
        	getContextService().addEntityListener(listener1, Visitor.class);
        	getContextService().addEntityListener(listener2, "visitor2@itu.dk");

            System.out.println(getContextService().getContext(visitor1.getId()).toXML());
            
            getContextService().addContextItem(visitor1.getId(), arrived, display1);
            getContextService().addContextItem(visitor2.getId(), arrived, display1);
            
            
            
            
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
