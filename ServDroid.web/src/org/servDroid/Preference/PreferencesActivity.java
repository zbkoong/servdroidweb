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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.StringTokenizer;

import org.servDroid.util.ZipUtils;
import org.servDroid.util.shell.ShellCommands;
import org.servDroid.web.R;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

/**
 * 
 * @author Joan Puig Sanz
 * 
 */
public class PreferencesActivity extends PreferenceActivity {
	private static final String TAG = "ServDroid";

	// v0.2
	private CheckBoxPreference mPreferenceAutostartWifi;
	private CheckBoxPreference mPreferenceAutostopWifi;
	private CheckBoxPreference mPreferenceAutostartBoot;
	private CheckBoxPreference mPreferenceShowNotification;
	private CheckBoxPreference mPreferenceShowAds;
	// /////
	private EditTextPreference mPreferencePort;
	private EditTextPreference mPreferenceMaxClients;
	private EditTextPreference mPreferenceWwwPath;
	private EditTextPreference mPreferenceErrorPath;
	private EditTextPreference mPreferenceLogPath;
	private EditTextPreference mPreferenceExpirationCache;
	private CheckBoxPreference mPreferenceVibrate;
	private CheckBoxPreference mPreferenceFileIndexing;
	private ListPreference mPreferenceLogEntries;
	private Preference mPreferenceResetPref;
	private Preference mPreferenceAbout;
	private Preference mPreferenceFileIndexingGetTemplate;
	private Preference mPreferenceReleaseNotes;

	private ProgressDialog mProgressDialog;

	private SharedPreferences mPreferences;

	private boolean mError = false;

	// ///////////////////////
	// ///////////////////////

