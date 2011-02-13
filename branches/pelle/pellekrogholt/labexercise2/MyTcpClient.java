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

		// note: 
		// create a new socket - not sure if this should be here in the constructor 
		// or in the send method - so fare do it in the send method - if done here 
		// only one send will go through per client.
		// this.socket = new Socket( server_address, port );
	}


	/**
	 * Send message with command
	 * 
	 * @param command 
	 * @param value
	 * @throws IOException 
	 */
	public void send(V value, C command) throws IOException {

		this.socket = new Socket( server_address, port );
		
		OutputStream os = socket.getOutputStream();
		// could have gotten an InputStream as well - perhaps try that within a receive method ?

		ObjectOutputStream oos = new ObjectOutputStream(os);
		
		oos.writeObject(value);
		oos.writeObject(command);
		oos.flush();
		
//		readMessage();

		
	}


	/**
	 * Send message
	 *  
	 * @param message
	 * @throws IOException 
	 */
	public void send(V message) throws IOException {
		
		System.out.println("send(V message) called");
		
		// create a new socket
		this.socket = new Socket( server_address, port );
		
		OutputStream os = socket.getOutputStream();
		// could have gotten an InputStream as well

		ObjectOutputStream oos = new ObjectOutputStream( os );

		
//		System.out.println(message);
		
		oos.writeObject(message);		
		oos.flush();
	}


	/**
	 * Receive (return) message form server
	 *  
	 * @throws ClassNotFoundException 
	 * @throws IOException 
	 */
	public V receive() throws IOException, ClassNotFoundException {
		ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
		Object o = ois.readObject();
		return (V) o; // not 100% sure about this cast - 
					  // perhaps it make not much sense to have a generic type in receive()?
	}







}