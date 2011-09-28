package assignment1.conference.entity;

import java.rmi.RemoteException;

import assignment1.conference.relationship.Attending;
import dk.pervasive.jcaf.ContextEvent;
import dk.pervasive.jcaf.ContextItem;
import dk.pervasive.jcaf.entity.Person;
 

/**
 * A room in the building. A room can host events. It has a WaveTrend reader attached so it can tell 
 * when somebody is approaching it!
 * 
 * @author mSpazzy
 */
public class Display extends Room {
	
	/**
	 * Constructor for display
	 * 
	 * @param id
	 * @param floor
	 * @param sector
	 * @param number
	 * @param name
	 */
	public Display(String id, int floor, char sector, int number, String name) {
		super(id, floor, sector, number, name);
	}

	public String getEntityInfo() {
		return "ITUDisplay entity";
	}

	public String toString() {
		return "[" + getId() + "] " + getFloor() + getSector() + getRoom();
	}
	
	@Override
	public void contextChanged(ContextEvent event) {
		super.contextChanged(event);
		
		ContextItem item = event.getItem();
		item.getClass();
		if(item != null) {
			if(item instanceof Person) {
				try {
					if (!getContextService().getContext("unicomp2011").contains(item)) {
						getContextService().addContextItem("unicomp2011", new Attending("checkedin"), item);
					}
					
					// Show info for the user
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public String toXML() {
		String context = "";
		if (getContext() != null) {
			context = getContext().toXML();
		}
		return super.toXML();
	}

}
