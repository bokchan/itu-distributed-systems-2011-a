package pellekrogholt.labexercise8;

import java.io.IOException;
import java.net.InetAddress;

public class TcpServerClientRunner implements Runnable {
	
	private static int server_port = 4004;
	
	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main (String args[]) throws Exception{ 
		System.out.println("Labexercise 1 + 2 (not 2 real machines) + 3 + 4");
		System.out.println("================================================");
		System.out.println("Server set up (in a thread)");
		
		InetAddress server_address = InetAddress.getByName("localhost");
		
		//start server in a separate thread
		new Thread(new TcpServerClientRunner()).start();
		
		System.out.println("Client created");
		
		// ? stands for a so called wild card...
		TcpClient client = new TcpClient (server_port, server_address); 		

		String message = "Hello world";
		
		System.out.println("A client sends message (" +  message  + ") to server with operator 1 (uppercase)");
		
		client.send(message, 1);
		System.out.println();
		
		message = "hello another world";
		
		System.out.println("A client sends message (" +  message  + ") to server with operator 0 (lowercase)");
		
		client.send(message, 0);
		System.out.println();
		System.out.println(client.receive().toString());
//		
		message = "Hello another another world";
		
		System.out.println("A client sends message (" +  message  + ") to server with operator 2 (do nothing)");
		
		client.send(message, 1);
//		System.out.println();
		System.out.println(client.receive().toString());
		
		System.out.println();
		System.out.println("Labexercise 5");
		System.out.println("================================================");
		
		System.out.println("We can handle multiple clients (thread/connections) - lets create another client (client 2)");
		
		// show we can handle multiple connections
		// create another client 
		// ? stands for a so called wild card...
		TcpClient client2 = new TcpClient (server_port, server_address);

		message = "client 2 says: hello world";
		
		System.out.println("Client 2 sends message (" +  message  + ") to server with operator 1 (uppercase)");

		
		client2.send(message, 1);
		System.out.println(client2.receive());

		System.out.println();
		System.out.println("Optional lab exercise 1.:");
		System.out.println("===========================================");
		
		
		System.out.println("We create 3 person objects of type Person.");
		
		// show the person object 
		Person p1 = new Person("Andreas", "Storegade 2", 1020, "60565656");
		Person p2 = new Person("Bettina", "Prinsegade 2", 2030, "60565656");
		Person p3 = new Person("Thor", "Allergade 4", 4030, "60565656");
	
		System.out.println("We send the 3 person objects through client 2 to the server and expects no returns.");
		for (int i = 0; i< 10; i++) {
			client2.send(p1, 3);
		}
		
		Object[] pers = new Object[10] ;
		for (int i = 0; i< 10; i++) {
			pers[i] = p2;	
		}
		client2.send(5, pers);
		
		System.out.println("We request person object (index 0 - name = Andreas) from server .");
		client2.send(0, 2);
		System.out.println(client2.receive().toString());
		
		
		System.out.println();
		System.out.println("Quit is now supported");
		System.out.println("================================================");
		System.out.println("Client 2 sends quit message to server"); 
		
		client2.send("quit", -1);
		System.out.println(client2.receive());

		System.out.println("Client 2 sends another message to server (shouldn't be possible):");
		client2.send("quit", -1);
		System.out.println(client2.receive());
		
		System.out.println("Client (1) sends quit message to server");
		client.send("quit", -1);
		
		
	}

	public void run() {
		// TODO Auto-generated method stub
		try {			
//			System.out.println("Runner thread called and server created");
			TcpServer server = new TcpServer(server_port);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} // this one result in a blocking call
	}    
}