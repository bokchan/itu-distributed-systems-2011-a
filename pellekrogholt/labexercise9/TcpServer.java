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

		System.out.println("server constructor called added to port: " + port);
		
		ServerSocket server_socket = new ServerSocket( port );

		// this part handles multiple connections / users concurrently
		while(true) {
			Socket client_socket = server_socket.accept(); // blocking call code bellow not executed before request from client
			Connection c = new Connection(client_socket);
			new Thread(c).start(); 
		}  
	}

	/**
	 * Connection(s)
	 * 
	 * It implements threads with use of the Runnable and not the Thread
	 * 
	 */
	protected class Connection implements Runnable {

		private ObjectInputStream ois;
		private ObjectOutputStream oos;
		
		Connection (Socket socket) throws IOException {

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


				//				Boolean keep_running = true;
				//				while(keep_running) {


//				System.out.println("keep_running results in multiple calls on each message");
				Object o = ois.readObject(); // blocking call

				if (o.toString().equalsIgnoreCase("quit")) 
				{
					destroy();
					//					keep_running = false;
				}

				send(o);

				//				}				

			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		} // end run()

		private void send(Object o) {
			try {				
				oos.writeObject(o.toString() + "_message4");
				
				System.out.println("TcpServer send(Object o)");
				
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