package lab3.pellekrogholt.monitor;

import java.rmi.RemoteException;

import com.alien.enterpriseRFID.reader.AlienClass1Reader;
import com.alien.enterpriseRFID.reader.AlienReaderConnectionException;
import com.alien.enterpriseRFID.reader.AlienReaderConnectionRefusedException;
import com.alien.enterpriseRFID.reader.AlienReaderException;
import com.alien.enterpriseRFID.reader.AlienReaderNotValidException;
import com.alien.enterpriseRFID.reader.AlienReaderTimeoutException;
import com.alien.enterpriseRFID.tags.Tag;

import lab3.pellekrogholt.entity.Display;
import lab3.pellekrogholt.relationship.Arrived;

import dk.pervasive.jcaf.relationship.Located;
import dk.pervasive.jcaf.util.AbstractMonitor;


/**
 * RFID monitor
 * 
 * based on RFIDMonitor on the tutorial:
 * public class DisplayMonitor extends AbstractMonitor implements RFIDScannerListener {
 * but where is the RFIDScannerListener from ? 
 * 
 */
public class RFIDMonitor extends AbstractMonitor {
	
	// todo: try to change to located insted of arrived ?
	private Located rfid_located = null; 
	
	private Display display;
	private Arrived arrived;
	
	public RFIDMonitor(String service_uri, Display display, Arrived arrived) throws RemoteException, AlienReaderConnectionRefusedException, AlienReaderNotValidException, AlienReaderTimeoutException, AlienReaderConnectionException{
		super(service_uri);
		this.display = display;
		this.arrived = arrived;
		
//		setupRFIDReader();
	}
	
	// might be moved to a central spot ?
	
		 
	/**
	 * Get RFID tags
	 * 
	 * We had expected to use a RFID listener approach 
	 * but the example MessageListenerTest.java did't play well
	 * 
	 * For that reason and for simplicity we use the approach lined 
	 * out in AlienClass1ReaderTest.java
	 * 
	 * Read more here xxx todo: insert link to wiki page
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
//		    System.out.println("Tag(s) found:");
		    for (int i=0; i<tagList.length; i++) {
		    	Tag tag = tagList[i];
		    	

//		    	System.out.println("getContextService().getAllEntityIds(): " + getContextService().getAllEntityIds());

		    	// bind Visitor with context
		    	getContextService().addContextItem(tag.toString(), arrived, display);
	
		    	// todo:  probably need a more sophisticated way to
		    	// check if people/rfid tags are coming in or leaving ?
		    	// but this will require the MessageListenerTest.java to work or similar
		    	// as stated in java doc // wiki...
		    	
		    	
		    	//		      System.out.println("ID:" + tag.getTagID()
		    }
		    
// note: print out full rfid data
		    for (int i=0; i<tagList.length; i++) {
		      Tag tag = tagList[i];
		      System.out.println("ID:" + tag.getTagID() +
		                         ", Discovered:" + tag.getDiscoverTime() +
		                         ", Last Seen:" + tag.getRenewTime() +
		                         ", Antenna:" + tag.getAntenna() +
		                         ", Reads:" + tag.getRenewCount()
		                         );
		    }
		    
		  }

		  // Close the connection
		  reader.close();
	}
	
// from the tutorial - jcaf.tutorial.v15.pdf
// 	
//	public void tag(RFIDScanEvent event) {  
//		
//		if (event.getMethod() == RFIDScanEvent.TAG_SCANNED) { 
//			rfid_located.resetTime(); 
//			getContextService().addContextItem( 
//					event.getId(), 
//					rfid_located, 
//					new Location(event.getScannerName())); 
//		} 
//		if (event.getMethod() == RFIDScanEvent.TAG_LEFT) { 
//			getContextService().removeContextItem( 
//					event.getId(), 
//					rfid_located); 
//		} 
//
//	}

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
		while(true) {
			Thread t = new Thread();
			t.start();
			try {
				System.out.println("RFIDMonitor: thread in rfid monitor startet and reading done");
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