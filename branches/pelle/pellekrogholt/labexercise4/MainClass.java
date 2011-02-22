// Main program entry point
package pellekrogholt.labexercise4;

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
    PhonebookServer server = new PhonebookServer ();
    IPhonebook phonebook = new RemotePhonebook (server.LocalEndpoints
        .getFirst ());
    UserInterface ui = new UserInterface (phonebook);
    Thread serverThread = new Thread (server);
    serverThread.start ();
    System.out.println ("I'm listening on");
    for (InetSocketAddress sa : server.LocalEndpoints) {
      System.out.println ("  " + sa);
    }
    System.out.println ("My GUID is "
        + GuidFromString (server.LocalEndpoints.getFirst ().toString ()));
    ui.Start ();
    server.abort ();
  }
}
