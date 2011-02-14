package pellekrogholt.labexercise2;

import java.net.*; 
import java.io.*;

public class SimpleTcpClient {
	public static void main (String args[]) throws Exception{
		
		InetAddress serverAddress = InetAddress.getByName("localhost");
		
		int serverPort = 7896;
		
		String message = "A Secret Message";
		
		// create a new socket
		Socket socket = new Socket( serverAddress, serverPort );
		
		OutputStream os = socket.getOutputStream();
		// could have gotten an InputStream as well
		
		DataOutputStream dos = new DataOutputStream( os );
		
		dos.writeUTF(message);
		
		dos.flush();
		
		socket.close();
	}	
}