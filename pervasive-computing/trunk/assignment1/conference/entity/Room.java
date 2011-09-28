package assignment1.conference.entity;

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
	private String blip_zone_id;

	public Room() {
		super();
	}

	public Room(String id) {
		super(id);
	}

	public Room(String id, int floor, char sector, int number, String name, String blip_zone_id) {
		super(id, name);

		this.floor = floor;
		this.sector = sector;
		this.room = number;
		this.blip_zone_id = blip_zone_id;
				
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

	public String getBlipZoneId() {
		return blip_zone_id;
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
