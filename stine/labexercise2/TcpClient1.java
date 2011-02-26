package stine.labexercise2;
import java.net.*;
import java.io.*;

public class TcpClient1
{
	  public static void main (String args[]) throws Exception
	  {
		    InetAddress serverAddress = InetAddress.getByName("localhost");
		    int serverPort = 7896;
		    String message = "Hello!!!";
		    Socket socket = new Socket( serverAddress, serverPort );
		    
		    // could have gotten an InputStream as well
		    DataOutputStream dos = new DataOutputStream( socket.getOutputStream());
		    dos.writeUTF(message);
		    dos.flush();
		    
		    DataInputStream dis = new DataInputStream(socket.getInputStream());
		    String message2 = dis.readUTF();
		    System.out.println( "Modtaget client: " + message2 ) ;
	  }
}
