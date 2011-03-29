package bok.labexercise4.extended.gui;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RemoteServerUI {
	private ServerInterface server;

	public RemoteServerUI(ServerInterface server) {
		this.server = server;
	}
	
	void JoinCommand(BufferedReader bisr, boolean asJoiner) throws IOException {
		InetSocketAddress isa;
		if (asJoiner) {
			isa = GetIP(bisr, "joinee server hostname");
		} else {
			isa = GetIP(bisr, "joining server hostname"); 
		}
		
		
		System.out.println("Pinging: " + isa);
		
		
		if (server.Ping(isa)) {
			server.addConnectionPoint(isa, asJoiner);
			System.out.println("Now joined with server:  " + isa); 
		} else
			System.out.println("Could not connect to server");
	}

	void RemoveCommand (BufferedReader bisr) throws IOException {
		InetSocketAddress isa = GetIP(bisr, "the hostname of the server you are connected to");
		server.removeConnectionPoint(isa);
	}

	void GetConnectionPointsCommand () throws IOException {
		Set<InetSocketAddress> list =  server.getConnectionPoints();
		System.out.printf("%s\n", "Connected servers: ");
		for (InetSocketAddress isa : list ) {
			System.out.println(isa);
		}
	}

	static InetSocketAddress GetIP (BufferedReader bisr, String args) throws IOException {
		System.out.printf("Input %s: ", args);

		String hostname = bisr.readLine ();
		while(!verifyISA(hostname)) {
			System.out.println("Input is not a valid IP. Please try again");
			hostname = bisr.readLine ();
		}

		System.out.printf("Input %s server port: ", args);

		String portStr = bisr.readLine();
		while(!verifyPort(portStr)) {
			System.out.println("Input is not a valid port number. Please try again");
			portStr = bisr.readLine ();
		}

		int port = Integer.valueOf(portStr);
		return new InetSocketAddress(hostname, port);
	}

	static boolean verifyISA(String input) {
		String regex = "\\b(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\b";
		Pattern pattern = Pattern.compile(regex);
		Matcher m = pattern.matcher(input) ;
		return m.matches();
	}

	static boolean verifyPort(String input) {
		String regex = "\\d+";
		Pattern pattern = Pattern.compile(regex);
		Matcher m = pattern.matcher(input) ;
		return m.matches();
	}

	void ConnectToServer(BufferedReader bisr) throws IOException {
		InetSocketAddress isa = GetIP(bisr, "hostname of the server you want to connect to");
		
		if (server.ConnectToServer(isa)) {
			System.out.println("Now connected to: " + isa);
		} else {
			System.out.println("Could not connect to server: " + isa);
		}
	}
	
	List<Class<?>> getClasses() throws IOException { 
		return server.getValidInputObjects();
		
	}
}	