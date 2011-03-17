package dk.itu.smds.android.bluetweet.async;

import dk.itu.smds.android.bluetweet.Constants;


/**
* Enqueue this command when all the updates
* has been sent to the remote device.
* Also note that the setLocalEnded() method
* is called on the target connection.
*/
public class WriteEnd extends Command {
	
	public WriteEnd() {
		super(Constants.CMD_WRITE_END);
	}

	@Override
	public void innerExecute() {
		// obtain a reference to the IOUtil and write the CMD_END command identifier.
		// call the "setLocalEnded" method on the conn object
		conn.getIOUtil().writeCommand(Constants.CMD_END);
		conn.setLocalEnded();
	}

}
