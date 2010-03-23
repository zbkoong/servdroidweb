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

package org.servDroid.Preference;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

import org.servDroid.web.R;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.util.Log;

/**
 * 
 * @author Joan Puig Sanz
 * 
 */
public class EditPreferences extends PreferenceActivity {
	private static final String TAG = "ServDroid";

	private EditTextPreference mPreferencePort;
	private EditTextPreference mPreferenceMaxClients;
	private EditTextPreference mPreferenceWwwPath;
	private EditTextPreference mPreferenceErrorPath;
	private EditTextPreference mPreferenceLogPath;
	private CheckBoxPreference mPreferenceVibrate;
	private ListPreference mPreferenceLogEntries;
	private Preference mPreferenceResetPref;
	private Preference mPreferenceAbout;

	private SharedPreferences mPreferences;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);

		mPreferences = PreferenceManager.getDefaultSharedPreferences(this);

		mPreferencePort = (EditTextPreference) findPreference(getResources()
				.getString(R.string.pref_port_key));

		mPreferenceMaxClients = (EditTextPreference) findPreference(getResources()
				.getString(R.string.pref_max_clients_key));

		mPreferenceWwwPath = (EditTextPreference) findPreference(getResources()
				.getString(R.string.pref_www_path_key));

		mPreferenceErrorPath = (EditTextPreference) findPreference(getResources()
				.getString(R.string.pref_error_path_key));

		mPreferenceLogPath = (EditTextPreference) findPreference(getResources()
				.getString(R.string.pref_log_path_key));

		mPreferenceResetPref = (Preference) findPreference(getResources()
				.getString(R.string.pref_reset_config_key));

		mPreferenceVibrate = (CheckBoxPreference) findPreference(getResources()
				.getString(R.string.pref_vibrate_key));

		mPreferenceLogEntries = (ListPreference) findPreference(getResources()
				.getString(R.string.pref_log_entries_key));

		mPreferenceAbout = (Preference) findPreference(getResources()
				.getString(R.string.pref_about_key));

		// Check the port
		mPreferencePort
				.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
					@Override
					public boolean onPreferenceChange(Preference preference,
							Object newValue) {

						try {
							int port = Integer.parseInt((String) newValue);
							if (port >= 65535 | port < 1024) {
								return false;
							}

						} catch (NumberFormatException e) {
							return false;
						}

						return true;
					}
				});

		mPreferenceMaxClients
				.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

					@Override
					public boolean onPreferenceChange(Preference preference,
							Object newValue) {

						try {
							Integer.parseInt((String) newValue);

						} catch (NumberFormatException e) {
							return false;
						}

						return true;
					}
				});

		// We check the path and create it if it does not exist
		mPreferenceWwwPath
				.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

					@Override
					public boolean onPreferenceChange(Preference preference,
							Object newValue) {

						return checkWwwPath((String) newValue);
					}
				});

		// We check the path and create it if it does not exist
		mPreferenceErrorPath
				.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

					@Override
					public boolean onPreferenceChange(Preference preference,
							Object newValue) {
						return checkErrorPath((String) newValue);
					}

				});

		// We check the path and create it if it does not exist
		mPreferenceLogPath
				.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

					@Override
					public boolean onPreferenceChange(Preference preference,
							Object newValue) {
						return checkLogPath((String) newValue);
					}
				});

		mPreferenceResetPref
				.setOnPreferenceClickListener(new OnPreferenceClickListener() {

					@Override
					public boolean onPreferenceClick(Preference preference) {

						showResetDialog();

						return true;
					}
				});

		mPreferenceAbout
				.setOnPreferenceClickListener(new OnPreferenceClickListener() {

					@Override
					public boolean onPreferenceClick(Preference preference) {

						showAboutDialog();

						return true;
					}
				});

	}

	/**
	 * Check if the WWW root path is available. If the folder does not exist
	 * create a template (index.html).
	 * 
	 * @param path
	 *            The www path
	 * @return true if it is available, false otherwise.
	 */
	private boolean checkWwwPath(String path) {
		if (path == null) {
			return false;
		}
		// if (!path.endsWith("/")) {
		// path = path + "/";
		// }
		if (path.contains("\n")) {
			return false;
		}
		File folder = new File(path);
		if (!folder.exists() | (folder.exists() & folder.isDirectory())) {
			try {
				folder.mkdirs();
				File file = new File(path + "/index.html");
				if (!file.exists()) {
					try {
						// Create file
						FileWriter fstream = new FileWriter(file);
						BufferedWriter out = new BufferedWriter(fstream);
						out
								.write("<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 4.01//EN\" \"http://www.w3.org/TR/html4/strict.dtd\">"
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
	 * Check if the Log path is available. create a template.
	 * 
	 * @param path
	 *            The log path
	 * @return true if it is available, false otherwise.
	 */
	private boolean checkLogPath(String path) {
		if (path == null) {
			return false;
		}
		// if (!path.endsWith("/")) {
		// path = path + "/";
		// }
		if (path.contains("\n")) {
			return false;
		}
		File folder = new File(path);
		if (!folder.exists() | (folder.exists() & folder.isDirectory())) {
			try {
				folder.mkdirs();

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
	 * create a template (404.html).
	 * 
	 * @param path
	 *            The error path
	 * @return true if it is available, false otherwise.
	 */
	private boolean checkErrorPath(String path) {
		if (path == null) {
			return false;
		}
		// if (!path.endsWith("/")) {
		// path = path + "/";
		// }
		if (path.contains("\n")) {
			return false;
		}
		File folder = new File(path);
		if (!folder.exists() | (folder.exists() & folder.isDirectory())) {
			try {
				folder.mkdirs();
				File file = new File(path + "/404.html");
				if (!file.exists()) {
					try {
						// Create file
						FileWriter fstream = new FileWriter(file);
						BufferedWriter out = new BufferedWriter(fstream);
						out
								.write("<HTML>"
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
		return false;

	}

	/**
	 * Show the "About" dialog
	 */
	private void showAboutDialog() {

		AlertDialog.Builder ab = new AlertDialog.Builder(this);
		ab.setMessage(R.string.about_servdroid_web).setTitle(
				R.string.other_about);
		ab.setPositiveButton(android.R.string.ok, null)
				.setIcon(R.drawable.icon);

		ab.show();

	}

	/**
	 * Shoe reset settings dialog
	 */
	private void showResetDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(R.string.reset_configurations_message)
				.setCancelable(false).setPositiveButton(android.R.string.yes,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {

								restorePreferences();

							}
						}).setNegativeButton(android.R.string.no,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								dialog.cancel();
							}
						}).setTitle(R.string.other_reset);
		AlertDialog alert = builder.create();
		alert.show();
	}

	/**
	 * Restore all the preferences and check then
	 */
	private void restorePreferences() {
		SharedPreferences.Editor editor = mPreferences.edit();
		editor.clear();
		editor.commit();
		mPreferencePort
				.setText(getResources().getString(R.string.default_port));
		mPreferenceWwwPath.setText(getResources().getString(
				R.string.default_www_path));
		checkWwwPath(getResources().getString(R.string.default_www_path));
		mPreferenceErrorPath.setText(getResources().getString(
				R.string.default_error_path));
		checkErrorPath(getResources().getString(R.string.default_error_path));
		mPreferenceLogPath.setText(getResources().getString(
				R.string.default_log_path));
		checkLogPath(getResources().getString(R.string.default_log_path));
		mPreferenceMaxClients.setText(getResources().getString(
				R.string.default_max_clients));
		mPreferenceVibrate.setChecked(false);
		mPreferenceLogEntries.setValue(getResources().getString(
				R.string.default_log_entries));

	}
}
