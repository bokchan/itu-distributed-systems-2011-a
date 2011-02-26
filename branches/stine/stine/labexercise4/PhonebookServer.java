package stine.labexercise4;
// Phonebook server accepts commands and executes them.

import java.io.*;
import java.util.*;
import java.net.*;
import java.util.concurrent.*;

import bok.labexercise4.IPhonebook;
import bok.labexercise4.ReplicatedPhonebook;

public class PhonebookServer extends AbstractServer implements Runnable {
	// Set of InetSocketAddresses - ensures distinct elements 
	private Set<InetSocketAddress> cPoints;
	// Storage
	private IPhonebook phonebook;

	public PhonebookServer (int port) throws IOException {
		// Call superclass constructor
		super(port);
		cPoints = new  HashSet<InetSocketAddress>();
		phonebook = new ReplicatedPhonebook();
	}
	public PhonebookServer () throws IOException {
		super();
		cPoints = new  HashSet<InetSocketAddress>();
		phonebook = new ReplicatedPhonebook();
	}

  public synchronized void abort () {
    abort = true;
  }
  
  //CAme to here - get the abstract server thing!!!!

  void ExecuteAndSend (Command command) throws IOException {
    Object result = command.Execute (phonebook);
    Socket client = new Socket ();
    try {
      client.connect (command.ReturnTo);
      OutputStream os = client.getOutputStream ();
      ObjectOutputStream oos = new ObjectOutputStream (os);
      oos.writeObject (result);
    } finally {
      if (client != null)
        client.close ();
    }
  }

  void HandleConnection (Socket client) throws IOException,
      ClassNotFoundException {
    try {
      InputStream is = client.getInputStream ();
      ObjectInputStream ois = new ObjectInputStream (is);
      Command command = (Command) ois.readObject ();
      ExecuteAndSend (command);
    } finally {
      if (client != null)
        client.close ();
    }
  }
@Override
void ExecuteAndSend(Object command) throws IOException {
	// TODO Auto-generated method stub
	
}
}
