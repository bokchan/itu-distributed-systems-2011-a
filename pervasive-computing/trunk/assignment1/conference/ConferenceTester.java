package assignment1.conference;

import java.rmi.RemoteException;
import java.util.Date;

import assignment1.conference.entity.Conference;
import assignment1.conference.entity.Display;
import assignment1.conference.entity.Participant;
import assignment1.conference.entity.Workshop;
import assignment1.conference.monitor.RFIDMonitor;
import assignment1.conference.relationship.Attending;
import assignment1.conference.relationship.Located;

import com.alien.enterpriseRFID.reader.AlienReaderConnectionException;
import com.alien.enterpriseRFID.reader.AlienReaderConnectionRefusedException;
import com.alien.enterpriseRFID.reader.AlienReaderNotValidException;
import com.alien.enterpriseRFID.reader.AlienReaderTimeoutException;

import dk.pervasive.jcaf.ContextEvent;
import dk.pervasive.jcaf.EntityListener;
import dk.pervasive.jcaf.impl.RemoteEntityListenerImpl;
import dk.pervasive.jcaf.util.AbstractContextClient;

public class ConferenceTester extends AbstractContextClient {
	
	private RemoteEntityListenerImpl display_listener;
	private RemoteEntityListenerImpl workshop_listener;
	
	// look up the zone's here http://tiger.itu.dk:8000/ITUitter/
	Participant p1 = new Participant("E200 9037 8904 0121 1860 5608", "Stine Hartmann Bierre", "4329b1550000");
	Participant p2 = new Participant("E200 9037 8904 0121 1540 7908", "Pelle Krøgholt", "4329b1550000"); 
	Workshop w1 = new Workshop("workshop1", 1, 'C', 57, "Ubiquitous Technologies in Hospitals", "1C"); 
	Workshop w2 = new Workshop("workshop2", 2, 'E', 57, "Mobile Sensing and Air Quality", "2E");
	Conference ubicomp = new Conference("ubicomp2011", "Ubicomp 2011", new Date(2011, 7,1), new Date(2011, 7, 7));
	
	Located located = new Located(this.getClass().getName());

	Display infodisplay = new Display("infodisplay@itu.dk", 3, 'S', 10, "Display in ", "2C");
	
	public ConferenceTester(String serviceUri) throws AlienReaderConnectionRefusedException, AlienReaderNotValidException, AlienReaderTimeoutException, AlienReaderConnectionException {
		super(serviceUri);		
		/*
		 * Create dummy conference
		 * 
		 */
		
		ubicomp.AddParticipant(p1);
		ubicomp.AddParticipant(p2);
		
		w1.AddParticipant(p1);
		w2.AddParticipant(p2);
		
		ubicomp.AddWorkshop(w1);
		ubicomp.AddWorkshop(w2);
		
		try {
			/**
			 * Located for inanimate objects 
			 * Attending for animate objects 
			 */
			RFIDMonitor rfid_monitor = new RFIDMonitor(serviceUri, infodisplay, located);
			Thread t = new Thread(rfid_monitor);
			t.start();
			
		} catch (RemoteException e) {}
		
		try {
			workshop_listener = new RemoteEntityListenerImpl();
			workshop_listener.addEntityListener(new EntityListener() {
				
				@Override
				public void contextChanged(ContextEvent event) {
					((Workshop) event.getItem()).contextChanged(event); 
					System.out.println("Workshop " + event.toString());		
				}
			});
		} catch (RemoteException e){}
		
		
		try {
			display_listener= new RemoteEntityListenerImpl();
			display_listener.addEntityListener(new EntityListener() {
				
				@Override
				public void contextChanged(ContextEvent event) {
					((Display) event.getItem()).contextChanged(event);
					System.out.println("Display listener: " + event.toString());
				}
			});
			
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		load();
		test();
	}
	
	public void load() {
		try {
			getContextService().addEntity(p1);
			getContextService().addEntity(p2);
			getContextService().addEntity(w1);
			getContextService().addEntity(w2);
			getContextService().addEntity(infodisplay);
			
		} catch (RemoteException e) {

			e.printStackTrace();
		}
	} 
	
	public void test()  {
		System.err.println("testing");
		try {
			Attending attending = new Attending(this.getClass().getName());
			located = new Located(this.getClass().getName());
			getContextService().addEntityListener(display_listener, Participant.class);
			
			System.out.println((getContextService().getContext(p1.getId()).toXML()));
			
			getContextService().addContextItem(p1.getId(),attending, w1);
			getContextService().addContextItem(p2.getId(),located, infodisplay);
			
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		System.err.println("done testing");
	} 

	@Override
	public void run() {
		System.err.println("run");
	}
	
	public static void main(String[] args) throws AlienReaderConnectionRefusedException, AlienReaderNotValidException, AlienReaderTimeoutException, AlienReaderConnectionException {
		new ConferenceTester("conference");		
	}	
}