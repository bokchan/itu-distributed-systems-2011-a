package assignment1.conference;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;

import assignment1.conference.entity.Conference;
import assignment1.conference.entity.Display;
import assignment1.conference.entity.Participant;
import assignment1.conference.entity.Workshop;
import assignment1.conference.eventbus.TerminalListener;
import assignment1.conference.monitor.RFIDMonitor;
import assignment1.conference.relationship.Located;

import com.alien.enterpriseRFID.reader.AlienReaderConnectionException;
import com.alien.enterpriseRFID.reader.AlienReaderConnectionRefusedException;
import com.alien.enterpriseRFID.reader.AlienReaderNotValidException;
import com.alien.enterpriseRFID.reader.AlienReaderTimeoutException;

import dk.itu.infobus.ws.EventBus;
import dk.itu.infobus.ws.Listener;
import dk.pervasive.jcaf.ContextEvent;
import dk.pervasive.jcaf.EntityListener;
import dk.pervasive.jcaf.impl.RemoteEntityListenerImpl;
import dk.pervasive.jcaf.util.AbstractContextClient;

public class ConferenceProgram extends AbstractContextClient {

	private RemoteEntityListenerImpl display_listener;

	// look up the zone's here http://tiger.itu.dk:8000/ITUitter/
	// E200 9037 8904 0121 1620 7040 is the one attached to the wall of pitLab -
	// mac address taken from a.book
	Participant participant1 = new Participant(
			"E200 9037 8904 0121 1620 7040", "Participant 1", "4329b1550000");

	Participant p1 = new Participant("E200 9037 8904 0121 1450 81CF",
			"Andreas Bok Andersen", "4329b1550000");
	Participant p2 = new Participant("E200 9037 8904 0121 1860 5608",
			"Pelle Krøgholt", "4329b1550000");
	Workshop w1 = new Workshop("workshop1", 1, 'C', 57,
			"Ubiquitous Technologies in Hospitals", "1C");
	Workshop w2 = new Workshop("workshop2", 2, 'E', 57,
			"Mobile Sensing and Air Quality", "2E");
	Conference ubicomp = new Conference("ubicomp2011", "Ubicomp 2011",
			new Date(2011, 7, 1), new Date(2011, 7, 7));

	Located located = new Located(this.getClass().getName());

	Display zone = new Display("itu.zone3.zone3e@itu.dk", 3, '4', 0, "Zone 3E",
			"itu.zone3.zone3e");
	Display infodisplay = new Display("infodisplay@itu.dk", 3, 'A', 54,
			"Display in ", "3A");

