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

package dk.itu.spct.motionrecorderandroid;

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
 * 
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
public class DbAdapter {

	public static final String KEY_LATITUDE = "latitude"; // previously title
															// KEY__TITLE
	public static final String KEY_LONGITUDE = "longitude"; // previously title
															// KEY__BODY
	public static final String KEY_LOCATION_PROVIDER = "location_provider";

	public static final String KEY_ROWID = "_id"; //
	public static final String KEY_UUID = "uuid";
	public static final String KEY_DATETIME = "date_time";
	public static final String KEY_TIME_STAMP_END = "time_stamp_end";
	public static final String KEY_MOTION_TYPE = "motion_type";

	public static final String KEY_X = "x";
	public static final String KEY_Y = "y";
	public static final String KEY_Z = "z";

	public static final String KEY_NOX = "nox";
	public static final String KEY_TEMPERATURE = "temperature";

	private static final String TAG = "DbAdapter";

	private DatabaseHelper mDbHelper;
	private SQLiteDatabase mDb;

	/*
	 * 
	 * TODO:
	 * 
	 * make a nox add handler
	 * 
	 * make a track add handler and update end time
	 * 
	 * 
	 * later make a accelerometer add record handler
	 */

	/**
	 * Database creation sql statement(s)
	 * 
	 * one statement per block / var - didn't work well in one chunk
	 * 
	 * 
	 * NB! more on sql lite 3 datatypes: http://www.sqlite.org/datatype3.html
	 * 
	 * if the sql statement get complex its possible to move to an xml file but
	 * for this simple example we keep it here. TODO: look up reference for that
	 * - took a short look it was placed in <App>/res/raw/media_cmd_line.sql but
	 * didn't find an immediate solution.
	 * 
	 * previously had time: date_time datetime default current_date
	 * 
	 * 
	 * RECORD_TIME NOT NULL DEFAULT CURRENT_TIMESTAMP nb set the utc time if
	 * should be get as local time then
	 * 
	 * 
	 * 
	 * 
	 * 
	 * Previously we had time_stamp_start integer not null default
	 * current_timestamp but current_timestamp results in the GMT time. for
	 * local time we use: (datetime('now','localtime')) don't do
	 * datetime('now','localtime') the () are crusial.
	 * 
	 * If you are in doubt fire up a empty sqlite db and fire of some tests
	 * queries.
	 * 
	 * 
	 */
	private static final String DATABASE_CREATE_MOTIONS = "create table motions (_id integer primary key autoincrement, "
			+ "uuid text not null, time_stamp_start integer not null default (datetime('now','localtime')),"
			+ "time_stamp_end integer, motion_type text not null,"
			+ "title text, description text, sync_flag integer, city text, country text)"
			+ ";";

	// TODO: ensure its real/double(s) which should be used.
	private static final String DATABASE_CREATE_ACCELEROMETER = "create table accelerometer (time_stamp integer not null default (datetime('now','localtime')),"
			+ "x real not null, y real not null, z real not null)" + ";";

	private static final String DATABASE_NAME = "motions.db";
	private static final String DATABASE_TABLE_MOTIONS = "motions";
	private static final String DATABASE_TABLE_ACCELEROMETER = "accelerometer";

	/*
	 * 
	 * CREATE TABLE accelerometer (time_stamp integer not null default
	 * current_timestamp,x real not null, y real not null, z real not null);
	 * CREATE TABLE android_metadata (locale TEXT); CREATE TABLE motions ( _id
	 * integer primary key autoincrement, uuid text not null, time_stamp_start
	 * integer not null default current_timestamp,time_stamp_end integer,
	 * motion_type text not null,title text, description text, sync_flag
	 * integer, city text, country text); sqlite>
	 */

	// private static final String DATABASE_TRACKS = "tracks";
	// private static final String DATABASE_TABLE_LOCATION = "locations";
	private static final int DATABASE_VERSION = 1;

	private final Context mCtx;

