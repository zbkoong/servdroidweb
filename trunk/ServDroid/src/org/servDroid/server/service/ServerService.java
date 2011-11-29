package org.servDroid.server.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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
import org.servDroid.web.R;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.os.Vibrator;
import android.util.Log;

public class ServerService extends Service {

	private static final String TAG = ServDroid.TAG;
	private static String mVersion;
	private static final int START_NOTIFICATION_ID = 1;

	private static final int VIBRATE_IDENTIFIER = 0x102;
	private static final int SERVER_STARTED_IDENTIFIER = 0x102 + 1;
	private static final int SERVER_STOPED_IDENTIFIER = 0x102 + 2;

	/** The Server is running */
	public static final int STATUS_RUNNING = 5435;
	/** The Server is stopped */
	public static final int STATUS_STOPED = 5435 + 1;
	// public static final int STATUS_SECURITY_ERROR = 5435 + 2;
	// public static final int STATUS_UNKNOW = 5435 + 3;

	// public static final String SERVICE_SHARED_KEY =
	// "org.servDroid.server.service.sharedKey";
	// private String mSharedKey;

	private static ServerParams mParams;
	private static ServerSocket mServerSocket;

	private static MainServerThread mServerThread;

	private static LogAdapter mLogAdapter;

	private volatile boolean mVibrate;
	private volatile static int mAutostartOnBoot = 0;

	private static NotificationManager mNotificationManager;

	final Handler mServiceHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case VIBRATE_IDENTIFIER:
				if (mVibrate) {
					((Vibrator) getSystemService(Context.VIBRATOR_SERVICE))
							.vibrate(300);
				}
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

	public static void setAutostartFlag(boolean flag) {
		//Log.d(TAG, "  ========== Autostart seted!");
		if (mAutostartOnBoot == 0 && flag) {
			mAutostartOnBoot = 1;
		}
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
			// String sharedKey = null;

			// @Override
			// public void setSecurityKey(String sharedKey) throws
			// RemoteException {
			// this.sharedKey = sharedKey;
			// }

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
				stopServer();
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
				return addLog(msg);
			}

			@Override
			public List<LogMessage> getLogList(int n) throws RemoteException {
				return mLogAdapter.fetchLogList(n);
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
				+ mParams.getPort();
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
		if (null == mLogAdapter) {
			mLogAdapter = LogAdapter.initializeInstance(this);
		}
		mVersion = this.getResources().getString(R.string.version);
		if (mAutostartOnBoot == 1) {
			mParams = AccessPreferences.getServerParameters();
			Log.d(TAG, "  Autostart ServDorid.web");
			addLog("", "", "", "System boot completed. Starting ServDroid.web");
			startServer();
//			Log.d(TAG, "  ========== Autostarting the server!!!!!!!!!!!");
//			Log.d(TAG, "  ========== Autostarting the server!!!!!!!!!!!  "
//					+ startServer());
		}

		super.onCreate();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.i("LocalService", "Received start id " + startId + ": " + intent);
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
		}
		return false;

	}

	private boolean stopServer() {
		clearRunningNotification();
		if (null == mServerThread) {
			addLog("", "ERROR stopping ServDroid.web server ", "", "");
			return false;
		}
		if (mServerThread.isAlive()) {
			mServerThread.stopThread();
			mServerThread = null;
			addLog("", "ServDroid.web server stoped ", "", "");
			return true;
		}
		addLog("", "ERROR stopping ServDroid.web server ", "", "");
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

	public static void addLog(LogMessage msg) {
		if (null == mLogAdapter) {
			return;
		}
		mLogAdapter.addLog(msg);
	}

	/**
	 * Get the current server {@link ServerParams}
	 * 
	 * @return the current server {@link ServerParams}
	 */
	public static ServerParams getServerParams() {
		return mParams;
	}

	private String runCommand(String cmd, boolean readOutput) {
		try {
			// Executes the command.
			Process exec = Runtime.getRuntime().exec(cmd);

			String out = null;
			StringBuffer output = null;
			if (readOutput) {
				// Reads stdout.
				// NOTE: You can write to stdin of the command using
				// process.getOutputStream().
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(exec.getInputStream()));
				int read;
				char[] buffer = new char[4096];
				output = new StringBuffer();
				while ((read = reader.read(buffer)) > 0) {
					output.append(buffer, 0, read);
				}
				reader.close();

			}
			// Waits for the command to finish.
			exec.waitFor();
			if (null != output) {
				out = output.toString();
			}

			return out;
		} catch (IOException e) {
			throw new RuntimeException(e);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	private boolean openNatPort() {
		Log.d(TAG, "Comand  su:   " + runCommand("su -c id", true));
		Log.d(TAG, "Comand  su:   " + runCommand("id", true));

		return true;

	}

	// ///////////////////////////////////////
	// ///////////////////////////////////////
	// ///////////////////////////////////////

	/**
	 * Private class for the server thread
	 */
	private class MainServerThread extends Thread {

		private volatile boolean run;

		public MainServerThread() {
			run = true;
		}

		public synchronized void stopThread() {
			if (run == false) {
				return;
			}
			run = false;
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
				if (mParams.getPort() < 1024) {
					openNatPort();
				}
				mServerSocket = new ServerSocket(mParams.getPort(),
						mParams.getMaxClients());
				Message m = new Message();
				m.what = ServerService.SERVER_STARTED_IDENTIFIER;
				mServiceHandler.sendMessage(m);
				addLog("", "", "", "ServDroid.web server running on port "
						+ mParams.getPort());
				Log.d(TAG,
						"ServDroid.web server running on port "
								+ mParams.getPort());
			} catch (IOException e) {
				if (run) {
					Log.e(TAG, "Error accepting connections: ", e);
				}
				addLog("", " ERROR starting the server ServDroid.web on port "
						+ mParams.getPort(), "", "");
				Message m = new Message();
				m.what = ServerService.SERVER_STOPED_IDENTIFIER;
				mServiceHandler.sendMessage(m);
				// Toast.makeText(ServerService.this,
				// R.string.error_starting_process,
				// Toast.LENGTH_LONG).show();

				return;
			}

			while (run) {
				Socket socket;
				try {
					socket = mServerSocket.accept();
				} catch (IOException e1) {
					if (run) {
						addLog("", " Warning! One connection has been droped! "
								+ mParams.getPort(), "", "");
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

				Message m = new Message();
				m.what = ServerService.VIBRATE_IDENTIFIER;
				mServiceHandler.sendMessage(m);

			}
		}
	}
}
