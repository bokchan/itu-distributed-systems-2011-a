package stine.labexercise2;
import java.net.*;
import java.io.*;

public class TspServer1 
{
	public static void main (String args[]) throws Exception
	{
	    int serverPort = 7896;
	    ServerSocket serverSocket = new ServerSocket( serverPort );

		System.out.println( "Waiting" ); 
	    Socket socket = serverSocket.accept(); // blocking call
	
		// could have gotten an OutputStream as well
		DataInputStream dis = new DataInputStream( socket.getInputStream() );
		String message = dis.readUTF(); // blocking call
		System.out.println( "Modtaget på server: " + message );  
		
		DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
		dos.writeUTF(message.toUpperCase());
		dos.flush();
	}    
}
