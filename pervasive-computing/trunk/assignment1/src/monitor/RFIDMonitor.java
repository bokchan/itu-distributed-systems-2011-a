package monitor;

import java.rmi.RemoteException;

import dk.pervasive.jcaf.relationship.Located;
import dk.pervasive.jcaf.util.AbstractMonitor;

// based on RFIDMonitor on the tutorial
// where is RFIDScannerListener from?
//public class DisplayMonitor extends AbstractMonitor implements RFIDScannerListener { 
public class RFIDMonitor extends AbstractMonitor {
	
	private Located rfid_located = null; 
	
	public RFIDMonitor(String service_uri) throws RemoteException { 
		super(service_uri); 
		rfid_located = new Located(this.getClass().getName()); 
	} 
	
// from the tutorial
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
		
	}

	@Override
	public void run() {
		
	} 
}