# Introduction #

If you have an application which needs to manage a web server you can use the ServDroid for this purpose.

This wiki shows how to bind the ServDroid service from your code.

Note that this is only possible for the ServDroid v0.2.4 and above.


# How to #
You can download the source code of a simple example [here](https://code.google.com/p/servdroidweb/downloads/detail?name=ServDroidServiceConnectionExample.zip)

First you need to copy the following files to your project:

  * org.servDroid.db.LogMessage.java
  * org.servDroid.db.LogMessage.aidl
  * org.servDroid.server.service.ServerValues.java
  * org.servDroid.server.service.ServiceController.aidl
  * org.servDroid.server.service.params.ServerParams.java
  * org.servDroid.server.service.params.ServerParams.aidl

Then prepare your manifest adding this lines:

```
<service android:name="org.servDroid.server.service.ServerService">
      <intent-filter>
        <action android:name="org.servDroid.server.service.ServiceController" />
      </intent-filter>
</service>
```

Now we are ready to bind the service:
```
// This is the controller and the connection for the service
private ServiceController mServDroidService;
private ServDroidConnection mServiceConnection;

....

// Request bind to the service
mServiceConnection = new ServDroidConnection();
Intent intent = new Itent("org.servDroid.server.service.ServiceController");
bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
	
....

// This private class will let us know when the service is ready
class ServDroidConnection implements ServiceConnection {

	public void onServiceConnected(ComponentName name, IBinder service) {
		mServDroidService = ServiceController.Stub.asInterface(service);

		String text = null;
		try {
			if (mServiceConnection == null) {
				text = "Error connecting to the service";
			} else {
				text = " Connected to ServDroid v"
						+ mServDroidService.getVersion();
			}
		} catch (RemoteException e1) {
			e1.printStackTrace();
			text = "Error connecting to the service";
		}
		Log.i(TAG, text);
		
		//Create a log line to append to the ServDroid logs
		LogMessage logLine = new LogMessage();
		logLine.setInfoEnd("Service linked succesfull from an external application");
		logLine.setTimeStamp(System.currentTimeMillis());
		
		try {
			//Send the line
			mServDroidService.addLog(logLine);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		
	}

	public void onServiceDisconnected(ComponentName name) {
		mServDroidService = null;
		Log.i(TAG, "disconnected");
	}
}
```


Now is time to use the service. For example, if you want to start the server, first you will need to specify what parameter

do you want to use:
```
ServerParams params = new ServerParams("/sdcard/",
		"/sdcard/error", 30, true, 8080, 10);
mServDroidService.startService(params);
```

The available functions are documented in the file _org.servDroid.server.service.ServiceController.aidl_:

  * startService()
  * restartService()
  * stopService()
  * getStatus()
  * getCurrentParams()
  * getDefaultPortOnRoot()
  * getVersion()
  * addLog()
  * getLogList()

```

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
 * {@link ServerValues.STATUS_RUNNING} The server is running <br>
 * {@link ServerValues.STATUS_STOPED} The server is stopped <br>
 *
 * @return True if the server has been initialized, false otherwise
 */
int getStatus();


/**
 * Get the parameters in use by the server.
 * @return The  {@link ServerParams} in use for the server
 */
ServerParams getCurrentParams();

 /**
 * This is the default port opened when the user ask for opening a port
 * under 1024. <br>
 * The system will try to use iptables like this:<br>
 * iptables -t nat -A PREROUTING -p tcp --dport 80 -j REDIRECT --to-port
 * DEFAULT_PORT_ON_ROOT
 * @return The default port when the root permissions are required.
 */
int getDefaultPortOnRoot();

/**
 * Use this function to enable and disable the vibrations
 * when a request is received.
 * @param params 
 *   True to vibrate if a petition is accepted, false otherwise.
 */
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
```