package dk.itu.smds.android.bluetweet;

/**
 * Identifiers of the commands used in the network protocol.
 * @author frza
 *
 */
public final class Constants {
	
	// these commands are sent through Bluetooth
	
	/**
	 * Sent at the beginning of the interaction,
	 * identifier of the CONFIGURATION command
	 */
	public static final byte CMD_CONF = 0;
	/**
	 * Sent before writing a TweetHash,
	 * identifier of the TWEET HASH command
	 */
	public static final byte CMD_HASH = 1;
	/**
	 * Sent when a device ends to send hash
	 */
	public static final byte CMD_END_HASH = 2;
	/**
	 * Sent before writing a Tweet,
	 * identifier of the TWEET command
	 */
	public static final byte CMD_TWEET = 4;
	/**
	 * Sent when a device ends to send tweets
	 */
	public static final byte CMD_END = 8;
	
	//
	
	// these commands are used only in
	// asynchronous connections implementations,
	// and are not sent through Bluetooth
	
	/**
	 * Identifier of the WRITE CONFIGURATION command
	 */
	public static final byte CMD_WRITE_CONF = 10;
	/**
	 * Identifier of the WRITE HASH command
	 */
	public static final byte CMD_WRITE_HASH = 11;
	/**
	 * Identifier of the WRITE TWEET command
	 */
	public static final byte CMD_WRITE_TWEET = 12;
	/**
	 * Identifier of the WRITE ENDHASH command
	 */
	public static final byte CMD_WRITE_ENDHASH = 13;
	/**
	 * Identifier of the WRITE END command
	 */
	public static final byte CMD_WRITE_END = 14;
	
	//

	/**
	 * Give the string representation of the given
	 * command identifier
	 */
	public static final String StringifyCommand(byte cmd) {
		String out = "";
		switch(cmd) {
		case CMD_CONF:			out="CONF"; break;
		case CMD_END:			out="END"; break;
		case CMD_END_HASH:		out="ENDHASH"; break;
		case CMD_HASH:			out="HASH"; break;
		case CMD_TWEET:			out="TWEET"; break;
		case CMD_WRITE_CONF:	out="WRITECONF"; break;
		case CMD_WRITE_END:		out="WRITEEND"; break;
		case CMD_WRITE_ENDHASH:	out="WRITEENDHASH"; break;
		case CMD_WRITE_HASH:	out="WRITEHASH"; break;
		case CMD_WRITE_TWEET:	out="WRITETWEET"; break;
		case -1:				out="end of stream!"; break;
		default:				out="Unknown command "+cmd+" - to char: " + ((char)cmd); break;
		}
		return out;
	}
}
