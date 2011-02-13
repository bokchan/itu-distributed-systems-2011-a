package pellekrogholt.labexercise2;

import java.io.IOException;

public interface IClient<V, C> {

	/**
	 * Send value to server
	 * 
	 * @param value
	 * @throws IOException 
	 */
	void send(V value) throws IOException;	
	
	
	/**
	 * Send value to server with a command
	 * 
	 * @param command
	 * @param value
	 * @throws IOException 
	 */
	void send(V value, C command) throws IOException;	
	
	
	
	/**
	 * Receive value
	 * @throws ClassNotFoundException 
	 * @throws IOException 
	 */
	V receive() throws IOException, ClassNotFoundException;
	
}
