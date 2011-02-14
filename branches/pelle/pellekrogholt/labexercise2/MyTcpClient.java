package pellekrogholt.labexercise2;

import java.net.*; 
import java.io.*;

/**
 * 
 * Simple Tcp Client
 *
 */
public class MyTcpClient<V, C> implements IClient<V, C> {

	private Socket socket;
	private int port;
	private InetAddress server_address;
	
	/**
	 * Simple Tcp Client constructor
	 * 
	 * @param port
	 * @param server_address
	 * @throws IOException
	 */
	public MyTcpClient(int port, InetAddress server_address) throws IOException {
		this.port = port;
		this.server_address = server_address;		

		// note/ observation: 
		// create a new socket - not sure if this should be here in the constructor 
		// or in the send method - so fare do it in the send method - if done here 
		// only one send will go through per client.
		// this.socket = new Socket( server_address, port );
	}

	/**
	 * Send message with command
	 *  
	 * @param message
	 * @param operator
	 * @throws IOException 
	 */
	public void send(V message, int operator) throws IOException {

		this.socket = new Socket( server_address, port );
		
		OutputStream os = socket.getOutputStream(); // could have gotten an InputStream as well used in receive()

		ObjectOutputStream oos = new ObjectOutputStream(os);
		oos.writeObject(message);
		oos.writeObject(operator);
		oos.flush();
		
		// don't plase receive() / readMessage() here
	}


	/**
	 * Send message
	 *  
	 * @param message
	 * @throws IOException 
	 */
	public void send(V message) throws IOException {
		
		// create a new socket
		this.socket = new Socket( server_address, port );
		
		OutputStream os = socket.getOutputStream(); // could have gotten an InputStream as well used in receive() 

		ObjectOutputStream oos = new ObjectOutputStream( os );
		oos.writeObject(message);
		oos.flush();
		
		// don't place receive() / readMessage() here 
	}

	public V receive() throws IOException, ClassNotFoundException {

		ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
		Object o = ois.readObject();
		return (V) o;
		
	}

}