package pellekrogholt.labexercise2;

import java.net.*;
import java.io.*;

public class SimpleTcpServer{
	public static void main (String args[]) throws Exception{
		int serverPort = 7896;
		ServerSocket serverSocket = new ServerSocket( serverPort );
		Socket socket = serverSocket.accept(); // blocking call
		
		// code bellow this is not executed before 
		// a connection from a client is done
		
		
		// now we have got a socekt from the client
		
		// note: here we are on the socket object not the socket server 
		InputStream is = socket.getInputStream();  
		
		// could have gotten an OutputStream as well
		DataInputStream dis = new DataInputStream( is );
		// note: is from the io lib
		// A data input stream lets an application read primitive Java 
		// data types from an underlying input stream in a machine-independent way.
		
		String message = dis.readUTF(); // blocking call
		
		System.out.println( message ) ;      
	}    
}