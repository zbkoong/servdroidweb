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
package org.servDroid.preference;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

import org.servDroid.server.service.params.ServerParams;
import org.servDroid.web.R;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;

public class AccessPreferences {

	private static final String TAG = "ServDroid";
	private final static String VERSION_KEY = "version";

	private static Context mContext;
	private static SharedPreferences pref;

	/**
	 * Set the context to be able to access to the preferences.
	 * 
	 * @param context
	 */
	public static void setContext(Context context) {
		if (null == mContext) {
			mContext = context;
			pref = PreferenceManager.getDefaultSharedPreferences(mContext);
		}
	}

	/**
	 * Get the error path through SharedPreferences
	 * 
	 * Where the error document paths (404.html, etc) are stored
	 * 
	 * @return error path
	 */
	public static String getErrorPath() {
		if (null == mContext) {
			return null;
		}

		return pref
				.getString(
						mContext.getResources().getString(
								R.string.pref_error_path_key),
						Environment.getExternalStorageDirectory()
								+ mContext.getResources().getString(
										R.string.default_error_path));

	}

	/**
	 * See vibration is enabled through SharedPreferences
	 * 
	 * @return true if vibrate is enabled, false otherwise
	 */
	public static boolean getVibrate() {
		if (null == mContext) {
			return false;
		}
		SharedPreferences pref = PreferenceManager
				.getDefaultSharedPreferences(mContext);
		return pref.getBoolean(
				mContext.getResources().getString(R.string.pref_vibrate_key),
				false);
	}

	/**
	 * See vibration is enabled through SharedPreferences
	 * 
	 * @return true if vibrate is enabled, false otherwise
	 */
	public static boolean getShowNotification() {
		if (null == mContext) {
			return false;
		}
		SharedPreferences pref = PreferenceManager
				.getDefaultSharedPreferences(mContext);
		return pref.getBoolean(
				mContext.getResources().getString(
						R.string.pref_show_notification_key), true);
	}

	/**
	 * Is file indexing enabled?
	 * 
	 * @return return true if file indexing is enabled, false otherwise
	 */
	public static boolean getFileIndexingEnabled() {
		if (null == mContext) {
			return false;
		}
		return pref.getBoolean(
				mContext.getResources().getString(
						R.string.pref_directory_indexing_key), true);
	}

	/**
	 * Start automatically the server when a wifi is connected?
	 * 
	 * @return return true if the server should start when a wifi is connected,
	 *         false otherwise
	 */
	public static boolean isAutostartWifiEnabled() {
		if (null == mContext) {
			return false;
		}
		return pref.getBoolean(
				mContext.getResources().getString(
						R.string.pref_autostart_wifi_key), false);
	}

	/**
	 * Stop automatically the server when a wifi is disconnected?
	 * 
	 * @return return true if the server should start when a wifi is connected,
	 *         false otherwise
	 */
	public static boolean isAutostopWifiEnabled() {
		if (null == mContext) {
			return false;
		}
		return pref.getBoolean(
				mContext.getResources().getString(
						R.string.pref_autostop_wifi_key), false)
				&& isAutostartWifiEnabled();
	}

	/**
	 * Start automatically the server when a the system start?
	 * 
	 * @return return true if the server should start when a system is started,
	 *         false otherwise
	 */
	public static boolean isAutostartBootEnabled() {
		if (null == mContext) {
			return false;
		}
		return pref.getBoolean(
				mContext.getResources().getString(
						R.string.pref_autostart_boot_key), false);
	}
	
	public static final boolean getShowAds() {
		if (null == mContext) {
			return false;
		}
		SharedPreferences pref = PreferenceManager
				.getDefaultSharedPreferences(mContext);
		return pref.getBoolean(
				mContext.getResources().getString(R.string.pref_show_ads_key),
				false);
	}


	/**
	 * Get server expiration cache for the browsers through SharedPreferences
	 * 
	 * @return the time in minutes
	 */
	public static int getExpirationCacheTime() {
		if (null == mContext) {
			return 30;
		}
		String expirationCache = pref.getString(mContext.getResources()
				.getString(R.string.pref_expiration_cache_key), mContext
				.getResources().getString(R.string.default_expiration_cache));

		try {
			return Integer.parseInt(expirationCache);
		} catch (NumberFormatException e) {
			return 60;
		}

	}

