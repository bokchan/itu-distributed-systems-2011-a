package pellekrogholt.labexercise2;

import java.net.*; 
import java.io.*;


//implements Runnable

public class MyTcpServerClientRunner implements Runnable {
	
	private static int server_port = 7898;
	
	public static void main (String args[]) throws Exception{



		InetAddress server_address = InetAddress.getByName("localhost");
		
		//start server in a separate thread
		new Thread(new MyTcpServerClientRunner()).start();
		
		MyTcpClient client = new MyTcpClient (server_port, server_address); 		
		client.sendMessage("hello world");
		client.sendMessage("hello another world");
		client.sendMessage("hello another another world");
		
		// create another client
		MyTcpClient client2 = new MyTcpClient (server_port, server_address);
		
		client2.sendMessage("client 2 says: hello world");
		
		
		// client closes down server - 'jonas' suggested this
		// as a truly distributed approach in stead f implementing a stop() method on MyServer
		// note: dosent work yet
		client2.sendMessage("quit");
		
		
		
	}

	public void run() {
		// TODO Auto-generated method stub
		try {
			
			System.out.println("Thread called and server created");
			MyTcpServer server = new MyTcpServer(server_port);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} // this one result in a blocking call
	}    
}