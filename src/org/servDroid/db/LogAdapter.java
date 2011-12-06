package org.servDroid.db;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.R;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

public class LogAdapter extends ServdroidDbAdapter {

	public LogAdapter(Context ctx) {
		super(ctx);
		if (mDbHelper == null | mDb == null) {
			open();
		}
	}

	/**
	 * Create a new note using the title and body provided. If the note is
	 * successfully created return the new rowId for that note, otherwise return
	 * a -1 to indicate failure.
	 * 
	 * @param ip
	 *            the IP of the request
	 * @param path
	 *            the requested path
	 * @param infoBegining
	 *            Additional information at the beginning
	 * @param infoEnd
	 *            Additional information at the end
	 * @return rowId or -1 if failed
	 */
	public long addLog(String ip, String path, String infoBegining,
			String infoEnd) {

		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_HOSTS, ip);
		initialValues.put(KEY_PATH, path);

		// Timestamp sqlDate = new Timestamp(new java.util.Date().getTime());

		// Log.d(TAG, "Data: " + (new java.util.Date().getTime()));
		initialValues.put(KEY_TIME, (new java.util.Date().getTime()));
		initialValues.put(KEY_INFOBEGINING, infoBegining);
		initialValues.put(KEY_INFOEND, infoEnd);

		return mDb.insert(DATABASE_TABLE_LOG, null, initialValues);
	}

	/**
	 * Delete the note with the given rowId
	 * 
	 * @param rowId
	 *            id of note to delete
	 * @return true if deleted, false otherwise
	 */
	public boolean deleteLogRow(long rowId) {

		return mDb.delete(DATABASE_TABLE_LOG, KEY_ROWID + "=" + rowId, null) > 0;
	}

	/**
	 * Return a Cursor over the list of all notes in the database
	 * 
	 * @return Cursor over all notes
	 */
	public Cursor fetchLog(int numRows) {

		return mDb.query(DATABASE_TABLE_LOG, new String[] { KEY_ROWID,
				KEY_HOSTS, KEY_PATH, KEY_TIME, KEY_INFOBEGINING, KEY_INFOEND },
				null, null, null, null, KEY_ROWID + " DESC", "" + numRows);
	}

	/**
	 * Return a Cursor over the list of all notes in the database
	 * 
	 * @return Cursor over all notes
	 */
	public Cursor fetchAllLog() {

		return mDb.query(DATABASE_TABLE_LOG, new String[] { KEY_ROWID,
				KEY_HOSTS, KEY_PATH, KEY_TIME, KEY_INFOBEGINING, KEY_INFOEND },
				null, null, null, null, KEY_ROWID + " DESC", null);
	}

	/**
	 * Return a Cursor over the list of all notes in the database
	 * 
	 * @return Cursor over all notes
	 */
	public ArrayList<LogLocal> fetchAllLogList(int numRows) {
		Cursor c = fetchLog(numRows);

		c.moveToFirst();
		int indexIp = c.getColumnIndex(KEY_HOSTS);
		int indexPath = c.getColumnIndex(KEY_PATH);
		int indexTimeStamp = c.getColumnIndex(KEY_TIME);
		int indexInfoBegining = c.getColumnIndex(KEY_INFOBEGINING);
		int indexInfoEnd = c.getColumnIndex(KEY_INFOEND);

		int counts = c.getCount();
		ArrayList<LogLocal> locals = new ArrayList<LogLocal>();

		LogLocal log;

		for (int i = 0; i < counts; i++) {
			log = new LogLocal();
			log.setLocalIp(c.getString(indexIp));
			log.setLocalPath(c.getString(indexPath));
			log.setLocalTimeStamp(c.getLong(indexTimeStamp));
			log.setLocalInfoBegining(c.getString(indexInfoBegining));
			log.setLocalInfoEnd(c.getString(indexInfoEnd));
			locals.add(log);
			c.moveToNext();
		}

		return locals;
	}

	/**
	 * Return a Cursor over the list of all notes in the database
	 * 
	 * @return Cursor over all notes
	 */
	public ArrayList<LogLocal> fetchAllLogList() {

		return fetchAllLogList(10);
	}

	/**
	 * Create a new note using the title and body provided. If the note is
	 * successfully created return the new rowId for that note, otherwise return
	 * a -1 to indicate failure.
	 * 
	 * @param ip
	 *            the IP of the request
	 * @param path
	 *            the requested path
	 * @param time
	 *            when it produceds
	 * @return rowId or -1 if failed
	 */
	public long addLog(String ip, String path) {
		return addLog(ip, path, "", "");
	}

	/**
	 * Return a Cursor over the list of all notes in the database
	 * 
	 * @return Cursor over all notes
	 */
	public Cursor fetchLog() {

		return fetchLog(30);
	}

	/**
	 * Get information the String line for log.
	 * 
	 * @return value Log line
	 */
	public String getLogLine(int id) {
		Cursor mCursor = mDb.query(true, DATABASE_TABLE_LOG, new String[] {
				KEY_ROWID, KEY_HOSTS, KEY_PATH, KEY_TIME, KEY_INFOBEGINING,
				KEY_INFOEND }, KEY_ROWID + " = " + id + "", null, null, null,
				null, null);

		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		// startManagingCursor(mCursor);

		if (mCursor.getCount() == 0) {
			return "";
		}

		Date timeStamp;

		String line = "";
		line = line
				+ mCursor.getString(mCursor.getColumnIndexOrThrow(KEY_HOSTS))
				+ " ";
		timeStamp = new Date(mCursor.getLong(mCursor
				.getColumnIndexOrThrow(KEY_TIME)));
		line = line + "[" + timeStamp.toGMTString() + "] ";

		line = line + "\""
				+ mCursor.getString(mCursor.getColumnIndexOrThrow(KEY_PATH))
				+ "\"";

		return line;

	}


}
