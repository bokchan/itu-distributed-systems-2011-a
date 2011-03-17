package dk.itu.smds.android.bluetweet.async;

import java.util.List;

import dk.itu.smds.android.bluetweet.Constants;
import dk.itu.smds.android.bluetweet.Tweet;

/**
* Created when an CMD_ENDHASH message has been read
* in the Bluetooth socket's input stream
*/
public class ReadEndHash extends Command {

	public ReadEndHash() {
		super(Constants.CMD_END_HASH);
	}
	
	@Override
	public void innerExecute() {
		// this means that the remote device ended to sends hash.
		// Retrieve the received hash, compute the remaining tweets
		// enqueue a WriteTweet command for each of them,
		// finally enqueue a WriteEnd command
		List<Tweet> tweets = conn.bulkLoadTweetUpdates();
		for(Tweet t: tweets) {
			conn.enqueue(new WriteTweet(t));
		}
		conn.enqueue(new WriteEnd());
		
	}

}
