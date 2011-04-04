package pellekrogholt.labexercise9;

import java.io.IOException;
import java.net.InetAddress;

public class TcpServerClientRunner implements Runnable {
	
	private static int server_port = 5000;
	
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
		
		System.out.println("A client sends message (" +  message  + ") to server");
		
		client.sendMessage(message);

		System.out.println(client.receiveMessage());
				
		
		System.out.println("A client quits server - sending message 'quit'");
		
		client.sendMessage("quit");
		
		
	}

	public void run() {
		try {			
			TcpServer server = new TcpServer(server_port); 
			// this one results in a blocking call 
		} catch (IOException e) {
			e.printStackTrace();
		}
	}    
}