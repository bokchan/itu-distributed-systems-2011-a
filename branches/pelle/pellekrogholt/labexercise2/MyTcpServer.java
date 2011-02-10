package pellekrogholt.labexercise2;

import java.net.*;  
import java.io.*;

public class MyTcpServer {

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
}


//class Connection extends Thread {
class Connection implements Runnable {
	DataInputStream in;
	DataOutputStream out;
	Socket clientSocket;
	public Connection (Socket aClientSocket) {
		try {
			System.out.println( "test" );
			clientSocket = aClientSocket;
			in = new DataInputStream( clientSocket.getInputStream());
			out =new DataOutputStream( clientSocket.getOutputStream());
//			this.start(); not used in 
		} catch(IOException e) {System.out.println("Connection:"+e.getMessage());}
	}
	public void run(){
		try {			                 // an echo server
			
			String data = in.readUTF();	                  // read a line of data from the stream
			out.writeUTF(data);
			if (data.equals("quit")) {
//				TODO: stop server
				System.out.println("quit server called");
				// quit server
				try {
					clientSocket.close();
				} catch (IOException e) 
				{/*close failed*/}
			}
			
		}catch (EOFException e){System.out.println("EOF:"+e.getMessage());
		} catch(IOException e) {System.out.println("readline:"+e.getMessage());
		} finally{ try {clientSocket.close();}catch (IOException e){/*close failed*/}}


	}
}



