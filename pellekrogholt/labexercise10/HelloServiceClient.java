package pellekrogholt.labexercise10;

import javax.xml.namespace.QName;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPConstants;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.ws.Dispatch;
import javax.xml.ws.Service;
import javax.xml.ws.soap.SOAPBinding;


public class HelloServiceClient {

	
	
	public static void main(String[] args) 
	{
		
		//System.out.println("Hello Rao!");
		
		QName service =
			new QName("http://hello.samples.smds2011.itu.dk/","helloService");
			QName port =
			new QName("http://hello.samples.smds2011.itu.dk/", "helloServicePort");
			String endpointAddress = "http://localhost:8085/labexercises/hello";
			Service webservice = Service.create(service);
			webservice.addPort(port,SOAPBinding.SOAP11HTTP_BINDING, endpointAddress);	
			//webservice.addPort(port,HTTPBinding.HTTP_BINDING, endpointAddress);

		
			
				try 
				{
					MessageFactory factory =
						MessageFactory.newInstance(SOAPConstants.SOAP_1_1_PROTOCOL);
						SOAPMessage request = factory.createMessage();
						SOAPPart soap = request.getSOAPPart();
						SOAPEnvelope envelope = soap.getEnvelope();
						SOAPBody body = envelope.getBody();
						SOAPElement content = body.addBodyElement(
						new QName("http://hello.samples.smds2011.itu.dk/",
						"helloOperation",
						"itu"));
						
						SOAPElement name;

						//name = content.addChildElement("arg0","", "http://hello.samples.smds2010.itu.dk/");
						name = content.addChildElement("arg0");
						//name.setTextContent("Rao");
						name.addTextNode("Hello DS");
					
						Utils.print(request);
					
					Dispatch<SOAPMessage> dispatch =
						webservice.createDispatch(port, SOAPMessage.class,
						Service.Mode.MESSAGE);
						SOAPMessage response = dispatch.invoke(request);
						String text = response.getSOAPBody().getTextContent();
						
						Utils.print(response);
					
						System.out.println(text);
					
				} 
				catch (SOAPException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	}
}
