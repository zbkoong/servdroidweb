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
package org.servDroid.server.service;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

import org.servDroid.ServDroid;
import org.servDroid.Preference.AccessPreferences;
import org.servDroid.db.LogAdapter;
import org.servDroid.db.LogMessage;
import org.servDroid.server.HttpRequestHandler;
import org.servDroid.server.service.params.ServerParams;
import org.servDroid.util.NetworkIp;
import org.servDroid.util.shell.ShellCommands;
import org.servDroid.web.R;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.WifiLock;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.os.Vibrator;
import android.util.Log;

public class ServerService extends Service implements ServerValues {
	
	private static final String TAG = ServDroid.TAG;
	private static String mVersion;
	private static final int START_NOTIFICATION_ID = 1;

	private static final int VIBRATE_IDENTIFIER = 0x102;
	private static final int SERVER_STARTED_IDENTIFIER = 0x102 + 1;
	private static final int SERVER_STOPED_IDENTIFIER = 0x102 + 2;

	/**
	 * This is the default port opened when the user ask for opening a port
	 * under 1024. <br>
	 * The system will try to use iptables like this:<br>
	 * iptables -t nat -A PREROUTING -p tcp --dport 80 -j REDIRECT --to-port
	 * DEFAULT_PORT_ON_ROOT
	 */
	public static final int DEFAULT_PORT_ON_ROOT = 65535 - 50;
	// public static final int STATUS_SECURITY_ERROR = 5435 + 2;
	// public static final int STATUS_UNKNOW = 5435 + 3;

	// public static final String SERVICE_SHARED_KEY =
	// "org.servDroid.server.service.sharedKey";
	// private String mSharedKey;

	private static ServerParams mParams;
	private static int mCurrentPort;
	private static String mLogPort;
	private static ServerSocket mServerSocket;

	private static MainServerThread mServerThread;

	private static LogAdapter mLogAdapter;

	private volatile boolean mVibrate;
	private volatile static int mAutostartOnBoot = 0;
	private volatile static boolean mAutostartOnWifiConnect = false;

	private static ServerService _this;

	private static NotificationManager mNotificationManager;

	private static BroadcastReceiver wifiStateChangedReceiver;

	final Handler mServiceHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case VIBRATE_IDENTIFIER:

