package dk.itu.smds.android.bluetweet.provider;

import java.util.HashMap;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.provider.LiveFolders;
import android.text.TextUtils;
import android.util.Log;
import dk.itu.smds.android.bluetweet.BlueTweet;

@SuppressWarnings("serial")
public class BlueTweetProvider extends ContentProvider  {
	static final String TAG = BlueTweetProvider.class.getSimpleName();

	static final String DB_NAME = "blue_tweet.db";
	static final int DB_VERSION = 2;
	static final String TWEET_TABLE_NAME = "bluetweet";
	
	private static HashMap<String, String> tweetsProjectionMap;
    private static HashMap<String, String> sLiveFolderProjectionMap;
    private static HashMap<String, String> tweetsHashProjectionMap;

    private static final int TWEETS = 1;
    private static final int TWEET_ID = 2;
    private static final int LIVE_FOLDER_TWEETS = 3;
    private static final int TWEETS_HASH = 4;
    private static final int TWEETS_BTADDR = 5;

    private static final UriMatcher sUriMatcher;
	
	private static class DatabaseHelper extends SQLiteOpenHelper {
		public DatabaseHelper(Context context) {
			super(context,DB_NAME,null,DB_VERSION);
		}
		
		@Override
		public void onCreate(SQLiteDatabase db) {
			Log.i(TAG, "crating db");
			db.execSQL("CREATE TABLE " + TWEET_TABLE_NAME + " ("
				+ BlueTweet.Tweet._ID + " INTEGER PRIMARY KEY, "
				+ BlueTweet.Tweet.BT_ADDR + " TEXT, "
				+ BlueTweet.Tweet.MSG + " TEXT, "
				+ BlueTweet.Tweet.NAME + " TEXT, "
				+ BlueTweet.Tweet.TIMESTAMP + " INTEGER"
				+ ");"
			);
			
		}
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS " + TWEET_TABLE_NAME);
            onCreate(db);
		}
	}
	
	DatabaseHelper helper;
	
	@Override
	public boolean onCreate() {
		helper = new DatabaseHelper(getContext());
		return true;
	}
	
	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		qb.setTables(TWEET_TABLE_NAME);
		Log.i(TAG, "querying tweets");
		
		String groupBy = null;
		String having = null;
		
		switch(sUriMatcher.match(uri)) {
		case TWEETS:
			qb.setProjectionMap(tweetsProjectionMap);
			break;
		case LIVE_FOLDER_TWEETS:
			qb.setProjectionMap(sLiveFolderProjectionMap);
			break;
		case TWEET_ID:
			qb.setProjectionMap(tweetsProjectionMap);
			qb.appendWhere(BlueTweet.Tweet._ID + "=" + uri.getPathSegments().get(1));
			break;
		case TWEETS_HASH:
			qb.setProjectionMap(tweetsHashProjectionMap);
			groupBy = BlueTweet.Tweet.BT_ADDR;
			having = "max(" + BlueTweet.Tweet.TIMESTAMP + ")";
			break;
		case TWEETS_BTADDR:
			qb.setProjectionMap(tweetsProjectionMap);
			qb.appendWhere(BlueTweet.Tweet.BT_ADDR + "='" + uri.getPathSegments().get(1)+"'");
			break;
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
		
		String orderBy;
		if(TextUtils.isEmpty(sortOrder)) {
			orderBy = BlueTweet.Tweet.DEFAULT_SORT_ORDER;
		} else {
			orderBy = sortOrder;
		}
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor c = qb.query(db, projection, selection, selectionArgs, groupBy, having, orderBy);
		c.setNotificationUri(getContext().getContentResolver(), uri);
		return c;
	}
	
	@Override
	public String getType(Uri uri) {
		switch(sUriMatcher.match(uri)) {
		case TWEETS:
		case LIVE_FOLDER_TWEETS:
			return BlueTweet.CONTENT_TYPE;
		case TWEET_ID:
			return BlueTweet.CONTENT_ITEM_TYPE;
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
	}

	@Override
	public Uri insert(Uri uri, ContentValues initValues) {
		Log.i(TAG, "creating new tweet");
		if(sUriMatcher.match(uri) != TWEETS) {
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
		
		ContentValues values = (initValues != null) ? new ContentValues(initValues) : new ContentValues();
		
		Long now = Long.valueOf(System.currentTimeMillis());
		
		if(!values.containsKey(BlueTweet.Tweet.TIMESTAMP)) {
			values.put(BlueTweet.Tweet.TIMESTAMP, now);
		} else {
			int id = rowExists( values.getAsString(BlueTweet.Tweet.BT_ADDR),values.getAsLong(BlueTweet.Tweet.TIMESTAMP) );
			if(id != -1) {
				Log.w(TAG,"tweet exists: "+values.get(BlueTweet.Tweet.BT_ADDR)+"/"+values.get(BlueTweet.Tweet.TIMESTAMP));
				return ContentUris.withAppendedId(BlueTweet.Tweet.CONTENT_URI, id);
			}
		}
		if(!values.containsKey(BlueTweet.Tweet.MSG)) {
			values.put(BlueTweet.Tweet.MSG, "");
		}
		
		SQLiteDatabase db = helper.getWritableDatabase();
		long rowId = db.insert(TWEET_TABLE_NAME, BlueTweet.Tweet.TWEET, values);
		if(rowId>0) {
			Uri noteUri = ContentUris.withAppendedId(BlueTweet.Tweet.CONTENT_URI, rowId);
			getContext().getContentResolver().notifyChange(noteUri, null);
			return noteUri;
		}
		throw new SQLException("Failed to insert row into " + uri);
	}
	private int rowExists(String btAddr,long timestamp) {
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor c = db.query(TWEET_TABLE_NAME, new String[]{BlueTweet.Tweet._ID}, 
			BlueTweet.Tweet.BT_ADDR+"=? AND "+BlueTweet.Tweet.TIMESTAMP+"=?", 
			new String[]{btAddr,timestamp+""}, null, null, null);
		int out = -1;
		if( c.moveToFirst() ) {
			out = c.getInt(0);
		}
		c.close();
		return out;
	}



	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		
		SQLiteDatabase db = helper.getWritableDatabase();
		int count;
		switch(sUriMatcher.match(uri)){
		case TWEETS:
			count = db.delete(TWEET_TABLE_NAME, selection, selectionArgs);
			break;
		case TWEET_ID:
			String tweetId = uri.getPathSegments().get(1);
			count = db.delete(TWEET_TABLE_NAME, BlueTweet.Tweet._ID + "=" + tweetId
					+ (!TextUtils.isEmpty(selection) ? " AND (" + selection + ")":""), selectionArgs);
			break;
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
		
		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}

	@Override
	public int update(Uri uri, ContentValues values, String where,
			String[] whereArgs) {
		SQLiteDatabase db = helper.getWritableDatabase();
        int count;
        switch (sUriMatcher.match(uri)) {
        case TWEETS:
            count = db.update(TWEET_TABLE_NAME, values, where, whereArgs);
            break;

        case TWEET_ID:
            String tweetId = uri.getPathSegments().get(1);
            count = db.update(TWEET_TABLE_NAME, values, BlueTweet.Tweet._ID + "=" + tweetId
                    + (!TextUtils.isEmpty(where) ? " AND (" + where + ')' : ""), whereArgs);
            break;

        default:
            throw new IllegalArgumentException("Unknown URI " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
	}
	
	static {
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        sUriMatcher.addURI(BlueTweet.AUTHORITY, "hash", TWEETS_HASH);
        sUriMatcher.addURI(BlueTweet.AUTHORITY, "btaddr/*", TWEETS_BTADDR);
        sUriMatcher.addURI(BlueTweet.AUTHORITY, "tweets", TWEETS);
        sUriMatcher.addURI(BlueTweet.AUTHORITY, "tweets/#", TWEET_ID);
        sUriMatcher.addURI(BlueTweet.AUTHORITY, "live_folders/tweets", LIVE_FOLDER_TWEETS);

        tweetsProjectionMap = new HashMap<String, String>();
        tweetsProjectionMap.put(BlueTweet.Tweet._ID, BlueTweet.Tweet._ID);
        tweetsProjectionMap.put(BlueTweet.Tweet.NAME, BlueTweet.Tweet.NAME);
        tweetsProjectionMap.put(BlueTweet.Tweet.MSG, BlueTweet.Tweet.MSG);
        tweetsProjectionMap.put(BlueTweet.Tweet.TIMESTAMP, BlueTweet.Tweet.TIMESTAMP);
        tweetsProjectionMap.put(BlueTweet.Tweet.BT_ADDR, BlueTweet.Tweet.BT_ADDR);
        
        tweetsHashProjectionMap = new HashMap<String,String>(){{
        	put(BlueTweet.Tweet._ID,BlueTweet.Tweet._ID);
        	put(BlueTweet.Tweet.BT_ADDR,BlueTweet.Tweet.BT_ADDR);
        	put(BlueTweet.Tweet.TIMESTAMP,BlueTweet.Tweet.TIMESTAMP);
        }};
        

        // Support for Live Folders.
        sLiveFolderProjectionMap = new HashMap<String, String>();
        sLiveFolderProjectionMap.put(LiveFolders._ID, BlueTweet.Tweet._ID + " AS " +
                LiveFolders._ID);
        sLiveFolderProjectionMap.put(LiveFolders.NAME, BlueTweet.Tweet.NAME + " AS " +
                LiveFolders.NAME);
        sLiveFolderProjectionMap.put(LiveFolders.DESCRIPTION, BlueTweet.Tweet.MSG + " AS " +
        		LiveFolders.DESCRIPTION);
    }
}
