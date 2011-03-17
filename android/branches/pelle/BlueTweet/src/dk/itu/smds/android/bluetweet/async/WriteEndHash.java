package dk.itu.smds.android.bluetweet.async;

import dk.itu.smds.android.bluetweet.Constants;


/**
* Created when the local device finished to
* write his TweetHashes on the Bluetooth
* socket's output stream.
*/
public class WriteEndHash extends Command {

	public WriteEndHash() {
		super(Constants.CMD_WRITE_ENDHASH);
	}
	
	@Override
	public void innerExecute() {
		// obtain a reference to the IOUtil and write the CMD_END_HASH command identifier
		conn.getIOUtil().writeCommand(Constants.CMD_END_HASH);
	}

}
