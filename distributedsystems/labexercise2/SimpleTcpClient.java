package socketSerialization;

import java.io.*;
import java.net.*;


//Your goal is to build a very simple client/server system. The flow should be:
//	1) Server creates a ServerSocket and starts listening to a port (e.g. 4567).
//	2) (This is a blocking call – server will not do anything until a client connects).
//	3) Client creates a Socket connecting to the IP-number of the server.
//	4) Concurrency: 5+6 below happens simultaneously:
//	5)
//	i) Server “wakes up” from the blocking call accept() and gets a socket.
//	ii) Server gets an InputStream from the socket.
//	iii) Server wraps the InputStream with an ObjectInputStream.
//	iv) Server reads an Object from the ObjectInputStream.
//	v) (The read blocks till the server actually receives an Object).
//	6)
//	i) Client gets an OutputStream from its socket.
//	ii) Client wraps the OutputStream with an ObjectOutputStream.
//	7) Client writes a String-object to the ObjectOutputStream.
//	8 ) Client flushes the object-stream (with the flush()-method)
//	9) Server “wakes up” from the blocking read and gets the String object.
//	10) Server prints the received String object.
//	11) Concurrency: i + ii below happens simultaneously:
//	i) Server closes socket and program ends.
//	ii) Client closes socket and program ends.


public class SimpleTcpClient 
{  
	public static void main (String args[]) throws Exception
	{
		Dog dog = new Dog(10, "Jeff");
		InetAddress serverAddress = InetAddress.getByName("localhost");
	    int serverPort = 7895;
	    Object message = dog;
	    
	    Socket socket = new Socket( serverAddress, serverPort );
	    ObjectOutputStream os = new ObjectOutputStream(socket.getOutputStream());
	    os.writeObject(message);
		os.flush();
		socket.close();
  }	 

}
