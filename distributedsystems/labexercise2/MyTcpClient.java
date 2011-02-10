package distributedsystems.labexercise2;

import java.net.*; 
import java.io.*;

public class MyTcpClient {

	public int port;
	
	InetAddress server_address;
	
	/* constructor */
	public MyTcpClient(int port, InetAddress server_address) throws IOException {
		this.port = port;
		this.server_address = server_address;		
	}

	/**
	 * Send message
	 * 
	 * @param message
	 * @throws IOException
	 */
	public void sendMessage(String message) throws IOException {
		
		// create a new socket
		Socket socket = new Socket( server_address, port );
		
		OutputStream os = socket.getOutputStream();
		// could have gotten an InputStream as well

		DataOutputStream dos = new DataOutputStream( os );

		
		System.out.println(message);
		
		dos.writeUTF(message);		
		dos.flush();

	}
	
//	TODO implement a get_message
	

}