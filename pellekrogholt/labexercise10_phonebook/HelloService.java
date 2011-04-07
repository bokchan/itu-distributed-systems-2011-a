package pellekrogholt.labexercise10_phonebook;

import java.util.ArrayList;

import javax.jws.WebMethod;  
import javax.jws.WebService;
import javax.xml.ws.Endpoint;
//import distributedsystems.labexercise2.Person;

@WebService(name="helloService", targetNamespace = "http://hello.samples.smds2011.itu.dk/", serviceName="helloService")
public class HelloService 
{

	private ArrayList<Person> persons =  new ArrayList<Person>(); 
	private int count;

	@WebMethod()
	public String helloOperation(String name) {
		System.out.println("helloOperation called");

		return "Hello " + name + "!";
	}

	@WebMethod()
	public String addPerson(Person person) {
		//		Person p = new Person(name, "Last name", 0, null)		
		System.out.println("addPerson called "  + ++count );
		System.out.println("person.getName(): "  + person.getName() );

		// for now just store person in memory
		persons.add(person);

		return person.getName() + "added to the phone book service";

		// when the bin/wsgen run in terminal it creates:
		//		[ProcessedMethods Class: java.lang.Object]
		//		 pellekrogholt/labexercise10_phonebook/jaxws/AddPerson.java
		//		 pellekrogholt/labexercise10_phonebook/jaxws/AddPersonResponse.java
		//		 pellekrogholt/labexercise10_phonebook/jaxws/HelloOperation.java
		//		 pellekrogholt/labexercise10_phonebook/jaxws/HelloOperationResponse.java


	}


	/**
	 * Publish the service end point.
	 * @param args not used.
	 */
	public static void main(String[] args) 
	{


		Endpoint.publish("http://localhost:8085/labexercises/hello", new HelloService());
		Endpoint.publish("http://localhost:8085/labexercises/person", new HelloService());
	}
}