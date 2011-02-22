package pellekrogholt.labexercise4;

import java.io.UnsupportedEncodingException; 
import java.security.MessageDigest;

import org.junit.*;  

public class PhoneBookServerTest 
{  

//	private UserInterface ui;
	private PhonebookServer server;
	private IPhonebook phonebook;
	
	private static MessageDigest MD5;
	private Thread serverThread;


	/**
	 * Setup server
	 * - has to be in a separate thread since server makes a blocking call ?
	 * -  
	 * 
	 * Important: don't use @Before here since that will call method before each method.
	 * 
	 * @throws Throwable
	 */
//	@BeforeClass had hopped to use this one but then it can set phonebook
//  that might be needed.
	@Before 
	public void setupServer() throws Throwable {		
		// MD5 has to be declared within class/method that are 
		// Throwable/NoSuchAlgorithmException 
		MD5 = MessageDigest.getInstance ("MD5");
		
		server = new PhonebookServer();
		phonebook = new RemotePhonebook(server.LocalEndpoints.getFirst ());
		serverThread = new Thread (server);
		serverThread.start();
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
		System.out.println ("My GUID is "
				+ GuidFromString(server.LocalEndpoints.getFirst ().toString ()));
		String name = "My Name";
		String  phone = "20202020";		
	    Contact contact = new Contact (name, phone);
	    phonebook.AddContact(contact);
	    Assert.assertEquals(phone, phonebook.Lookup(name));

	}

}
