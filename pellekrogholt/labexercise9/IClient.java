package pellekrogholt.labexercise9;

import java.io.IOException;

public interface IClient {

	/**
	 * Send value to server
	 * 
	 * @param value
	 * @return 
	 * @throws IOException 
	 */
	void send(Object message) throws IOException, ClassNotFoundException;	
	
	/**
	 * Receive value
	 * 
	 * @throws ClassNotFoundException 
	 * @throws IOException 
	 */
	Object receive() throws IOException, ClassNotFoundException;	
}