				((Vibrator) getSystemService(Context.VIBRATOR_SERVICE))
						.vibrate(300);
				break;
			case SERVER_STARTED_IDENTIFIER:
				showRunningNotification();
				break;
			case SERVER_STOPED_IDENTIFIER:
				clearRunningNotification();
				break;
			}
			super.handleMessage(msg);
		}
	};

	public static void notifySystemBoot() {
		// Log.d(TAG, "  ========== Autostart seted!");
		if (mAutostartOnBoot == 0) {
			mAutostartOnBoot = 1;
		}
	}

	public static void notifyConnectedToWifi() {
		if (!AccessPreferences.isAutostartWifiEnabled()) {
			return;
		}
		mAutostartOnWifiConnect = true;
		if (null == _this) {
			return;
		}
		if (null == mParams) {
			mParams = AccessPreferences.getServerParameters();
		}
		if (_this.getServerStatus() == STATUS_RUNNING) {
			_this.stopServer();
		}
		_this.startServer();

		// // Log.d(TAG, "  ========== Autostart seted!");
		// if (mAutostartOnWifiConnect == 0 ) {
		// mAutostartOnBoot = 1;
		// }
	}

	@Override
	public IBinder onBind(Intent intent) {
		mAutostartOnBoot = 2;// the BroadcastRecivers can't bind a service


		// Bundle bundle = intent.getExtras();
		// if (null != bundle) {
		// mSharedKey = bundle.getString(SERVICE_SHARED_KEY);
		// }
		// Log.d(TAG, " Binding service  ");

		if (getServerStatus() != STATUS_RUNNING) {
			clearRunningNotification();
		}

		return new ServiceController.Stub() {
			@Override
			public boolean startService(ServerParams params)
					throws RemoteException {
				if (null == params) {
					return false;
				}
				mParams = params;
				return startServer();
			}

			@Override
			public boolean restartService(ServerParams params)
					throws RemoteException {
				if (null == params) {
					return false;
				}
				if (getStatus() == STATUS_RUNNING) {
					stopServer();
				}
				return startServer();
			}

			@Override
			public boolean stopService() throws RemoteException {
				return stopServer();
			}

			@Override
			public int getStatus() throws RemoteException {
				return getServerStatus();
			}

			@Override
			public void setVibrate(boolean vibrate) throws RemoteException {
				mVibrate = vibrate;
			}

			@Override
			public String getVersion() throws RemoteException {
				return mVersion;
			}

			@Override
			public long addLog(LogMessage msg) throws RemoteException {
				return ServerService.addLog(msg);
			}

			@Override
			public List<LogMessage> getLogList(int n) throws RemoteException {
				return mLogAdapter.fetchLogList(n);
			}

			@Override
			public ServerParams getCurrentParams() throws RemoteException {
				return mParams;
			}

			@Override
			public int getDefaultPortOnRoot() throws RemoteException {
				return DEFAULT_PORT_ON_ROOT;
			}

		};
	}

	private int getServerStatus() {
		if (null == mServerThread) {
			return STATUS_STOPED;
		} else if (mServerThread.isAlive()) {
			return STATUS_RUNNING;
		} else {
			return STATUS_STOPED;
		}
	}

	/**
	 * This function displays the notifications
	 */
	private void showRunningNotification() {
		if (null == mNotificationManager) {
			mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		}
		if (!AccessPreferences.getShowNotification()) {
			return;
		}

		int icon = R.drawable.icon;
		CharSequence tickerText = getResources().getString(
				R.string.text_running);
		long when = System.currentTimeMillis();

		Notification notification = new Notification(icon, tickerText, when);

		Context context = getApplicationContext();
		CharSequence contentTitle = getResources().getString(R.string.app_name);
		CharSequence contentText = getResources().getString(
				R.string.text_running)
				+ "  IP: "
				+ NetworkIp.getLocalIpAddress()
				+ " "
				+ getResources().getString(R.string.text_port)
				+ ": "
				+ mLogPort;
		Intent notificationIntent = new Intent(this, ServDroid.class);
		PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
				notificationIntent, 0);

		notification.setLatestEventInfo(context, contentTitle, contentText,
				contentIntent);
		notification.flags = Notification.FLAG_ONGOING_EVENT;

		mNotificationManager.notify(START_NOTIFICATION_ID, notification);
	}

	/**
	 * Clear all notifications
	 */
	private void clearRunningNotification() {
		if (null == mNotificationManager) {
			mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		}

		try {
			mNotificationManager.cancel(START_NOTIFICATION_ID);
			// mNotificationManager.cancel(START_NOTIFICATION_ID);
		} catch (Exception e) {

		}
	}

	@Override
	public void onCreate() {
		super.onCreate();

		wifiStateChangedReceiver = new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {
				// TODO Auto-generated method stub

				int extraWifiState = intent.getIntExtra(
						WifiManager.EXTRA_WIFI_STATE,
						WifiManager.WIFI_STATE_UNKNOWN);

				switch (extraWifiState) {
				case WifiManager.WIFI_STATE_DISABLED:
					// Log.d(TAG, "WIFI STATE DISABLED");
					break;
				case WifiManager.WIFI_STATE_DISABLING:
					if (AccessPreferences.isAutostopWifiEnabled()
							&& getServerStatus() == STATUS_RUNNING) {
						addLog("", "", "",
								"Wifi connection down... Stopping server");
						stopServer();
					}
					break;
				case WifiManager.WIFI_STATE_ENABLED:
					// Log.d(TAG, "WIFI STATE ENABLED");
					break;
				case WifiManager.WIFI_STATE_ENABLING:
					// Log.d(TAG, "WIFI STATE ENABLING");
					break;
				case WifiManager.WIFI_STATE_UNKNOWN:
					// Log.d(TAG, "WIFI STATE UNKNOWN");
					break;
				}

			}
		};

		registerReceiver(wifiStateChangedReceiver, new IntentFilter(
				WifiManager.WIFI_STATE_CHANGED_ACTION));

		if (null == mLogAdapter) {
			mLogAdapter = LogAdapter.initializeInstance(this);
		}
		mVersion = this.getResources().getString(R.string.version);
		_this = this;
		if (mAutostartOnBoot == 1) {
			mParams = AccessPreferences.getServerParameters();
			startServer();
		} else if (mAutostartOnWifiConnect) {
			mParams = AccessPreferences.getServerParameters();
			if (getServerStatus() == STATUS_RUNNING) {
				stopServer();
			}
			startServer();
		}
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// Log.i("LocalService", "Received start id " + startId + ": " +
		// intent);
		// We want this service to continue running until it is explicitly
		// stopped, so return sticky.
		return START_STICKY;
	}

	@Override
	public void onDestroy() {
		// mLogAdapter.close();
		Log.d(TAG, "  Destroing ServDroid Service");
		// stopServer();
		if (getServerStatus() != STATUS_RUNNING) {
			clearRunningNotification();
		}
		super.onDestroy();
	}

	private boolean startServer() {
		if (null == mServerThread) {
			mServerThread = new MainServerThread();
			mServerThread.start();
			return true;
		} else {
			mAutostartOnBoot = 2;
		}
		return false;

	}

	private boolean stopServer() {

		clearRunningNotification();
		if (mCurrentPort < 1024) {
			ShellCommands.closeNatPorts();
		}
		if (null == mServerThread) {
			addLog("", "", "", "ERROR stopping ServDroid.web server ");
			return false;
		}
		if (mServerThread.isAlive()) {
			mServerThread.stopThread();
			mServerThread = null;
			addLog("", "", "", "ServDroid.web server stoped ");
			return true;
		}
		addLog("", "", "", "ERROR stopping ServDroid.web server");
		mServerThread = null;
		return false;
	}

	public static void addLog(String ip, String path, String infoBeginning,
			String infoEnd) {
		if (null == mLogAdapter) {
			return;
		}
		mLogAdapter.addLog(ip, path, infoBeginning, infoEnd);
	}

	public static void addLog(String ip, String path) {
		if (null == mLogAdapter) {
			return;
		}
		mLogAdapter.addLog(ip, path);
	}

	public static long addLog(LogMessage msg) {
		if (null == mLogAdapter) {
			return -1;
		}
		return mLogAdapter.addLog(msg);
	}

	/**
	 * Get the current server {@link ServerParams}
	 * 
	 * @return the current server {@link ServerParams}
	 */
	public static ServerParams getServerParams() {
		return mParams;
	}

	// ///////////////////////////////////////
	// ///////////////////////////////////////
	// ///////////////////////////////////////

	/**
	 * Private class for the server thread
	 */
	private class MainServerThread extends Thread {

		private volatile boolean mRun;
		private WifiLock mWl;

		public MainServerThread() {
			mRun = true;
		}

		public synchronized void stopThread() {
			if (null != mWl) {
				mWl.release();
			}
			if (mRun == false) {
				return;
			}
			mRun = false;
			if (mServerSocket == null) {
				return;
			}
			try {
				mServerSocket.close();
			} catch (IOException e) {
				Log.e(TAG, "Error stoping server thread: ", e);
				e.printStackTrace();
			}

		}

		public void run() {

			try {
				WifiManager manager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
				mWl = manager.createWifiLock("wifilock");
				mWl.acquire();
			} catch (Exception e) {
			}

			mCurrentPort = mParams.getPort();
			mLogPort = "" + mCurrentPort;

			if (mAutostartOnBoot == 1) {
				Log.d(TAG, "  Autostart ServDorid.web: System boot completed");
				addLog("", "", "",
						"System boot completed. Starting ServDroid.web");
				mAutostartOnBoot = 2;
			} else if (mAutostartOnWifiConnect) {
				mParams = AccessPreferences.getServerParameters();
				Log.d(TAG, "  Autostart ServDorid.web:  Wifi connect");
				addLog("", "", "",
						"Wifi connection stablished. Starting ServDroid.web");
				mAutostartOnWifiConnect = true;
			}
			try {
				if (mParams.getPort() < 1024) {
					if (!ShellCommands.isDeviceRooted()
							|| !ShellCommands.openNatPort(mParams.getPort(),
									DEFAULT_PORT_ON_ROOT)) {
						mLogPort = "" + DEFAULT_PORT_ON_ROOT;
						addLog("", "", "",
								"ERROR opening port " + mParams.getPort());
						Log.d(TAG, "ERROR opening port " + mParams.getPort());
						mCurrentPort = 8080;
						mLogPort = "" + mCurrentPort;
					} else {
						mCurrentPort = DEFAULT_PORT_ON_ROOT;
						mLogPort = mLogPort + " / " + DEFAULT_PORT_ON_ROOT;
					}

				}
				mServerSocket = new ServerSocket(mCurrentPort,
						mParams.getMaxClients());
				Message m = new Message();

				m.what = ServerService.SERVER_STARTED_IDENTIFIER;
				mServiceHandler.sendMessage(m);
				addLog("", "", "", "ServDroid.web server running on port: "
						+ mLogPort + " | WWW path: " + mParams.getWwwPath()
						+ " | Error path: " + mParams.getErrorPath()
						+ " | Max clients: " + mParams.getMaxClients()
						+ " | File indexing: " + mParams.isFileIndexing());
				Log.d(TAG, "ServDroid.web server running on port " + mLogPort);
			} catch (IOException e) {
				if (mRun) {
					Log.e(TAG, "Error accepting connections: ", e);
				}
				addLog("", "", "",
						"ERROR starting server ServDroid.web on port "
								+ mLogPort);
				Message m = new Message();
				m.what = ServerService.SERVER_STOPED_IDENTIFIER;
				mServiceHandler.sendMessage(m);
				// Toast.makeText(ServerService.this,
				// R.string.error_starting_process,
				// Toast.LENGTH_LONG).show();

				return;
			}

			while (mRun) {
				Socket socket;
				try {
					socket = mServerSocket.accept();
				} catch (IOException e1) {
					if (mRun) {
						addLog("", "", "",
								"Warning! One connection has been droped! "
										+ mParams.getPort());
					}
					return;
				}
				// Log.d(TAG, "New connection accepted " +
				// socket.getInetAddress()
				// + ":" + socket.getPort());

				try {
					HttpRequestHandler request = new HttpRequestHandler(socket,
							mVersion);
					Thread thread = new Thread(request);

					thread.start();

				} catch (Exception e) {
					// Log.e(TAG, "ERROR handing request: " + e.getMessage());
					return;
				}
				if (mVibrate) {
					Message m = new Message();
					m.what = ServerService.VIBRATE_IDENTIFIER;
					mServiceHandler.sendMessage(m);
				}

			}
		}
	}
}
