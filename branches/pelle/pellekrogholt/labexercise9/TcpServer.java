package pellekrogholt.labexercise9;


import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

public class TcpServer implements IServer {


	/* constructor */
	public TcpServer(int port) throws IOException { 
		ServerSocket server_socket = new ServerSocket( port );

		// this part handles multiple connections / users concurrently
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
	 * because it should be accessible by that class only.
	 * 
	 * It implements threads with use of the Runnable and not the Thread 
	 * 
	 */
	private class Connection implements Runnable {

		private ObjectInputStream ois;
		private ObjectOutputStream oos;

		private Connection (Socket socket) throws IOException {

			oos = new ObjectOutputStream( socket.getOutputStream());
			ois = new ObjectInputStream( socket.getInputStream());
		}

		public void run() {
 
			try {	
				
				/*
				 * Note/ TODO:
				 * 
				 * 
				 mads suggested the while(keep_running) approach 
				 so it keeps running listening for communication on one socket
				 when trying to move socket creation away from send on the client.
				 
				 */

				
				Boolean keep_running = true;
				while(keep_running) {

					Object o = ois.readObject(); // blocking call
					
					if (o.toString().equalsIgnoreCase("quit")) 
					{
					destroy();
					keep_running = false;
					}
	
					send(o);
				
				}				

			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		} // end run()

		private void send(Object o) {
			try {				
				oos.writeObject(o);
//				oos.reset(); try out to solve java.io.StreamCorruptedException: invalid type code: AC
			} 
			catch (Exception e) {	
			}

			// end Connection	

		} // end MyTcpServer
		
		private void destroy() {
			System.out.println("Server is closing down...");
			System.exit(-1);
		}
	}	 
}