	public ConferenceProgram(String serviceUri)
			throws AlienReaderConnectionRefusedException,
			AlienReaderNotValidException, AlienReaderTimeoutException,
			AlienReaderConnectionException {
		super(serviceUri);
		/*
		 * Create dummy conference
		 */
		ubicomp.AddParticipant(p1);
		ubicomp.AddParticipant(p2);

		w1.AddParticipant(p1);
		w2.AddParticipant(p1);

		w1.AddParticipant(p2);
		w2.AddParticipant(p2);

		ubicomp.AddWorkshop(w1);
		ubicomp.AddWorkshop(w2);

		try {
			/**
			 * Located for inanimate objects Attending for animate objects
			 */
			// BLIPMonitor blip_monitor = new BLIPMonitor(serviceUri, zone,
			// located);
			// Thread tBlip = new Thread(blip_monitor);
			// tBlip.start();

			RFIDMonitor rfid_monitor = new RFIDMonitor(serviceUri, infodisplay,
					located);
			Thread t = new Thread(rfid_monitor);
			t.start();
			
		} catch (RemoteException e) {

		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			display_listener = new RemoteEntityListenerImpl();
			display_listener.addEntityListener(new EntityListener() {
				@Override
				public void contextChanged(ContextEvent event) {
					if (event.getEventType() == ContextEvent.RELATIONSHIP_ADDED) {
						Display d = (Display) event.getItem();
						if (d.getId().equalsIgnoreCase("infodisplay@itu.dk")) {
							try {
								toConsole("event fired");
								Conference conf = (Conference) getContextService()
										.getEntity("ubicomp2011");
								conf.contextChanged(event);
								Participant p = (Participant) event.getEntity();

								toConsole("Welcome " + p.getName());
								ArrayList<Workshop> wList = (ArrayList<Workshop>) conf
										.getWorkshopsByParticipant(p);
								toConsole("You are registrered for Ubicomp 2011 and participating in the following Workshops");
								for (Workshop w : wList) {
									toConsole(String.format(
											"14:00 %s: %s %s %s", w.getName(),
											w.getFloor(), w.getSector(),
											w.getRoom()));
								}
								toConsole("\n(A map is shown)");

								Scanner in = new Scanner(System.in);
								toConsole("\n\nDo you wish to be tracked during the conference inside ITU? \nPress Y or N");
								String input = in.nextLine();
								if (input.equalsIgnoreCase("y")) {
									EventBus eb = new EventBus("tiger.itu.dk",
											8004);
									eb.start();
									Listener listener = new TerminalListener(p.getMac_addr(), p.getName());  
									eb.addListener(listener);

									toConsole("\nYour current location is: "
											+ d.getFloor() + d.getSector()
											+ d.getRoom());
								}

							} catch (RemoteException e) {
								e.printStackTrace();
							}
							// System.out.println("@ conf display" +
							// event.toString());
							catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}
				}
			});

		} catch (RemoteException e) {

		}

		load();

		toConsole("------------------------------------------------------------------");
		toConsole("Welcome to ITU");

		toConsole("Todays events 30/09/2011");
		toConsole("Ubicomp conference: \13th International Conference on Ubiquitous Computing (UbiComp 2011) at ITU Copenhagen");

		toConsole("Workshop: \nUbiquitous Technologies in Hospitals\n");

		toConsole("Workshop:\nMobile Sensing and Air Quality\n");

		toConsole("Annual party 2011\n Students and employees we hope to see you at this festive occasion. Invitation is sent to your mailbox. Buy tickets now at the Information desk.");

		toConsole("ITU.Film and Analog present: True Grit at 14:00 in Analog:\nDrop by Analog at 14:00 and watch this great western from the Coen Brothers (Big Lebowski, Fargo). All are welcome, and there will be free popcorn as well!");
		toConsole("------------------------------------------------------------------");

	}

	public void load() {
		try {
			getContextService().addEntity(p1);
			getContextService().addEntity(p2);
			getContextService().addEntity(w1);
			getContextService().addEntity(w2);
			getContextService().addEntity(infodisplay);
			getContextService().addEntity(ubicomp);

			getContextService().addEntityListener(display_listener,
					Participant.class);
			
			EventBus eb = new EventBus("tiger.itu.dk",
					8004);
			eb.start();
			TerminalListener listener = new TerminalListener("4329b1550000", this.p1.getName());
			eb.addListener(listener);

		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

//	public void test() {
//		toConsole("testing");
//		try {
//			// Attending attending = new Attending(this.getClass().getName());
//			// located = new Located(this.getClass().getName());
//			// getContextService().addEntityListener(display_listener,
//			// Participant.class);
//			//
//			// System.out.println((getContextService().getContext(p1.getId())
//			// .toXML()));
//			//
//			// // getContextService().addContextItem(p1.getId(),attending, w1);
//			// getContextService()
//			// .addContextItem(p1.getId(), located, infodisplay);
//			// // getContextService().addContextItem(p2.getId(),located,
//			// // infodisplay);
//			//
//			// getContextService().removeContextItem(
//			// "E200 9037 8904 0121 1620 7040", located);
//
//			TerminalListener listener = new TerminalListener("4329b1550000", serviceui);
//			getContextService().getEntity("E200 9037 8904 0121 1620 7040");
//
//		} catch (RemoteException e) {
//			e.printStackTrace();
//		}
//	}

	@Override
	public void run() {
		System.err.println("run");
	}

	public static void main(String[] args)
			throws AlienReaderConnectionRefusedException,
			AlienReaderNotValidException, AlienReaderTimeoutException,
			AlienReaderConnectionException {
		new ConferenceProgram("conference");
	}

	private void toConsole(Object o) {
		System.out.println(o.toString());
	}
}