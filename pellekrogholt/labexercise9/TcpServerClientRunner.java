package pellekrogholt.labexercise9;

import java.io.IOException;
import java.net.InetAddress;

import org.junit.Assert;

public class TcpServerClientRunner implements Runnable {
	
	private boolean server_authentication = false;
	private int server_port;
	private int client_port;
	
	public TcpServerClientRunner(int server_port) {
		this.server_port = server_port;
	}
	
	public TcpServerClientRunner(int server_port, boolean server_authentication) {
		this.server_port = server_port;
		this.server_authentication = server_authentication;
	}
	
	
	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main (String args[]) throws Exception{ 

		
		InetAddress server_address = InetAddress.getByName("localhost");
		
		//start authentication server in a separate thread
		new Thread(new TcpServerClientRunner(5001, true)).start();
		
		new Thread(new TcpServerClientRunner(5002)).start();
		
		new Thread(new TcpServerClientRunner(5003)).start();
		
		TcpClient client = new TcpClient (5001, server_address);
		String message = "message_to_5001";
		client.sendMessage(message);
		System.out.println("receive message " + client.receiveMessage().toString());		
		client.sendMessage(message+"_1");
		
		TcpClient client2 = new TcpClient (5002, server_address);
		message = "message_to_5002";
		client2.sendMessage(message);
		System.out.println("receive message " + client2.receiveMessage().toString());
		client2.sendMessage(message+"_2");
		
		TcpClient client3 = new TcpClient (5003, server_address);
		message = "message_to_5003";
		client3.sendMessage(message);
		System.out.println("receive message " + client3.receiveMessage().toString());
		client3.sendMessage(message+"_3");
		
		System.out.println("close down servers");
		client.sendMessage("quit");
		client2.sendMessage("quit");		
		client3.sendMessage("quit");				
		
		
		
		
		
		
//		// A will communicate with S (Authentication Server)
//		TcpClient client = new TcpClient (5001, "localhost");
//		String message = "message1";
//
//		client.sendMessage(message);
//
//		// from S to A
//		message = client.receiveMessage().toString();
//		
//		Assert.assertEquals(message, "message1_message2");		
//		
//		// from A to B (Another Server)
//		client = new TcpClient (5002, server_address);		
//
//		client.sendMessage(message+"_message3");
//		
//		// from B to A
//		message = client.receiveMessage().toString();
//		
//		Assert.assertEquals(message, "message1_message2_message3_message4");
//		
//		// final message from A to B   
//		client.sendMessage(message+"_message5");
//		
//		// quit server 
//		client.sendMessage("quit");
//		
		
		
		
		
		
		
		
		
		
		
//		System.out.println("Labexercise 1 + 2 (not 2 real machines) + 3 + 4");
//		System.out.println("================================================");
//		System.out.println("Server set up (in a thread)");
//		
//		InetAddress server_address = InetAddress.getByName("localhost");
//		
//		//start server in a separate thread
//		new Thread(new TcpServerClientRunner()).start();
//		
//		System.out.println("Client created");
//		
//		// ? stands for a so called wild card...
//		TcpClient client = new TcpClient (server_port, server_address); 		
//
//		String message = "Hello world";
//		
//		System.out.println("A client sends message (" +  message  + ") to server");
//		
//		client.sendMessage(message);
//
//		System.out.println(client.receiveMessage());
//				
//		
//		System.out.println("A client quits server - sending message 'quit'");
		
//		client.sendMessage("quit");
		
		
	}

	/**
	 * approach is to create one authentication server then a *normal* server
	 */
	@Override
	public void run() {
		try {			
			if (server_authentication) {
				// nb! next line results in a blocking call
				AuthenticationTcpServer authentication_server = new AuthenticationTcpServer(server_port);
			} else {
				// nb! next line results in a blocking call
				TcpServer server = new TcpServer(server_port);
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}    
}