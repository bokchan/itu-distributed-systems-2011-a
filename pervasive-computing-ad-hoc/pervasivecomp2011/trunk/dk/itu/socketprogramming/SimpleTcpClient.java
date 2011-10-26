package itu.socketprogramming;

import java.net.*;  
import java.io.*;

public class SimpleTcpClient {
	public static void main (String args[]) throws Exception{
		
		
		// local machine easiest
//        InetAddress serverAddress = InetAddress.getByName("localhost");
		// int serverPort = 3333;
		
		//itu
//        InetAddress serverAddress = InetAddress.getByName("10.25.254.241");
//        int serverPort = 7656;
        
		
		// android device
        InetAddress serverAddress = InetAddress.getByName("10.25.253.150");
        int serverPort = 50299;
		
		
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