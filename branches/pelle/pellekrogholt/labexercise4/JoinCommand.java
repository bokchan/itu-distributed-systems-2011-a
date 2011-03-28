// Enum for indicating result of adding an entry
package pellekrogholt.labexercise4;

import java.io.IOException;
import java.net.InetSocketAddress;


// command is a design pattern...
public class JoinCommand extends Command {
	

	
	public InetSocketAddress joing_server;
	public InetSocketAddress receiving_server;
	
	public JoinCommand(InetSocketAddress joing, InetSocketAddress receiving) {

		this.joing_server = joing;
		this.receiving_server = receiving; // coordinator		
	}
	
	// not used since we are not going to work on the phonebook but on the server
	public Object Execute (IPhonebook phonebook) throws IOException {
		throw new IOException("bla bla");
	}
	
}
