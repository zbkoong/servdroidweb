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

import org.servDroid.Preference.AccessPreferences;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class AutoStartReceiver extends BroadcastReceiver {

	public static final String TAG = "ServDroid";

	@Override
	public void onReceive(Context context, Intent intent) {
		AccessPreferences.setContext(context);
		//Log.d(TAG, " " + intent.getAction());
		if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
			if (!AccessPreferences.isAutostartBootEnabled()) {
				return;
			}
			startService(context);
		} else if (intent.getAction().equals(
				ConnectivityManager.CONNECTIVITY_ACTION)) {

			ConnectivityManager connectivityManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo activeNetInfo = connectivityManager
					.getActiveNetworkInfo();
			// NetworkInfo mobNetInfo = connectivityManager
			// .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
			if (activeNetInfo != null
					&& activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI
					&& AccessPreferences.isAutostartWifiEnabled()) {
				startService(context);
				ServerService.notifyConnectedToWifi();

			}
			// if (mobNetInfo != null) {
			// Toast.makeText(context,
			// "Mobile Network Type : " + mobNetInfo.getTypeName(),
			// Toast.LENGTH_SHORT).show();
			// }
		}

	}

	private void startService(Context context) {
		ServerService.notifySystemBoot();
		Intent serviceIntent = new Intent(
				"org.servDroid.server.service.ServiceController");

		context.startService(serviceIntent);
	}
}
