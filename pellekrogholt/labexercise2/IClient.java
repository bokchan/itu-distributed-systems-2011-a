package pellekrogholt.labexercise2;

import java.io.IOException;

public interface IClient<V, K> {

	/**
	 * Send message to server
	 * 
	 * @param message
	 * @throws IOException 
	 */
	void send(V message) throws IOException;	
	
	
	/**
	 * Send message to server with a command
	 * 
	 * @param command
	 * @param message
	 * @throws IOException 
	 */
	void send(K command, V message) throws IOException;	
	
}
