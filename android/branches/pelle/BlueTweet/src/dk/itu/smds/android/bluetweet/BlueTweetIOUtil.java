package dk.itu.smds.android.bluetweet;

import static dk.itu.smds.android.bluetweet.Constants.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.Collection;

import android.util.Log;

/**
 * Utility class to handle IO interactions.
 * @author frza
 *
 */
public class BlueTweetIOUtil {
	static final String TAG = BlueTweetIOUtil.class.getSimpleName();
	
	ByteBuffer write_bbuf = ByteBuffer.allocate(512);
	ByteBuffer read_bbuf = ByteBuffer.allocate(512);
	
	AbstractBlueTweetConnection parent;
	
	byte[] longIntBuf = new byte[12];
	byte[] longIntIntIntBuf = new byte[20];
	
	InputStream is;
	OutputStream os;
	
	public BlueTweetIOUtil(AbstractBlueTweetConnection parent) {
		this.parent = parent;
		is = parent.is;
		os = parent.os;
	}
	
	private byte[] getUtf8Bytes(String str) {
		try {
			return str.getBytes("UTF-8");
		} catch(UnsupportedEncodingException e) {
			return new byte[]{};
		}
	}
	
	private void write(ByteBuffer buf, boolean flush) {
		write(buf.array(),0,buf.position(),flush);
	}
	private void write(byte[] buffer, int offset, int count, boolean flush) {
		try {
			os.write(buffer, offset, count);
			if(flush) {
				os.flush();
			}
		} catch(IOException e){
			parent.setError(e);
		}
	}
	
	/**
	 * Write the <code>cmd</code> to the output stream, and flush.
	 * @param cmd
	 */
	public void writeCommand(byte cmd){writeCommand(cmd,true);}
	public void writeCommand(byte cmd, boolean flush) {
		try {
			Log.i(TAG,"writing command: "+cmd);
			os.write(cmd);
			if(flush) {
				Log.i(TAG,"flushing writeCommand");
				os.flush();
			}
		} catch(IOException e){
			parent.setError(e);
		}
	}
	
	/**
	 * Write the connection configuration and flush
	 */
	public void writeConfiguration() {
		String localDevice = parent.getLocalConfiguration().localBluetoothAddress;
		long localAge = parent.getLocalConfiguration().ageTime;
		byte[] btAddr = getUtf8Bytes(localDevice);
		write_bbuf.rewind();
		write_bbuf.put(CMD_CONF)
		.putLong(localAge)
		.putInt(btAddr.length)
		.put(btAddr);
		write(write_bbuf,true);
	}
	
	/**
	 * Write a TweetHash to the output stream.
	 * The command <code>CMD_HASH</code> is also sent.
	 * @see Constants.CMD_HASH
	 * @param hash
	 */
	public void writeHash(TweetHash hash){writeHash(hash.getOriginAddress(),hash.getTimestamp());}
	public void writeHash(String originalAddress,long timestamp) {
		byte[] btAddr = getUtf8Bytes(originalAddress);
		write_bbuf.rewind();
		write_bbuf.put(CMD_HASH)
		.putLong(timestamp)
		.putInt(btAddr.length)
		.put(btAddr);
		write(write_bbuf,true);
	}
	
	/**
	 * Write a collection of TweetHash to the output stream.
	 * For each TweetHash, the method <code>writeHash(TweetHash)</code> is called.
	 * @param hash
	 */
	public void writeHashes(Collection<TweetHash> hash) {
		if(hash.size()==0){return;}
		for(TweetHash h : hash) {
			writeHash(h);
			if(parent.isError){return;}
		}
	}
	
	/**
	 * Write a Tweet to the output stream.
	 * The command <code>CMD_TWEET</code> is also sent.
	 * @param tweet
	 */
	public void writeTweet(Tweet tweet) {
		writeTweet(tweet.getBluetoothAddress(),tweet.getTimestamp(),tweet.getDeviceName(),tweet.getMessage());
	}
	public void writeTweet(String deviceAddress, long timestamp, String deviceName, String message) {
		byte[] btAddr,name,msg;
		btAddr = getUtf8Bytes(deviceAddress);
		name = getUtf8Bytes(deviceName);
		msg = getUtf8Bytes(message);
		write_bbuf.rewind();
		write_bbuf.put(CMD_TWEET)
		.putLong(timestamp)
		.putInt(btAddr.length)
		.putInt(name.length)
		.putInt(msg.length)
		.put(btAddr)
		.put(name)
		.put(msg);
		write(write_bbuf,true);
	}
	
	/**
	 * Write a collection of Tweet to the output stream.
	 * For each Tweet, the method <code>writeTweet(Tweet)</code> is called.
	 * @param tweets
	 */
	public void writeTweets(Collection<Tweet> tweets) {
		if(tweets.size()==0){
			return;
		}
		for(Tweet t : tweets) {
			writeTweet(t);
			if(parent.isError){return;}
		}
	}
	
	/**
	 * Read a single byte from the input stream and return it.
	 * @return
	 */
	public byte readCommand() {
		try {
			byte out = (byte)is.read();
			Log.i(TAG,"Read command: "+Constants.StringifyCommand(out));
			return out;
		} catch(IOException e) {
			parent.setError(e);
		}
		return -1;
	}
	
	/**
	 * After reading the <code>CMD_CONF</code> or the
	 * <code>CMD_HASH</code>,
	 * this method can be called to read the remote configuration
	 * or the TweetHash (respectively).
	 * <br/><br/>
	 * Note that the remote configuration is returned in
	 * a TweetHash container.
	 * @return
	 */
	public TweetHash readConfigurationOrHash() {
		try {
			is.read(longIntBuf);
			read_bbuf.rewind();
			read_bbuf.put(longIntBuf);
			read_bbuf.rewind();
			long timestamp = read_bbuf.getLong();
			int len = read_bbuf.getInt();
			
			String btAddr = readString(is,len,false);
			
			return new TweetHash(btAddr,timestamp);
		} catch (IOException e) {
			parent.setError(e);
		}
		return null;
	}
	
	/**
	 * After reading the <code>CMD_TWEET</code>,
	 * this method can be called to read a tweet
	 * @return
	 */
	public Tweet readTweet() {
		try {
			Tweet out = new Tweet();
			is.read(longIntIntIntBuf);
			read_bbuf.rewind();
			read_bbuf.put(longIntIntIntBuf);
			read_bbuf.rewind();
			
			out.setTimestamp( read_bbuf.getLong() );
			out.setBluetoothAddress( readString(is, read_bbuf.getInt(), false) );
			out.setDeviceName( readString(is, read_bbuf.getInt(), true) );
			out.setMessage( readString(is, read_bbuf.getInt(), true) );
			
			return out;
		} catch (IOException e) {
			parent.setError(e);
			return null;
		}
	}
	
	private String readString(InputStream is, int len, boolean utf) throws IOException {
		byte[] bytes = new byte[len];
		is.read(bytes);
		String out = null;
		if(utf) {
			out = new String(bytes,"UTF-8");
		} else {
			out = new String(bytes,"ASCII");
		}
		Log.i(TAG,"read string: "+out+", which bytesize is: "+len);
		return out;
	}
}
