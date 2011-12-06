/*
 * Copyright (C) 2008 Google Inc.
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

package org.servDroid.web;

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
import org.servDroid.db.SettingsAdapter;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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

	private int mMaxDisplayedEntries = 30;

	// private static final int PROGRESS_DIALOG = 0;
	private SettingsAdapter mSettings;

	private LogListViewAdapter mLogListViewAdapter;

	private ProgressDialog mProgressDialog;

	private String mFile;

	final Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			int total = msg.getData().getInt("counter");
			if (total == -1) {
				mLogHelper.deleteTableLog();
				fillData();
				return;
			}

			mProgressDialog.setProgress(total);
			if (total == mProgressDialog.getMax()) {
				// dismissDialog(PROGRESS_DIALOG);
				mProgressDialog.dismiss();
				finalizeSavingLog();
			}

		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mLogHelper = new LogAdapter(this);
		// mLogHelper.open();

		setContentView(R.layout.log_list);

		mTextEntriesInfo = (TextView) findViewById(R.id.EntriesInfo);

		// mRowId = savedInstanceState != null ? savedInstanceState
		// .getLong(ServdroidDbAdapter.KEY_ROWID) : null;
		// if (mRowId == null) {
		// Bundle extras = getIntent().getExtras();
		// mRowId = extras != null ? extras
		// .getLong(ServdroidDbAdapter.KEY_ROWID) : null;
		// }

		fillData();
		registerForContextMenu(getListView());

	}
	// @Override
	// protected void onSaveInstanceState(Bundle outState) {
	// super.onSaveInstanceState(outState);
	// outState.putLong(ServdroidDbAdapter.KEY_ROWID, mRowId);
	// Log.d("servDroid", "SaveInstance");
	// }

	@Override
	protected void onPause() {

		// saveState();
//		Log.d("servDroid", "on pause");

		super.onPause();
	}

	@Override
	protected void onResume() {

		fillData();
//		Log.d("servDroid", "Resume");

		super.onResume();
	}


	private void finalizeSavingLog() {
		Toast
				.makeText(
						this,
						this.getResources().getString(R.string.log_saved) + " "
								+ mFile, Toast.LENGTH_LONG).show();
		// mLogHelper.deleteTableLog();
		// fillData();
	}

	private void fillData() {

		if (mLogListViewAdapter == null) {

			mLogListViewAdapter = new LogListViewAdapter(
					this,
					R.layout.row_log,
					(LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE));
			setListAdapter(mLogListViewAdapter);

		}

		ArrayList<LogLocal> locals = mLogHelper
				.fetchAllLogList(mMaxDisplayedEntries);

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
		// setListAdapter(mLogListViewAdapter);
		mLogListViewAdapter.notifyDataSetChanged();
		// Cursor notesCursor = mLogHelper.fetchAllLog();
		// startManagingCursor(notesCursor);
		//        
		// // Create an array to specify the fields we want to display in the
		// list
		// String[] from = new
		// String[]{ServdroidDbAdapter.KEY_HOSTS,ServdroidDbAdapter.KEY_PATH,
		// ServdroidDbAdapter.KEY_TIME };
		//        
		// // and an array of the fields we want to bind those fields to (in
		// this case just text1)
		// int[] to = new int[]{R.id.row1, R.id.row2, R.id.row3};
		//        
		// // Now create a simple cursor adapter and set it to display
		// SimpleCursorAdapter notes =
		// new SimpleCursorAdapter(this, R.layout.row_log, notesCursor, from,
		// to);
		// setListAdapter(notes);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		// menu.add(0, SHOW_INFO_ID, 0, R.string.menu_show_more_info);
		menu.add(0, DELETE_ID, 1, R.string.menu_delete);
		menu.add(0, DELETE_ALL_ID, 2, R.string.menu_delete_all);
		// menu.add(0, ADD_IP_BLACK_LIST_ID, 4, R.string.menu_add_black_list);
		// menu.add(0, ADD_IP_WHITE_LIST_ID, 5, R.string.menu_add_white_list);
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
				android.R.drawable.ic_menu_rotate);
		// menu.add(0, SEE_STATICS_ID, 0,
		// R.string.menu_statics).setIcon(R.drawable.analitics);
		return true;
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		// Intent i = new Intent(this, LogViewer.class);
		// i.putExtra(ServdroidDbAdapter.KEY_ROWID, id);
		// startActivityForResult(i, ACTIVITY_EDIT);
	}

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
			// fillData();
			return true;
		case REFRSH_LOG:
			fillData();
			return true;
		}

		return super.onMenuItemSelected(featureId, item);
	}

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

		mSettings = new SettingsAdapter(this);

		mFile = mSettings.getLogPath() + "/web_" + dateChain + ".log";
		mFile = mFile.replace("//", "/");

		// //SHOW DIALOG

		if (mProgressDialog != null) {
			mProgressDialog.dismiss();
		}

		mProgressDialog = new ProgressDialog(LogViewer.this);
		mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		mProgressDialog.setTitle(this.getResources().getString(
				R.string.saving_log_progress));
		// mProgressDialog.setMessage(this.getResources().getString(
		// R.string.loading));
		mProgressDialog.setMax(counter - 1);
		mProgressDialog.setCancelable(true);
		mProgressDialog.show();

		// Run thread for saving
		ProgressThread progressThread = new ProgressThread(handler, mFile);
		progressThread.start();

	}

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

			// Date timer = new Date((new java.util.Date().getTime()));

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
					// mProgressDialog.setProgress((i / counts) * 100);

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

}
