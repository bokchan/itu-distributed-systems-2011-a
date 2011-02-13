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
		private DataInputStream in;
		private DataOutputStream out;
		private Socket socket;
		
		
		private Connection (Socket socket) {
			try {
				System.out.println( "test" );
				this.socket = socket;
				in = new DataInputStream( socket.getInputStream());
				out =new DataOutputStream( socket.getOutputStream());
//				this.start(); not used in 
			} catch(IOException e) {System.out.println("Connection:"+e.getMessage());}
		}
		public void run(){
			try {			                 // an echo server
				
				String data = in.readUTF();	                  // read a line of data from the stream
				out.writeUTF(data);
				if (data.equals("quit")) {
//					TODO: stop server
					System.out.println("quit server called");
					// quit server
					try {
						System.out.println("socket.close() called but the connection seems still to hang...");
						socket.close();
					} catch (IOException e) 
					{/*close failed*/}
				}
				
			} catch (EOFException e){System.out.println("EOF:"+e.getMessage());
			} catch(IOException e) {System.out.println("readline:"+e.getMessage());
			} finally{ try {socket.close();}catch (IOException e){/*close failed*/}}


		}
	}




}





