package dk.itu.smds.android.bluetweet.async;

import android.util.Log;
import dk.itu.smds.android.bluetweet.AbstractAsyncBlueTweetConnection;
import dk.itu.smds.android.bluetweet.Tweet;
import dk.itu.smds.android.bluetweet.TweetHash;

public abstract class Command {
	static final String TAG = "Command";
	
	protected byte cmd;
	
	// a command may contains a tweet or a tweethash
	protected Tweet tweet;
	protected TweetHash hash;
	//
	
	// the async connection to which this command regards
	protected AbstractAsyncBlueTweetConnection conn;
	
	protected Command(byte cmd, Tweet tweet) {
		this(cmd);
		this.tweet = tweet;
	}
	protected Command(byte cmd, TweetHash hash){
		this(cmd);
		this.hash = hash;
	}
	
	protected Command(
		byte cmd) {
		this.cmd = cmd;
	}

	public byte getCmd() {
		return cmd;
	}
	
	public void setConn(AbstractAsyncBlueTweetConnection conn) {
		this.conn = conn;
	}
	
	public void execute() {
		if(conn == null || conn.isError() || conn.ended()) {
			Log.w(TAG,"connection is null, in error or terminated, skipping command");
			return;
		}
		innerExecute();
	}
	
	protected abstract void innerExecute();
}
