/* 
 * 
 * dosen't have to be build like a soap wbeservice with bin/wsgen 
 * 
 * just run as java app right away 
 * 
 * and then called through browser: http://127.0.0.1:8084/hello/world
 * 
 * based on http://www.oracle.com/technetwork/articles/javase/index-137171.html

there is a handy example of who to handle GET/POST/PUT/DELETE
 
public Source invoke(Source source) {
  try{
    MessageContext mc = wsContext.getMessageContext();
    String path = (String)mc.get(MessageContext.PATH_INFO);
    String method = (String)mc.get(MessageContext.HTTP_REQUEST_METHOD);
    if (method.equals("GET")) 
          return get(mc);
    if (method.equals("POST")) 
          return post(source, mc);
    if (method.equals("PUT")) 
          return put(source, mc); 
    if (method.equals("DELETE")) 
          return delete(source, mc); 
    throw new WebServiceException("Unsupported method:" +method); 
        } catch(JAXBException je) {
            throw new WebServiceException(je);
        }
    }
 
and a couple of use cases mentioned like placing/modifying an order with in an e-commerce system etc...
 
 
 */

package pellekrogholt.labexercise10_restful;

import java.io.StringReader; 

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.ws.Endpoint;
import javax.xml.ws.Provider;
import javax.xml.ws.Service;
import javax.xml.ws.ServiceMode;
import javax.xml.ws.WebServiceProvider;
import javax.xml.ws.http.HTTPBinding;

@WebServiceProvider

@ServiceMode(value=Service.Mode.PAYLOAD)
public class MyProvider implements Provider<Source> {
    public Source invoke(Source source) {
         String replyElement = new String("<p>hello world</p>");
         StreamSource reply = new StreamSource(
                                  new StringReader(replyElement));
         return reply;
      }

public static void main(String args[]) {
       Endpoint e = Endpoint.create( HTTPBinding.HTTP_BINDING,
                                     new MyProvider());
      e.publish("http://127.0.0.1:8084/hello/world");
       // Run forever  e.stop();
 }
}