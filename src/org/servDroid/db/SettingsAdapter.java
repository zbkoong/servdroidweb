package org.servDroid.db;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

public class SettingsAdapter extends ServdroidDbAdapter {

	public SettingsAdapter(Context ctx) {
		super(ctx);
		if (mDbHelper == null | mDb == null) {
			open();
		}
		initializeSettings();
	}

	/**
	 * Write the configurations if it does not exist
	 */
	public void initializeSettings() {
		// mDb.execSQL("DELETE FROM " + DATABASE_TABLE_SETTINGS);
		Cursor c = mDb.query(true, DATABASE_TABLE_SETTINGS, new String[] {
				KEY_ROWID, KEY_SETTINGS_PARAM, KEY_VALUE }, null, null, null,
				null, null, null);

		if (c.getCount() < NUM_SETTINGS_PARAMETERS) {
			setDefaultSettings();
			Log.d(TAG, "Settings inicialized");
		}

	}

	/**
	 * Posa els valors per defecte en la taula de les preferencies
	 * 
	 * 
	 */
	public void setDefaultSettings() {
		// Purge configurations
		mDb.execSQL("DELETE FROM " + DATABASE_TABLE_SETTINGS);

		setCookies(null);
		setMaxClients(null);
		setPassword(null);
		setPort(null);
		setWwwPath(null);
		setErrorPage(null);
		setLogPath(null);
		enableBlackList(false);
		enableWhiteList(false);
		enableVibrate(false);
	}

	/**
	 * Afegir un valor a la llista de les preferencies
	 * 
	 * @param settingParam
	 *            paramentra a afegir (port, vibracio, wwwPath, etc)
	 * @param value
	 *            Valor del paramentre afegit
	 * @return The row ID of the newly inserted row, or -1 if an error occurred.
	 */
	public double addSettingsValue(String settingParam, String value) {
		ContentValues args = new ContentValues();
		args.put(KEY_SETTINGS_PARAM, settingParam);
		args.put(KEY_VALUE, value);

		long id = mDb.insert(DATABASE_TABLE_SETTINGS, null, args);

		Log.d(TAG, "Inserted in Settings table the value " + value
				+ " for parameter " + settingParam);
		return id;
	}

	/**
	 * Actualitzar un valor a la llista de les preferencies
	 * 
	 * @param settingParam
	 *            paramentra a afegir (port, vibracio, wwwPath, etc)
	 * @param value
	 *            Valor del paramentre afegit
	 * @return The row ID of the newly inserted row, or -1 if an error occurred.
	 */
	public double updateSettingsValue(String settingParam, String value) {
		ContentValues args = new ContentValues();
		args.put(KEY_VALUE, value);

		long id = mDb.update(DATABASE_TABLE_SETTINGS, args, KEY_SETTINGS_PARAM
				+ "= \"" + settingParam + "\"", null);
		Log.d(TAG, "Inserted in Settings table the value " + value
				+ " for parameter " + settingParam);
		return id;
	}

	/**
	 * Comprovar si el paramentre de les preferencies existeix en la taula
	 * 
	 * @param settingParam
	 *            paramentra a afegir (port, vibracio, wwwPath, etc)
	 * @return true si existeix, false en cas contrari
	 */
	public boolean checkIfExistSettingParam(String settingParam) {
		Cursor mCursor = mDb.query(true, DATABASE_TABLE_SETTINGS, new String[] {
				KEY_SETTINGS_PARAM, KEY_VALUE }, KEY_SETTINGS_PARAM + "= \""
				+ settingParam + "\"", null, null, null, null, null);
		return mCursor.getCount() != 0;

	}

	/**
	 * Comprovar si el paramentre de les preferencies existeix en la taula
	 * 
	 * @param host
	 *            paramentra a afegir (port, vibracio, wwwPath, etc)
	 * @return true si existeix, false en cas contrari
	 */
	public boolean checkListIfExistHost(String host) {
		Cursor mCursor = mDb.query(true, DATABASE_TABLE_SETTINGS, new String[] {
				KEY_SETTINGS_PARAM, KEY_VALUE }, KEY_VALUE + "= \"" + host
				+ "\"", null, null, null, null, null);
		return mCursor.getCount() != 0;

	}

