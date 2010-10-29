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
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.StringTokenizer;

import org.servDroid.util.ZipUtils;
import org.servDroid.web.R;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.util.Log;
import android.widget.Toast;

/**
 * 
 * @author Joan Puig Sanz
 * 
 */
public class ManagePreferences extends PreferenceActivity {
	private static final String TAG = "ServDroid";

	private EditTextPreference mPreferencePort;
	private EditTextPreference mPreferenceMaxClients;
	private EditTextPreference mPreferenceWwwPath;
	private EditTextPreference mPreferenceErrorPath;
	private EditTextPreference mPreferenceLogPath;
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

	// Handler for the progress bar
	final Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			
			if (mError== true){
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
			if (total == -2) { //ERROR downloading template
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

		mPreferenceLogEntries = (ListPreference) findPreference(getResources()
				.getString(R.string.pref_log_entries_key));

		mPreferenceAbout = (Preference) findPreference(getResources()
				.getString(R.string.pref_about_key));

		mPreferenceFileIndexingGetTemplate = (Preference) findPreference(getResources()
				.getString(R.string.pref_directory_indexing_get_template_key));

		mPreferenceReleaseNotes = (Preference) findPreference(getResources()
				.getString(R.string.pref_release_notes_key));

		// Check the port
		mPreferencePort
				.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
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

		// We check the path and create it if it does not exist
		mPreferenceWwwPath
				.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

					public boolean onPreferenceChange(Preference preference,
							Object newValue) {

						return checkWwwPath((String) newValue);
					}
				});

		// We check the path and create it if it does not exist
		mPreferenceErrorPath
				.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

					public boolean onPreferenceChange(Preference preference,
							Object newValue) {
						return checkErrorPath((String) newValue);
					}

				});

		// We check the path and create it if it does not exist
		mPreferenceLogPath
				.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

					public boolean onPreferenceChange(Preference preference,
							Object newValue) {
						return checkLogPath((String) newValue);
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

	}

	/**
	 * Check if the WWW root path is available. If the folder does not exist
	 * create one and add a template (index.html).
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
	 * Check if the Log path is available. If the folder does not exists create
	 * one and add a template.
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
	 * create one and add a template (404.html).
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
	 * Show the "download template" dialog
	 */
	private void showDownloadTemplateDialog() {

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(R.string.directory_indexing_question).setTitle(
				R.string.directory_indexing).setIcon(R.drawable.icon)
				.setCancelable(false).setPositiveButton(android.R.string.yes,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {

								if (mProgressDialog != null) {
									mProgressDialog.dismiss();
								}

								mProgressDialog = new ProgressDialog(
										ManagePreferences.this);
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
										getWwwPath());
								progressThread.start();

							}
						}).setNegativeButton(android.R.string.no,
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
				}).setIcon(R.drawable.icon).setNegativeButton(
				android.R.string.no, new DialogInterface.OnClickListener() {
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
				fos = new FileOutputStream(mPath + "/" + localFile);

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
				Message msg2 = mHandler.obtainMessage();
				Bundle b2 = new Bundle();
				b2.putInt("counter", -2);
				msg2.setData(b2);
				mHandler.sendMessage(msg2);
			} catch (IOException e) {

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
			if (!unzip.unzipArchive(new File(getWwwPath()
					+ "/servdroid-file-indexing-template.zip"), new File(
					getWwwPath()))) {
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
						donateDialog();
					}
				}).setNegativeButton(android.R.string.ok, null).setIcon(
				R.drawable.icon);

		ab.show();

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
						}).setTitle(R.string.other_reset).setIcon(
						android.R.drawable.ic_dialog_alert);
		AlertDialog alert = builder.create();
		alert.show();
	}

	private void showReleaseNotesDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(R.string.release_notes_info).setCancelable(true)
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
		mPreferenceFileIndexing.setChecked(true);
		mPreferenceLogEntries.setValue(getResources().getString(
				R.string.default_log_entries));

	}
}
