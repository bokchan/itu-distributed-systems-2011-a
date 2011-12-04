package dk.itu.noxdroid.resources;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.xml.bind.JAXBElement;

import dk.itu.noxdroid.dao.SensorDao;
import dk.itu.noxdroid.model.Sensor;




/* single sensor */
public class SensorResource {
	@Context
	UriInfo uriInfo;
	@Context
	Request request;
	String id;
	public SensorResource(UriInfo uriInfo, Request request, String id) {
		this.uriInfo = uriInfo;
		this.request = request;
		this.id = id;
	}
	
	//Application integration 		
	@GET
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	public Sensor getSensor() {
		Sensor sensor = SensorDao.instance.getModel().get(id);
		if(sensor==null)
			throw new RuntimeException("Get: Sensor with " + id +  " not found");
		return sensor;
	}
	
	// For the browser
	@GET
	@Produces(MediaType.TEXT_XML)
	public Sensor getSensorHTML() {
		Sensor sensor = SensorDao.instance.getModel().get(id);
		if(sensor==null)
			throw new RuntimeException("Get: Sensor with " + id +  " not found");
		return sensor;
	}
	
	@PUT
	@Consumes(MediaType.APPLICATION_XML)
	public Response putSensor(JAXBElement<Sensor> sensor) {
		Sensor c = sensor.getValue();
		return putAndGetResponse(c);
	}
	
	// note:  naive try out to support json post 
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	public Response putSensorJSon(JAXBElement<Sensor> sensor) {
		Sensor c = sensor.getValue();
		return putAndGetResponse(c);
	}	
	
	@DELETE
	public void deleteSensor() {
		Sensor c = SensorDao.instance.getModel().remove(id);
		if(c==null)
			throw new RuntimeException("Delete: Sensor with " + id +  " not found");
	}
	
	private Response putAndGetResponse(Sensor sensor) {
		Response res;
		if(SensorDao.instance.getModel().containsKey(sensor.getId())) {
			res = Response.noContent().build();
		} else {
			res = Response.created(uriInfo.getAbsolutePath()).build();
		}
		SensorDao.instance.getModel().put(sensor.getId(), sensor);
		return res;
	}
	
	

}
