package assignment1.conference.monitor;

import java.rmi.RemoteException;

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

	}

}
