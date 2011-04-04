package pellekrogholt.labexercise9;


import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

public class AuthenticationTcpServer extends TcpServer {

	public AuthenticationTcpServer(int port) throws IOException {
		super(port);
		ServerSocket server_socket = new ServerSocket( port );

		// this part handles multiple connections / users concurrently
		while(true) {
			Socket client_socket = server_socket.accept(); // blocking call code bellow not executed before request from client
			Connection c = new Connection(client_socket);
			new Thread(c).start(); 
		}  

	}

	private void send(Object o) {
		try {				
			super.oos.writeObject(o);
		} 
		catch (Exception e) {	
		}
	}

}