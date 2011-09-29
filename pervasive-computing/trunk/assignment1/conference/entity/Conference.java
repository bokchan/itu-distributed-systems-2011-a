package assignment1.conference.entity;

import java.rmi.RemoteException;
import java.util.Date;
import java.util.HashSet;

import assignment1.conference.relationship.Attending;
import dk.pervasive.jcaf.ContextEvent;
import dk.pervasive.jcaf.Entity;
import dk.pervasive.jcaf.entity.GenericEntity;
import dk.pervasive.jcaf.entity.Person;

public class Conference extends GenericEntity {
	private String name;
	
	private Date dateStart;
	private Date dateEnd;
	private HashSet<Participant> participants;
	private HashSet<Workshop> workshops;
	
	
	public Conference(String id, String name, Date dateStart, Date dateEnd) {
		super(id);
		this.name = name;
		this.dateStart = dateStart;
		this.dateEnd = dateEnd;
		this.participants = new HashSet<Participant>();
		this.workshops = new HashSet<Workshop>();
	}
	
	public Conference(String id, String name, Date dateStart, Date dateEnd,
			HashSet<Participant> participants, HashSet<Workshop> workshops) {
		super(id);
		this.name = name;
		this.dateStart = dateStart;
		this.dateEnd = dateEnd;
		this.participants = participants;
		this.workshops = workshops;
	}

	public Conference(String name, Date dateStart, Date dateEnd) {
		super();
		this.name = name;
		this.dateStart = dateStart;
		this.dateEnd = dateEnd;
	}
	
	public String getName() {
		return name;
	}

	public void AddWorkshop(Workshop workshop)
	{
		this.workshops.add(workshop);
	}
	
	public void AddParticipant(Participant participant)
	{
		participants.add(participant);
	}
	
	public HashSet<Workshop> GetWorkshops()
	{
		return this.workshops;
	}
	
	public HashSet<Participant> GetParticipants()
	{
		return this.participants;
	}
	
	@Override
	public String getEntityInfo() {
	
		return "Conference";
	}
	
	public Date getDateStart() {
		return dateStart;
	}

	public Date getDateEnd() {
		return dateEnd;
	}
	
	public boolean isParticipant(Participant p) {
		return participants.contains(p);
	}
	
	@Override
	public void contextChanged(ContextEvent event) {
		super.contextChanged(event);
		
		System.out.println("Conf ContextChanged: " +  event);
		Entity participant = event.getEntity() ; 
		if (participant instanceof Person) {
			try {
				if (participants.contains(participant)  && !getContext().contains(participant)) {
					getContextService().addContextItem(this.getId(), new Attending("attending"), (Person)event.getItem());
					System.out.println("added " + ((Participant)participant).getName() );
				}
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	@Override
	public int hashCode() {
		return this.getId().hashCode();
	};

	public String toXML() {
		String context = "";
    	if (getContext() != null) {
    		context = getContext().toXML();
    	}
    	return String.format(
    			"<entity id=\"%s\"><name>%s</name><type>%s</type><date_start>%s</date_start><date_end>%s</date_end></entity>", 
    			getId(), getName(), getEntityInfo(), getDateStart().toString(), getDateEnd().toString());
	}
}