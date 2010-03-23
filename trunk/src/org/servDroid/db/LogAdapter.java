/*
 * Copyright (C) 2010 Joan Puig Sanz
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.servDroid.db;

import java.util.ArrayList;
import java.util.Date;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

/**
 * 
 * @author Joan Puig Sanz
 * 
 */
public class LogAdapter extends ServdroidDbAdapter {

	public LogAdapter(Context ctx) {
		super(ctx);
		if (mDbHelper == null | mDb == null) {
			open();
		}
	}

	/**
	 * Create a new log entry using the the IP, request path, some extra
	 * information. If the log is successfully added return the new rowId for
	 * that note, otherwise return a -1 to indicate failure.
	 * 
	 * @param ip
	 *            the IP of the request
	 * @param path
	 *            the requested path
	 * @param infoBegining
	 *            Additional information to append at the beginning
	 * @param infoEnd
	 *            Additional information to append at the end
	 * @return rowId or -1 if failed
	 */
	public long addLog(String ip, String path, String infoBegining,
			String infoEnd) {

		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_HOSTS, ip);
		initialValues.put(KEY_PATH, path);

		initialValues.put(KEY_TIME, (new java.util.Date().getTime()));
		initialValues.put(KEY_INFOBEGINING, infoBegining);
		initialValues.put(KEY_INFOEND, infoEnd);

		return mDb.insert(DATABASE_TABLE_LOG, null, initialValues);
	}

	/**
	 * Delete the log entri with the given rowId
	 * 
	 * @param rowId
	 *            id of log entrie to delete
	 * @return true if deleted, false otherwise
	 */
	public boolean deleteLogRow(long rowId) {

		return mDb.delete(DATABASE_TABLE_LOG, KEY_ROWID + "=" + rowId, null) > 0;
	}

	/**
	 * Return a Cursor over the list of the specified log entry in the database
	 * 
	 * @return Cursor over the specified log entries
	 */
	public Cursor fetchLog(int numRows) {

		return mDb.query(DATABASE_TABLE_LOG, new String[] { KEY_ROWID,
				KEY_HOSTS, KEY_PATH, KEY_TIME, KEY_INFOBEGINING, KEY_INFOEND },
				null, null, null, null, KEY_ROWID + " DESC", "" + numRows);
	}

	/**
	 * Return a Cursor over the list of all log entries in the database
	 * 
	 * @return Cursor over all log entries
	 */
	public Cursor fetchAllLog() {

		return mDb.query(DATABASE_TABLE_LOG, new String[] { KEY_ROWID,
				KEY_HOSTS, KEY_PATH, KEY_TIME, KEY_INFOBEGINING, KEY_INFOEND },
				null, null, null, null, KEY_ROWID + " DESC", null);
	}

	/**
	 * Return the ArrayList which contains the log list
	 * 
	 * @param numRows
	 *            The number of row to get
	 * 
	 * @return ArrayList with the log entries
	 */
	public ArrayList<LogLocal> fetchLogList(int numRows) {
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
	 * Create a new log entry using the IP and provided. If the log entry is
	 * successfully created return the new rowId for that log entry, otherwise
	 * return a -1 to indicate failure.
	 * 
	 * @param ip
	 *            the IP of the request
	 * @param path
	 *            the requested path
	 * 
	 * @return rowId or -1 if failed
	 */
	public long addLog(String ip, String path) {
		return addLog(ip, path, "", "");
	}

	/**
	 * Get the String line of a log entry.
	 * 
	 * @param id
	 *            The id of log entry
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
