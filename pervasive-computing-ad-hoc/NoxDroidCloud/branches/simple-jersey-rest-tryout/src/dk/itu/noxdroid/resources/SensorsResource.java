package dk.itu.noxdroid.resources;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import dk.itu.noxdroid.dao.SensorDao;
import dk.itu.noxdroid.model.Sensor;

//import com.sun.tools.javac.comp.Sensor;

/* multiple todo(s) */
// Will map the resource to the URL todos
@Path("/sensors")
public class SensorsResource {


	// Allows to insert contextual objects into the class,
	// e.g. ServletContext, Request, Response, UriInfo
	@Context
	UriInfo uriInfo;
	@Context
	Request request;

	// Return the list of todos to the user in the browser
	@GET
	@Produces(MediaType.TEXT_XML)
	public List<Sensor> getSensorsBrowser() {
		List<Sensor> todos = new ArrayList<Sensor>();
		todos.addAll(SensorDao.instance.getModel().values());
		return todos;
	}

	// Return the list of todos for applications
	@GET
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public List<Sensor> getSensors() {
		List<Sensor> todos = new ArrayList<Sensor>();
		todos.addAll(SensorDao.instance.getModel().values());
		return todos;
	}

	// retuns the number of todos
	// Use http://localhost:8080/de.vogella.jersey.todo/rest/todos/count
	// to get the total number of records
	@GET
	@Path("count")
	@Produces(MediaType.TEXT_PLAIN)
	public String getCount() {
		int count = SensorDao.instance.getModel().size();
		return String.valueOf(count);
	}

	@POST
	@Produces(MediaType.TEXT_HTML)
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public void newSensor(@FormParam("id") String id,
			@FormParam("summary") String summary,
			@FormParam("description") String description,
			@Context HttpServletResponse servletResponse) throws IOException {
		Sensor todo = new Sensor(id, summary);
		if (description != null) {
			todo.setDescription(description);
		}
		SensorDao.instance.getModel().put(id, todo);

		URI uri = uriInfo.getAbsolutePathBuilder().path(id).build();
		Response.created(uri).build();

		servletResponse.sendRedirect("../create_todo.html");
	}

	
	/*
	 * putStuff() and teh helper methods based upon the book
	 * restful jax book 
	 * 72 | Chapter 6: JAX-RS Content Handlers
	 * 
	 */
	@PUT
	@Path("/add")
	@Consumes(MediaType.APPLICATION_JSON)
	public void putStuff(InputStream is) throws IOException {
		// log.log("ping");
		byte[] bytes = readFromStream(is);
		String input = new String(bytes);
		System.out.println(input);
	}

	private byte[] readFromStream(InputStream stream) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] buffer = new byte[1000];
		int wasRead = 0;
		do {
			wasRead = stream.read(buffer);
			if (wasRead > 0) {
				baos.write(buffer, 0, wasRead);
			}
		} while (wasRead > -1);
		return baos.toByteArray();
	}

	/*

	public Response createTrackInJSON(Track track) {
		 
		String result = "Track saved : " + track;
		return Response.status(201).entity(result).build();

	 */
	@PUT
	@Path("/add_single_todo")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response addSingleTODO(Sensor todo, 
							 @Context HttpServletResponse servletResponse) throws IOException {
//	public void addSingleTODO(InputStream is) throws IOException {
		// log.log("ping");
//		byte[] bytes = readFromStream(is);
//		String input = new String(bytes);
//		System.out.println(input);

		// store to todo in the temp model
		SensorDao.instance.getModel().put(todo.getId(), todo);
		
//		return servletResponse.setStatus(201);
//		status(201).entity(todo).build()
		return Response.status(201).entity(todo).build();
		//servletResponse.setStatus(200);
	}

	
	/*

	it was not clear to us how to handle a list of objects but it turned out to be simple:

	@POST
	@Path("/addCustomers/")
	@Consumes(MediaType.APPLICATION_JSON)
	public List<Customer> addCustomers(List<Customer> list){
	logger.debug(list);
	    return list;
	}
	
	from http://goo.gl/ikdvn - but its not possible to put multiple object types or what my be with some generics ? 


	"PUT is required by the specification to send a response code of 201, “Created” if a new 
	resource was created on the server as a result of the request." source the jax_rs book "Assigning HTTP Methods | 21"


	 */
	@PUT
	@Path("/add_multiple_todo")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response addMultipleTODO(List<Sensor> list) throws IOException {
		
		ListIterator<Sensor> i = list.listIterator();
		Sensor tmp;
		while(i.hasNext()) {
			tmp = i.next();
			SensorDao.instance.getModel().put(tmp.getId(), tmp);
		}		

		// this one didn't play well
		// return Response.status(201).entity(list).build();
		// but the following seems sane - right ?:
		
		return Response.status(201).build();
		
	}	
	
	
	
	
	
	
	// Defines that the next path parameter after todos is
	// treated as a parameter and passed to the SensorResources
	// Allows to type http://localhost:8080/de.vogella.jersey.todo/rest/todos/1
	// 1 will be treaded as parameter todo and passed to SensorResource
	@Path("{sensor}")
	public SensorResource getSensor(@PathParam("sensor") String id) {
		return new SensorResource(uriInfo, request, id);
	}

}