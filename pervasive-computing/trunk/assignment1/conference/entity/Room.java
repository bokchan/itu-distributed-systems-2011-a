package entity;

import dk.pervasive.jcaf.entity.Place;


/**
 * A room in the building. A room can host events. It has a WaveTrend reader attached so it can tell 
 * when somebody is approaching it!
 * 
 * @author mSpazzy
 */
public class Room extends Place {

	private int floor;
	private char sector;
	private int room;

	public Room() {
		super();
	}

	public Room(String id) {
		super(id);
	}

	public Room(String id, int floor, char sector, int number, String name) {
		super(id, name);

		this.floor = floor;
		this.sector = sector;
		this.room = number;
	}

	public String getEntityInfo() {
		return "ITURoom entity";
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
		return String.format(
				"<room id=\"%s\"><floor>%s</floor><sector></sector><room_number>%s</room_number><context>%s</context></room>", 
				getId(), getFloor(), getSector(), getRoom(), context);
	}
}
