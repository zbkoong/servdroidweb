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

import java.util.List;

import org.servDroid.preference.AccessPreferences;
import org.servDroid.preference.PreferencesActivity;
import org.servDroid.db.LogMessage;
import org.servDroid.db.ServdroidDbAdapter;
import org.servDroid.server.service.ServerService;
import org.servDroid.server.service.ServiceController;
import org.servDroid.util.NetworkIp;
import org.servDroid.web.AdFrame;
import org.servDroid.web.R;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

//iptables -t nat -A PREROUTING -p tcp --dport 80 -j REDIRECT --to-port 8080
/**
 * This is the main class.
 * 
 * @author Joan Puig Sanz
 * 
 */
public class ServDroid extends ListActivity {

	public static final String TAG = "ServDroid";
	public static String mVersion;

	// private static final int ACTIVITY_CREATE = 0;
	private static final int ACTIVITY_EDIT = 1;
	private static final int ACTIVITY_SETTINGS = 2;

	public static final int GUIUPDATEIDENTIFIER = 0x101;

	private static final int SEE_LOG_ID = Menu.FIRST;
	private static final int SETTINGS_ID = Menu.FIRST + 1;
	private static final int REFRSH_LOG = Menu.FIRST + 2;
	private static final int OPEN_BROWSER_ID = Menu.FIRST + 3;
	// private static final int SEE_STATICS_ID = Menu.FIRST + 4; //NOT
	// Implemented
	private static final int HELP_ID = Menu.FIRST + 5;

	// private static final int SHOW_INFO_ID = Menu.FIRST + 20;
	// private static final int DELETE_ID = Menu.FIRST + 21;
	// private static final int DELETE_ALL_ID = Menu.FIRST + 22;
	// private static final int CLEAR_LIST_ID = Menu.FIRST + 23;
	// private static final int ADD_IP_BLACK_LIST_ID = Menu.FIRST + 24;
	// private static final int ADD_IP_WHITE_LIST_ID = Menu.FIRST + 25;

	private ToggleButton mStartStopButton;
	private TextView mTexStat, mTexServerInfo;
	private ViewGroup mMainViewGrou;

	// The service for the server
	private static ServerConnection mConnection;
	private static volatile ServiceController mServiceController;
	private static volatile boolean fillOnConnectService = false;
	private static Intent mServiceIntent;
	private static volatile boolean mServiceBinded = false;

	private LogListViewAdapter mLogListViewAdapter;

	private long mTimeRefresh = 5000;

	private static RefreshThread mRefresh;

	private static WifiManager mWifiManager;

	private static List<LogMessage> oldLogMsg;

	// private static boolean mExit;
	private static boolean mAppKilled = false;

	private LinearLayout adMobLayout;

	private static int mportInUse;

