package assignment1.ubicompconference.entity;

import dk.pervasive.jcaf.entity.GenericEntity;


/**
 * A room in the building. A room can host events. It has a WaveTrend reader attached so it can tell 
 * when somebody is approaching it!
 * 
 * @author mSpazzy
 */
public class Display extends GenericEntity {

	private int floor;
	private char sector;
	private int room;

	public Display() {
		super();
	}

	public Display(String id) {
		super(id);
	}

	public Display(String id, int floor, char sector, int number) {
		super(id);

		this.floor = floor;
		this.sector = sector;
		this.room = number;
	}

	public String getEntityInfo() {
		return "ITUDisplay entity";
	}

	public int getFloor() {
		return floor;
	}

	public char getSector() {
		return sector;
	}

	public int getRoom() {
		return room;
	}

	public String toString() {
		return "[" + getId() + "] " + floor + sector + room;
	}

	public String toXML() {
		String context = "";
		if (getContext() != null) {
			context = getContext().toXML();
		}
		return "<room id=\"" + getId() + "\">" +
		"<floor>" + getFloor() + "</floor>" + 
		"<sector>" + getSector() + "</sector>" +
		"<room>" + getRoom() + "</room>" +
		context + "</room>";
	}

}
