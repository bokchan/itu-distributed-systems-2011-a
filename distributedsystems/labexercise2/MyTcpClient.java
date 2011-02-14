package pellekrogholt.labexercise2;

import java.net.*;  
import java.io.*;

import bok.labexercise2.optional_1.Person;

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
	public Object send(V message, int operator) throws IOException, ClassNotFoundException {

		System.out.println("client Object send(V message, int operator) called");
		
		this.socket = new Socket( server_address, port );
		
		OutputStream os = socket.getOutputStream(); // could have gotten an InputStream as well used in receive()

		ObjectOutputStream oos = new ObjectOutputStream(os);
		oos.writeObject(message);
		oos.writeObject(operator);
		oos.flush();
		
		return receive();
	}
	

	/**
	 * Send message
	 *  
	 * @param message
	 * @throws IOException 
	 */
	public Object send(V message) throws IOException, ClassNotFoundException {
		
		System.out.println("client Object send(V message) called");
		
		// create a new socket
		this.socket = new Socket( server_address, port );
		
		OutputStream os = socket.getOutputStream(); // could have gotten an InputStream as well used in receive() 

		ObjectOutputStream oos = new ObjectOutputStream( os );
		oos.writeObject(message);
		oos.flush();
		
		return receive();
		// don't place receive() / readMessage() here 
	}

	/**
	 * Send a Person object with command
	 *  
	 * @param p
	 * @param operator
	 * @throws IOException 
	 */	
	public Object send(Person p, int operator) throws IOException, ClassNotFoundException {

//		System.out.println("");
		
		System.out.println("client Object send(Person p, int operator) called");
		
		this.socket = new Socket( server_address, port );
		
		OutputStream os = socket.getOutputStream(); // could have gotten an InputStream as well used in receive()

		ObjectOutputStream oos = new ObjectOutputStream(os);
		oos.writeObject(p);
		oos.writeObject(operator);
		oos.flush();
		
		if (operator == 1)
			return null;

		return receive();
		// don't plase receive() / readMessage() here
	}
	
	
	/**
	 * Send a key and get Person object
	 *  
	 * @param p
	 * @param operator
	 * @throws IOException 
	 */	
	public Object send(int key, int operator) throws IOException, ClassNotFoundException {

		
		System.out.println("client Object send(int key, int operator) called");
		
		this.socket = new Socket( server_address, port );
		
		OutputStream os = socket.getOutputStream(); // could have gotten an InputStream as well used in receive()

		ObjectOutputStream oos = new ObjectOutputStream(os);
		oos.writeObject(key);
		oos.writeObject(operator);
		oos.flush();
		
		
		return receive();
		// don't plase receive() / readMessage() here
	}
	
	public Object receive() throws IOException, ClassNotFoundException {

		ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
		Object o = ois.readObject();
		return o;
		
	}
	
	
//	public V receive() throws IOException, ClassNotFoundException {
//
//		ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
//		Object o = ois.readObject();
//		return (V) o;
//		
//	}

}