	// Handler for the progress bar
	final Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {

			if (mError == true) {
				return;
			}
			int max = msg.getData().getInt("max");
			int total = msg.getData().getInt("counter");
			int message = msg.getData().getInt("message");

			if (max > 0) {
				mProgressDialog.setMax(max);
				// mLogHelper.deleteTableLog();
				// fillData();
				return;
			}
			if (message == 1) {
				mProgressDialog.setMessage(getResources().getString(
						R.string.downloading));
			} else if (message == 2) {
				mProgressDialog.setMessage(getResources().getString(
						R.string.extracting));

			}

			mProgressDialog.setProgress(total);
			if (total == -2) { // ERROR downloading template
				mProgressDialog.dismiss();
				mError = true;
				showErrorDownloadMessage();
			} else if (total == -1) {
				mProgressDialog.dismiss();
				finalizeGetIndexingTemplate();
			}

		}
	};

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
		mPreferenceExpirationCache = (EditTextPreference) findPreference(getResources()
				.getString(R.string.pref_expiration_cache_key));

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

		mPreferenceFileIndexing = (CheckBoxPreference) findPreference(getResources()
				.getString(R.string.pref_vibrate_key));

		mPreferenceAutostartBoot = (CheckBoxPreference) findPreference(getResources()
				.getString(R.string.pref_autostart_boot_key));

		mPreferenceAutostartWifi = (CheckBoxPreference) findPreference(getResources()
				.getString(R.string.pref_autostart_wifi_key));
		mPreferenceAutostopWifi = (CheckBoxPreference) findPreference(getResources()
				.getString(R.string.pref_autostop_wifi_key));

		mPreferenceShowNotification = (CheckBoxPreference) findPreference(getResources()
				.getString(R.string.pref_show_notification_key));
		mPreferenceShowAds = (CheckBoxPreference) findPreference(getResources()
				.getString(R.string.pref_show_ads_key));

		mPreferenceLogEntries = (ListPreference) findPreference(getResources()
				.getString(R.string.pref_log_entries_key));

		mPreferenceAbout = (Preference) findPreference(getResources()
				.getString(R.string.pref_about_key));

		mPreferenceFileIndexingGetTemplate = (Preference) findPreference(getResources()
				.getString(R.string.pref_directory_indexing_get_template_key));

		mPreferenceReleaseNotes = (Preference) findPreference(getResources()
				.getString(R.string.pref_release_notes_key));

		mPreferenceWwwPath.setText(AccessPreferences.getWwwPath());
		mPreferenceErrorPath.setText(AccessPreferences.getErrorPath());
		mPreferenceLogPath.setText(AccessPreferences.getLogPath());

		mPreferenceAutostartWifi
				.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
					public boolean onPreferenceChange(Preference preference,
							Object newValue) {

						if (newValue instanceof Boolean) {
							boolean value = ((Boolean) newValue).booleanValue();
							if (value) {
								mPreferenceAutostartBoot.setEnabled(false);
							} else {
								mPreferenceAutostartBoot.setEnabled(true);
								// mPreferenceAutostopWifi.setChecked(false);
							}
						}
						return true;
					}
				});

		mPreferenceAutostartBoot
				.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
					public boolean onPreferenceChange(Preference preference,
							Object newValue) {

						if (newValue instanceof Boolean) {
							boolean value = ((Boolean) newValue).booleanValue();
							if (value) {
								mPreferenceAutostartWifi.setEnabled(false);
								mPreferenceAutostopWifi.setEnabled(false);
								mPreferenceAutostopWifi.setEnabled(false);
							} else {
								mPreferenceAutostartWifi.setEnabled(true);
							}
						}
						return true;
					}
				});

		// Check the port
		mPreferencePort
				.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
					public boolean onPreferenceChange(Preference preference,
							Object newValue) {

						try {
							int port = Integer.parseInt((String) newValue);
							if (port >= 65535 || port < 1) {// If you are not
															// root, you only
															// can until 1024
								return false;
							}
							if (port > 1 && port < 1024) {// check if it is
															// rooted
								if (ShellCommands.isIptablesExist()) {
									if (!ShellCommands.isDeviceRooted()) {
										Toast.makeText(
												PreferencesActivity.this,
												R.string.no_su_permissions,
												Toast.LENGTH_LONG).show();
										;
										return false;
									}
								} else {
									Toast.makeText(PreferencesActivity.this,
											R.string.no_iptables,
											Toast.LENGTH_LONG).show();
									;
									return false;
								}

							}

						} catch (NumberFormatException e) {
							return false;
						}

						return true;
					}
				});

		// Check the max clients
		mPreferenceMaxClients
				.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

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

		mPreferenceExpirationCache
				.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

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

					public boolean onPreferenceChange(Preference preference,
							Object newValue) {

						return AccessPreferences
								.checkWwwPath((String) newValue);
					}
				});

		// We check the path and create it if it does not exist
		mPreferenceErrorPath
				.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

					public boolean onPreferenceChange(Preference preference,
							Object newValue) {
						return AccessPreferences
								.checkErrorPath((String) newValue);
					}

				});

		// We check the path and create it if it does not exist
		mPreferenceLogPath
				.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

					public boolean onPreferenceChange(Preference preference,
							Object newValue) {
						return AccessPreferences
								.checkLogPath((String) newValue);
					}
				});

		// Reset to default preferences
		mPreferenceResetPref
				.setOnPreferenceClickListener(new OnPreferenceClickListener() {

					public boolean onPreferenceClick(Preference preference) {

						showResetDialog();

						return true;
					}
				});

		mPreferenceAbout
				.setOnPreferenceClickListener(new OnPreferenceClickListener() {

					public boolean onPreferenceClick(Preference preference) {

						showAboutDialog();

						return true;
					}
				});

		mPreferenceFileIndexingGetTemplate
				.setOnPreferenceClickListener(new OnPreferenceClickListener() {

					public boolean onPreferenceClick(Preference preference) {

						showDownloadTemplateDialog();

						return true;
					}
				});

		mPreferenceReleaseNotes
				.setOnPreferenceClickListener(new OnPreferenceClickListener() {

					public boolean onPreferenceClick(Preference preference) {

						showReleaseNotesDialog();

						return true;
					}
				});

		mPreferenceShowAds.setEnabled(false);
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	/**
	 * Show the "download template" dialog
	 */
	private void showDownloadTemplateDialog() {

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(R.string.directory_indexing_question)
				.setTitle(R.string.directory_indexing)
				.setIcon(R.drawable.icon)
				.setCancelable(false)
				.setPositiveButton(android.R.string.yes,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {

								if (mProgressDialog != null) {
									mProgressDialog.dismiss();
								}

								mProgressDialog = new ProgressDialog(
										PreferencesActivity.this);
								mProgressDialog
										.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
								mProgressDialog
										.setTitle(R.string.installing_template);
								mProgressDialog.setCancelable(true);
								mProgressDialog.setMessage(getResources()
										.getString(R.string.connecting));
								mProgressDialog.show();

								// Run thread for saving
								ProgressThread progressThread = new ProgressThread(
										handler,
										"http://servdroidweb.googlecode.com/files/servdroid-file-indexing-template.zip",
										AccessPreferences.getWwwPath());
								progressThread.start();

							}
						})
				.setNegativeButton(android.R.string.no,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								dialog.cancel();
							}
						});
		builder.show();

	}

	/**
	 * Open a web browser to download template
	 */
	private void openWebBrowser() {
		Intent i = new Intent(Intent.ACTION_VIEW);
		Uri u = Uri
				.parse("http://code.google.com/p/servdroidweb/downloads/list");
		i.setData(u);
		startActivity(i);
	}

	private void showErrorDownloadMessage() {
		AlertDialog.Builder ab = new AlertDialog.Builder(this);
		// this.getPackageManager().
		// http://android.hlidskialf.com/blog/code/android-get-version-name

		ab.setMessage(R.string.error_finish_downloading_extracting).setTitle(
				R.string.error);
		ab.setPositiveButton(android.R.string.ok,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						openWebBrowser();
						mError = false;
					}
				})
				.setIcon(R.drawable.icon)
				.setNegativeButton(android.R.string.no,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								dialog.cancel();
								mError = false;

							}
						});

		ab.show();
	}

	private void finalizeGetIndexingTemplate() {
		Toast.makeText(this, R.string.finish_downloading_extracting,
				Toast.LENGTH_LONG).show();

	}

	private class ProgressThread extends Thread {
		Handler mHandler;
		String mUrlFile;
		String mPath;

		ProgressThread(Handler h, String urlFile, String path) {
			mHandler = h;
			mUrlFile = urlFile;
			mPath = path;

		}

		public void run() {

			try {
				URL url = new URL(mUrlFile);
				URLConnection urlC = url.openConnection();

				Message msg = mHandler.obtainMessage();
				Bundle b = new Bundle();
				b.putInt("max", urlC.getContentLength());
				msg.setData(b);
				mHandler.sendMessage(msg);

				// Copy resource to local file, use remote file
				// if no local file name specified
				InputStream is = url.openStream();

				System.out.flush();
				FileOutputStream fos = null;

				String localFile = null;
				// Get only file name
				StringTokenizer st = new StringTokenizer(url.getFile(), "/");
				while (st.hasMoreTokens())
					localFile = st.nextToken();

				// Check if the file exist
				File folder = new File(mPath);
				if (!folder.exists() | (folder.exists() & folder.isDirectory())) {
					folder.mkdir();
				}
				File file = new File(mPath + "/" + localFile);
				if (file.exists()) {
					file.delete();
				}
				// file.createNewFile();
				fos = new FileOutputStream(file);

				Message msg2 = mHandler.obtainMessage();
				Bundle b2 = new Bundle();
				b2.putInt("message", 1);
				msg2.setData(b2);
				mHandler.sendMessage(msg2);

				int oneChar, count = 0;
				while ((oneChar = is.read()) != -1
						&& mProgressDialog.isShowing()) {
					fos.write(oneChar);
					count++;

					if (count % 550 == 0) {
						Message msg3 = mHandler.obtainMessage();
						Bundle b3 = new Bundle();
						b3.putInt("counter", count);
						msg3.setData(b3);
						mHandler.sendMessage(msg3);
					}
				}
				is.close();
				fos.close();
			} catch (MalformedURLException e) {
				Log.e(TAG, e.getMessage());

				Message msg2 = mHandler.obtainMessage();
				Bundle b2 = new Bundle();
				b2.putInt("counter", -2);
				msg2.setData(b2);
				mHandler.sendMessage(msg2);
			} catch (IOException e) {
				Log.e(TAG, e.getMessage());

				Message msg2 = mHandler.obtainMessage();
				Bundle b2 = new Bundle();
				b2.putInt("counter", -2);
				msg2.setData(b2);
				mHandler.sendMessage(msg2);
			}

			Message msg = mHandler.obtainMessage();
			Bundle b = new Bundle();
			b.putInt("message", 2);
			msg.setData(b);
			mHandler.sendMessage(msg);

			ZipUtils unzip = new ZipUtils();
			if (!unzip.unzipArchive(new File(AccessPreferences.getWwwPath()
					+ "/servdroid-file-indexing-template.zip"), new File(
					AccessPreferences.getWwwPath()))) {
				Message msg2 = mHandler.obtainMessage();
				Bundle b2 = new Bundle();
				b2.putInt("counter", -2);
				msg2.setData(b2);
				mHandler.sendMessage(msg2);
			}

			Message msg2 = mHandler.obtainMessage();
			Bundle b2 = new Bundle();
			b2.putInt("counter", -1);
			msg2.setData(b2);
			mHandler.sendMessage(msg2);

		}
	}

	/**
	 * Show the "About" dialog
	 */
	private void showAboutDialog() {

		AlertDialog.Builder ab = new AlertDialog.Builder(this);
		// this.getPackageManager().
		// http://android.hlidskialf.com/blog/code/android-get-version-name
		String message = this.getResources().getString(R.string.app_name)
				+ " v" + this.getResources().getString(R.string.version)
				+ "\n\n"
				+ this.getResources().getString(R.string.about_servdroid_web);
		ab.setMessage(message).setTitle(R.string.other_about);
		ab.setPositiveButton(R.string.donate,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						ShowDonateDialog();
					}
				}).setNegativeButton(android.R.string.ok, null)
				.setIcon(R.drawable.icon);

		ab.show();

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
	 * Open a web browser to see if ServDroid is working
	 */
	private void openWebBrowser(String url) {
		Intent i = new Intent(Intent.ACTION_VIEW);
		Uri u = Uri.parse(url);
		i.setData(u);
		startActivity(i);
	}

	/**
	 * Show reset settings dialog
	 */
	private void showResetDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(R.string.reset_configurations_message)
				.setCancelable(false)
				.setPositiveButton(android.R.string.yes,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {

								restorePreferences();

							}
						})
				.setNegativeButton(android.R.string.no,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								dialog.cancel();
							}
						}).setTitle(R.string.other_reset)
				.setIcon(android.R.drawable.ic_dialog_alert);
		AlertDialog alert = builder.create();
		alert.show();
	}

	private void showReleaseNotesDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(R.string.release_notes_info)
				.setCancelable(true)
				.setNeutralButton(android.R.string.ok,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								dialog.dismiss();
							}
						});
		builder.setIcon(android.R.drawable.ic_dialog_info);
		builder.create();
		builder.setTitle(R.string.release_notes);
		builder.show();
	}

	/**
	 * Restore all the preferences and check them
	 */
	private void restorePreferences() {
		SharedPreferences.Editor editor = mPreferences.edit();
		editor.clear();
		editor.commit();
		mPreferencePort
				.setText(getResources().getString(R.string.default_port));
		mPreferenceWwwPath.setText(Environment.getExternalStorageDirectory()
				+ getResources().getString(R.string.default_www_path));
		AccessPreferences.checkWwwPath(Environment
				.getExternalStorageDirectory()
				+ getResources().getString(R.string.default_www_path));
		mPreferenceErrorPath.setText(Environment.getExternalStorageDirectory()
				+ getResources().getString(R.string.default_error_path));
		AccessPreferences.checkErrorPath(Environment
				.getExternalStorageDirectory()
				+ getResources().getString(R.string.default_error_path));
		mPreferenceLogPath.setText(Environment.getExternalStorageDirectory()
				+ getResources().getString(R.string.default_log_path));
		AccessPreferences.checkLogPath(Environment
				.getExternalStorageDirectory()
				+ getResources().getString(R.string.default_log_path));
		mPreferenceMaxClients.setText(getResources().getString(
				R.string.default_max_clients));
		mPreferenceExpirationCache.setText(getResources().getString(
				R.string.default_expiration_cache));
		mPreferenceVibrate.setChecked(false);
		mPreferenceFileIndexing.setChecked(true);
		mPreferenceLogEntries.setValue(getResources().getString(
				R.string.default_log_entries));
		// v0.2
		AccessPreferences
				.setVersion(getResources().getString(R.string.version));
		mPreferenceAutostartBoot.setChecked(false);
		mPreferenceAutostartWifi.setChecked(false);
		mPreferenceAutostopWifi.setChecked(false);
		mPreferenceAutostartBoot.setEnabled(true);
		mPreferenceAutostartWifi.setEnabled(true);
		mPreferenceShowNotification.setChecked(true);
		// mPreferenceAutostopWifi.setEnabled(false);

	}

	// //////////////////////////////////////////
	// //////////////////////////////////////////
	// //////////////////////////////////////////
	// //////////////////////////////////////////
	// //////////////////////////////////////////

}
