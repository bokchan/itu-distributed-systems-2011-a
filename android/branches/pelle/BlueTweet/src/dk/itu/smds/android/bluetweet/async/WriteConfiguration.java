package dk.itu.smds.android.bluetweet.async;

import dk.itu.smds.android.bluetweet.Constants;


/**
* The first command that an async connection 
* has to enqueue; writes the local configuration
* to the Bluetooth socket's output stream
*/
public class WriteConfiguration extends Command {

	public WriteConfiguration() {
		super(Constants.CMD_WRITE_CONF);
	}
	
	@Override
	public void innerExecute() {
		// obtain a reference to the IOUtil and call the writeConfiguration method
		conn.getIOUtil().writeConfiguration();
	}

}
