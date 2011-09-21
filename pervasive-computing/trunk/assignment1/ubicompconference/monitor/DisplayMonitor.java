package assignment1.ubicompconference.monitor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.rmi.RemoteException;

import assignment1.ubicompconference.entity.Display;
import assignment1.ubicompconference.relationship.Arrived;
import dk.pervasive.jcaf.util.AbstractMonitor;
 
public class DisplayMonitor extends AbstractMonitor {
	 

	private Display display;
	private Arrived arrived;
	
	public DisplayMonitor(String service_uri, Display display, Arrived arrived) throws RemoteException{
		super(service_uri);
		this.display = display;
		this.arrived = arrived;
	}	
	
	@Override
	public void monitor(String arg0) throws RemoteException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		while (true) {
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			try {
				String name = br.readLine();  
					getContextService().addContextItem(name, arrived, display);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	} 

}