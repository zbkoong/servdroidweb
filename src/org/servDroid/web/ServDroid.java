package org.servDroid.web;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import org.servDroid.db.LogAdapter;
import org.servDroid.db.LogLocal;
import org.servDroid.db.ServdroidDbAdapter;
import org.servDroid.db.SettingsAdapter;
import org.servDroid.server.httpRequestHandler;
import org.servDroid.settings.SettingsInterface;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
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

public class ServDroid extends ListActivity {

	private static final String TAG = "ServDroid";

	private static final int ACTIVITY_CREATE = 0;
	private static final int ACTIVITY_EDIT = 1;
	private static final int ACTIVITY_SETTINGS = 2;

	private static final int SEE_LOG_ID = Menu.FIRST;
	private static final int SETTINGS_ID = Menu.FIRST + 1;
	private static final int REFRSH_LOG = Menu.FIRST + 2;
	private static final int OPEN_BROWSER_ID = Menu.FIRST + 3;
	private static final int SEE_STATICS_ID = Menu.FIRST + 4;

	private static final int SHOW_INFO_ID = Menu.FIRST + 20;
	private static final int DELETE_ID = Menu.FIRST + 21;
	private static final int DELETE_ALL_ID = Menu.FIRST + 22;
	private static final int CLEAR_LIST_ID = Menu.FIRST + 23;
	private static final int ADD_IP_BLACK_LIST_ID = Menu.FIRST + 24;
	private static final int ADD_IP_WHITE_LIST_ID = Menu.FIRST + 25;

	private LogAdapter mLogAdapter;
	private ToggleButton mStartStopButton;
	private TextView mTexStat, mTexServerInfo;

	private ServerSocket mServerSocket;
	private serverThread mLaunchWebServer;
	private Thread mServerThread;

	private LogListViewAdapter mLogListViewAdapter;

	private SettingsAdapter mSettings;

	private long mTimeRefresh = 10000;

	private Thread mRefresh;

	public static final int GUIUPDATEIDENTIFIER = 0x101;
	public static final int VIBRATEIDENTIFIER = 0x102;

	private WifiManager mWifiManager;

	// private Long mRowId;

	private boolean mExit;

	private boolean mVibrate;

