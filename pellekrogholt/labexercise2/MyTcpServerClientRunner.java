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
		
		// ? stands for a so called wild card...
		MyTcpClient<String, ?> client = new MyTcpClient (server_port, server_address); 		

		client.send("hello world", 1);
//		client.receive().toString(); 
//		note: dosen't work not sure if the approach is sane  
		System.out.println(client.readMessageFromServer());
		
		
		client.send("hello another world", 0);
		System.out.println(client.readMessageFromServer());
		
		client.send("hello another another world", 0);
		System.out.println(client.readMessageFromServer());
		
		
		// show we can handle multiple connections
		// create another client 
		// ? stands for a so called wild card...
		MyTcpClient<String, ?> client2 = new MyTcpClient (server_port, server_address);
		
		client2.send("client 2 says: hello world", 1);
		System.out.println(client2.readMessageFromServer());
		
		
		// client closes down server - 'jonas' suggested this
		// as a truly distributed approach in stead f implementing a stop() method on MyServer
		// note: dosent work yet
		client2.send("quit", 0);
		System.out.println(client2.readMessageFromServer());		

		client2.send("message send after quit message shouldn't be possible", 0);
		System.out.println(client2.readMessageFromServer());
		
	}

	public void run() {
		// TODO Auto-generated method stub
		try {
			
//			System.out.println("Runner thread called and server created");
			MyTcpServer server = new MyTcpServer(server_port);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} // this one result in a blocking call
	}    
}