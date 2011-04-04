package pellekrogholt.labexercise9;

import java.io.IOException;
import java.net.InetAddress;  

import org.junit.*;  

public class TcpServerClientTest implements Runnable 
{  

	private static int run_call = 0;

	private static int authentication_server_flag = 0;

	private static int client_port = 4000;
	private static int server_port = 5000;

	//	private static int authentication_port = 4004;
	private static InetAddress server_address;

	/**
	 * Setup server
	 * - has to be in a separate thread since server makes a blocking call 
	 * 
	 * Important: don't use @Before here since that will call method before each method.
	 * 
	 * @throws Throwable
	 */
	@BeforeClass 
	public static void setupServer() throws Throwable {
		server_address = InetAddress.getByName("localhost");
		new Thread(new TcpServerClientTest()).start();
		new Thread(new TcpServerClientTest()).start();
	}

	
	
	@Test 
	public void testA_to_S_to_A_to_B_to_A_to_B() throws Throwable { 

		// a will communicate with S (Authentication Server)
		TcpClient client = new TcpClient (5001, server_address);
		String message = "message1";

		client.sendMessage(message);

		// from S
		message = client.receiveMessage().toString();
		
		System.out.println(message);
		
		// no message manipulation is done so fare so message is the same
		Assert.assertEquals(message, "message1_message2");		
		
		// a will communicate with B (Another Server)
		client = new TcpClient (5002, server_address);		

		client.sendMessage(message+"_message3");
		
		
		// from B
		message = client.receiveMessage().toString();
		
		Assert.assertEquals(message, "message1_message2_message3_message4");
		
		// final message to B 
		// TODO: raises error it should be posisble to send more than one message 
//		client.sendMessage(message+"_message5");
		
		
//		// hmmmmm - this works but should be needed
//		client = new TcpClient (5002, server_address);		
//		client.sendMessage(message+"_message5");
		
		// quit server 
		client.sendMessage("quit");
	}

	
	
	
	
	
	

//	@Test 
	public void testClient2AuthenticationServerMessage() throws Throwable { 

		// a will communicate with s
		TcpClient client = new TcpClient (5001, server_address);
		String message = "message1";

		client.sendMessage(message);

		// no message manipulation is done so fare so message is the same
		Assert.assertEquals(client.receiveMessage(), message+"message2");

		// quit server 
		client.sendMessage("quit");
	}

//	@Test 
	public void testClient2ServerMessage() throws Throwable { 


		// a will communicate with b
		TcpClient client = new TcpClient (5002, server_address);
		String message = "message3";

		client.sendMessage(message);

		// no message manipulation is done so fare so message is the same
		Assert.assertEquals(client.receiveMessage(), message);

		// quit server 
		client.sendMessage("quit");
	}


	@Override
	public void run() {
		try {

			System.out.println("run() called");
			System.out.println("run_call: " + ++run_call);
			if (run_call == 1) {
				AuthenticationTcpServer authentication_server = new AuthenticationTcpServer(++server_port);
			} else {
				TcpServer server = new TcpServer(++server_port);
			}

		} catch (IOException e) {
			e.printStackTrace();
		} 
	}



}
