// Main program entry point
package pellekrogholt.labexercise4;

import java.security.*;
import java.io.*;
import java.net.*;

import bok.labexercise4.JoinServerCommand;

class MainClass {

  static MessageDigest MD5;

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

  public static void main (String [] args) throws NoSuchAlgorithmException,
      IOException {
    MD5 = MessageDigest.getInstance ("MD5");
    
    
    
    //phone book 1
    PhonebookServer server = new PhonebookServer();
    IPhonebook phonebook = new RemotePhonebook(server.LocalEndpoints.getFirst());
    UserInterface ui = new UserInterface(phonebook);
    Thread serverThread = new Thread(server);
    serverThread.start();
    
    //phone book 2    
    PhonebookServer server2 = new PhonebookServer();

//    UserInterface ui = new UserInterface(phonebook2);
    Thread serverThread2 = new Thread(server2);
    serverThread2.start();
    
    //remotePrimary.addConnectionPoint(new JoinServerCommand(secondaryISA, primaryISA));    
    
    //phone book 2 should join server 1
//    server.
//    server.SendJoinCommand(secondaryISA, primaryISA)

    
//    // mads code
//    // Add a second server to the network
//    PhonebookServer replica1 = new PhonebookServer();
//    Thread replica1Thread    = new Thread (replica1);
//    replica1Thread.start ();
//    JoinCommand joinCmd      = new JoinCommand(replica1.LocalEndpoints.getFirst(), server.LocalEndpoints.getFirst());
//    replica1.SendJoinCommand(joinCmd, server.LocalEndpoints.getFirst());
    
    
    System.out.format("server2.LocalEndpoints.getFirst(): %s \nserver.LocalEndpoints.getFirst(): %s\n\n", server2.LocalEndpoints.getFirst(), server.LocalEndpoints.getFirst());
    
    
    
	JoinCommand joinCmd = new JoinCommand(server2.LocalEndpoints.getFirst(), server.LocalEndpoints.getFirst());
	
	server2.SendJoinCommand(joinCmd, server.LocalEndpoints.getFirst());
	// pelle: it confuses me that SendJoinCommand is called on server2 ?
	// and the way it takes the first argument as a joinCmd-object....    
    
    System.out.println ("Server1 GUID is "
            + GuidFromString (server.LocalEndpoints.getFirst().toString()));
    System.out.println ("Server2 GUID is "
            + GuidFromString (server2.LocalEndpoints.getFirst().toString()));
    
    System.out.println ("Server1 listening on");
    for (InetSocketAddress sa : server.LocalEndpoints) {
      System.out.println ("  " + sa);
    }
    
    ui.Start();
    server.abort();
    server2.abort();
  }
}
