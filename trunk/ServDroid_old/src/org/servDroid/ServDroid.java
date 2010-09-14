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

import java.io.IOException;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;

import org.servDroid.Preference.ManagePreferences;
import org.servDroid.db.LogAdapter;
import org.servDroid.db.LogLocal;
import org.servDroid.db.ServdroidDbAdapter;
import org.servDroid.server.HttpRequestHandler;
import org.servDroid.web.R;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.widget.AdapterView.AdapterContextMenuInfo;

/**
 * This is the main class.
 * 
 * @author Joan Puig Sanz
 * 
 */
public class ServDroid extends ListActivity {

	public static final String TAG = "ServDroid";
	

	// private static final int ACTIVITY_CREATE = 0;
	private static final int ACTIVITY_EDIT = 1;
	private static final int ACTIVITY_SETTINGS = 2;

	private static final int SEE_LOG_ID = Menu.FIRST;
	private static final int SETTINGS_ID = Menu.FIRST + 1;
	private static final int REFRSH_LOG = Menu.FIRST + 2;
	private static final int OPEN_BROWSER_ID = Menu.FIRST + 3;
	// private static final int SEE_STATICS_ID = Menu.FIRST + 4; //NOT
	// Implemented
	private static final int HELP_ID = Menu.FIRST + 5;

	private static final int SHOW_INFO_ID = Menu.FIRST + 20;
	private static final int DELETE_ID = Menu.FIRST + 21;
	private static final int DELETE_ALL_ID = Menu.FIRST + 22;
	private static final int CLEAR_LIST_ID = Menu.FIRST + 23;
	private static final int ADD_IP_BLACK_LIST_ID = Menu.FIRST + 24;
	private static final int ADD_IP_WHITE_LIST_ID = Menu.FIRST + 25;

	private static final int START_NOTIFICATION_ID = 1;

	private LogAdapter mLogAdapter;
	private ToggleButton mStartStopButton;
	private TextView mTexStat, mTexServerInfo;

	private ServerSocket mServerSocket;
	private serverThread mLaunchWebServer;
	private Thread mServerThread;

	private LogListViewAdapter mLogListViewAdapter;

	// private ServerPreferences mPreferences;

	private long mTimeRefresh = 10000;

	private Thread mRefresh;

	public static final int GUIUPDATEIDENTIFIER = 0x101;
	public static final int VIBRATE_IDENTIFIER = 0x102;

	private NotificationManager mNotificationManager;

	private WifiManager mWifiManager;

	private boolean mExit;

	private boolean mVibrate, mFileIndexing;

