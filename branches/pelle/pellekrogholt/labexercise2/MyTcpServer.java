package pellekrogholt.labexercise2;

import java.net.*;  
import java.io.*;

public class MyTcpServer implements IServer {

	private int port;

	/* constructor */
	public MyTcpServer(int port) throws IOException {
		this.port = port; 
		ServerSocket server_socket = new ServerSocket( port );

		// this part handles multiple connections / users
		while(true) {
			Socket client_socket = server_socket.accept(); // blocking call code bellow not executed before request from client
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
		
		private ObjectInputStream object_in_stream; // previously in
		private ObjectOutputStream object_out_stream; // previously out
		private Socket socket;
		
		
		// pelle: whats best practice to throw exception or try/catch clauses ?
		private Connection (Socket socket) throws IOException {
			
			System.out.println("Connection - created ...");
			
			this.socket = socket;
			
			
			object_out_stream = new ObjectOutputStream( socket.getOutputStream());
			
			
			object_in_stream = new ObjectInputStream( socket.getInputStream());

		}

		public void run() {
			

			
			// TODO Auto-generated method stub
			Object o = null;
			
			System.out.println("Connection run () called...");
			
			
			try {
//				String method = object_in_stream.readObject().toString();
				
				System.out.println("Connection run () called... and now inside try clause");
				
				o = object_in_stream.readObject();
				
				System.out.println(o.toString()) ;
				
//				sendMessage(method, o);

				
				
				if (o.toString().equals("quit")) {
		//			TODO: stop server
					System.out.println("quit server called");
					// quit server
					socket.close(); 
				}
				
				
				
				
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} // end run()

	} // end Connection
} // end MyTcpServer 





