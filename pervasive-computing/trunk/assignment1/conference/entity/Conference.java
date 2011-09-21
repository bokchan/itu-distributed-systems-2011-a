package assignment1.conference.entity;

import java.rmi.RemoteException;
import java.util.Date;
import java.util.List;

import assignment1.conference.relationship.Attending;
import dk.pervasive.jcaf.ContextEvent;
import dk.pervasive.jcaf.entity.GenericEntity;
import dk.pervasive.jcaf.entity.Person;

public class Conference extends GenericEntity {
	private String name;
	
	private Date dateStart;
	private Date dateEnd;
	private List <Participant> participants;
	private List <Workshop> workshops;
	
	public Conference(String id, String name, Date dateStart, Date dateEnd,
			List<Participant> participants, List<Workshop> workshops) {
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
		this.participants.add(participant);
	}
	
	public List<Workshop> GetWorkshops()
	{
		return this.workshops;
	}
	
	public List<Participant> GetParticipants()
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
	
	@Override
	public void contextChanged(ContextEvent event) {
		super.contextChanged(event);
		if (event.getItem() instanceof Person) {
			try {
				getContextService().addContextItem(this.getId(), new Attending("attending"), (Person)event.getItem());
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
		
	}
	

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