	/**
	 * Get server port through SharedPreferences
	 * 
	 * @return the port number
	 */
	public static int getPort() {
		if (null == mContext) {
			return 8080;
		}
		String port = pref.getString(
				mContext.getResources().getString(R.string.pref_port_key),
				mContext.getResources().getString(R.string.default_port));

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
	public static int getMaxClients() {
		if (null == mContext) {
			return 10;
		}
		String max = pref
				.getString(
						mContext.getResources().getString(
								R.string.pref_max_clients_key),
						mContext.getResources().getString(
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
	public static String getWwwPath() {
		if (null == mContext) {
			return null;
		}
		return pref.getString(
				mContext.getResources().getString(R.string.pref_www_path_key),
				Environment.getExternalStorageDirectory()
						+ mContext.getResources().getString(
								R.string.default_www_path));
	}

	/**
	 * Get the number of entries to be displayed through SharedPreferences
	 * 
	 * @return Number of entries to be displayed
	 */
	public static int getNumLogEntries() {
		if (null == mContext) {
			return 100;
		}
		String logEntries = pref
				.getString(
						mContext.getResources().getString(
								R.string.pref_log_entries_key),
						mContext.getResources().getString(
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
	public static String getLogPath() {
		if (null == mContext) {
			return null;
		}
		return pref.getString(
				mContext.getResources().getString(R.string.pref_log_path_key),
				Environment.getExternalStorageDirectory()
						+ mContext.getResources().getString(
								R.string.default_log_path));

	}

	/**
	 * Get the version using the sharedPreferences. The version String stored in
	 * the sharedPreferences is used to control if the app has been updated, so
	 * don't use to check the version, use instead ServDroid.getVersion()
	 * 
	 * @return The version using the sharedPreferences
	 */
	public static String getVersion() {
		if (null == mContext) {
			return null;
		}
		return pref.getString(VERSION_KEY, "-");
	}

	public static void setVersion(String version) {
		// SharedPreferences settings = getSharedPreferences(getResources()
		// .getString(R.string.pref_release_notes_key), 0);
		SharedPreferences.Editor editor = pref.edit();
		editor.putString(VERSION_KEY,
				mContext.getResources().getString(R.string.version));
		// Commit the edits!
		editor.commit();

	}

	/**
	 * Check if the Log path is available. If the folder does not exists create
	 * one and add a template.
	 * 
	 * @param path
	 *            The log path
	 * @return true if it is available, false otherwise.
	 */
	public static boolean checkLogPath(String path) {
		if (null == mContext) {
			return false;
		}
		if (path == null) {
			path = Environment.getExternalStorageDirectory()
					+ mContext.getResources().getString(
							R.string.default_log_path);
		}

		// if (!path.endsWith("/")) {
		// path = path + "/";
		// }
		if (path.contains("\n")) {
			return false;
		}
		File folder = new File(path);
		if (!folder.exists() || (folder.exists() && !folder.isDirectory())) {
			try {
				folder.mkdir();

			} catch (Exception e) {
				Log.e(TAG, "Error creating folder " + folder.getAbsolutePath(),
						e);
				return false;
			}

		}

		return true;

	}

	/**
	 * Check if the WWW root path is available. If the folder does not exist
	 * create one and add a template (index.html).
	 * 
	 * @param path
	 *            The www path
	 * @return true if it is available, false otherwise.
	 */
	public static boolean checkWwwPath(String path) {
		if (null == mContext) {
			return false;
		}
		if (path == null) {
			path = Environment.getExternalStorageDirectory()
					+ mContext.getResources().getString(
							R.string.default_www_path);
		}
		// if (!path.endsWith("/")) {
		// path = path + "/";
		// }
		if (path.contains("\n")) {
			return false;
		}
		File folder = new File(path);
		boolean exist = folder.exists();
		boolean isDirectory = folder.isDirectory();
		if (!exist || (exist && !isDirectory)) {
			try {
				folder.mkdirs();
				File file = new File(path + "/index.html");
				if (!file.exists()) {
					try {
						// Create file
						FileWriter fstream = new FileWriter(file);
						BufferedWriter out = new BufferedWriter(fstream);
						out.write("<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 4.01//EN\" \"http://www.w3.org/TR/html4/strict.dtd\">"
								+ "<html><head><meta content=\"text/html; charset=UTF8\" http-equiv=\"content-type\"><title>Hello</title></head><body>"
								+ "<div style=\"text-align: center;\"><big><big><big><span style=\"font-weight: bold;\">ServDroid:<br>"
								+ "It works!<br>"
								+ "</span></big></big></big></div>"
								+ "</body></html>");
						// Close the output stream
						out.close();
					} catch (Exception e) {
						Log.e(TAG, "Error: Writing default index.html", e);
						return false;
					}
				}

			} catch (Exception e) {
				Log.e(TAG, "Error creating folder " + folder.getAbsolutePath(),
						e);
				return false;
			}
		}

		return true;

	}

	/**
	 * Check if the Error root path is available. If the folder does not exist
	 * create one and add a template (404.html).
	 * 
	 * @param path
	 *            The error path
	 * @return true if it is available, false otherwise.
	 */
	public static boolean checkErrorPath(String path) {
		if (null == mContext) {
			return false;
		}
		if (path == null) {
			path = Environment.getExternalStorageDirectory()
					+ mContext.getResources().getString(
							R.string.default_error_path);
		}
		// if (!path.endsWith("/")) {
		// path = path + "/";
		// }
		if (path.contains("\n")) {
			return false;
		}
		File folder = new File(path);
		if (!folder.exists() || (folder.exists() && !folder.isDirectory())) {
			try {
				if (!folder.mkdirs()) {
					Log.e(TAG, "ERROR creating th folder: " + path);
				}
				File file = new File(path + "/404.html");
				if (!file.exists()) {
					try {
						// Create file
						FileWriter fstream = new FileWriter(file);
						BufferedWriter out = new BufferedWriter(fstream);
						out.write("<HTML>"
								+ "<HEAD><title>404 Not Found</title>"
								+ "</head><body> <div style=\"text-align: center;\">"
								+ "<big><big><big><span style=\"font-weight: bold;\">"
								+ "<br>ERROR 404: Document not Found<br></span></big></big></big></div>"
								+ "</BODY></HTML>");
						// Close the output stream
						out.close();
					} catch (Exception e) {
						Log.e(TAG, "Error: Writing default index.html", e);
						return false;
					}
				}

			} catch (Exception e) {
				Log.e(TAG, "Error creating folder " + folder.getAbsolutePath(),
						e);
				return false;
			}

		}
		return true;
	}

	/**
	 * Get the {@link ServerParams} object with the stored parameters
	 * 
	 * @return
	 */
	public static ServerParams getServerParameters() {
		if (null == mContext) {
			return null;
		}
		ServerParams params = new ServerParams(getWwwPath(), getErrorPath(),
				getExpirationCacheTime(), getFileIndexingEnabled(), getPort(),
				AccessPreferences.getMaxClients());
		return params;
	}
}
