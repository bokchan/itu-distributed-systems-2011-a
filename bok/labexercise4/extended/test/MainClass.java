package bok.labexercise4.extended.test;
// Main program entry point

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import bok.labexercise4.Guid;
import bok.labexercise4.extended.BokServer;
import bok.labexercise4.extended.commands.Command;
import bok.labexercise4.extended.commands.JoinServerCommand;

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
		BufferedReader bisr = new BufferedReader (new InputStreamReader (System.in));
		System.out.println("Input the number of servers you want to start");
		String input = bisr.readLine();
		while(input.length() == 0 ) {
			input = bisr.readLine();
		}
			int serverCount = Integer.valueOf(input);

			MD5 = MessageDigest.getInstance ("MD5");

			ArrayList<BokServer> servers  = new ArrayList<BokServer>(); 
			for (int i = 0; i< serverCount; i++) {
				BokServer server = new BokServer();

				Thread serverThread = new Thread (server);
				serverThread.start ();

				servers.add(server);
				String s = i == 0 ? " your server" : " another server"; 
				System.out.printf("I'm%s listening on\n", s);
				for (InetSocketAddress sa : server.LocalEndpoints) {
					System.out.println ("  " + sa);
				}
				System.out.println ("My GUID is "
						+ GuidFromString (server.getIP().toString()));

			}
		

		BokServer server = (BokServer) servers.get(0);
		BokServer server2 = (BokServer) servers.get(1);

		Command<JoinServerCommand> command4 = new JoinServerCommand(server2.getIP(), server.getIP());
		command4.setReceiver(server.getIP());
		command4.setSender(server2.getIP());
		server2.Send(command4, server.getIP());


		//		Book book = new Book("13-123-12-987", "The adventures of Hans Gr�ner", 2001, "Peter Hansen"); 
		//		ICommand<AddItemCommand> command = new AddItemCommand(book);
		//		server.Send(command, server2.getIP());
		//		
		//		Car car = new Car("AZ13234", "SunRazer", "FIAT", 1999); 
		//		command = new AddItemCommand(car);
		//		server.Send(command, server2.getIP());
		//				
		//		Command<RemoveItemCommand> command3 = new RemoveItemCommand("AZ13234");
		//		command3.setReceiver(server.getIP());
		//		command3.setSender(server2.getIP());
		//		server2.Send(command3, server.getIP());
		//		
		//		ICommand<GetAllCommand> command2 = new GetAllCommand();
		//		server.ExecuteAndSend(command2);
		//		List<IItem<IItem<?>>> list= (List<IItem<IItem<?>>>) server.getData().GetAll(); 
		//		System.out.println(list.toString());
		//		


		if (servers.size()> 0) {
			//			RemotePhonebookServer remoteserver = new RemotePhonebookServer(servers.get(0).getIP());
			//			ServerInterface s = new ServerInterface(remoteserver);
			//			IPhonebook phonebook = new RemotePhonebook (servers.get(0).getIP());
			//			UserInterface ui = new UserInterface (phonebook, s);
			//			ui.Start ();
		}else {
			System.out.println("Exiting main...missing specification of server count");
		}

//		for (AbstractServer s: servers) {
//			s.abort();
//		}

	}
}