	private static class DatabaseHelper extends SQLiteOpenHelper {

		DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(DATABASE_CREATE_MOTIONS);
			// db.execSQL(DATABASE_CREATE_LOCATIONS);
			// db.execSQL(DATABASE_CREATE_NOX);
			db.execSQL(DATABASE_CREATE_ACCELEROMETER);

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
	 * @param ctx
	 *            the Context within which to work
	 */
	public DbAdapter(Context ctx) {
		this.mCtx = ctx;
	}

	/**
	 * Open a tracks database. If it cannot be opened, try to create a new
	 * instance of the database. If it cannot be created, throw an exception to
	 * signal the failure
	 * 
	 * @return this (self reference, allowing this to be chained in an
	 *         initialization call)
	 * @throws SQLException
	 *             if the database could be neither opened or created
	 */
	// public synchronized DbAdapter open() throws SQLException {
	public DbAdapter open() throws SQLException {

		Log.d(TAG, "open called before mDbHelper = new DatabaseHelper(mCtx)");

		mDbHelper = new DatabaseHelper(mCtx);

		Log.d(TAG, "after mDbHelper = new DatabaseHelper(mCtx)");

		mDb = mDbHelper.getWritableDatabase();

		Log.d(TAG, "before return");
		return this;
	}

	// public synchronized void close() {
	public void close() throws SQLException {

		Log.d(TAG, "close called");

		mDbHelper.close();
	}

	/**
	 * Create a new track using the latitude and longitude provided. If a track
	 * is successfully created return the new rowId for that track, otherwise
	 * return a -1 to indicate failure.
	 * 
	 * Direct update sqlite statements looks like this: UPDATE tracks SET
	 * time_stamp_end=current_timestamp WHERE
	 * track_uuid="9aa57056-8d6e-4c9d-9919-79f55cd7e180";
	 * 
	 * 
	 * 
	 * 
	 * raw sqlite insert:
	 * 
	 * insert into motions (uuid, motion_type) values
	 * ("19e20a70-8690-40db-bcb5-585ee0c154df", "running"); insert into motions
	 * (uuid, motion_type) values ("19e20a70-8690-40db-bcb5-585ee0c154df",
	 * "running"); sqlite> select * from motions;
	 * 1|19e20a70-8690-40db-bcb5-585ee0c154df|2011-11-20 22:05:54||running|||||
	 * 
	 * 
	 * 
	 * @param UUID
	 * @param latitude
	 * @param longitude
	 * @return
	 */

	// public synchronized void createMotion(String UUID, String motionType) {
	public void createMotion(String UUID, String motionType) {

		Log.d(TAG, "createMotion called");
		Log.d(TAG, "UUID: " + UUID);

		ContentValues initialValues = new ContentValues();

		initialValues.put(KEY_UUID, UUID);
		initialValues.put(KEY_MOTION_TYPE, motionType);

		mDb.insert(DATABASE_TABLE_MOTIONS, null, initialValues);
	}

	/**
	 * End motion
	 * 
	 * Raw sqlite in use (example): UPDATE motions SET
	 * time_stamp_end=current_timestamp WHERE
	 * uuid="020b5eb3-6d50-41c0-893a-0a31d8332bb1";
	 * 
	 * @param UUID
	 * @return
	 */
	// public synchronized void endMotion(String UUID) {
	public void endMotion(String UUID) {

		Log.d(TAG, "endMotion called");

		// Note we dont use java/android sqlite approach since it cant handle
		// insert / update sqlite time stamp - based upon:
		// http://stackoverflow.com/questions/754684/how-to-insert-a-sqlite-record-with-a-datetime-set-to-now-in-android-applicatio
		// TODO: verify this

		// ContentValues args = new ContentValues();
		// // or datetime('now')
		// args.put(KEY_TIME_STAMP_END, "current_timestamp");
		// // note: UUID should be compared as string and for that reason
		// surrounded with ''
		// return mDb.update(DATABASE_TABLE_MOTIONS, args, KEY_UUID + "=" + "'"
		// + UUID + "'", null) > 0;

		// Raw sqlite in use - remember to sourround UUID with ''
		String sql = "UPDATE "
				+ DATABASE_TABLE_MOTIONS
				+ " SET time_stamp_end=(datetime('now','localtime')) WHERE uuid='"
				+ UUID + "'";
		Log.d(TAG, "sql: " + sql);
		mDb.execSQL(sql);

	}

