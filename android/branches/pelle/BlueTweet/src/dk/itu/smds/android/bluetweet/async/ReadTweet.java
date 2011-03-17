package dk.itu.smds.android.bluetweet.async;

import dk.itu.smds.android.bluetweet.Constants;
import dk.itu.smds.android.bluetweet.Tweet;


/**
* Created when an CMD_TWEET message has been read
* in the Bluetooth socket's input stream.
* The read Tweet is passed as a constructor
* parameter.
*/
public class ReadTweet extends Command {

	public ReadTweet(Tweet tweet) {
		super(Constants.CMD_TWEET,tweet);
	}
	
	@Override
	public void innerExecute() {
		// store the tweet in the conn object.
		conn.store(tweet);
	}

}
