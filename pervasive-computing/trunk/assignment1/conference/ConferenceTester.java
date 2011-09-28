package assignment1.conference;

import java.util.Date;

import assignment1.conference.entity.Conference;
import assignment1.conference.entity.Participant;
import assignment1.conference.entity.Workshop;
import dk.pervasive.jcaf.RemoteEntityListener;
import dk.pervasive.jcaf.util.AbstractContextClient;

public class ConferenceTester extends AbstractContextClient {
	
	private RemoteEntityListener listener;
	
	Participant p1 = new Participant("E200 9037 8904 0121 1860 5608", "Stine Hartmann Bierre", "4329b1550000");
	Participant p2 = new Participant("E200 9037 8904 0121 1540 7908", "Pelle Krøgholt", "4329b1550000"); 
	Workshop w1 = new Workshop("workshop1", 1, 'D', 57, "Ubiquitous Technologies in Hospitals"); 
	Workshop w2 = new Workshop("workshop2", 2, 'E', 57, "Mobile Sensing and Air Quality");
	Conference ubicomp = new Conference("ubicomp2011", "Ubicomp 2011", new Date(2011, 7,1), new Date(2011, 7, 7));

	public ConferenceTester(String service_uri) {
		super(service_uri);
		
		/*
		 * Create dummy conference
		 */
		ubicomp.AddParticipant(p1);
		ubicomp.AddParticipant(p2);
		
		w1.AddParticipant(p1);
		w2.AddParticipant(p2);
		
		ubicomp.AddWorkshop(w1);
		ubicomp.AddWorkshop(w2);
		
	}

	@Override
	public void run() {
		System.err.println("run");
	}
	
	public static void main(String[] args) {
		new ConferenceTester("conference");		
	}	
}