	/**
	 * Set listen port for server
	 * 
	 * @param port
	 *            The listener port, null if want the default port
	 * @return true if the port was successfully updated, false otherwise
	 */
	public boolean setPort(String port) {
		if (port == null) {
			port = "8080";
		}
		if (checkIfExistSettingParam(STRING_PORT)) {
			return updateSettingsValue(STRING_PORT, port) > 0;
		} else {
			return addSettingsValue(STRING_PORT, port) > 0;
		}
	}

	/**
	 * Set max clients for server
	 * 
	 * @param maxClients
	 *            Max clients number, null if want the default value
	 * @return true if the max clients was successfully updated, false otherwise
	 */
	public boolean setMaxClients(String maxClients) {
		if (maxClients == null) {
			maxClients = "10";
		}
		if (checkIfExistSettingParam(STRING_MAX_CLIENTS)) {
			return updateSettingsValue(STRING_MAX_CLIENTS, maxClients) > 0;
		} else {
			return addSettingsValue(STRING_MAX_CLIENTS, maxClients) > 0;
		}
	}

	/**
	 * Set cookies for server
	 * 
	 * @param cookies
	 *            Cookie and its value, null if want to disable cookies.
	 * @return true if cookies was successfully updated, false otherwise
	 */
	public boolean setCookies(String cookies) {
		if (cookies == null) {
			cookies = "-1";
			enableCookies(false);
		}
		if (checkIfExistSettingParam(STRING_COOKIES)) {
			return updateSettingsValue(STRING_COOKIES, cookies) > 0;
		} else {
			return addSettingsValue(STRING_COOKIES, cookies) > 0;
		}
	}

	/**
	 * Enable cookies
	 * 
	 * @param cookies
	 *            true if want cookies, false otherwise.
	 * @return true if value was successfully updated, false otherwise
	 */
	public boolean enableCookies(boolean enable) {
		String value;
		if (enable) {
			value = "true";
		} else {
			value = "false";
		}
		if (checkIfExistSettingParam(STRING_ENABLE_COOKIES)) {
			return updateSettingsValue(STRING_ENABLE_COOKIES, value) > 0;
		} else {
			return addSettingsValue(STRING_ENABLE_COOKIES, value) > 0;
		}
	}

	/**
	 * enable vibrate
	 * 
	 * @param cookies
	 *            true if want vibrate, false otherwise.
	 * @return true if value was successfully updated, false otherwise
	 */
	public boolean enableVibrate(boolean enable) {
		String value;
		if (enable) {
			value = "true";
		} else {
			value = "false";
		}
		if (checkIfExistSettingParam(STRING_VIBRATE)) {
			return updateSettingsValue(STRING_VIBRATE, value) > 0;
		} else {
			return addSettingsValue(STRING_VIBRATE, value) > 0;
		}
	}

	/**
	 * enable black list
	 * 
	 * @param cookies
	 *            true if want enable black list, false otherwise. If white list
	 *            is enabled, this function will disable it.
	 * @return true if value was successfully updated, false otherwise
	 */
	public boolean enableBlackList(boolean enable) {
		String value;
		if (enable) {
			value = "true";
			enableWhiteList(false);
		} else {
			value = "false";
		}
		if (checkIfExistSettingParam(STRING_ENABLE_BLACK_LIST)) {
			return updateSettingsValue(STRING_ENABLE_BLACK_LIST, value) > 0;
		} else {
			return addSettingsValue(STRING_ENABLE_BLACK_LIST, value) > 0;
		}
	}

