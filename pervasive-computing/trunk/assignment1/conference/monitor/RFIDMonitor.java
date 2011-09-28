package assignment1.conference.monitor;

import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.HashSet;

import assignment1.conference.entity.Display;
import assignment1.conference.relationship.Located;

import com.alien.enterpriseRFID.reader.AlienClass1Reader;
import com.alien.enterpriseRFID.reader.AlienReaderConnectionException;
import com.alien.enterpriseRFID.reader.AlienReaderConnectionRefusedException;
import com.alien.enterpriseRFID.reader.AlienReaderException;
import com.alien.enterpriseRFID.reader.AlienReaderNotValidException;
import com.alien.enterpriseRFID.reader.AlienReaderTimeoutException;
import com.alien.enterpriseRFID.tags.Tag;

import dk.pervasive.jcaf.util.AbstractMonitor;

/**
 * RFID monitor
 * 
 * based on RFIDMonitor on the tutorial
 * 
 */
public class RFIDMonitor extends AbstractMonitor {

	private Located rfid_located = null;
	private HashSet<Tag> currentTAGS; // Tags in context before each request to the AlienReader  
	private Display display;

	public RFIDMonitor(String service_uri, Display display, Located located)
			throws RemoteException, AlienReaderConnectionRefusedException,
			AlienReaderNotValidException, AlienReaderTimeoutException,
			AlienReaderConnectionException {
		super(service_uri);
		this.display = display;
		this.rfid_located = located;
		this.currentTAGS = new HashSet<Tag>();
	}

	/**
	 * Get RFID tags
	 * 
	 * We had expected to use a RFID listener approach but the example
	 * MessageListenerTest.java did't play well
	 * 
	 * For that reason and for simplicity we use the approach lined out in
	 * AlienClass1ReaderTest.java
	 * 
	 * Read more here
	 * 
	 * @throws AlienReaderException
	 * @throws RemoteException
	 */
	private void getRFIDTags() throws AlienReaderException, RemoteException {

		AlienClass1Reader reader = new AlienClass1Reader();

		// the reader set up in pitLab
		reader.setConnection("130.226.142.167", 23);
		reader.setUsername("alien");
		reader.setPassword("aline");

		// Open a connection to the reader
		reader.open();

		// Ask the reader to read tags and print them

		Tag tagList[] = reader.getTagList();

		if (tagList == null) {
			System.out.println("No Tags Found");
		} else {
			// Convert located rfid tags to a hashset 
			HashSet<Tag> tmp = new HashSet<Tag>(Arrays.asList(tagList));  
			HashSet<Tag> diff = (HashSet<Tag>) currentTAGS.clone();
			// Remove from a tmp copy of currentTags all items from the new reading 
			diff.removeAll(tmp);
			// Add all tags to currentTags
			currentTAGS.addAll(tmp);
			// Remove the difference between currentTags and the new reading from 
			// the union of currentTags and the new reading
			currentTAGS.removeAll(diff);

			// System.out.println("Tag(s) found:");
			for (Tag t : currentTAGS) {
				getContextService().addContextItem(t.toString(), rfid_located,
						display);
			}
			for (Tag t : diff) {
				getContextService().removeContextItem(t.toString(),
						rfid_located);
			}

			// System.out.println("getContextService().getAllEntityIds(): "
			// + getContextService().getAllEntityIds());

			// bind Visitor with context
			// TODO: also remove people - andreas mentioned something with
			// comparing sets :
			// getContextService().removeContextItem(entity_id, relation)

			// System.out.println("ID:" + tag.getTagID()
		}
		// Close the connection
		reader.close();

		//
	}

	@Override
	public void monitor(String arg0) throws RemoteException {
		// TODO Auto-generated method stub

	}

	/**
	 * Used to have the read getRFIDTags run in a separate and on going thread
	 */
	@Override
	public void run() {
		// TODO Auto-generated method stub

		// setup thread that keeps listing for the rfid reader
		while (true) {
			Thread t = new Thread();
			t.start();
			try {
				System.out
						.println("RFIDMonitor: thread in rfid monitor startet and reading done");
				getRFIDTags();
				t.sleep(5000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (AlienReaderException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}