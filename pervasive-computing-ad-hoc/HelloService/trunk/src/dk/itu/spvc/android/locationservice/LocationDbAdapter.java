/*
 * Copyright (C) 2008 Google Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package dk.itu.spvc.android.locationservice;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * 
 * Create, read, update and save a track to a data base on the android device.
 * 
 * Based on the note tutorial xxx TODO: write link
 *  
 * Old note class description.
 * 
 * Simple notes database access helper class. Defines the basic CRUD operations
 * for the notepad example, and gives the ability to list all notes as well as
 * retrieve or modify a specific note.
 * 
 * This has been improved from the first version of this tutorial through the
 * addition of better error handling and also using returning a Cursor instead
 * of using a collection of inner classes (which is less scalable and not
 * recommended).
 */
public class LocationDbAdapter {

    public static final String KEY_LATITUDE = "latitude"; // previously title KEY__TITLE 
    public static final String KEY_LONGITUDE = "longitude"; // previously title KEY__BODY
    public static final String KEY_ROWID = "_id"; // 
    public static final String KEY_TRACKID = "track_id"; //
    public static final String KEY_DATETIME = "date_time"; //

    private static final String TAG = "LocationDbAdapter";
    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;

    /**
     * Database creation sql statement
     * 
     * NB! more on sql lite 3 datatypes http://www.sqlite.org/datatype3.html 
     * 
     * if the sql statement get complex its possible to move to an xml file
     * but for this simple example we keep it here.
     * TODO: look up reference for that
     * 
     */
    private static final String DATABASE_CREATE =
        "create table tracks (_id integer primary key autoincrement, "
		+ "track_id integer not null, date_time datetime default current_date,"
		+ "latitude double not null, longitude double not null);";
    
    private static final String DATABASE_NAME = "locationservicedata.db";
    private static final String DATABASE_TABLE = "tracks";
    private static final int DATABASE_VERSION = 1;

    private final Context mCtx;

    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(DATABASE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS tracks");
            onCreate(db);
        }
    }

    /**
     * Constructor - takes the context to allow the database to be
     * opened/created
     * 
     * @param ctx the Context within which to work
     */
    public LocationDbAdapter(Context ctx) {
        this.mCtx = ctx;
    }

    /**
     * Open a tracks database. If it cannot be opened, try to create a new
     * instance of the database. If it cannot be created, throw an exception to
     * signal the failure
     * 
     * @return this (self reference, allowing this to be chained in an
     *         initialization call)
     * @throws SQLException if the database could be neither opened or created
     */
    public LocationDbAdapter open() throws SQLException {
        mDbHelper = new DatabaseHelper(mCtx);
        mDb = mDbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        mDbHelper.close();
    }


    
    public int createTrackId() {
    	
    	//select track_id from tracks order by track_id desc limit 1;
    	
    	String sql = "select track_id from " + DATABASE_TABLE + " order by track_id desc limit 1";
    	//int lastTrackId = mDb.execSQL(sql);
    	
//    	mDb.execSQL(sql);
//    	
//    	
//    	selectionArgs
//    	
    	
    	String[] selectionArgs = null; 
    	Cursor test = mDb.rawQuery(sql, selectionArgs);
    	Log.w(TAG, "=====================================================================" );
    	Log.w(TAG, "createTrackId called and cursor = " + test );
    	// prints: W/LocationDbAdapter(  745): createTrackId called and cursor = android.database.sqlite.SQLiteCursor@44f08918
    	
    	Cursor listCursor = test;
    	listCursor.moveToFirst();
    	if(! listCursor.isAfterLast()) {
    		do {
    			
    			long id = listCursor.getLong(0);
//    			int track_id = listCursor.getLong(1);
    	
    			Log.w(TAG, "listCursor.getLong(0): " + id );
    			
//    			Long id = listCursor.getLong(0);
//    			String name= listCursor.getString(1);
//    			t = new Food(name);
//    			foods.add(t);
    		} while (listCursor.moveToNext());
    	} 
    	listCursor.close();
    	Log.w(TAG, "=====================================================================" );
    	
    	return 2;
    }
    
    
    
    
    
    
    
    /**
     * Create a new track using the latitude and longitude provided. If a track is
     * successfully created return the new rowId for that track, otherwise return
     * a -1 to indicate failure.
     * 
     * @param trackId
     * @param latitude
     * @param longitude
     * @return
     */
    public long createTrack(int trackId, double latitude, double longitude) {
        ContentValues initialValues = new ContentValues();
        
        initialValues.put(KEY_TRACKID, trackId);
        // KEY_DATETIME - inserted automatically
        initialValues.put(KEY_LATITUDE, latitude);
        initialValues.put(KEY_LONGITUDE, longitude);

        return mDb.insert(DATABASE_TABLE, null, initialValues);
    }

    /**
     * Delete an entry with the given rowId
     * 
     * @param rowId id of track to delete
     * @return true if deleted, false otherwise
     */
    public boolean deleteRowId(long rowId) {

        return mDb.delete(DATABASE_TABLE, KEY_ROWID + "=" + rowId, null) > 0;
    }
    
//    TODO: make an deleteTrack
//    public boolean deleteTack(long trackId) {

    /**
     * Return a Cursor over the list of all tracks in the database
     * 
     * @return Cursor over all tracks
     */
    public Cursor fetchAllTracks() {

        return mDb.query(DATABASE_TABLE, new String[] {KEY_ROWID, KEY_LATITUDE,
                KEY_LONGITUDE}, null, null, null, null, null);
    }

    /**
     * Return a Cursor positioned at a track that matches the given rowId
     * 
     * @param rowId id of track to retrieve
     * @return Cursor positioned to matching track, if found
     * @throws SQLException if track could not be found/retrieved
     */
    public Cursor fetchTrack(long rowId) throws SQLException {

        Cursor mCursor =

            mDb.query(true, DATABASE_TABLE, new String[] {KEY_ROWID,
                    KEY_LATITUDE, KEY_LONGITUDE}, KEY_ROWID + "=" + rowId, null,
                    null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;

    }

    
//    TODO: probobaly just delete this one since we might not need to update 
//    modify existing records..
    
    /**
     * Update a track using the details provided. The track to be updated is
     * specified using the rowId, and it is altered to use the latitude and longitude
     * values passed in
     * 
     * @param rowId id of track to update
     * @param latitude value to set track latitude to
     * @param longitude value to set track longitude to
     * @return true if a track was successfully updated, false otherwise
     */
    public boolean updateNote(int rowId, int trackId, double latitude, double longitude) {
        ContentValues args = new ContentValues();
        
        args.put(KEY_TRACKID, trackId);
        args.put(KEY_LATITUDE, latitude);
        args.put(KEY_LONGITUDE, longitude);

        return mDb.update(DATABASE_TABLE, args, KEY_ROWID + "=" + rowId, null) > 0;
    }
}
