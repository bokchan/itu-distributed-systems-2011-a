package dk.itu.smds.android.bluetweet.async;

import java.util.Set;

import dk.itu.smds.android.bluetweet.Constants;
import dk.itu.smds.android.bluetweet.TweetHash;

/**
* Created when a CMD_CONF message has been read
* in the Bluetooth socket's input stream
*/
public class ReadConfiguration extends Command {

	public ReadConfiguration(TweetHash remoteConfiguration) {
		super(Constants.CMD_CONF,remoteConfiguration);
	}
	
	@Override
	public void innerExecute() {
		// set the remoteAgeTime of the conn to this.timestamp
		conn.setRemoteAgeTime(hash.getTimestamp());
		
		// load the latests hash and for each of them, 
		// enqueue a WriteHash command.
		// finally, enqueue a WriteEndHash command
		Set<TweetHash> hash = conn.loadLatestHash();
		for(TweetHash h : hash) {
			conn.enqueue(new WriteHash(h));
		}
		conn.enqueue(new WriteEndHash());
	}
}
