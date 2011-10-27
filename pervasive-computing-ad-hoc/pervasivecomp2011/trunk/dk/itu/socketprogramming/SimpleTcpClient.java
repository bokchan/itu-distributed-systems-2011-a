package itu.socketprogramming;

import java.net.*;  
import java.io.*;

public class SimpleTcpClient {
	
	
	public SimpleTcpClient(String serverIPNumber, int portNumber, String message) throws IOException {

		Socket socket = null;
		
        try {
			InetAddress serverAddress = InetAddress.getByName(serverIPNumber);
			int serverPort = portNumber;

			// create a new socket
			socket = new Socket( serverAddress, serverPort );
			
			OutputStream os = socket.getOutputStream();
			// could have gotten an InputStream as well
			
			DataOutputStream dos = new DataOutputStream( os );
			
			dos.writeUTF(message);
			
			dos.flush();
			
			socket.close();

			
        } catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
			// ensure socket is closed
			if (socket!=null) socket.close();
		} 
		
	}
	
	
	public static void main (String args[]) throws Exception{

		SimpleTcpClient client = new SimpleTcpClient("localhost", 7656, "A Secret Message");

		SimpleTcpClient client2 = new SimpleTcpClient("localhost", 7656, "Another Secret Message");

		// android simple text server
//		SimpleTcpClient client3 = new SimpleTcpClient("10.25.253.150", 50299, "Hi android message from osx");

		// android image text server
//		SimpleTcpClient client4 = new SimpleTcpClient("10.25.253.150", 50230, "Hi android message from osx - next time i will send an image");
		
		
	}	
}