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

package org.servDroid;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.servDroid.db.LogAdapter;
import org.servDroid.db.LogLocal;
import org.servDroid.db.ServdroidDbAdapter;
import org.servDroid.web.R;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.AdapterContextMenuInfo;

/**
 * 
 * @author Joan Puig Sanz
 * 
 */
public class LogViewer extends ListActivity {

	private static final String TAG = "ServDroid";

	private final int DUMP_LOG_ID = Menu.FIRST;
	private final int PURGE_ID = Menu.FIRST + 1;
	private final int REFRSH_LOG = Menu.FIRST + 2;
	private final int SEE_STATICS_ID = Menu.FIRST + 4;

	private TextView mTextEntriesInfo;

	private LogAdapter mLogHelper;

	private final int SHOW_INFO_ID = Menu.FIRST + 20;
	private final int DELETE_ID = Menu.FIRST + 21;
	private final int DELETE_ALL_ID = Menu.FIRST + 22;
	private final int ADD_IP_BLACK_LIST_ID = Menu.FIRST + 24;
	private final int ADD_IP_WHITE_LIST_ID = Menu.FIRST + 25;

	private LogListViewAdapter mLogListViewAdapter;

	private ProgressDialog mProgressDialog;

	private String mFile;

	// Handler for the progress bar
	final Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			int total = msg.getData().getInt("counter");
			if (total == -1) {
				mLogHelper.deleteTableLog();
				fillData();
				return;
			}

			mProgressDialog.setProgress(total);
			if (total == mProgressDialog.getMax()) {
				mProgressDialog.dismiss();
				finalizeSavingLog();
			}

		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mLogHelper = new LogAdapter(this);

		setContentView(R.layout.log_list);

		mTextEntriesInfo = (TextView) findViewById(R.id.EntriesInfo);

