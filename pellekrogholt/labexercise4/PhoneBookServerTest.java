package pellekrogholt.labexercise4;

import java.io.IOException; 
import java.io.UnsupportedEncodingException; 
import java.net.InetSocketAddress;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.junit.*;  

//import bok.labexercise4.Guid;


public class PhoneBookServerTest 
{  

	
	
	
//	private UserInterface ui;
	private PhonebookServer server;
	private PhonebookServer server2;
	
	private IPhonebook remote_phonebook;
	
	static MessageDigest MD5;
	private PhonebookServer primary;
	private PhonebookServer secondary;
	private PhonebookServer tertiary;

	private IPhonebook phonebook1;
	private IPhonebook phonebook2;
	private IPhonebook phonebook3;

	InetSocketAddress primaryISA;
	InetSocketAddress secondaryISA;
	InetSocketAddress tertiaryISA;		

	private int port;
	private Thread serverThread;


	
	
	/**
	 * Setup server
	 * - has to be in a separate thread since server makes a blocking call ?
	 * -  
	 * 
	 * Important: don't use @Before here since that will call method before each method.
	 * @throws NoSuchAlgorithmException 
	 * @throws IOException 
	 * 
	 * @throws Throwable
	 */
//	@BeforeClass had hopped to use this one but then it can set phonebook
//  that might be needed.
	@Before 
	public void setupServer() throws Throwable {
		
//		MD5 = MessageDigest.getInstance ("MD5");
//
//		System.out.println("Starting servers...");
//		port = 10 + ((int) Math.random() * 10000); 
//		primary = new PhonebookServer(); 
//		Thread serverThread = new Thread (primary);
//		serverThread.start ();
		
		
//		Guid primaryGUID = GuidFromString(primary.getIP().toString());
//		String primaryHostName = primary.getIP().toString();
//		phonebook1 = new RemotePhonebook(primary.getIP());
//
//		System.out.println ("Primary hostname is " + primaryHostName);
//		System.out.println ("Primary server GUID is " + primaryGUID);
//
//		secondary = new PhonebookServer(); 
//		Thread serverThread2 = new Thread (secondary);
//		serverThread2.start ();
//
//		Guid secondaryGUID = GuidFromString(secondary.getIP().toString());
//		String secondaryHostName = secondary.getIP().toString();
//		phonebook2 = new RemotePhonebook(secondary.getIP());
//
//		System.out.println ("secondary hostname is " + secondaryHostName);
//		System.out.println ("secondary GUID is " + secondaryGUID);

//		
//		
//		
//		
//		
//		// MD5 has to be declared within class/method that are 
//		// Throwable/NoSuchAlgorithmException 
//		MD5 = MessageDigest.getInstance ("MD5");
//		
//		server = new PhonebookServer();
//		
////		// its important its the remote phone book in use here - 
////		remote_phonebook = (IPhonebook) new RemotePhonebook(server.LocalEndpoints.getFirst());
//		
//		serverThread = new Thread (server);
////		serverThread = new Thread (server);
//		serverThread.start();
//		
//		
//		// MD5 has to be declared within class/method that are 
//		// Throwable/NoSuchAlgorithmException 
//		MD5 = MessageDigest.getInstance ("MD5");
//		
//		server2 = new PhonebookServer();
//		
////		// its important its the remote phone book in use here - 
////		remote_phonebook = (IPhonebook) new RemotePhonebook(server.LocalEndpoints.getFirst());
//		
//		serverThread = new Thread (server2);
////		serverThread = new Thread (server);
//		serverThread.start();
		
		
	    	    MD5 = MessageDigest.getInstance ("MD5");
	    	    
	    	    
	    	    
	    	    //phone book 1
	    	    server = new PhonebookServer();
//	    	    IPhonebook phonebook = new RemotePhonebook(server.LocalEndpoints.getFirst());
//	    	    UserInterface ui = new UserInterface(phonebook);
	    	    Thread serverThread = new Thread(server);
	    	    serverThread.start();
	    	    
	    	    //phone book 2    
	    	    server2 = new PhonebookServer();

//	    	    UserInterface ui = new UserInterface(phonebook2);
	    	    Thread serverThread2 = new Thread(server2);
	    	    serverThread2.start();
	    	    
	    	    //remotePrimary.addConnectionPoint(new JoinServerCommand(secondaryISA, primaryISA));    
	    	    
	    	    //phone book 2 should join server 1
//	    	    server.
//	    	    server.SendJoinCommand(secondaryISA, primaryISA)

	    	    
//	    	    // mads code
//	    	    // Add a second server to the network
//	    	    PhonebookServer replica1 = new PhonebookServer();
//	    	    Thread replica1Thread    = new Thread (replica1);
//	    	    replica1Thread.start ();
//	    	    JoinCommand joinCmd      = new JoinCommand(replica1.LocalEndpoints.getFirst(), server.LocalEndpoints.getFirst());
//	    	    replica1.SendJoinCommand(joinCmd, server.LocalEndpoints.getFirst());
	    	    
	    	    
	    	    System.out.format("print from PhoneBookServerTest:\nserver2.LocalEndpoints.getFirst(): %s \nserver.LocalEndpoints.getFirst(): %s\n\n", server2.LocalEndpoints.getFirst(), server.LocalEndpoints.getFirst());
	    	    
	    	    
	    	    
	    		
	    	    
//	    	    System.out.println ("Server1 GUID is "
//	    	            + GuidFromString (server.LocalEndpoints.getFirst().toString()));
//	    	    System.out.println ("Server2 GUID is "
//	    	            + GuidFromString (server2.LocalEndpoints.getFirst().toString()));
//	    	    
//	    	    System.out.println ("Server1 listening on");
//	    	    for (InetSocketAddress sa : server.LocalEndpoints) {
//	    	      System.out.println ("  " + sa);
//	    	    }		
		
	}


	// had hoped to call it directly from MainClass.GuidFromString
	// but that didn't play well
	static Guid GuidFromString (String s) {
		byte [] bytes = {};
		try {
			bytes = s.getBytes ("UTF-8");
		} catch (UnsupportedEncodingException e) {
			System.err.println (e.getMessage ());
			System.exit (-1);
		}
		bytes = MD5.digest (bytes);
		return new Guid (bytes);
	}	


	@Test 
	public void testPhoneBookAddGetName() throws Throwable { 
	    
		JoinCommand joinCmd = new JoinCommand(server2.LocalEndpoints.getFirst(), server.LocalEndpoints.getFirst());
		server2.SendJoinCommand(joinCmd, server.LocalEndpoints.getFirst());
		// pelle: it confuses me that SendJoinCommand is called on server2 ?
		// and the way it takes the first argument as a joinCmd-object....

		
		
//	    System.out.println ("Server1 GUID is "
//	            + GuidFromString (server.LocalEndpoints.getFirst().toString()));
//	    System.out.println ("Server2 GUID is "
//	            + GuidFromString (server2.LocalEndpoints.getFirst().toString()));
//	    
//	    System.out.println ("Server1 listening on");
//	    for (InetSocketAddress sa : server.LocalEndpoints) {
//	      System.out.println ("  " + sa);
//	    }
	    
	}
	
	
	@After 
	public void AfterTest() {
		server.abort();
		server2.abort();
		
		System.out.println("Exiting test...close servers");
//		primary.abort();
//		secondary.abort();
//		tertiary.abort();
	}
	

}