	// This is the handler for the thread which refreshes the log screen
	final Handler mServDroidHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case GUIUPDATEIDENTIFIER:
				fillData(false);
				if (null != mServiceController) {
					try {
						if (mServiceController.getStatus() == ServerService.STATUS_RUNNING) {
							setStartedStatus();
						} else {
							setStoppedStatus();
						}
					} catch (RemoteException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				break;
			}
			super.handleMessage(msg);
		}
	};

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			// System.gc();
			mAppKilled = false;
			// System.exit(0);
			return super.onKeyDown(keyCode, event);
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onStart() {
		startRefreshThread();
		super.onStart();
	}

	private void doBindServiceConnection() {
		// if (!mServiceBinded && mServiceController == null) {
		mConnection = this.new ServerConnection();
		mServiceIntent = new Intent(
				"org.servDroid.server.service.ServiceController");
		startService(mServiceIntent);
		bindService(mServiceIntent, mConnection, Context.BIND_AUTO_CREATE);
	}

	private void doUnbindServiceConnection() {
		if (mServiceBinded && mConnection != null) {
			unbindService(mConnection);
			mConnection = null;
			mServiceBinded = false;
		}
	}

	@Override
	protected void onRestart() {
		super.onRestart();
	}

	@Override
	protected void onResume() {
		if (mServiceController != null) {
			try {
				if (mServiceController.getStatus() == ServerService.STATUS_RUNNING) {
					setStartedStatus();
				} else {
					setStoppedStatus();
				}
			} catch (RemoteException e) {
				setStoppedStatus();
				e.printStackTrace();
			}
		}
		startRefreshThread();

		// ///////////////////////////////
		adMobLayout = (LinearLayout) findViewById(R.id.adMobLayout);
		AdFrame.load(adMobLayout, this);
		mMainViewGrou.invalidate();
		// ///////////////////////////////
		super.onResume();
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	protected void onPause() {
		stopRefreshThread();
		doUnbindServiceConnection();
		super.onPause();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (mAppKilled) {
			finish();
		}

		AccessPreferences.setContext(getApplicationContext());
		mVersion = this.getResources().getString(R.string.version);

		// if (null == mConnection) {
		fillOnConnectService = true;
		doBindServiceConnection();
		// }

		setContentView(R.layout.main);

		mStartStopButton = (ToggleButton) findViewById(R.id.startstop);
		mTexStat = (TextView) findViewById(R.id.textStat);
		mTexServerInfo = (TextView) findViewById(R.id.textServerInfo);

		mMainViewGrou = (ViewGroup) findViewById(R.id.mainLayout);

		if (null == mWifiManager) {
			mWifiManager = (WifiManager) this
					.getSystemService(Context.WIFI_SERVICE);
		}

		fillData(true);
		registerForContextMenu(getListView());

		mStartStopButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {

				if (true) {
					if (mStartStopButton.isChecked()) {
						if (!mWifiManager.isWifiEnabled()) {
							Toast.makeText(ServDroid.this,
									R.string.text_server_info_no_wifi,
									Toast.LENGTH_LONG).show();
						}
						mTexStat.setText(R.string.text_starting);
						if (startWebServer()) {
							setStartedStatus();
						} else {
							setStoppedStatus();
						}

					} else {
						mTexStat.setText(R.string.text_stopping);
						if (stopWebServer()) {
							setStoppedStatus();
						} else {
							mTexServerInfo
									.setText(R.string.error_stopping_process);
						}
					}
				}

			}
		});

		checkVersion();

	}

	private void setStartedStatus() {
		mStartStopButton.setChecked(true);

		WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		String ip = NetworkIp.getWifiIp(wifiManager);

		mTexServerInfo.setText("IP: " + ip + " "
				+ getResources().getString(R.string.text_port) + ": "
				+ mportInUse);
		mTexStat.setText(R.string.text_running);
	}

	private void setStoppedStatus() {
		mStartStopButton.setChecked(false);
		mTexServerInfo.setText(" -- ");
		mTexStat.setText(R.string.text_stopped);
	}

	/**
	 * Show release notes
	 */
	private void checkVersion() {

		String version = AccessPreferences.getVersion();
		if (version.equals("-")) {
			AccessPreferences.checkWwwPath(null);
			AccessPreferences.checkErrorPath(null);
			AccessPreferences.checkLogPath(null);

		}

		if (!version.equals(getResources().getString(R.string.version))) {

			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage(R.string.release_notes_info)
					.setCancelable(true)
					.setNeutralButton(android.R.string.ok,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									changePreviousVersion();
									dialog.dismiss();
								}
							});
			builder.setIcon(android.R.drawable.ic_dialog_info);
			builder.create();
			builder.setTitle(R.string.release_notes);
			builder.show();
		}

	}

	/**
	 * Change the previous version (for the release notes dialog)
	 */
	private void changePreviousVersion() {
		AccessPreferences
				.setVersion(getResources().getString(R.string.version));
	}

	/**
	 * This function displays the help dialog
	 */
	private void showHelpDialog() {

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(
				getResources().getString(R.string.info_dialog_1) + " \""
						+ AccessPreferences.getWwwPath() + "\" "
						+ getResources().getString(R.string.info_dialog_2)
						+ "\n\n"
						+ getResources().getString(R.string.info_dialog_3)
						+ "\n\n"
						+ getResources().getString(R.string.info_dialog_4)
						+ "\n\n"
						+ getResources().getString(R.string.info_dialog_5)
						+ "\n\n"
						+ getResources().getString(R.string.info_dialog_6)
						+ "\n\n"
						+ getResources().getString(R.string.info_dialog_7))
				.setCancelable(true)
				.setPositiveButton(R.string.web_page,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								openWebBrowser("http://code.google.com/p/servdroidweb/");
							}
						})
				.setNegativeButton(R.string.donate,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {

								ShowDonateDialog();
							}
						})
				.setNeutralButton(android.R.string.ok,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {

								dialog.cancel();
							}
						});
		builder.setIcon(R.drawable.icon);
		builder.create();
		builder.setTitle(R.string.information);
		builder.show();
	}

	private void ShowDonateDialog() {

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(R.string.donate_info)
				.setCancelable(true)
				.setPositiveButton(R.string.donate,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								openWebBrowser("https://market.android.com/details?id=org.servDroid.web&hl=en");
							}
						});
		builder.setIcon(android.R.drawable.ic_dialog_info);
		builder.create();
		builder.setTitle(R.string.donate);
		builder.show();
	}

	/**
	 * Show log entries on the screen
	 */
	public void fillData(boolean force) {
		if (mServiceController == null || fillOnConnectService) {
			fillOnConnectService = true;
			return;
		}

		if (mLogListViewAdapter == null) {

			mLogListViewAdapter = new LogListViewAdapter(
					this,
					R.layout.row_log,
					(LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE));
			setListAdapter(mLogListViewAdapter);

		}

		List<LogMessage> locals;
		try {
			locals = mServiceController.getLogList(AccessPreferences
					.getNumLogEntries());
			if (locals == null) {
				return;
			}
			if (!force && oldLogMsg != null
					&& (oldLogMsg.size() == locals.size())) {
				// Don't update the log
				return;
			}
			mLogListViewAdapter.clear();
			int size = locals.size();
			if (locals != null && size > 0) {
				for (int i = 0; i < size; i++)
					mLogListViewAdapter.add(locals.get(i));
			}
			mLogListViewAdapter.setItems(locals);
			mLogListViewAdapter.notifyDataSetChanged();
			oldLogMsg = locals;
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.add(0, SEE_LOG_ID, 0, R.string.menu_log).setIcon(
				android.R.drawable.ic_menu_edit);

		menu.add(0, SETTINGS_ID, 0, R.string.menu_preferences).setIcon(
				android.R.drawable.ic_menu_preferences);

		menu.add(0, REFRSH_LOG, 0, R.string.menu_refresh_log).setIcon(
				android.R.drawable.ic_menu_rotate);

		// TODO Not implemented yet
		// menu.add(0, SEE_STATICS_ID, 0, R.string.menu_statics).setIcon(
		// R.drawable.analitics);

		menu.add(0, OPEN_BROWSER_ID, 0, R.string.menu_open_browser).setIcon(
				android.R.drawable.ic_menu_view);

		menu.add(0, HELP_ID, 0, R.string.menu_help).setIcon(
				android.R.drawable.ic_menu_help);

		return true;

	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case SEE_LOG_ID:
			showLog();
			return true;
		case OPEN_BROWSER_ID:
			openWebBrowser("http://127.0.0.1:" + AccessPreferences.getPort());
			return true;
		case REFRSH_LOG:
			fillData(true);
			return true;
		case SETTINGS_ID:
			Intent i = new Intent(this, PreferencesActivity.class);
			startActivityForResult(i, ACTIVITY_SETTINGS);
			return true;
		case HELP_ID:
			showHelpDialog();
			return true;
		}
		return super.onMenuItemSelected(featureId, item);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		// menu.add(0, SHOW_INFO_ID, 0, R.string.menu_show_more_info);
		// menu.add(0, DELETE_ID, 1, R.string.menu_delete);
		// menu.add(0, DELETE_ALL_ID, 2, R.string.menu_delete_all);
		// menu.add(0, CLEAR_LIST_ID, 3, R.string.menu_clear_list);
		// menu.add(0, ADD_IP_BLACK_LIST_ID, 4, R.string.menu_add_black_list);
		// menu.add(0, ADD_IP_WHITE_LIST_ID, 5, R.string.menu_add_white_list);
	}

	// @Override
	// public boolean onContextItemSelected(MenuItem item) {
	// AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
	// .getMenuInfo();
	// switch (item.getItemId()) {
	// case SHOW_INFO_ID:
	// return true;
	// case DELETE_ID:
	// mLogAdapter.deleteLogRow(info.id);
	// fillData();
	// return true;
	// case DELETE_ALL_ID:
	// mLogAdapter.deleteTableLog();
	// fillData();
	// return true;
	// case CLEAR_LIST_ID:
	// return false;
	// case ADD_IP_BLACK_LIST_ID:
	// mLogAdapter.addIpBlackList(info.id);
	// return true;
	// case ADD_IP_WHITE_LIST_ID:
	// return true;
	//
	// }
	// return super.onContextItemSelected(item);
	// }

	/**
	 * Open a web browser to see if ServDroid is working
	 */
	private void openWebBrowser(String url) {
		Intent i = new Intent(Intent.ACTION_VIEW);
		Uri u = Uri.parse(url);
		i.setData(u);
		startActivity(i);
	}

	// @Override
	// protected void onListItemClick(ListView l, View v, int position, long id)
	// {
	// super.onListItemClick(l, v, position, id);
	// showLog(id);
	// }

	/**
	 * Show log entries on the screen
	 */
	private void showLog() {
		showLog(0);
	}

	/**
	 * Show log entries on the screen
	 * 
	 * @param id
	 *            Row identifier - NOT IMPLEMENTED
	 */
	private void showLog(long id) {
		Intent i = new Intent(this, LogViewer.class);
		i.putExtra(ServdroidDbAdapter.KEY_ROWID, id);
		startActivityForResult(i, ACTIVITY_EDIT);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		// fillData(true);
	}

	/**
	 * This function starts the web server
	 * 
	 * @return True if no error, false otherwise.
	 * 
	 */
	private boolean startWebServer() {

		if (null == mConnection) {
			doBindServiceConnection();
			try {
				Thread.sleep(500);
			} catch (InterruptedException e1) {

			}
		}
		try {
			mportInUse = AccessPreferences.getPort();
			if (!mServiceController.startService(AccessPreferences
					.getServerParameters())) {
				return false;
			}
		} catch (RemoteException e) {
			e.printStackTrace();
			return false;
		}
		try {
			Thread.sleep(500);
		} catch (InterruptedException e1) {

		}

		try {
			if (mServiceController.getStatus() != ServerService.STATUS_RUNNING) {
				return false;
			}
		} catch (RemoteException e) {
			e.printStackTrace();
			return false;
		}

		startRefreshThread();

		return true;
	}

	/**
	 * Stop the web server and close connections
	 * 
	 * @return True if no error, false otherwise.
	 */
	private boolean stopWebServer() {
		stopRefreshThread();

		try {
			mServiceController.stopService();
		} catch (RemoteException e) {
			Log.e(TAG,
					"Error stoping the server - stopService() fail: "
							+ e.getMessage());
			e.printStackTrace();
			return false;
		}
		fillData(true);
		return true;

	}

	/**
	 * Force start the log refreshing thread
	 */
	private void startRefreshThread() {
		if (null == mRefresh) {
			mRefresh = new RefreshThread();
			mRefresh.start();
		} else if (!mRefresh.isAlive()) {
			mRefresh = null;
			mRefresh = new RefreshThread();
			mRefresh.start();
		}
	}

	/**
	 * Force stop the log refreshing thread
	 */
	private void stopRefreshThread() {
		if (null != mRefresh) {
			if (mRefresh.isAlive()) {
				mRefresh.stopThread();
			}
		}
		mRefresh = null;
	}

	private class RefreshThread extends Thread {

		private volatile boolean run = true;

		public synchronized void stopThread() {
			run = false;
		}

		@Override
		public void run() {

			while (run) {
				try {
					Message m = new Message();
					m.what = ServDroid.GUIUPDATEIDENTIFIER;
					ServDroid.this.mServDroidHandler.sendMessage(m);
					// Log.d(TAG, "Refreshing log!!!!");
					Thread.sleep(mTimeRefresh);
				} catch (InterruptedException e) {
					Log.e(TAG, "Error refreshing the log", e);
				}
			}
		}
	}

	private class ServerConnection implements ServiceConnection {

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			mServiceController = ServiceController.Stub.asInterface(service);
			Log.i(TAG, "Connected to ServDroid.web Service");
			mServiceBinded = true;

			try {
				switch (mServiceController.getStatus()) {
				case ServerService.STATUS_RUNNING:
					startRefreshThread();
					setStartedStatus();
					break;
				default:
					setStoppedStatus();
					break;
				}
			} catch (RemoteException e) {
				e.printStackTrace();
			}
			try {
				mServiceController.setVibrate(AccessPreferences.getVibrate());
			} catch (RemoteException e) {
				e.printStackTrace();
			}
			if (fillOnConnectService) {
				fillOnConnectService = false;
				fillData(true);
			}

		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			Log.i(TAG, "disconnected from ServDroid.web Service");
			mServiceBinded = false;

		}
	}

	/**
	 * Get the version of this app
	 * 
	 * @return
	 */
	public static final String getVersion() {
		return mVersion;
	}

	// //////////////////////////////////////////
	// //////////////////////////////////////////
	// //////////////////////////////////////////
	// //////////////////////////////////////////
	// //////////////////////////////////////////

}