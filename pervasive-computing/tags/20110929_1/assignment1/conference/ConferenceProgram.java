package assignment1.conference;

import java.util.Date;

import assignment1.conference.entity.Conference;
import assignment1.conference.entity.Display;
import assignment1.conference.entity.Participant;
import assignment1.conference.entity.Workshop;

public class ConferenceProgram {

	Display display = new Display("pitlab@itu.dk", 3, 'A', 54, "Conference Info Display ", "3B");
	Participant p1 = new Participant("E200 9037 8904 0121 1860 5608", "Stine Hartmann Bierre", "4329b1550000");
	Participant p2 = new Participant("E200 9037 8904 0121 1540 7908", "Pelle Krøgholt", "4329b1550000"); 
	Workshop w1 = new Workshop("workshop1", 1, 'C', 57, "Ubiquitous Technologies in Hospitals", "1C"); 
	Workshop w2 = new Workshop("workshop2", 2, 'E', 57, "Mobile Sensing and Air Quality", "2E");
	Conference ubicomp = new Conference("Ubicomp 2011", new Date(2011, 7,1), new Date(2011, 7, 7));
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		/**
		 *  Start BLIP
		 *  Start RFID
		 *  Create dummy conference
		 *  Start GUI
		 *  
		 */
		

	}

}
