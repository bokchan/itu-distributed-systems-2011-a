package assignment1.conference.monitor;

import java.rmi.RemoteException;

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
	
	private Tag currentTAGS[] = null; // Tags in context before each request to
										// the AlienReader
	private Display display;

	public RFIDMonitor(String service_uri, Display display, Located located)
			throws RemoteException, AlienReaderConnectionRefusedException,
			AlienReaderNotValidException, AlienReaderTimeoutException,
			AlienReaderConnectionException {
		super(service_uri);
		this.display = display;
		this.rfid_located = located;
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
			//System.out.println("No Tags Found");
		} else {
			// match against print 
			// Convert located rfid tags to a hashset
			if (currentTAGS != null) {
				for (Tag t : currentTAGS) {
					getContextService().removeContextItem(t.getTagID(),
							rfid_located);
				}
			}

			for (Tag t : tagList) {
				getContextService().addContextItem(t.getTagID(), rfid_located,display);
			}
			
			currentTAGS = tagList;
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
				//System.out.println("RFIDMonitor: thread in rfid monitor startet and reading done");
				getRFIDTags();
				t.sleep(5000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (AlienReaderException e) {
				e.printStackTrace();
			} catch (RemoteException e) {

				e.printStackTrace();
			}
		}
	}
}