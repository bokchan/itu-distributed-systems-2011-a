package dk.itu.smds.android.bluetweet.async;

import dk.itu.smds.android.bluetweet.Constants;
import dk.itu.smds.android.bluetweet.Tweet;


/**
* Created to write a single Tweet on a
* Bluetooth socket.
*/
public class WriteTweet extends Command {

	public WriteTweet(Tweet tweet) {
		super(Constants.CMD_WRITE_TWEET,tweet);
	}
	
	@Override
	public void innerExecute() {
		// obtain a refereice to the IOUtil
		// and call the writeTweet method
		// using the tweet field as parameter
		conn.getIOUtil().writeTweet(tweet);
	}

}