	/**
	 * 
	 * Create location point
	 * 
	 * @param latitude
	 * @param longitude
	 */
	// public synchronized void createMotionPoint(double x, double y, double z )
	// {
	public void createMotionPoint(double x, double y, double z, long time) {

		Log.d(TAG, "createMotionPoint called");

		// NICETOHAVE: add time to the database
		// its from the accelerator event and is in nano seconds
		
		// TODO: check that latitude and longitude has value ? - long story
		// short had problems when they where not added right from Location
		// service - if they where not initialized yet etc..

		ContentValues initialValues = new ContentValues();

		// KEY_DATETIME - inserted automatically
		initialValues.put(KEY_X, x);
		initialValues.put(KEY_Y, y);
		initialValues.put(KEY_Z, z);

		// Quick fix:
		// - some times the service has closed down database and the sensor
		// event tries to make insert - perhaps an issue of concurrency ?
		// trace back: http://pastebin.com/XxffJdJt - was though perhaps related
		// to the fact that we did some heavy logging of time etc ?
		try {
			mDb.insert(DATABASE_TABLE_ACCELEROMETER, null, initialValues);
		} catch (Exception e) {
			Log.e(TAG, "database insert reaised error: " + e.getMessage());
		}
		// or narrow down excpetion handling to IllegalStateException

	}

	/**
	 * Return a Cursor positioned at motion recording
	 * 
	 * select motions.motion_type, motions.uuid, accelerometer.x, accelerometer.y, accelerometer.z, accelerometer.time_stamp from accelerometer, motions where motions.uuid="c7b80dc9-8733-4a7e-81c6-6a0555a82878" and (accelerometer.time_stamp > motions.time_stamp_start and accelerometer.time_stamp < motions.time_stamp_end);
	 * 
	 * time wize adjusted: 
	 * 
	 * select motions.motion_type, motions.uuid, accelerometer.x, accelerometer.y, accelerometer.z, accelerometer.time_stamp from accelerometer, motions where motions.uuid="a9db7906-c692-4629-8a0e-d9d169f6889f" and (accelerometer.time_stamp > datetime(motions.time_stamp_start, '+4 seconds') and accelerometer.time_stamp < datetime(motions.time_stamp_end, '-5 seconds'));
	 * 
	 * For simplicity we stick to the sqlite time format
	 * if a unix time format is needed then replace 
	 * 
	 * accelerometer.time_stamp
	 * with
	 * strftime('%s',accelerometer.time_stamp)
	 * 
	 * @param rowId
	 *            id of track to retrieve
	 * @return Cursor positioned to matching track, if found
	 * @throws SQLException
	 *             if track could not be found/retrieved
	 */
	public Cursor fetchMotion(String UUID) throws SQLException {

		String sql = "select motions.motion_type, motions.uuid, accelerometer.x, accelerometer.y, "
				+ "accelerometer.z, accelerometer.time_stamp from "
				+ DATABASE_TABLE_MOTIONS 
				+ ", " 
				+ DATABASE_TABLE_ACCELEROMETER 
				+ " where motions.uuid='"
				+ UUID
				+ "' and (accelerometer.time_stamp > datetime(motions.time_stamp_start, '+4 seconds')"
				+ " and accelerometer.time_stamp < datetime(motions.time_stamp_end, '-5 seconds'))";

		Cursor mCursor = mDb.rawQuery(sql, null);

		// TODO: not sure what the right approach is here - move to first or not
		// ? or is it one when return a single sql row / result item etc..
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;

	}

}
