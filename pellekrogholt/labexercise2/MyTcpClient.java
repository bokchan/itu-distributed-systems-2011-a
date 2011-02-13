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

		// create a new socket - not sure if this should be here in the constructor 
		// or in the send method
		this.socket = new Socket( server_address, port );
	}


	/**
	 * Send message with command
	 * 
	 * @param command 
	 * @param value
	 * @throws IOException 
	 */
	public void send(V value, C command) throws IOException {

		OutputStream os = socket.getOutputStream();
		// could have gotten an InputStream as well - perhaps try that within a recieve method ?

		ObjectOutputStream oos = new ObjectOutputStream(os);
		
		oos.writeObject(value);
		oos.writeObject(command);
		oos.flush();
		
//		readMessage();

		
//		DataOutputStream dos = new DataOutputStream( os );
//
//		
//		System.out.println(message);
//		
//		dos.writeUTF( (String) message);		
//		dos.flush();
	}


	/**
	 * Send message
	 *  
	 * @param message
	 * @throws IOException 
	 */
	public void send(V message) throws IOException {
		// create a new socket
		Socket socket = new Socket( server_address, port );
		
		OutputStream os = socket.getOutputStream();
		// could have gotten an InputStream as well

		DataOutputStream dos = new DataOutputStream( os );

		
		System.out.println(message);
		
		dos.writeUTF( (String) message);		
		dos.flush();
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