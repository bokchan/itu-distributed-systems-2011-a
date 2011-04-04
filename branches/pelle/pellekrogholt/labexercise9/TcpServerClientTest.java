package pellekrogholt.labexercise9;

import java.io.IOException;
import java.net.InetAddress;  

import org.junit.*;  

public class TcpServerClientTest implements Runnable 
{  

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
	}


//	@BeforeClass 
//	public static void setupAuthenticationTcpServer() throws Throwable {
//		server_address = InetAddress.getByName("localhost");
//		new Thread(new TcpServerClientTest()).start();
//	}

	
	@Test 
	public void testClient2ServerMessage() throws Throwable { 
		
		TcpClient client = new TcpClient (server_port, server_address);
		String message = "Hello world";
		
		client.sendMessage(message);
		
		// no message manipulation is done so fare so message is the same
		Assert.assertEquals(client.receiveMessage(), message);

		// quit server 
		client.sendMessage("quit");
	}

	
	@Override
	public void run() {
		try {
			TcpServer server = new TcpServer(server_port++);
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}
	
	
	
}