		fillData();
		registerForContextMenu(getListView());

	}

	@Override
	protected void onPause() {

		super.onPause();
	}

	@Override
	protected void onResume() {

		fillData();

		super.onResume();
	}

	/**
	 * Finalise the saving log process
	 */
	private void finalizeSavingLog() {
		Toast
				.makeText(
						this,
						this.getResources().getString(R.string.log_saved) + " "
								+ mFile, Toast.LENGTH_LONG).show();
	}

	/**
	 * Show log entries on the screen
	 */
	private void fillData() {

		if (mLogListViewAdapter == null) {

			mLogListViewAdapter = new LogListViewAdapter(
					this,
					R.layout.row_log,
					(LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE));
			setListAdapter(mLogListViewAdapter);

		}

		ArrayList<LogLocal> locals = mLogHelper
				.fetchLogList(getNumLogEntries());

		mLogListViewAdapter.clear();
		int size = locals.size();
		if (locals != null && size > 0) {
			for (int i = 0; i < size; i++)
				mLogListViewAdapter.add(locals.get(i));
		}
		Cursor c = mLogHelper.fetchAllLog();
		int counter = c.getCount();

		mTextEntriesInfo.setText(this.getResources().getString(
				R.string.log_info_1)
				+ " "
				+ size
				+ "/"
				+ counter
				+ " "
				+ this.getResources().getString(R.string.log_info_2));

		mLogListViewAdapter.setItems(locals);
		mLogListViewAdapter.notifyDataSetChanged();
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.add(0, DELETE_ID, 1, R.string.menu_delete);
		menu.add(0, DELETE_ALL_ID, 2, R.string.menu_delete_all);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case SHOW_INFO_ID:
			return true;
		case DELETE_ID:
			AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
					.getMenuInfo();
			mLogHelper.deleteLogRow(info.id);
			fillData();
			return true;
		case DELETE_ALL_ID:
			mLogHelper.deleteTableLog();
			fillData();
			return true;
		case ADD_IP_BLACK_LIST_ID:
			return true;
		case ADD_IP_WHITE_LIST_ID:
			return true;

		}
		return super.onContextItemSelected(item);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.add(0, DUMP_LOG_ID, 0, R.string.menu_save_log).setIcon(
				android.R.drawable.ic_menu_save);
		menu.add(0, PURGE_ID, 0, R.string.menu_delete_all).setIcon(
				android.R.drawable.ic_menu_delete);
		menu.add(0, REFRSH_LOG, 0, R.string.menu_refresh_log).setIcon(
				R.drawable.refresh);
		// menu.add(0, SEE_STATICS_ID, 0,
		// R.string.menu_statics).setIcon(R.drawable.analitics);
		return true;
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case DUMP_LOG_ID:
			saveLog();
			return true;
		case PURGE_ID:
			mLogHelper.deleteTableLog();
			fillData();
			return true;
		case SEE_STATICS_ID:
			// TODO To do
			return true;
		case REFRSH_LOG:
			fillData();
			return true;
		}

		return super.onMenuItemSelected(featureId, item);
	}

	/**
	 * Dump log to file
	 */
	private void saveLog() {

		Cursor c = mLogHelper.fetchAllLog();
		int counter = c.getCount();
		if (counter == 0) {
			Toast.makeText(this,
					this.getResources().getString(R.string.no_log_entries),
					Toast.LENGTH_LONG).show();
			return;
		}

		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_Hmmss");
		String dateChain = dateFormat.format(new Date((new java.util.Date()
				.getTime())));

		mFile = getLogPath() + "/web_" + dateChain + ".log";
		mFile = mFile.replace("//", "/");


		if (mProgressDialog != null) {
			mProgressDialog.dismiss();
		}

		mProgressDialog = new ProgressDialog(LogViewer.this);
		mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		mProgressDialog.setTitle(this.getResources().getString(
				R.string.saving_log_progress));
		mProgressDialog.setMax(counter - 1);
		mProgressDialog.setCancelable(true);
		mProgressDialog.show();

		// Run thread for saving
		ProgressThread progressThread = new ProgressThread(handler, mFile);
		progressThread.start();

	}

	/**
	 * This a private class to create a thread to control the saving process
	 * 
	 * @author Joan Puig Sanz
	 * 
	 */
	private class ProgressThread extends Thread {
		Handler mHandler;
		String mPath;

		ProgressThread(Handler h, String p) {
			mHandler = h;
			mPath = p;
		}

		public void run() {

			Cursor c = mLogHelper.fetchAllLog();
			int counts = c.getCount();

			c.moveToFirst();
			int indexIp = c.getColumnIndex(ServdroidDbAdapter.KEY_HOSTS);
			int indexPath = c.getColumnIndex(ServdroidDbAdapter.KEY_PATH);
			int indexTimeStamp = c.getColumnIndex(ServdroidDbAdapter.KEY_TIME);
			int indexInfoBegining = c
					.getColumnIndex(ServdroidDbAdapter.KEY_INFOBEGINING);
			int indexInfoEnd = c.getColumnIndex(ServdroidDbAdapter.KEY_INFOEND);

			FileWriter fw;
			try {
				fw = new FileWriter(mPath);
				BufferedWriter bw = new BufferedWriter(fw);
				PrintWriter pw = new PrintWriter(bw, false);
				String line, begining, end;
				Date timeStamp;
				for (int i = 0; i < counts; i++) {

					line = "";

					begining = c.getString(indexInfoBegining);

					if (!begining.equals("") || begining == null) {
						line = line + "[" + begining + "] ";
					}

					line = line + c.getString(indexIp) + " ";

					timeStamp = new Date(c.getLong(indexTimeStamp));

					line = line + "[" + timeStamp.toGMTString() + "] ";

					line = line + "\"" + c.getString(indexPath) + "\"";

					end = c.getString(indexInfoEnd);

					if (!end.equals("") || begining == null) {
						line = line + " -- " + end + "";
					}
					pw.println(line);
					c.moveToNext();

					Message msg = mHandler.obtainMessage();
					Bundle b = new Bundle();
					b.putInt("counter", i);
					msg.setData(b);
					mHandler.sendMessage(msg);
					if (!mProgressDialog.isShowing()) {
						pw.close();
						return;
					}

				}

				pw.close();

				Message msg = mHandler.obtainMessage();
				Bundle b = new Bundle();
				b.putInt("counter", -1);
				msg.setData(b);
				mHandler.sendMessage(msg);

			} catch (IOException e) {

				Log.e(TAG, "Error saving log", e);
			}

		}

	}

	/**
	 * Get the number of entries to be displayed through SharedPreferences
	 * 
	 * @return Number of entries to be displayed
	 */
	private int getNumLogEntries() {
		SharedPreferences pref = PreferenceManager
				.getDefaultSharedPreferences(this);
		String logEntries = pref.getString(getResources().getString(
				R.string.pref_log_entries_key), getResources().getString(
				R.string.default_log_entries));

		try {
			return Integer.parseInt(logEntries);
		} catch (NumberFormatException e) {
			return 35;
		}

	}

	/**
	 * Get the log path through SharedPreferences
	 * 
	 * @return Log path
	 */
	private String getLogPath() {
		SharedPreferences pref = PreferenceManager
				.getDefaultSharedPreferences(this);
		return pref.getString(getResources().getString(
				R.string.pref_log_path_key), getResources().getString(
				R.string.default_log_path));

	}

}
