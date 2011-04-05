package pellekrogholt.labexercise10;

import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.xml.ws.Endpoint;
@WebService(name="helloService", targetNamespace = "http://hello.samples.smds2011.itu.dk/",serviceName="helloService")
public class HelloService 
{
	@WebMethod()
	public String helloOperation(String name) {
		return "Hello " + name + "!";
	}
	/**
	* Publish the service end point.
	* @param args not used.
	*/
	public static void main(String[] args) 
	{
		Endpoint.publish("http://localhost:8085/labexercises/hello", new HelloService());
	}
}