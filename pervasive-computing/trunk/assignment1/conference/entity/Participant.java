package assignment1.conference.entity;

import assignment1.conference.relationship.Located;
import dk.pervasive.jcaf.ContextEvent;
import dk.pervasive.jcaf.entity.Person;

public class Participant extends Person {

	private String location; 
	
	public Participant() {
		super();
	}
	/**
	 * Constructor for Visitor.
	 * @param id
	 */ 
	public Participant(String id) {
		super(id);
	}

	/**
	 * Constructor for Visitor.
	 * 
	 * @param id
	 * @param name
	 */
	public Participant(String id, String name) {
		super(id, name);
	}	

	
	/**
	 * Constructor for Visitor.
	 * 	
	 * @param id
	 * @param name
	 * @param type
	 */
	public Participant(String id, String name, String type) {
		super(id, name, type);
	}	
	
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	@Override
	public String getEntityInfo() {
		return "Participant entity";
	}
	
	@Override
	public void contextChanged(ContextEvent event) {
		super.contextChanged(event);
		String new_location = null;
		
		if (event.getRelationship() instanceof Located) {
			if (event.getItem() instanceof Display) {
				new_location = ((Display)event.getItem()).getName();
			}
			if (event.getItem() instanceof Workshop) {
				new_location = ((Workshop)event.getItem()).getName();
			}
		}
		
		if (new_location != null) {
			if (event.getEventType() == ContextEvent.RELATIONSHIP_ADDED) {
				this.setLocation(new_location);
			} else {
				this.setLocation("Unknown");
			}
		}
	}
	public String toXML() {
		String context = "";
		if (getContext() != null) {
			context = getContext().toXML();
		}
		return String.format(
				"<participant id=\"%s\" type=\"%s\"><name>%s</name></participant>", 
				getId(), getType(), getName());
	}
}