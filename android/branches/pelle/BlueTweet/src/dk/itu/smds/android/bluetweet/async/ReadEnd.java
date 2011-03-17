package dk.itu.smds.android.bluetweet.async;

import dk.itu.smds.android.bluetweet.Constants;

/**
* Created when a CMD_END message has been read
* in the Bluetooth socket's input stream
*/
public class ReadEnd extends Command {

	public ReadEnd() {
		super(Constants.CMD_END);
	}
	
	@Override
	public void innerExecute() {
		// since this means that the remote device ended to send
		// his tweets, set the remoteEnded variable of the conn to true
		conn.setRemoteEnded();
	}

}
