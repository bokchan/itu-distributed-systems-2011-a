package dk.itu.noxdroid.model;

import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;


/*
 * TODO: not 100% sure about the Date type
 */
@XmlRootElement
// JAX-RS supports an automatic mapping from JAXB annotated class to XML and JSON
public class Track {
	
	private String id;
	private Date startTime;
	private Date endTime;
	
	public Track () {
	}		

	public Track (String id){
		this.id = id;
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
	public Date getStartTime() {
		return startTime;
	}
	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}	

	public Date getEndTime() {
		return endTime;
	}
	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}		

	
}
