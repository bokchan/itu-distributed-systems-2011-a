package assignment1.conference;

import java.util.Date;

import assignment1.conference.entity.Conference;
import assignment1.conference.entity.Participant;
import dk.pervasive.jcaf.RemoteEntityListener;
import dk.pervasive.jcaf.util.AbstractContextClient;

public class ConferenceTester extends AbstractContextClient {
	
	private RemoteEntityListener listener;
	
	Participant p1 = new Participant("E200 9037 8904 0121 1860 5608", "4329b1550000", "Stine Hartmann Bierre");
	Participant p2 = new Participant("E200 9037 8904 0121 1540 7908", "4329b1550000", "Pelle Krøgholt"); 
	
	Conference ubicomp = new Conference("Ubicomp 2011", new Date(2011, 7,1), new Date(2011, 7, 7));

	public ConferenceTester(String service_uri) {
		super(service_uri);
		
	}

	@Override
	public void run() {
		
	}
	
	public static void main(String[] args) {
		
	}
	
}
