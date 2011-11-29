package org.servDroid.server.service;

import org.servDroid.ServDroid;
import org.servDroid.Preference.AccessPreferences;
import org.servDroid.server.service.params.ServerParams;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

public class AutoStartReceiver extends BroadcastReceiver {

	public static final String TAG = "ServDroid";

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d(TAG, " " + intent.getAction());
		if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
			AccessPreferences.setContext(context);
			if (!AccessPreferences.getAutostartBootEnabled()) {
				return;
			}
			// if (null == mServiceController) {

			// mConnection = this.new ServerConnection();
			//Log.d(TAG, " Sending atuostart*//*/**!");
			ServerService.setAutostartFlag(true);
			Intent serviceIntent = new Intent(
					"org.servDroid.server.service.ServiceController");
			serviceIntent.putExtra("AutoStart", true);

			context.startService(serviceIntent);
			// context.bindService(mServiceIntent, mConnection,
			// Context.BIND_AUTO_CREATE); // 4
			// mConnect = AccessPreferences.getAutostartBootEnabled();
			// } else {
			// startServer();
			// }
		}

		// if
		// (intent.getAction().equals(WifiManager.NETWORK_STATE_CHANGED_ACTION))
		// {
		// AccessPreferences.setContext(context);
		// if (!AccessPreferences.getAutostartBootEnabled()) {
		// return;
		// }
		// if (null == mServiceController) {
		// mConnection = this.new ServerConnection();
		// mServiceIntent = new Intent(
		// "org.servDroid.server.service.ServiceController");
		// context.startService(mServiceIntent);
		// context.bindService(mServiceIntent, mConnection,
		// Context.BIND_AUTO_CREATE); // 4
		// mConnect = AccessPreferences.getAutostartWifiEnabled();
		// } else {
		// NetworkInfo info = (NetworkInfo) intent
		// .getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
		// if (info.getState().equals(NetworkInfo.State.CONNECTED)) {
		// startServer();
		// } else {
		// stopServer();
		// }
		//
		// }
		// }
	}

	// private class ServerConnection implements ServiceConnection {
	//
	// @Override
	// public void onServiceConnected(ComponentName name, IBinder service) {
	// mServiceController = ServiceController.Stub.asInterface(service);
	// Log.i(TAG, "Connected to ServDroid.web Service");
	// mServiceBinded = true;
	// if (mConnect) {
	// startServer();
	// } else {
	// stopServer();
	// }
	// }
	//
	// @Override
	// public void onServiceDisconnected(ComponentName name) {
	// Log.i(TAG, "disconnected from ServDroid.web Service");
	// mServiceBinded = false;
	//
	// }
	// }
	//
	// private void startServer() {
	// ServerParams params = new ServerParams(AccessPreferences.getWwwPath(),
	// AccessPreferences.getErrorPath(),
	// AccessPreferences.getExpirationCacheTime(),
	// AccessPreferences.getFileIndexingEnabled(),
	// AccessPreferences.getPort(), AccessPreferences.getMaxClients());
	// try {
	// mServiceController.startService(params);
	// } catch (RemoteException e) {
	// e.printStackTrace();
	// }
	// }
	//
	// private void stopServer() {
	// try {
	// mServiceController.stopService();
	// } catch (RemoteException e) {
	// e.printStackTrace();
	// }
	// }

}
