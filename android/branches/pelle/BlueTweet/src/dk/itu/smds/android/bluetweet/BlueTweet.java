package dk.itu.smds.android.bluetweet;

import android.net.Uri;
import android.provider.BaseColumns;

public final class BlueTweet {
	public static final String AUTHORITY = "dk.itu.android.provider.BlueTweet";
	
	public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.google.note";
	public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.google.note";
	
	private BlueTweet(){}
	public static final class Tweet implements BaseColumns {
		private Tweet(){}
		
		public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/tweets");
		public static final Uri CONTENT_HASH_URI = Uri.parse("content://" + AUTHORITY + "/hash");
		public static final Uri CONTENT_BTADDR_URI = Uri.parse("content://" + AUTHORITY + "/btaddr");

		public static final String BT_ADDR = "BTADDR";
		public static final String TIMESTAMP = "TIMESTAMP";
		public static final String NAME = "DEVICEFRIENDLYNAME";
		public static final String MSG = "MESSAGE";
		
		public static final String DEFAULT_SORT_ORDER = TIMESTAMP+" DESC";
		public static final String REVERSE_TIMESTAMP_ORDER = TIMESTAMP+" ASC";
		
		public static final String TWEET = "TWEET";

	}
}
