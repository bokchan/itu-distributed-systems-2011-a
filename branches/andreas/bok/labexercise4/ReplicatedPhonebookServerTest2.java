package bok.labexercise4;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.List;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ReplicatedPhonebookServerTest2 {
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

	int port;

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

	@Before	
	public void BeforeTest() throws NoSuchAlgorithmException, IOException {
		MD5 = MessageDigest.getInstance ("MD5");

		System.out.println("Starting servers...");
		port = 10 + ((int) Math.random() * 10000); 
		primary = new PhonebookServer(0); 
		Thread serverThread = new Thread (primary);
		serverThread.start ();

		Guid primaryGUID = GuidFromString(primary.getIP().toString());
		String primaryHostName = primary.getIP().toString();
		phonebook1 = new RemotePhonebook(primary.getIP());

		System.out.println ("Primary hostname is " + primaryHostName);
		System.out.println ("Primary server GUID is " + primaryGUID);

		secondary = new PhonebookServer(0); 
		Thread serverThread2 = new Thread (secondary);
		serverThread2.start ();

		Guid secondaryGUID = GuidFromString(secondary.getIP().toString());
		String secondaryHostName = secondary.getIP().toString();
		phonebook2 = new RemotePhonebook(secondary.getIP());

		System.out.println ("secondary hostname is " + secondaryHostName);
		System.out.println ("secondary GUID is " + secondaryGUID);

		tertiary = new PhonebookServer(0); 
		Thread serverThread3 = new Thread (tertiary);
		serverThread3.start ();

		Guid tertiaryGUID = GuidFromString(tertiary.getIP().toString());
		String tertiaryHostName = tertiary.getIP().toString();
		phonebook3 = new RemotePhonebook(tertiary.getIP());

		System.out.println ("tertiary hostname is " + tertiaryHostName);
		System.out.println ("tertiary GUID is " + tertiaryGUID);
		
		primaryISA = primary.getIP();
		secondaryISA = secondary.getIP();
		tertiaryISA = tertiary.getIP();		

	}

	@After 
	public void AfterTest() {
		System.out.println("Exiting test...");
		primary.abort ();
		secondary.abort();
		tertiary.abort();
	}

	@Test
	/***
	 * Testing add server and remove server
	 */
	public void TestJoinRemoveServer() throws IOException, NoSuchAlgorithmException, InterruptedException {
		System.out.println("*********************************************************");
		System.out.println("Starting test...");
			
		System.out.println("*********************************************************");
		System.out.println("\nTest join command with synchronization of connectionpoints between joiner and joinee");

		Assert.assertEquals("[]", primary.getConnectionPoints().toString());
		//primary.ExecuteAndSend(new JoinServerCommand(secondaryISA, primaryISA));
		
		RemotePhonebookServer remotePrimary = new  RemotePhonebookServer(primaryISA);
		remotePrimary.addConnectionPoint(new JoinServerCommand(secondaryISA, primaryISA));		
//
//		System.out.println("*********************************************************");
//		System.out.println("\nTest Add contact command with synchronization between replicated servers");
//		Contact c =  new Contact("Andreas", "3958475");
//		phonebook1.AddContact(c);
//
//		
//		Assert.assertEquals("[" + secondary.getIP().toString()  +"]",  primary.getConnectionPoints().toString());
//		Assert.assertEquals("["+ primary.getIP().toString() + "]", secondary.getConnectionPoints().toString());
//		
//		Assert.assertEquals("3958475", phonebook2.Lookup("Andreas"));
//		Assert.assertEquals("3958475", phonebook1.Lookup("Andreas"));
//
//		
//		Contact c2 =  new Contact("Pelle", "24334");
//		phonebook1.AddContact(c2);
//		Assert.assertEquals("24334", phonebook1.Lookup("Pelle"));
//		Assert.assertEquals("24334", phonebook2.Lookup("Pelle"));
//
//		
//		System.out.println("*********************************************************");
//		System.out.println("\nTest of adding new phonebookserver synchronization of connectionpoints between servers");
//		primary.ExecuteAndSend(new JoinServerCommand(tertiaryISA, primaryISA));
//
//		
//
//		
//		System.out.println("*********************************************************");
//		System.out.println("\nTest of adding new contact to last joined server, should be updated to server one and two");
//		Contact c3 =  new Contact("Mette", "45234");
//		phonebook3.AddContact(c3);		
//
//		
//		System.out.println("*********************************************************");
//		System.out.println("\nRemove server two");
//		primary.ExecuteAndSend(new RemoveServerCommand(secondaryISA, primaryISA));
//
//		System.out.println("*********************************************************");
//		System.out.println("\nAdd contact to server two after leaving primaryServer");
//		Contact c6 = new Contact("Alin", "7234");
//		phonebook2.AddContact(c6);
//
//		System.out.println("*********************************************************");
//		System.out.println("\nAdd contact to server one: " + c.toString());
//
//		Contact c4 = new Contact("Peter", "1234");
//		phonebook1.AddContact(c4);
//
//		
//		System.out.println("*********************************************************");
//		System.out.println("Test primary and tertiary server both removed server two");
//		Assert.assertEquals("[" + tertiary.getIP().toString()  +"]",  primary.getConnectionPoints().toString());
//		Assert.assertEquals("["+ primary.getIP().toString() + "]", tertiary.getConnectionPoints().toString());
//
//		
//		System.out.println("*********************************************************");
//		System.out.println("Test Mette is added to Server 1");
//		Assert.assertEquals("45234", phonebook1.Lookup("Mette"));
//
//		System.out.println("Test that all connectionpoints have been removed from server two ");
//		Assert.assertEquals(0,secondary.getConnectionPoints().size());

		
//		System.out.println("*********************************************************");
//
//		System.out.println("Rejoin server two");
//		primary.ExecuteAndSend(new JoinServerCommand(secondaryISA, primaryISA));

//		
//		
//		System.out.println("*********************************************************");
//		System.out.println("Add contact to server 1");
//		Contact c5 = new Contact("Karen", "12345");
//		phonebook1.AddContact(c5);

		
//		System.out.println("*********************************************************");
//		System.out.println("Test update command on server Mette, 123456789");
//		Assert.assertEquals(UpdateResult.OK, phonebook1.Update("Mette", "123456789"));
//		
//		
//		System.out.println("*********************************************************");
//		System.out.println("Test that server 2's contacts are up to date");
//		Assert.assertEquals("1234", phonebook2.Lookup("Peter"));
		
		phonebook1 = new RemotePhonebook(primaryISA);
		
		List<Contact> l1 =  phonebook1.GetAllContacts();
		List<Contact> l2 = phonebook2.GetAllContacts();
//		List<Contact> l3 = phonebook3.GetAllContacts();
//
		System.out.println("\nPrint out phonebooks for all three servers");
		System.out.println("Phonebook 1:" + l1);
		System.out.println("Phonebook 2" + l2);
//		System.out.println("Phonebook 3" + l3);
//
		System.out.println("\nPrint out connectionpoints for all three servers");
		System.out.println("Server 1: Own ip:" + primaryISA+ " - " +  primary.getConnectionPoints());
		System.out.println("Server 2: Own ip:" + secondaryISA + " - "+ secondary.getConnectionPoints());
//		System.out.println("Server 3: Own ip: " + tertiaryISA + " - "  +  tertiary.getConnectionPoints());
//
		
		Collections.sort(l1);
//		Collections.sort(l2);
//		Collections.sort(l3);

		System.out.println("Test implementation of Comparable interface in Contact");
		System.out.println("Phonebook 1: " + l1);
//		System.out.println("Phonebook 2: " + l2);
//		System.out.println("Phonebook 3: " + l3);		
	}
}