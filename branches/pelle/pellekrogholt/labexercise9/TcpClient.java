package pellekrogholt.labexercise9;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;


/**
 * 
 * Simple Tcp Client
 *
 */
public class TcpClient implements IClient {

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
	public TcpClient(int port, InetAddress server_address) throws IOException {
		this.port = port;
		this.server_address = server_address;		

		// note/ observation: 
		// create a new socket - not sure if this should be here in the constructor 
		// or in the send method - so fare do it in the send method - if done here 
		// only one send will go through per client.
		// this.socket = new Socket( server_address, port );
		
//		// create a new socket - apparently don't move to constructor
//		this.socket = new Socket( server_address, port );
		
	
	}
 
	// - we had some problems with the generics perhaps just switch to Object 

	/**
	 * Sends message to server. Wraps argument in an array 
	 * methodid, objectarg
	 * TODO: Reverse order so it is methodid, objectarg  
	 */ 
	public void send(Object o) throws IOException, ClassNotFoundException {

		// create a new socket - apparently don't move to constructor
		this.socket = new Socket( server_address, port );
		
		
		OutputStream os = socket.getOutputStream(); // could have gotten an InputStream as well used in receive() 
		ObjectOutputStream oos = new ObjectOutputStream( os );
		oos.writeObject(o);
		
	}
	
		
		
	
	
	
//	public void send(Object o, Object methodid) throws IOException, ClassNotFoundException {
//
//		System.out.println("send(Object o, Object methodid) called");
//		
//		Object[] args = new Object[] {o};
//		
//		send(methodid, args);
////		return send(methodid, args);
//	}
//	
//	/**
//	 * Send message
//	 * Takes a methodid and an array of argument 
//	 */
//	public void send(Object methodid, Object[] args) throws IOException, ClassNotFoundException {
//		
////		// create a new socket - apparently don't move to constructor
////		this.socket = new Socket( server_address, port );
//		
//		OutputStream os = socket.getOutputStream(); // could have gotten an InputStream as well used in receive() 
//		ObjectOutputStream oos = new ObjectOutputStream( os );
//		
//		// Write the number of elements in the argument array. 
//		// The +1 is not meant to be.  
//		oos.writeObject(args.length + 1);
//		
//		oos.writeObject(methodid);
//		for (Object o : args) {
//			oos.writeObject(o);
//		} 
//		oos.flush(); 
//	}
		
	/**
	 * 
	 */
	public Object receive() throws IOException, ClassNotFoundException {
		ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
		Object o = ois.readObject();
		return o;
	}
}