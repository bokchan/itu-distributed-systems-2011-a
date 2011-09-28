package assignment1.conference.entity;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import assignment1.conference.relationship.Attending;
import dk.pervasive.jcaf.ContextEvent;
import dk.pervasive.jcaf.ContextItem;
import dk.pervasive.jcaf.entity.Person;


public class Workshop extends Room {
	private List <Participant> participants;
	private String name;
	
	public Workshop(String id, int floor, char sector, int number, String name) {
		super(id, floor, sector, number, name);
		this.participants = new ArrayList<Participant>();
		this.name = name; 
	}
	
	public Workshop(String id, int floor, char sector, int number, String name,
			List<Participant> participants) {
		super(id, floor, sector, number, name);
		this.participants = participants;
		this.name = name; 
	}
	
	public String getName() {
		return this.name;
	}

	public List<Participant> getParticipants() {
		return participants;
	}
	
	public void AddParticipant(Participant participant) {
		this.participants.add(participant);
	}
	
	public void AddParticipant(List<Participant> participants) {
		this.participants.addAll(participants);
	}
	
	@Override
	public void contextChanged(ContextEvent event) {
		super.contextChanged(event);
		ContextItem item = event.getItem();
		if (item instanceof Person) {
			try {
				getContextService().addContextItem(this.getName(), new Attending("attending"), ((Person)event.getItem()));
			} catch (RemoteException e) {
				e.printStackTrace();
			}
			System.out.println("Name: " + ((Person) item).getName() + " in Room: " + ((Room)event.getItem()).toString());
		}
	}
	public String toXML() {
		String context = "";
		if (getContext() != null) {
			context = getContext().toXML();
		}
		
		return String.format("<workshop id=\"%s\" ><name>%s</name>%s</workshop>", super.getId(), getName(), super.toXML()); 
	}	
}