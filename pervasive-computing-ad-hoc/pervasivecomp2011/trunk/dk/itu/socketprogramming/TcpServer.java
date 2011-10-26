package itu.socketprogramming;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;




/**
 * 
 * can serve multiple client connections
 *
 */
public class TcpServer {

	private int port;

	/* constructor */
	public TcpServer(int port) throws IOException {
		this.port = port; 
		ServerSocket server_socket = new ServerSocket( port );

		// this part handles multiple connections / users
		while(true) {
			Socket client_socket = server_socket.accept(); // blocking call code bellow not executed before request from client
			System.out.println("client_socket.getLocalAddress() :" + client_socket.getLocalAddress());
			System.out.println("client_socket.getRemoteSocketAddress() :" + client_socket.getRemoteSocketAddress());
//			RemoteSocket: /10.25.233.246:53910
//			LocalAddress: /10.25.250.231
//			InetAddress: /10.25.233.246
			
			Connection c = new Connection(client_socket);
			new Thread(c).start(); 
		}  
	}

	//class Connection extends Thread {
	/**
	 * Connection(s)
	 * 
	 * Class that can handle multiple connections - its based on xxx (add reference)
	 * but its made private and for that reason moved into / under the server class
	 * because it should be accessible by that class.
	 * 
	 * It implements threads with use of the Runnable and not the Thread 
	 * 
	 */
	private class Connection implements Runnable {

//		private ObjectInputStream ois; // previously in
//		private ObjectOutputStream oos; // previously out
		
		private DataInputStream  in; // previously in
//		private DataOutputStream out; // previously out
		
		 
		private String message;
		private Socket socket;

		private Connection (Socket socket) throws IOException {

			this.socket = socket;
			//out = new OutputStream( socket.getOutputStream());
//			oos = new AppendableObjectOutputStream( socket.getOutputStream());
			InputStream is = socket.getInputStream();
			in = new DataInputStream( is );

			message = in.readUTF();  // blocking call
			System.out.println( message );

			// could also been written in a one line
//			String message = ((DataInputStream) dis).readUTF(); // blocking call
			
			
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub			
		}

	}
	
	
	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main (String args[]) throws Exception{ 

//		int serverPort = 7656;
//
//		//10.25.254.241
		
		TcpServer server = new TcpServer(7656);
	}
	
}