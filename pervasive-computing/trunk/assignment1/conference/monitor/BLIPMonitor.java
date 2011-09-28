package assignment1.conference.monitor;

import java.rmi.RemoteException;

import com.alien.enterpriseRFID.reader.AlienReaderException;

import dk.pervasive.jcaf.util.AbstractMonitor;

public class BLIPMonitor extends AbstractMonitor {

	public BLIPMonitor(String service_uri) throws RemoteException {
		super(service_uri);
	}

	@Override
	public void monitor(String arg0) throws RemoteException {

	}

	@Override
	public void run() {

		// setup thread that keeps listing to blip system for a specific user / mac address
		while(true) {
			Thread t = new Thread();
			t.start();
			try {
				System.out.println("BLIPMonitor: thread in blip monitor startet and reading done");
//				getRFIDTags();
				t.sleep(5000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}  

		
		
	}

}
