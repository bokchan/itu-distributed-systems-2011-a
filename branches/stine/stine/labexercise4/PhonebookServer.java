// Phonebook server accepts commands and executes them.
package stine.labexercise4;

import java.io.*;
import java.util.*;
import java.net.*;
import java.util.concurrent.*;


public class PhonebookServer implements Runnable {
  private IPhonebook phonebook = new Phonebook ();

  private ServerSocket Listener;

  private InetAddress localIP;

  public LinkedList<InetSocketAddress> LocalEndpoints = new LinkedList<InetSocketAddress> ();

  public PhonebookServer () throws IOException {
    Listener = new ServerSocket (0);
    Listener.setSoTimeout (2000);

    Enumeration ifs = NetworkInterface.getNetworkInterfaces ();
    for (; ifs.hasMoreElements ();) {
      NetworkInterface nif = (NetworkInterface) ifs.nextElement ();
      Enumeration addrs = nif.getInetAddresses ();
      for (; addrs.hasMoreElements ();) {
        InetAddress ip = (InetAddress) addrs.nextElement ();
        String hostname = ip.getCanonicalHostName ();
        if (!hostname.equals (ip.getHostAddress ()))
          LocalEndpoints.add (new InetSocketAddress (hostname, Listener
              .getLocalPort ()));
      }
    }
  }

  boolean abort = false;

  static ExecutorService exeservice = Executors.newCachedThreadPool ();

  public void run () {
    try {
      while (!abort) {
        try {
          final Socket client = Listener.accept ();
          exeservice.execute (new Runnable () {
            public void run () {
              try {
                HandleConnection (client);
              } catch (Exception e) {
                System.err.println (e.getMessage ());
                System.exit (-1);
              }
            }
          });
        } catch (SocketTimeoutException e) {
        }
      }
    } catch (Exception e) {
      System.err.println (e.getMessage ());
      System.exit (-1);
    } finally {
      try {
        Listener.close ();
      } catch (IOException e) {
      }
      exeservice.shutdown ();
    }
  }

  public synchronized void abort () {
    abort = true;
  }
  
  public Set<InetSocketAddress> serverSet = new HashSet<InetSocketAddress>();
  
  void SendJoinCommand (JoinCommand serverAdresses, InetSocketAddress receiver) throws IOException 
  {
		    Socket joiningServer = new Socket ();
		    try {
		      joiningServer.connect (receiver);
		      OutputStream os = joiningServer.getOutputStream ();
		      ObjectOutputStream oos = new ObjectOutputStream (os);
		      oos.writeObject (serverAdresses);
		    } finally {
		      if (joiningServer != null)
		        joiningServer.close ();
		    }
  }
  //This sends a JoinCommand to the server with the adddress of 'sendTo'
  void SendJoinCommand (JoinCommand joiningServers) throws IOException 
  {
	  SendJoinCommand(joiningServers, joiningServers.coordinator);
  }
  
  void send(JoinCommand receiver) throws IOException
  {
	  //Adds my own IP and set of joined servers to a new set
	  Set<InetSocketAddress> setWithOwnIP = serverSet;
	  setWithOwnIP.add(receiver.coordinator);
	  
	  Socket joiningServer = new Socket ();
	    try {
	      joiningServer.connect (receiver.joining);
	      OutputStream os = joiningServer.getOutputStream ();
	      ObjectOutputStream oos = new ObjectOutputStream (os);
	      oos.writeObject (setWithOwnIP);
	    } finally {
	      if (joiningServer != null)
	        joiningServer.close ();
	    
	    }
  }
  void ExecuteAndSend (Command command) throws IOException 
  {
	//  these are the replication servers
	  
	  if (command instanceof JoinCommand ) 
	  {
	      JoinCommand joincmd = (JoinCommand)command;
	      if (joincmd.coordinator.equals(LocalEndpoints.getFirst() ) ) 
	      {
	    	  //add
	    	
	    	
	    	 //forwards to the servers in its server set.
	    	 for(InetSocketAddress current: serverSet)
	    	 {
	    	 SendJoinCommand(joincmd, current);
	    	 }
	    	 //sends the server list and its own IP back to the joining server. 
	    	 send(joincmd);
	    	 
	    	 InetSocketAddress joining = joincmd.joining;
	    	 serverSet.add(joining);
	      } 
	      
	      else 
	      {
	        // add and do nothing
	    	  InetSocketAddress joining = joincmd.joining;
	    	  serverSet.add(joining);
	      }
	    } 
	  else 
	  {
		  Object result = command.Execute (phonebook);
		    Socket client = new Socket ();
		    try 
		    {
		      client.connect (command.ReturnTo);
		      OutputStream os = client.getOutputStream ();
		      ObjectOutputStream oos = new ObjectOutputStream (os);
		      oos.writeObject (result);
		    } 
		    finally 
		    {
		      if (client != null)
		        client.close ();
		    }
		}
	  }

  	//Recieves and handles the incoming message
	  void HandleConnection (Socket receiver) throws IOException,
	      ClassNotFoundException 
	      {
		  	try 
		  	{
		  		InputStream is = receiver.getInputStream ();
		  		ObjectInputStream ois = new ObjectInputStream (is);
		  		Object o = ois.readObject();
		  		// If it is a phonebookContact
		  		if (o instanceof Command) 
		  		{
		  			Command command = (Command) ois.readObject ();
		  			ExecuteAndSend(command);
		  		}
		  		//if it is a set with it adds the servercontacts to the old set
		  		if (o instanceof Set && o!=null) 
		  		{	
		  			//adds if the collection contains InetSock....
		  			serverSet.addAll((Collection<? extends InetSocketAddress>) o);
		  		}
	    } finally {
	      if (receiver != null)
	        receiver.close ();
	    }
	  }
}