	// This is the handler for the thread which refreshes the log screen
	final Handler mServDroidHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case GUIUPDATEIDENTIFIER:
				fillData();
				break;
			case VIBRATE_IDENTIFIER:
				if (mVibrate) {
					((Vibrator) getSystemService(VIBRATOR_SERVICE))
							.vibrate(300);
				}
			}
			super.handleMessage(msg);
		}
	};

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if ((keyCode == KeyEvent.KEYCODE_BACK & !mExit)) {
			Toast.makeText(ServDroid.this, R.string.press_back_button,
					Toast.LENGTH_LONG).show();
			mExit = true;
			return false;
		}
		if ((keyCode == KeyEvent.KEYCODE_BACK & mExit)) {
			clearAllNotifications();
			// System.gc();
			System.exit(0);
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onStart() {
		mExit = false;
		mVibrate = getVibrate();
		mFileIndexing = getFileIndexingEnabled();

		super.onStart();

	}

	@Override
	protected void onRestart() {
		// Log.d(TAG, "Restart");
		mExit = false;
		mVibrate = getVibrate();
		mFileIndexing = getFileIndexingEnabled();
		super.onRestart();
	}

	@Override
	protected void onResume() {
		mExit = false;
		// Log.d(TAG, "Resume");
		mVibrate = getVibrate();
		mFileIndexing = getFileIndexingEnabled();
		if (mServerThread != null) {
			if (mServerThread.isAlive()) {
				mStartStopButton.setChecked(true);
			}
		}
		super.onResume();
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.main);
		setRequestedOrientation(1);

		mStartStopButton = (ToggleButton) findViewById(R.id.startstop);
		mTexStat = (TextView) findViewById(R.id.textStat);
		mTexServerInfo = (TextView) findViewById(R.id.textServerInfo);

		mLogAdapter = new LogAdapter(this);

		mTexServerInfo.setText(" -- ");
		mTexStat.setText(R.string.text_stopped);

		mWifiManager = (WifiManager) this
				.getSystemService(Context.WIFI_SERVICE);

		fillData();
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

							mTexStat.setText(R.string.text_running);
							mTexServerInfo.setText("IP: "
									+ getLocalIpAddress()
									+ " "
									+ getResources().getString(
											R.string.text_port) + ": "
									+ getPort());
						} else {
							mTexStat.setText(R.string.text_stopped);
							mTexServerInfo
									.setText(R.string.error_starting_process);
						}

					} else {
						mTexStat.setText(R.string.text_stopping);
						if (stopWebServer()) {
							mTexServerInfo.setText(" -- ");
							mTexStat.setText(R.string.text_stopped);
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

	/**
	 * Show release notes
	 */
	private void checkVersion() {

		SharedPreferences settings = getSharedPreferences(getResources()
				.getString(R.string.pref_release_notes_key), 0);

		String version = settings.getString("version", "-");

		if (!version.equals(getResources().getString(R.string.version))) {

			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage(R.string.release_notes_info).setCancelable(true)
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

		SharedPreferences settings = getSharedPreferences(getResources()
				.getString(R.string.pref_release_notes_key), 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString("version", getResources().getString(R.string.version));
		// Commit the edits!
		editor.commit();

	}

	/**
	 * This function displays the help dialog
	 */
	private void showHelpDialog() {

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(
				getResources().getString(R.string.info_dialog_1) + " \""
						+ getWwwPath() + "\" "
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
				.setCancelable(true).setPositiveButton(R.string.web_page,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								openWebBrowser("http://code.google.com/p/servdroidweb/");
							}
						}).setNegativeButton(R.string.donate,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {

								donateDialog();
							}
						}).setNeutralButton(android.R.string.ok,
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

	private void donateDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(R.string.donate_info).setCancelable(true)
				.setNeutralButton("PayPal",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								openWebBrowser("https://www.paypal.com/cgi-bin/webscr?cmd=_donations&business=GS8EPVN7QZTAN&lc=ES&item_name=ServDroid%2eweb&currency_code=EUR&bn=PP%2dDonationsBF%3abtn_donateCC_LG%2egif%3aNonHosted");
							}
						}).setPositiveButton("Market",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								openWebBrowser("market://search?q=ServDroid.web donate");
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
	public void fillData() {

		if (mLogListViewAdapter == null) {

			mLogListViewAdapter = new LogListViewAdapter(
					this,
					R.layout.row_log,
					(LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE));
			setListAdapter(mLogListViewAdapter);

		}

		ArrayList<LogLocal> locals = mLogAdapter.fetchLogList(10);

		mLogListViewAdapter.clear();
		int size = locals.size();
		if (locals != null && size > 0) {
			for (int i = 0; i < size; i++)
				mLogListViewAdapter.add(locals.get(i));
		}
		mLogListViewAdapter.setItems(locals);
		mLogListViewAdapter.notifyDataSetChanged();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.add(0, SEE_LOG_ID, 0, R.string.menu_log).setIcon(
				android.R.drawable.ic_menu_info_details);

		menu.add(0, SETTINGS_ID, 0, R.string.menu_preferences).setIcon(
				android.R.drawable.ic_menu_preferences);

		menu.add(0, REFRSH_LOG, 0, R.string.menu_refresh_log).setIcon(
				R.drawable.refresh);

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
			openWebBrowser("http://127.0.0.1:" + getPort());
			return true;
		case REFRSH_LOG:
			fillData();
			return true;
		case SETTINGS_ID:
			Intent i = new Intent(this, ManagePreferences.class);
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
		menu.add(0, DELETE_ID, 1, R.string.menu_delete);
		menu.add(0, DELETE_ALL_ID, 2, R.string.menu_delete_all);
		// menu.add(0, CLEAR_LIST_ID, 3, R.string.menu_clear_list);
		// menu.add(0, ADD_IP_BLACK_LIST_ID, 4, R.string.menu_add_black_list);
		// menu.add(0, ADD_IP_WHITE_LIST_ID, 5, R.string.menu_add_white_list);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();
		switch (item.getItemId()) {
		case SHOW_INFO_ID:
			return true;
		case DELETE_ID:
			mLogAdapter.deleteLogRow(info.id);
			fillData();
			return true;
		case DELETE_ALL_ID:
			mLogAdapter.deleteTableLog();
			fillData();
			return true;
		case CLEAR_LIST_ID:
			return false;
		case ADD_IP_BLACK_LIST_ID:
			mLogAdapter.addIpBlackList(info.id);
			return true;
		case ADD_IP_WHITE_LIST_ID:
			return true;

		}
		return super.onContextItemSelected(item);
	}

	/**
	 * Open a web browser to see if ServDroid is working
	 */
	private void openWebBrowser(String url) {
		Intent i = new Intent(Intent.ACTION_VIEW);
		Uri u = Uri.parse(url);
		i.setData(u);
		startActivity(i);
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		showLog(id);
	}

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
		fillData();
	}

	/**
	 * This function starts the web server
	 * 
	 * @return True if no error, false otherwise.
	 * 
	 */
	private boolean startWebServer() {

		mRefresh = new Thread(new Runnable() {

			public void run() {

				while (mServerThread.isAlive()) {

					try {
						Thread.sleep(mTimeRefresh);
						Message m = new Message();
						m.what = ServDroid.GUIUPDATEIDENTIFIER;
						ServDroid.this.mServDroidHandler.sendMessage(m);
					} catch (InterruptedException e) {
						Log.e(TAG, "Error refreshing the log", e);
					}
				}
			}
		});

		try {
			mServerSocket = new ServerSocket(getPort(), getMaxClients());
		} catch (IOException e) {
			Log.e(TAG, "Error accepting connections: ", e);
			Toast.makeText(ServDroid.this, R.string.error_starting_process,
					Toast.LENGTH_LONG).show();
			mStartStopButton.setChecked(false);
			return false;
		}
		mLogAdapter.addLog("", "ServDroid.web server running on port "
				+ mServerSocket.getLocalPort(), "", "");
		// Log.d(TAG, "ServDroid.web server running on port "
		// + mServerSocket.getLocalPort());

		mLaunchWebServer = new serverThread(mServerSocket, getWwwPath(),
				getErrorPath(), mLogAdapter, mServDroidHandler);
		mServerThread = new Thread(mLaunchWebServer);
		// serverThread.setDaemon(true);
		mServerThread.start();
		try {
			Thread.sleep(500);
		} catch (InterruptedException e1) {

		}
		if (mServerThread.isAlive()) {
			mRefresh.start();
		}

		showNotification();
		return true;

	}

	/**
	 * Stop the web server and close connections
	 * 
	 * @return True if no error, false otherwise.
	 */
	private boolean stopWebServer() {
		// Log.d(TAG, "Interrupting thread...");
		clearAllNotifications();

		if (mRefresh != null) {
			if (mRefresh.isAlive()) {
				mRefresh.stop();
			}
		}
		try {
			mServerSocket.close();
		} catch (IOException e) {
			Log.e(TAG, "ERROR clossing socket", e);
		}

		if (mLaunchWebServer != null) {
			try {
				mLaunchWebServer.stop();
			} catch (Exception e) {
				Log.e(TAG, "ERROR stopping the server thread", e);
				Toast.makeText(ServDroid.this, R.string.error_stopping_process,
						Toast.LENGTH_LONG).show();
				mStartStopButton.setChecked(false);
			}

			// Log.d(TAG, "Thread interrupted");
			return true;
		} else {
			Log.e(TAG, "Thread Has not been incialised (null value)");
			return false;
		}

	}

	/**
	 * Get the IP used for ServDroid.web
	 * 
	 * @return The IP of your device
	 */
	public String getLocalIpAddress() {
		try {
			for (Enumeration<NetworkInterface> en = NetworkInterface
					.getNetworkInterfaces(); en.hasMoreElements();) {
				NetworkInterface intf = en.nextElement();
				for (Enumeration<InetAddress> enumIpAddr = intf
						.getInetAddresses(); enumIpAddr.hasMoreElements();) {
					InetAddress inetAddress = enumIpAddr.nextElement();
					if (!inetAddress.isLoopbackAddress()) {
						return inetAddress.getHostAddress().toString();
					}
				}
			}
		} catch (SocketException ex) {
			Log.e(TAG, ex.toString());
		}
		return null;
	}

	/**
	 * Private class for the server thread
	 * 
	 * @author Joan Puig Sanz
	 * 
	 */
	private class serverThread extends Thread {
		private ServerSocket mServerSocket;
		private String mWwwPath, mErrorPath;
		private LogAdapter mLogAdapter;
		private Handler mHandler;

		public serverThread(ServerSocket serverSocket, String wwwPath,
				String errorPath, LogAdapter logAdapter, Handler handler) {
			this.mServerSocket = serverSocket;
			this.mWwwPath = wwwPath;
			this.mErrorPath = errorPath;
			this.mLogAdapter = logAdapter;
			this.mHandler = handler;
		}

		public void run() {

			while (true) {
				Socket socket;
				try {
					socket = mServerSocket.accept();
				} catch (IOException e1) {
					return;
				}
				// Log.d(TAG, "New connection accepted " +
				// socket.getInetAddress()
				// + ":" + socket.getPort());

				try {
					HttpRequestHandler request = new HttpRequestHandler(socket,
							mWwwPath, mErrorPath, mLogAdapter, mFileIndexing);
					Thread thread = new Thread(request);

					thread.start();

				} catch (Exception e) {
					// Log.e(TAG, "ERROR handing request: " + e.getMessage());
					return;
				}

				Message m = new Message();
				m.what = ServDroid.VIBRATE_IDENTIFIER;
				mHandler.sendMessage(m);

			}

		}
	}

	/**
	 * This function displays the notifications
	 */
	private void showNotification() {
		String ns = Context.NOTIFICATION_SERVICE;
		mNotificationManager = (NotificationManager) getSystemService(ns);

		int icon = R.drawable.icon;
		CharSequence tickerText = getResources().getString(
				R.string.text_running);
		long when = System.currentTimeMillis();

		Notification notification = new Notification(icon, tickerText, when);

		Context context = getApplicationContext();
		CharSequence contentTitle = getResources().getString(R.string.app_name);
		CharSequence contentText = getResources().getString(
				R.string.text_running);
		Intent notificationIntent = new Intent(this, ServDroid.class);
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
				notificationIntent, 0);

		notification.setLatestEventInfo(context, contentTitle, contentText,
				contentIntent);

		mNotificationManager.notify(START_NOTIFICATION_ID, notification);
	}

	/**
	 * Clear all notifications
	 */
	private void clearAllNotifications() {

		try {
			mNotificationManager.cancelAll();
			// mNotificationManager.cancel(START_NOTIFICATION_ID);
		} catch (Exception e) {

		}
	}

	/**
	 * See vibration is enabled through SharedPreferences
	 * 
	 * @return true if vibrate is enabled, false otherwise
	 */
	private boolean getVibrate() {
		SharedPreferences pref = PreferenceManager
				.getDefaultSharedPreferences(this);
		return pref.getBoolean(getResources().getString(
				R.string.pref_vibrate_key), false);

	}

	/**
	 * Is file indexing enabled?
	 * 
	 * @return return true if file indexing is enabled, false otherwise
	 */
	private boolean getFileIndexingEnabled() {
		SharedPreferences pref = PreferenceManager
				.getDefaultSharedPreferences(this);
		return pref.getBoolean(getResources().getString(
				R.string.pref_directory_indexing_key), true);
	}

	/**
	 * Get server port through SharedPreferences
	 * 
	 * @return the port number
	 */
	private int getPort() {
		SharedPreferences pref = PreferenceManager
				.getDefaultSharedPreferences(this);
		String port = pref.getString(getResources().getString(
				R.string.pref_port_key), getResources().getString(
				R.string.default_port));

		try {
			return Integer.parseInt(port);
		} catch (NumberFormatException e) {
			return 8080;
		}

	}

	/**
	 * Get the max clients allowed through SharedPreferences
	 * 
	 * @return the max clients allowed
	 */
	private int getMaxClients() {
		SharedPreferences pref = PreferenceManager
				.getDefaultSharedPreferences(this);
		String max = pref.getString(getResources().getString(
				R.string.pref_max_clients_key), getResources().getString(
				R.string.default_max_clients));

		try {
			return Integer.parseInt(max);
		} catch (NumberFormatException e) {
			return 10;
		}

	}

	/**
	 * Get the www path through SharedPreferences
	 * 
	 * @return www path
	 */
	private String getWwwPath() {
		SharedPreferences pref = PreferenceManager
				.getDefaultSharedPreferences(this);
		return pref.getString(getResources().getString(
				R.string.pref_www_path_key), getResources().getString(
				R.string.default_www_path));
	}

	/**
	 * Get the error path through SharedPreferences
	 * 
	 * Where the error document paths (404.html, etc) are stored
	 * 
	 * @return error path
	 */
	private String getErrorPath() {
		SharedPreferences pref = PreferenceManager
				.getDefaultSharedPreferences(this);
		return pref.getString(getResources().getString(
				R.string.pref_error_path_key), getResources().getString(
				R.string.default_error_path));

	}
}