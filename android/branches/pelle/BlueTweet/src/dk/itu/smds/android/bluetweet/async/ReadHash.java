package dk.itu.smds.android.bluetweet.async;

import java.util.List;

import dk.itu.smds.android.bluetweet.Constants;
import dk.itu.smds.android.bluetweet.Tweet;
import dk.itu.smds.android.bluetweet.TweetHash;

/**
* Created when an CMD_HASH message has been read
* in the Bluetooth socket's input stream.
* The read TweetHash is passed as a constructor
* parameter.
*/
public class ReadHash extends Command {

	public ReadHash(TweetHash hash) {
		super(Constants.CMD_HASH,hash);
	}
	
	@Override
	public void innerExecute() {
		// store the received hash in the conn
		conn.addReceivedHash(hash);
		
		// load the updates for this hash,
		// then for each of them, enqueue a WriteTweet command
		List<Tweet> tweets = conn.loadTweetUpdates(hash);
		for(Tweet t : tweets) {
			conn.enqueue(new WriteTweet(t));
		}
	}

}
