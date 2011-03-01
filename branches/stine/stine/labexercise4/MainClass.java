package stine.labexercise4;
// Main program entry point

import java.security.*;
import java.io.*;
import java.net.*;

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
    
    PhonebookServer server1 = new PhonebookServer ();
    IPhonebook phonebook = new RemotePhonebook (server1.LocalEndpoints
        .getFirst ());
    UserInterface ui = new UserInterface (phonebook);
    Thread serverThread = new Thread (server1);
    serverThread.start ();
    
    PhonebookServer server2 = new PhonebookServer();
    Thread server2Thread    = new Thread (server2);
    server2Thread.start ();
    
    JoinCommand addRole = new JoinCommand(server2.LocalEndpoints
        .getFirst (), server1.LocalEndpoints
        .getFirst ());
    
    server1.ExecuteAndSend(addRole);
    
    System.out.println ("I'm listening on");
    for (InetSocketAddress sa : server1.LocalEndpoints) {
      System.out.println ("  " + sa);
    }
    System.out.println ("My GUID is "
        + GuidFromString (server1.LocalEndpoints.getFirst ().toString ()));
    System.out.println(server1.serverSet);
    ui.Start ();
    

    server1.abort();
    server2.abort();
  }
}
