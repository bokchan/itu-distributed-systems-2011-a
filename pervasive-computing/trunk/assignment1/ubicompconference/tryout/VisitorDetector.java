package lab3.pellekrogholt.tryout;

import java.awt.List;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;

import dk.itu.jcafdemo.entity.Room;
import dk.itu.jcafdemo.relationship.Arrived;
import dk.pervasive.jcaf.util.AbstractMonitor;

public class VisitorDetector extends AbstractMonitor{
	
	private Room hall;
	private Arrived arrived;
	
	public VisitorDetector(String uri, Room hall, Arrived arrived) throws RemoteException{
		super(uri);
		this.hall = hall;
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
				//ArrayList<String> items = new ArrayList<String>(Arrays.asList(getContextService().getAllEntityIds()));
				
				//if (items.contains(name)) {
					//getContextService().addContextItem(name, leaved, room1);
				//} else {
					getContextService().addContextItem(name, arrived, hall);
				//}
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	

}
