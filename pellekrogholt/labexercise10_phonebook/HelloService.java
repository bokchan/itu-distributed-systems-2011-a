package pellekrogholt.labexercise10_phonebook;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.jws.WebMethod;  
import javax.jws.WebService;
import javax.xml.ws.Endpoint;

@WebService(name="helloService", targetNamespace = "http://hello.samples.smds2011.itu.dk/", serviceName="helloService")
public class HelloService 
{
	
	// for now just store person in memory - simply in a hash map. 
	private Map<String, Person> map = new HashMap<String, Person>();
	
	private int count, count2;
	
	/* note: a simpler approach could have been
	   public String addPerson(Sting name, String street, int phone ...) { */
	
	@WebMethod()
	public String addPerson(Person person) {
		//		Person p = new Person(name, "Last name", 0, null)		
		System.out.println("addPerson called "  + ++count );
		System.out.println("person: "  + person );

		
		map.put(person.getName(), person);

		return person.getName() + " added to the phone book service";
	}

	@WebMethod()
	public String getPerson(String name) {
		//		Person p = new Person(name, "Last name", 0, null)		
		System.out.println("getPerson called "  + ++count2 );
		System.out.println("name "  + name );
		System.out.println("map.size() "  + map.size());


		// for now just store person in memory
		Person person = map.get(name);

		System.out.println("person: "  + person );
		
		return person.toString() + " picked from the the phone book service";
	}
	
	
	

	/**
	 * Publish the service end point.
	 * @param args not used.
	 */
	public static void main(String[] args) 
	{
		Endpoint.publish("http://localhost:8085/labexercises/person", new HelloService());
	}
}