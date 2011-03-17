package dk.itu.smds.android.bluetweet.async;

import dk.itu.smds.android.bluetweet.Constants;
import dk.itu.smds.android.bluetweet.TweetHash;

/**
* Created to write a single TweetHash on a
* Bluetooth socket.
*/
public class WriteHash extends Command {

	public WriteHash(TweetHash hash) {
		super(Constants.CMD_WRITE_HASH,hash);
	}
	
	@Override
	public void innerExecute() {
		// obtain a reference to the IOUtil and call the method writeHash
		// using the this.hash property as parameter
		conn.getIOUtil().writeHash(hash);
	}

}
