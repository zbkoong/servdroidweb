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

package org.servDroid.db;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Simple notes database access helper class. Defines the basic CRUD operations
 * for the notepad example, and gives the ability to list all notes as well as
 * retrieve or modify a specific note.
 * 
 * This has been improved from the first version of this tutorial through the
 * addition of better error handling and also using returning a Cursor instead
 * of using a collection of inner classes (which is less scalable and not
 * recommended).
 */
public class ServdroidDbAdapter extends Activity {

	public static final String KEY_ROWID = "_id";
	public static final String KEY_HOSTS = "host";
	public static final String KEY_PATH = "path";
	public static final String KEY_TIME = "time";
	public static final String KEY_INFOBEGINING = "info_begining";
	public static final String KEY_INFOEND = "info_end";
	public static final String KEY_SETTINGS_PARAM = "settings_param";
	public static final String KEY_VALUE = "value";

	protected static final String TAG = "ServDroid";
	protected DatabaseHelper mDbHelper;
	protected SQLiteDatabase mDb;

	protected static final String STRING_PORT = "port";
	protected static final String STRING_MAX_CLIENTS = "max_clients";
	protected static final String STRING_WWW_PATH = "www_path";
	protected static final String STRING_LOG_PATH = "log_path";
	protected static final String STRING_ERROR_PAGE = "error_page";
	protected static final String STRING_COOKIES = "cookies";
	protected static final String STRING_VIBRATE = "vibrate";
	protected static final String STRING_PASSWORD = "password";
	protected static final String STRING_ENABLE_BLACK_LIST = "black_list_enable";
	protected static final String STRING_ENABLE_WHITE_LIST = "white_list_enable";
	protected static final String STRING_ENABLE_PASSWORD = "password_enable";
	protected static final String STRING_ENABLE_COOKIES = "cookies_enable";
	
	// Number of parameters
	protected static final int NUM_SETTINGS_PARAMETERS = 12;

	protected static final int DEFAULT_MIN_LENGTH_PASSWORD = 4;
	protected static final String DEFAULT_WWW_PATH = "/sdcard/servdroid/var/www/";
	protected static final String DEFAULT_ERROR_PATH = "/sdcard/servdroid/var/www/error/";
	protected static final String DEFAULT_LOG_PATH = "/sdcard/servdroid/var/log/";

	protected static final String DATABASE_NAME = "servdroid_web";
	protected static final String DATABASE_TABLE_LOG = "web_log";
	protected static final String DATABASE_TABLE_BLACK_LIST = "black_list";
	protected static final String DATABASE_TABLE_WHITE_LIST = "white_list";
	protected static final String DATABASE_TABLE_SETTINGS = "settings_list";
	protected static final int DATABASE_VERSION = 2;

	/**
	 * Database creation sql statement
	 */
	private static final String DATABASE_CREATE_LOG = "create table "
			+ DATABASE_TABLE_LOG + " ( " + KEY_ROWID
			+ " integer primary key autoincrement, " + KEY_HOSTS
			+ " text not null, " + KEY_PATH + " text not null, " + KEY_TIME
			+ " long, " + KEY_INFOBEGINING + " text not null, " + KEY_INFOEND
			+ "  text not null);";
	private static final String DATABASE_CREATE_BLACK_LIST = "create table "
			+ DATABASE_TABLE_BLACK_LIST + " ( " + KEY_ROWID
			+ " integer primary key autoincrement, " + KEY_HOSTS
			+ " text not null);";
	private static final String DATABASE_CREATE_WHITE_LIST = "create table "
			+ DATABASE_TABLE_WHITE_LIST + " ( " + KEY_ROWID
			+ " integer primary key autoincrement, " + KEY_HOSTS
			+ " text not null);";
	private static final String DATABASE_CREATE_SETTINGS = "create table "
			+ DATABASE_TABLE_SETTINGS + " ( " + KEY_ROWID
			+ " integer primary key autoincrement, " + KEY_SETTINGS_PARAM
			+ " text not null, " + KEY_VALUE + " text not null);";

	private final Context mCtx;

	private static class DatabaseHelper extends SQLiteOpenHelper {

		DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {

			db.execSQL(DATABASE_CREATE_LOG);
			db.execSQL(DATABASE_CREATE_BLACK_LIST);
			db.execSQL(DATABASE_CREATE_WHITE_LIST);
			db.execSQL(DATABASE_CREATE_SETTINGS);

		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
					+ newVersion + ", which will destroy all old data");
			db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE_LOG);
			db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE_BLACK_LIST);
			db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE_WHITE_LIST);
			db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE_SETTINGS);
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
	public ServdroidDbAdapter(Context ctx) {
		this.mCtx = ctx;
	}

	/**
	 * Open the notes database. If it cannot be opened, try to create a new
	 * instance of the database. If it cannot be created, throw an exception to
	 * signal the failure
	 * 
	 * @return this (self reference, allowing this to be chained in an
	 *         initialization call)
	 * @throws SQLException
	 *             if the database could be neither opened or created
	 */
	public ServdroidDbAdapter open() throws SQLException {
		mDbHelper = new DatabaseHelper(mCtx);
		mDb = mDbHelper.getWritableDatabase();

		return this;
	}

	public void close() {
		mDbHelper.close();
	}

	public void deleteTableLog() {
		mDb.execSQL("DELETE FROM " + DATABASE_TABLE_LOG);
	}

//	public SettingsAdapter getSettingsAdpater() {
//		if (mSettings == null) {
//			mSettings = new SettingsAdapter(mDb);
//		}
//
//		return mSettings;
//	}

	/**
	 * Add an IP to black list server.
	 * 
	 * @param ip
	 *            The IP of the client.
	 * @return rowId or -1 if failed, -2 if already exist
	 */
	public long addIpBlackList(long rowIdLog) {

		Cursor mLogCursor = fetchEntry(rowIdLog);
		startManagingCursor(mLogCursor);

		String mIp = mLogCursor.getString(mLogCursor
				.getColumnIndexOrThrow(ServdroidDbAdapter.KEY_HOSTS));

		// Cursor blackListCursor = fetchEntryBlackList(mIp);
		// startManagingCursor(blackListCursor);

		return addIpBlackList(mIp);

	}

	/**
	 * Add an IP to black list server.
	 * 
	 * @param ip
	 *            The IP of the client.
	 * @return rowId or -1 if failed, -2 if already exist
	 */
	public long addIpBlackList(String ip) {

		Cursor blackListCursor = fetchEntryBlackList(ip);
		startManagingCursor(blackListCursor);

		if (blackListCursor.getCount() == 0) {
			Log.d("servDroid", ip + " afegida a la blacklist");
			ContentValues initialValues = new ContentValues();
			initialValues.put(KEY_HOSTS, ip);

			return mDb.insert(DATABASE_TABLE_BLACK_LIST, null, initialValues);

		} else {
			Log.d("servDroid", "Ip already in the list");
			return -2;

		}

	}

	/**
	 * Return a Cursor positioned at the note that matches the given rowId
	 * 
	 * @param rowId
	 *            id of note to retrieve
	 * @return Cursor positioned to matching note, if found
	 * @throws SQLException
	 *             if note could not be found/retrieved
	 */
	public Cursor fetchEntry(long rowId) throws SQLException {

		Cursor mCursor =

		mDb.query(true, DATABASE_TABLE_LOG, new String[] { KEY_ROWID,
				KEY_HOSTS, KEY_PATH }, KEY_ROWID + "=" + rowId, null, null,
				null, null, null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;

	}

	/**
	 * Return a Cursor positioned at the note that matches the given rowId
	 * 
	 * @param rowId
	 *            id of note to retrieve
	 * @return Cursor positioned to matching note, if found
	 * @throws SQLException
	 *             if note could not be found/retrieved
	 */
	public Cursor fetchEntryBlackList(long rowId) throws SQLException {

		Cursor mCursor =

		mDb.query(true, DATABASE_TABLE_BLACK_LIST, new String[] { KEY_ROWID,
				KEY_HOSTS }, KEY_ROWID + "=" + rowId, null, null, null, null,
				null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;

	}

	/**
	 * Torna el cursor on esta la ip de la black list
	 * 
	 * @param ip
	 * @return
	 * @throws SQLException
	 */
	public Cursor fetchEntryBlackList(String ip) throws SQLException {

		Cursor mCursor = mDb.query(true, DATABASE_TABLE_BLACK_LIST,
				new String[] { KEY_HOSTS }, KEY_HOSTS + "= \"" + ip + "\"",
				null, null, null, null, null);

		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;

	}

	/**
	 * Update the note using the details provided. The note to be updated is
	 * specified using the rowId, and it is altered to use the title and body
	 * values passed in
	 * 
	 * @param rowId
	 *            id of note to update
	 * @param title
	 *            value to set note title to
	 * @param body
	 *            value to set note body to
	 * @return true if the note was successfully updated, false otherwise
	 */
	public boolean updateNote(long rowId, String title, String body) {
		ContentValues args = new ContentValues();
		args.put(KEY_HOSTS, title);
		args.put(KEY_PATH, body);

		return mDb.update(DATABASE_TABLE_LOG, args, KEY_ROWID + "=" + rowId,
				null) > 0;
	}

}
