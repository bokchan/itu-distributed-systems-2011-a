// Phonebook server accepts commands and executes them.
package pellekrogholt.labexercise4;

import java.io.*;
import java.util.*;
import java.net.*;
import java.util.concurrent.*;

public class PhonebookServer implements Runnable {
  private IPhonebook phonebook = new Phonebook ();

  private ServerSocket Listener;

  private InetAddress localip;

  
  // this is orignal code that 
  public LinkedList<InetSocketAddress> LocalEndpoints = new LinkedList<InetSocketAddress> ();


  // these are the replication servers
  private ArrayList<InetSocketAddress> server_end_points = new ArrayList<InetSocketAddress>();
  
  
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
          final Socket client = Listener.accept(); // note: blocking call 
          exeservice.execute (new Runnable () {
            public void run () {
              try {
                HandleConnection(client);
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
  
  
  // send to the other server
  void SendJoinCommand (JoinCommand command, InetSocketAddress sendTo) throws IOException {
//	    // This sends a JoinCommand to the server with the address of 'sendTo'
//	  JoinCommand joincmd = (JoinCommand) command;
//	  server_end_points.add(joincmd.joing_server);
	  
//	  	// here should the socket be made to server we like to connect to - TCP communication
//	  	JoinCommand joincmd = (JoinCommand) command;
//	    Socket joiningServer = new Socket();
//	    try {
//	      joiningServer.connect(joincmd.receiving_server); //TODO: not sure about this one
//	      // or joincmd.joing_server
//	      OutputStream os = joiningServer.getOutputStream ();
//	      ObjectOutputStream oos = new ObjectOutputStream (os);
//	      oos.writeObject(joincmd);
//	    } finally {
//	      if (joiningServer != null)
//	        joiningServer.close ();
//	    }
	  
	    //based on 
	    // Object SendAndReceive (Command command) 
	    // from RemotePhoneBook
//        ServerSocket listener = new ServerSocket (0);
//        command.ReturnTo = (InetSocketAddress) listener.getLocalSocketAddress ();
        
	  
	  JoinCommand joincmd = (JoinCommand) command;
	  
	  System.out.println("joincmd.joing_server: " + joincmd.joing_server);
	  System.out.println("joincmd.receiving_server: " + joincmd.receiving_server);
	  
	    Socket client = new Socket();
        try {
          client.connect (joincmd.receiving_server); // pelle: confused med but for 
          // now JoinCommand is called on the joining server.... 
          OutputStream os = client.getOutputStream();
          ObjectOutputStream oos = new ObjectOutputStream (os);
          oos.writeObject(joincmd);
        } finally {
          if (client != null)
            client.close();
        }

  }
  
  void ExecuteAndSend (Command command) throws IOException {
	if (command instanceof JoinCommand ) {
	    JoinCommand joincmd = (JoinCommand) command;
	    if ( joincmd.receiving_server.equals(LocalEndpoints.getFirst() ) ) {
	    	// add joining server and forward - according to the algorithm is 'my' responsibility to forward
	    	//TODO:
	    } else {
			// add joining server and do nothing
	    	//TODO:
	    }
	  } else {
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
  }

  void HandleConnection (Socket client) throws IOException, ClassNotFoundException {
      try {
        InputStream is = client.getInputStream ();
        ObjectInputStream ois = new ObjectInputStream (is);
        Object o = ois.readObject();
        if (o instanceof Command) {
          // ... do the same as it has always done ...
            Command command = (Command) ois.readObject ();
            ExecuteAndSend (command);
        }
        if (o instanceof ArrayList) {
          // ... update the endpoints list ...
        	// TODO: note 100% sure what should happen here 
        }
      } finally {
        if (client != null)
          client.close ();
      }
  }
  
  
  public ArrayList<InetSocketAddress> getServerEndPoint(){
	  return server_end_points;
  }
  
}
