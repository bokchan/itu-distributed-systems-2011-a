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
	 * @param value
	 * @param operator
	 * @throws IOException 
	 */
	void send(V value, int operator) throws IOException;	
	
	
// temporarily disabled
//	/**
//	 * Receive value
//	 * @throws ClassNotFoundException 
//	 * @throws IOException 
//	 */
//	V receive() throws IOException, ClassNotFoundException;
	
}