	final Handler mServDroidHandler = new Handler() {
		// @Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case GUIUPDATEIDENTIFIER:
				fillData();
				break;
			case VIBRATEIDENTIFIER:
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
		// TODO Auto-generated method stub
		if ((keyCode == KeyEvent.KEYCODE_BACK & !mExit)) {
			Toast.makeText(ServDroid.this, R.string.press_back_button,
					Toast.LENGTH_LONG).show();
			mExit = true;
			return false;
		}
		if ((keyCode == KeyEvent.KEYCODE_BACK & mExit)) {
			System.exit(0);
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onStart() {
		mExit = false;
		// TODO Auto-generated method stub
		mVibrate = mSettings.isVibrateEnabled();
		super.onStart();

	}

	@Override
	protected void onRestart() {
		// Log.d(TAG, "Restart");
		mExit = false;
		mVibrate = mSettings.isVibrateEnabled();
		super.onRestart();
	}

	@Override
	protected void onResume() {
		mExit = false;
		// Log.d(TAG, "Resume");
		mVibrate = mSettings.isVibrateEnabled();
		if (mServerThread != null) {
			if (mServerThread.isAlive()) {
				mStartStopButton.setChecked(true);
			}
		}
		super.onResume();
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}

	@Override
	protected void onPause() {
		// Log.d(TAG, "Pause");
		super.onPause();
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.main);
		setRequestedOrientation(1);

		// mRowId = savedInstanceState != null ?
		// savedInstanceState.getLong("epe")
		// : null;
		// if (mRowId == null) {
		// Bundle extras = getIntent().getExtras();
		// if (extras != null)
		// mRowId = extras.getLong("epe");
		// }

		mStartStopButton = (ToggleButton) findViewById(R.id.startstop);
		mTexStat = (TextView) findViewById(R.id.textStat);
		mTexServerInfo = (TextView) findViewById(R.id.textServerInfo);

		// mBoolean = savedInstanceState != null ? savedInstanceState
		// .getBoolean("MyBoolean") : false;

		// savedInstanceState.getBoolean("MyBoolean");
		// if (!mBoolean) {
		// mTexServerInfo.setText(" -- ");
		// } else {
		// mTexServerInfo.setText("Activity recovered");
		// }

		mLogAdapter = new LogAdapter(this);
		mSettings = new SettingsAdapter(this);

		mTexServerInfo.setText(" -- ");
		mTexStat.setText(R.string.text_stopped);

		mWifiManager = (WifiManager) this
				.getSystemService(Context.WIFI_SERVICE);

		fillData();
		registerForContextMenu(getListView());

		mStartStopButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// if (mWifiManager.isWifiEnabled()) {
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
									+ getWifiIp()
									+ " "
									+ getResources().getString(
											R.string.text_port) + ": "
									+ mSettings.getPort());
						} else {
							mTexStat.setText(R.string.text_stopped);
							mTexServerInfo
									.setText(R.string.error_starting_process);
						}

					} else {
						// Toast.makeText(ServDroid.this, "OFF",
						// Toast.LENGTH_SHORT)
						// .show();
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
				// else {
				// Toast.makeText(ServDroid.this,
				// R.string.text_server_info_no_wifi,
				// Toast.LENGTH_LONG).show();
				// mStartStopButton.setChecked(false);
				// }

			}
		});

	}

	public void fillData() {

		if (mLogListViewAdapter == null) {

			mLogListViewAdapter = new LogListViewAdapter(
					this,
					R.layout.row_log,
					(LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE));
			setListAdapter(mLogListViewAdapter);

		}

		ArrayList<LogLocal> locals = mLogAdapter.fetchAllLogList();

		mLogListViewAdapter.clear();
		int size = locals.size();
		if (locals != null && size > 0) {
			for (int i = 0; i < size; i++)
				mLogListViewAdapter.add(locals.get(i));
		}
		mLogListViewAdapter.setItems(locals);
		// setListAdapter(mLogListViewAdapter);
		mLogListViewAdapter.notifyDataSetChanged();

		// Cursor logCursor = mLogHelper.fetchAllLog(30);
		// startManagingCursor(logCursor);
		//
		// // Create an array to specify the fields we want to display in the
		// list
		// String[] from = new String[] { ServdroidDbAdapter.KEY_HOSTS };
		//
		// // and an array of the fields we want to bind those fields to (in
		// this
		// // case just text1)
		// int[] to = new int[] { R.id.textLog };
		//
		// // Now create a simple cursor adapter and set it to display
		// SimpleCursorAdapter log = new SimpleCursorAdapter(this,
		// R.layout.row_log, logCursor, from, to);
		// setListAdapter(log);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.add(0, SEE_LOG_ID, 0, R.string.menu_log).setIcon(
				android.R.drawable.ic_menu_info_details);

		menu.add(0, SETTINGS_ID, 0, R.string.menu_preferences).setIcon(
				android.R.drawable.ic_menu_preferences);

		menu.add(0, REFRSH_LOG, 0, R.string.menu_refresh_log).setIcon(
				android.R.drawable.ic_menu_rotate);

		// menu.add(0, SEE_STATICS_ID, 0, R.string.menu_statics).setIcon(
		// R.drawable.analitics);

		menu.add(0, OPEN_BROWSER_ID, 0, R.string.menu_open_browser).setIcon(
				android.R.drawable.ic_menu_view);

		return true;

	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case SEE_LOG_ID:
			showLog();
			return true;
		case OPEN_BROWSER_ID:
			openWebBrowser();
			return true;
		case REFRSH_LOG:
			fillData();
			return true;
		case SETTINGS_ID:

			Intent i = new Intent(this, SettingsInterface.class);
			startActivityForResult(i, ACTIVITY_SETTINGS);

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

	private void openWebBrowser() {
		Intent i = new Intent(Intent.ACTION_VIEW);
		Uri u = Uri.parse("http://127.0.0.1:" + mSettings.getPort());
		i.setData(u);
		startActivity(i);
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		showLog(id);
	}

	private void showLog() {
		showLog(0);
	}

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
	 * This function start the web server
	 * 
	 * @return True if no error, false otherwise.
	 * 
	 */
	private boolean startWebServer() {

		mRefresh = new Thread(new Runnable() {

			@Override
			public void run() {

				while (mServerThread.isAlive()) {

					try {
						Thread.sleep(mTimeRefresh);
						Message m = new Message();
						m.what = ServDroid.GUIUPDATEIDENTIFIER;
						ServDroid.this.mServDroidHandler.sendMessage(m);
						// Log.d(TAG, "Refreshing");
					} catch (InterruptedException e) {
						Log.e(TAG, "Error refreshing the log", e);
					}
				}
			}
		});

		try {
			mServerSocket = new ServerSocket(mSettings.getPort(), mSettings
					.getMaxClients());
		} catch (IOException e) {
			Log.e(TAG, "Error accepting connections: ", e);
			Toast.makeText(ServDroid.this, R.string.error_starting_process,
					Toast.LENGTH_LONG).show();
			mStartStopButton.setChecked(false);
			return false;
		}
		mLogAdapter.addLog("",  "ServDroid.web server running on port "
				+ mServerSocket.getLocalPort(),"", "");
		Log.d(TAG, "ServDroid.web server running on port "
				+ mServerSocket.getLocalPort());

		mLaunchWebServer = new serverThread(mServerSocket, mSettings
				.getWwwPath(), mSettings.getErrorPage(), mLogAdapter,
				mServDroidHandler);
		mServerThread = new Thread(mLaunchWebServer);
		// serverThread.setDaemon(true);
		mServerThread.start();
		try {
			Thread.sleep(500);
		} catch (InterruptedException e1) {

		}
		// if (mLaunchWebServer.isErrors()) {
		// Toast.makeText(ServDroid.this, R.string.error_starting_process,
		// Toast.LENGTH_LONG).show();
		// mStartStopButton.setChecked(false);
		// return false;
		// }
		if (mServerThread.isAlive()) {
			mRefresh.start();
		}
		return true;

	}

	/**
	 * Stop the web server and close connections
	 * 
	 * @return True if no error, false otherwise.
	 */
	private boolean stopWebServer() {
		Log.d(TAG, "Interrupting thread...");

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

			Log.d(TAG, "Thread interrupted");
			return true;
		} else {
			Log.e(TAG, "Thread Has not incialized (null value)");
			return false;
		}

	}

	private String getWifiIp() {
		int ip = mWifiManager.getConnectionInfo().getIpAddress();
		return (ip & 0xFF) + "." + ((ip >> 8) & 0xFF) + "."
				+ ((ip >> 16) & 0xFF) + "." + ((ip >> 24) & 0xFF);

	}

	public LogAdapter getLogAdapter() {
		return mLogAdapter;
	}

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

			// server infinite loop
			while (true) {
				Socket socket;
				try {
					socket = mServerSocket.accept();
				} catch (IOException e1) {
					// Log.e(TAG, "ERROR handing request", e1);
					return;
				}
				Log.d(TAG, "New connection accepted " + socket.getInetAddress()
						+ ":" + socket.getPort());
				// mServDroid.addLog("" + socket.getInetAddress(), "", "",
				// "");

				try {
					httpRequestHandler request = new httpRequestHandler(socket,
							mWwwPath, mErrorPath, mLogAdapter);
					Thread thread = new Thread(request);

					thread.start();

				} catch (Exception e) {
					Log.e(TAG, "ERROR handing request: " + e.getMessage());
					return;
				}

				Message m = new Message();
				m.what = ServDroid.VIBRATEIDENTIFIER;
				mHandler.sendMessage(m);

			}

		}

	}
}