	/**
	 * enable white list
	 * 
	 * @param cookies
	 *            true if want enable white list, false otherwise. If black list
	 *            is enabled, this function will disable it. Set null if want to
	 *            restore the default value.
	 * @return true if value was successfully updated, false otherwise
	 */
	public boolean enableWhiteList(boolean enable) {
		String value;
		if (enable) {
			value = "true";
			enableBlackList(false);
		} else {
			value = "false";
		}
		if (checkIfExistSettingParam(STRING_ENABLE_WHITE_LIST)) {
			return updateSettingsValue(STRING_ENABLE_WHITE_LIST, value) > 0;
		} else {
			return addSettingsValue(STRING_ENABLE_WHITE_LIST, value) > 0;
		}
	}

	/**
	 * Afegir un host a la black list
	 * 
	 * @param host
	 * @return true correcte, flase en cas contrari
	 */
	public boolean addHostBlackList(String host) {
		if (host == null) {
			return false;
		}
		// Che if IP exist
		Cursor cursor = mDb.query(true, DATABASE_TABLE_BLACK_LIST,
				new String[] { KEY_ROWID, KEY_HOSTS }, KEY_HOSTS + "= \""
						+ host + "\"", null, null, null, null, null);
		if (cursor.getCount() != 0) {
			Log.d(TAG, "The host " + host + " alredy exist in the black list");
			return true;

		}

		// ADD IP
		ContentValues args = new ContentValues();
		args.put(KEY_HOSTS, host);

		long id = mDb.insert(DATABASE_TABLE_BLACK_LIST, null, args);

		Log.d(TAG, "Inserted the host " + host + " in to the black list");
		return id > 0;

	}

	/**
	 * Elimina un host de la black list
	 * 
	 * @param host
	 * @return
	 */
	public boolean deleteHostFromBlackList(String host) {

		return mDb.delete(DATABASE_TABLE_BLACK_LIST, KEY_HOSTS + "= \"" + host
				+ "\"", null) > 0;
	}

	/**
	 * Afegir un host a la white list
	 * 
	 * @param host
	 * @return true correcte, flase en cas contrari
	 */
	public boolean addHostWhiteList(String host) {
		if (host == null) {
			return false;
		}
		// Che if IP exist
		Cursor cursor = mDb.query(true, DATABASE_TABLE_WHITE_LIST,
				new String[] { KEY_ROWID, KEY_HOSTS }, KEY_HOSTS + "= \""
						+ host + "\"", null, null, null, null, null);
		if (cursor.getCount() != 0) {
			Log.d(TAG, "The host " + host + " alredy exist in the white list");
			return true;

		}

		// ADD IP
		ContentValues args = new ContentValues();
		args.put(KEY_HOSTS, host);

		long id = mDb.insert(DATABASE_TABLE_WHITE_LIST, null, args);

		Log.d(TAG, "Inserted the host " + host + " in to the white list");
		return id > 0;

	}

	/**
	 * Elimina un host de la white list
	 * 
	 * @param host
	 * @return
	 */
	public boolean deleteHostFromWhiteList(String host) {

		return mDb.delete(DATABASE_TABLE_WHITE_LIST, KEY_HOSTS + "= \"" + host
				+ "\"", null) > 0;
	}

	/**
	 * Return a Cursor over the list of all IPs in the list
	 * 
	 * @return Cursor over all notes
	 */
	public Cursor fetchAllHostInList(String tableList) {

		return mDb.query(tableList, new String[] { KEY_ROWID, KEY_HOSTS },
				null, null, null, null, null, null);
	}

	public Cursor fetchAllBlackListHosts() {
		return fetchAllHostInList(DATABASE_TABLE_BLACK_LIST);
	}

	public Cursor fetchAllWhiteListHosts() {
		return fetchAllHostInList(DATABASE_TABLE_WHITE_LIST);
	}

