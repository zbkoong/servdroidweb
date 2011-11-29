package org.servDroid.server.service; 

import org.servDroid.server.service.params.ServerParams;
import org.servDroid.db.LogMessage;

import java.util.List;

interface ServiceController { 

	/**
	 * Use this method to set the security key. If this key is defined
	 * previously, it will prevent external process to close the remote
	 * service, unless the key is the same
	 * 
	 * @param secKey
	 *            The shared key needed to control the service.
	 */
	// void setSecurityKey(in String secKey);
	 
	/**
	 * Start the server with the defined parameters
	 *
	 * @param params
	 *	The parameter with the configuration of the server
	 *
	 * @return True if the server has been initialized, false otherwise
	 */
	boolean startService(in ServerParams params);
	
	/**
	 * Restart the server with the defined parameters
	 *
	 * @param params
	 *	The parameter with the configuration of the server
	 *
	 * @return True if the server has been initialized, false otherwise
	 */
	boolean restartService(in ServerParams params);
	
	 /**
	 * Stop the server.
	 *
	 * @return True if the server has been stopped, false otherwise
	 */
	boolean stopService(); 
	
	/**
	 * Get the status of the server:<br>
	 * {@link ServerService.STATUS_RUNNING} The server is running <br>
	 * {@link ServerService.STATUS_STOPED} The server is stopped <br>
	 *
	 * @return True if the server has been initialized, false otherwise
	 */
	int getStatus();
	
	void setVibrate(in boolean vibrate);
	/**
	 * Get the servDroid software version
	 * @return The ServDroid.web version
	 */
	String getVersion();
	
	/**
	 * Create a new log entry using the the IP, request path, some extra
	 * information. If the log is added successfully return the new rowId for
	 * that log entry, otherwise return a -1 to indicate failure.
	 * 
	 * @param msg
	 *            The message to be stored in the log
	
	 * @return rowId or -1 if failed
	 */
	long addLog(in LogMessage msg);
	
	/**
	 * Return the ArrayList which contains the log list
	 * 
	 * @param numRows
	 *            The number of rows to get
	 * 
	 * @return List with the log entries
	 */
	List<LogMessage> getLogList(in int numRows);
	
}