package stine.labexercise2;

import java.io.*;
import java.net.*;

public class SimpleTcpServer 
{
	public static void main (String args[]) throws Exception
	{
	    int serverPort = 7895;
	    ServerSocket serverSocket = new ServerSocket( serverPort );
	    Socket socket = serverSocket.accept(); // blocking call
	    ObjectInputStream is = new ObjectInputStream(socket.getInputStream());
			// could have gotten an OutputStream as well
//			ObjectInputStream dis = new ObjectInputStream( is );
			Object message = is.readObject(); // blocking call
			((Dog)message).print();
		socket.close();
		System.exit(-1);
	  } 
	
}