	/**
	 * Set error page
	 * 
	 * @param path
	 *            path were document is, null if want the default error page,
	 *            null if want the default value
	 * @return true if path was successfully updated, false otherwise
	 */
	public boolean setErrorPage(String path) {
		if (path == null) {
			path = DEFAULT_ERROR_PATH;
		}
		if (!path.endsWith("/")) {
			path = path + "/";
		}
		if (path.contains("\n")) {
			path = DEFAULT_ERROR_PATH;
		}
		File folder = new File(path);
		if (!folder.exists() |(folder.exists() & folder.isDirectory())) {
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
					}
				}

			} catch (Exception e) {
				Log.d(TAG, "Error creating folder " + folder.getAbsolutePath());
			}

		}
		if (checkIfExistSettingParam(STRING_ERROR_PAGE)) {
			return updateSettingsValue(STRING_ERROR_PAGE, path) > 0;
		} else {
			return addSettingsValue(STRING_ERROR_PAGE, path) > 0;
		}
	}

	public boolean setLogPath(String path) {
		if (path == null) {
			path = DEFAULT_LOG_PATH;
		}
		if (!path.endsWith("/")) {
			path = path + "/";
		}
		if (path.contains("\n")) {
			path = DEFAULT_LOG_PATH;
		}
		File folder = new File(path);
		if (!folder.exists() |(folder.exists() & folder.isDirectory())) {
			try {
				folder.mkdirs();

			} catch (Exception e) {
				Log.d(TAG, "Error creating folder " + folder.getAbsolutePath());
			}

		}
		if (checkIfExistSettingParam(STRING_LOG_PATH)) {
			return updateSettingsValue(STRING_LOG_PATH, path) > 0;
		} else {
			return addSettingsValue(STRING_LOG_PATH, path) > 0;
		}
	}

	/**
	 * Set WWW path (root directory for server)
	 * 
	 * @param path
	 *            path were folder is, null if want the default value
	 * @return true if path was successfully updated, false otherwise
	 */
	public boolean setWwwPath(String path) {
		if (path == null) {
			path = DEFAULT_WWW_PATH;
		}
		if (!path.endsWith("/")) {
			path = path + "/";
		}
		if (path.contains("\n")) {
			path = DEFAULT_WWW_PATH;
		}
		File folder = new File(path);
		if (!folder.exists() |(folder.exists() & folder.isDirectory())) {
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
					} catch (Exception e) {// Catch exception if any
						Log.e(TAG, "Error: Writing default index.html", e);
					}
				}

			} catch (Exception e) {
				Log.d(TAG, "Error creating folder " + folder.getAbsolutePath());
			}

		}
		if (checkIfExistSettingParam(STRING_WWW_PATH)) {
			return updateSettingsValue(STRING_WWW_PATH, path) > 0;
		} else {
			return addSettingsValue(STRING_WWW_PATH, path) > 0;
		}
	}

	/**
	 * Enable password
	 * 
	 * @param cookies
	 *            true if want enable password acces, false otherwise. Set null
	 *            if want to restore the default value.
	 * @return true if value was successfully updated, false otherwise
	 */
	public boolean enablePassword(Boolean enable) {
		String value;
		if (enable == null) {
			enable = false;
		}
		if (enable) {
			value = "true";
			enableBlackList(false);
		} else {
			value = "false";
		}
		if (checkIfExistSettingParam(STRING_ENABLE_PASSWORD)) {
			return updateSettingsValue(STRING_ENABLE_PASSWORD, value) > 0;
		} else {
			return addSettingsValue(STRING_ENABLE_PASSWORD, value) > 0;
		}
	}

	/**
	 * Set password for access to the server
	 * 
	 * @param password
	 *            The password to access to server. null if want default values.
	 * @return true if cookies was successfully updated, false otherwise
	 */
	public boolean setPassword(String password) {

		if (password == null) {
			password = "-1";
			enablePassword(false);
		}
		if (checkIfExistSettingParam(STRING_PASSWORD)) {
			return updateSettingsValue(STRING_PASSWORD, password) > 0;
		} else {
			return addSettingsValue(STRING_PASSWORD, password) > 0;
		}
	}

	/**
	 * Get server port
	 * 
	 * @return port
	 */
	public int getPort() {

		String mPort = getSettingsValue(STRING_PORT);
		try {
			int mPort_ = Integer.parseInt(mPort);
			return mPort_;
		} catch (NumberFormatException e) {
			setPort("8080");
			return 8080;
		}

	}

	/**
	 * Get the maximum clients available for server.
	 * 
	 * @return port
	 */
	public int getMaxClients() {

		String maxClients = getSettingsValue(STRING_MAX_CLIENTS);
		if (maxClients == null) {
			setMaxClients(null);
			return getMaxClients();
		}
		try {
			int mMaxClients_ = Integer.parseInt(maxClients);
			return mMaxClients_;
		} catch (NumberFormatException e) {
			setMaxClients(null);
			return 10;
		}

	}

	/**
	 * Get server cookies.
	 * 
	 * @return cookies value or null if it is not enabled.
	 */
	public String getCookieServer() {

		String cookies = getSettingsValue(STRING_COOKIES);
		if (cookies.equals("-1")) {
			return null;
		}
		return cookies;

	}

	/**
	 * Get if server cookies are enabled.
	 * 
	 * @return true if are enabled, false otherwise.
	 */
	public boolean isCookiesEnabled() {
		String mResults = getSettingsValue(STRING_ENABLE_COOKIES);
		if (mResults.equals("true")) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Get if server password are enabled.
	 * 
	 * @return true if are enabled, false otherwise.
	 */
	public boolean isPasswordEnabled() {
		String mResults = getSettingsValue(STRING_ENABLE_PASSWORD);
		if (mResults.equals("true")) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Get error page path
	 * 
	 * @return Error document path.
	 */
	public String getErrorPage() {
		return getSettingsValue(STRING_ERROR_PAGE);
	}

	/**
	 * Get www log path
	 * 
	 * @return www log path.
	 */
	public String getLogPath() {
		return getSettingsValue(STRING_LOG_PATH);
	}

	/**
	 * Get vibrating configuration
	 * 
	 * @return true if is enable,false otherwise.
	 */
	public boolean isVibrateEnabled() {
		String mResults = getSettingsValue(STRING_VIBRATE);
		if (mResults.equals("true")) {
			return true;
		} else {
			return false;
		}

	}

	/**
	 * Get www root path
	 * 
	 * @return www root path.
	 */
	public String getWwwPath() {

		return getSettingsValue(STRING_WWW_PATH);

	}

	/**
	 * Get if black list is enabled
	 * 
	 * @return true if black list is enabled, false otherwise.
	 */
	public boolean isBlackListEnabled() {

		String mResults = getSettingsValue(STRING_ENABLE_BLACK_LIST);
		if (mResults.equals("true")) {
			return true;
		} else {
			return false;
		}

	}

	/**
	 * Get if white list is enabled
	 * 
	 * @return true if white list is enabled, false otherwise.
	 */
	public boolean isWhiteListEnabled() {

		String mResults = getSettingsValue(STRING_ENABLE_WHITE_LIST);
		if (mResults.equals("true")) {
			return true;
		} else {
			return false;
		}

	}

	/**
	 * Get server password
	 * 
	 * @return null null if there are not password, the password otherwise.
	 */
	public String getPassword() {

		String mResults = getSettingsValue(STRING_PASSWORD);
		if (mResults.length() < DEFAULT_MIN_LENGTH_PASSWORD) {
			return null;
		} else {
			return mResults;
		}

	}

	/**
	 * Get information from the parameter configuration table
	 * 
	 * @return value
	 */
	public String getSettingsValue(String param) {
		Cursor mCursor = mDb.query(true, DATABASE_TABLE_SETTINGS, new String[] {
				KEY_SETTINGS_PARAM, KEY_VALUE }, KEY_SETTINGS_PARAM + "= \""
				+ param + "\"", null, null, null, null, null);

		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		startManagingCursor(mCursor);

		if (mCursor.getCount() == 0) {
			return null;
		}

		return mCursor.getString(mCursor.getColumnIndexOrThrow(KEY_VALUE));

	}

}
