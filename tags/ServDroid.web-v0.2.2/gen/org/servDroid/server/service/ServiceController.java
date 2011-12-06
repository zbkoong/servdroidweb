/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: C:\\android\\workspace\\ServDroid\\src\\org\\servDroid\\server\\service\\ServiceController.aidl
 */
package org.servDroid.server.service;
public interface ServiceController extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements org.servDroid.server.service.ServiceController
{
private static final java.lang.String DESCRIPTOR = "org.servDroid.server.service.ServiceController";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an org.servDroid.server.service.ServiceController interface,
 * generating a proxy if needed.
 */
public static org.servDroid.server.service.ServiceController asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = (android.os.IInterface)obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof org.servDroid.server.service.ServiceController))) {
return ((org.servDroid.server.service.ServiceController)iin);
}
return new org.servDroid.server.service.ServiceController.Stub.Proxy(obj);
}
public android.os.IBinder asBinder()
{
return this;
}
@Override public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException
{
switch (code)
{
case INTERFACE_TRANSACTION:
{
reply.writeString(DESCRIPTOR);
return true;
}
case TRANSACTION_startService:
{
data.enforceInterface(DESCRIPTOR);
org.servDroid.server.service.params.ServerParams _arg0;
if ((0!=data.readInt())) {
_arg0 = org.servDroid.server.service.params.ServerParams.CREATOR.createFromParcel(data);
}
else {
_arg0 = null;
}
boolean _result = this.startService(_arg0);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_restartService:
{
data.enforceInterface(DESCRIPTOR);
org.servDroid.server.service.params.ServerParams _arg0;
if ((0!=data.readInt())) {
_arg0 = org.servDroid.server.service.params.ServerParams.CREATOR.createFromParcel(data);
}
else {
_arg0 = null;
}
boolean _result = this.restartService(_arg0);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_stopService:
{
data.enforceInterface(DESCRIPTOR);
boolean _result = this.stopService();
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_getStatus:
{
data.enforceInterface(DESCRIPTOR);
int _result = this.getStatus();
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_setVibrate:
{
data.enforceInterface(DESCRIPTOR);
boolean _arg0;
_arg0 = (0!=data.readInt());
this.setVibrate(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_getVersion:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _result = this.getVersion();
reply.writeNoException();
reply.writeString(_result);
return true;
}
case TRANSACTION_addLog:
{
data.enforceInterface(DESCRIPTOR);
org.servDroid.db.LogMessage _arg0;
if ((0!=data.readInt())) {
_arg0 = org.servDroid.db.LogMessage.CREATOR.createFromParcel(data);
}
else {
_arg0 = null;
}
long _result = this.addLog(_arg0);
reply.writeNoException();
reply.writeLong(_result);
return true;
}
case TRANSACTION_getLogList:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
java.util.List<org.servDroid.db.LogMessage> _result = this.getLogList(_arg0);
reply.writeNoException();
reply.writeTypedList(_result);
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements org.servDroid.server.service.ServiceController
{
private android.os.IBinder mRemote;
Proxy(android.os.IBinder remote)
{
mRemote = remote;
}
public android.os.IBinder asBinder()
{
return mRemote;
}
public java.lang.String getInterfaceDescriptor()
{
return DESCRIPTOR;
}
/**
	 * Use this method to set the security key. If this key is defined
	 * previously, it will prevent external process to close the remote
	 * service, unless the key is the same
	 * 
	 * @param secKey
	 *            The shared key needed to control the service.
	 */// void setSecurityKey(in String secKey);
/**
	 * Start the server with the defined parameters
	 *
	 * @param params
	 *	The parameter with the configuration of the server
	 *
	 * @return True if the server has been initialized, false otherwise
	 */
public boolean startService(org.servDroid.server.service.params.ServerParams params) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
if ((params!=null)) {
_data.writeInt(1);
params.writeToParcel(_data, 0);
}
else {
_data.writeInt(0);
}
mRemote.transact(Stub.TRANSACTION_startService, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/**
	 * Restart the server with the defined parameters
	 *
	 * @param params
	 *	The parameter with the configuration of the server
	 *
	 * @return True if the server has been initialized, false otherwise
	 */
public boolean restartService(org.servDroid.server.service.params.ServerParams params) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
if ((params!=null)) {
_data.writeInt(1);
params.writeToParcel(_data, 0);
}
else {
_data.writeInt(0);
}
mRemote.transact(Stub.TRANSACTION_restartService, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/**
	 * Stop the server.
	 *
	 * @return True if the server has been stopped, false otherwise
	 */
public boolean stopService() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_stopService, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/**
	 * Get the status of the server:<br>
	 * {@link ServerService.STATUS_RUNNING} The server is running <br>
	 * {@link ServerService.STATUS_STOPED} The server is stopped <br>
	 *
	 * @return True if the server has been initialized, false otherwise
	 */
public int getStatus() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getStatus, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public void setVibrate(boolean vibrate) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(((vibrate)?(1):(0)));
mRemote.transact(Stub.TRANSACTION_setVibrate, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
/**
	 * Get the servDroid software version
	 * @return The ServDroid.web version
	 */
public java.lang.String getVersion() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.lang.String _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getVersion, _data, _reply, 0);
_reply.readException();
_result = _reply.readString();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/**
	 * Create a new log entry using the the IP, request path, some extra
	 * information. If the log is added successfully return the new rowId for
	 * that log entry, otherwise return a -1 to indicate failure.
	 * 
	 * @param msg
	 *            The message to be stored in the log
	
	 * @return rowId or -1 if failed
	 */
public long addLog(org.servDroid.db.LogMessage msg) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
long _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
if ((msg!=null)) {
_data.writeInt(1);
msg.writeToParcel(_data, 0);
}
else {
_data.writeInt(0);
}
mRemote.transact(Stub.TRANSACTION_addLog, _data, _reply, 0);
_reply.readException();
_result = _reply.readLong();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/**
	 * Return the ArrayList which contains the log list
	 * 
	 * @param numRows
	 *            The number of rows to get
	 * 
	 * @return List with the log entries
	 */
public java.util.List<org.servDroid.db.LogMessage> getLogList(int numRows) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.util.List<org.servDroid.db.LogMessage> _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(numRows);
mRemote.transact(Stub.TRANSACTION_getLogList, _data, _reply, 0);
_reply.readException();
_result = _reply.createTypedArrayList(org.servDroid.db.LogMessage.CREATOR);
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
}
static final int TRANSACTION_startService = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_restartService = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
static final int TRANSACTION_stopService = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
static final int TRANSACTION_getStatus = (android.os.IBinder.FIRST_CALL_TRANSACTION + 3);
static final int TRANSACTION_setVibrate = (android.os.IBinder.FIRST_CALL_TRANSACTION + 4);
static final int TRANSACTION_getVersion = (android.os.IBinder.FIRST_CALL_TRANSACTION + 5);
static final int TRANSACTION_addLog = (android.os.IBinder.FIRST_CALL_TRANSACTION + 6);
static final int TRANSACTION_getLogList = (android.os.IBinder.FIRST_CALL_TRANSACTION + 7);
}
/**
	 * Use this method to set the security key. If this key is defined
	 * previously, it will prevent external process to close the remote
	 * service, unless the key is the same
	 * 
	 * @param secKey
	 *            The shared key needed to control the service.
	 */// void setSecurityKey(in String secKey);
/**
	 * Start the server with the defined parameters
	 *
	 * @param params
	 *	The parameter with the configuration of the server
	 *
	 * @return True if the server has been initialized, false otherwise
	 */
public boolean startService(org.servDroid.server.service.params.ServerParams params) throws android.os.RemoteException;
/**
	 * Restart the server with the defined parameters
	 *
	 * @param params
	 *	The parameter with the configuration of the server
	 *
	 * @return True if the server has been initialized, false otherwise
	 */
public boolean restartService(org.servDroid.server.service.params.ServerParams params) throws android.os.RemoteException;
/**
	 * Stop the server.
	 *
	 * @return True if the server has been stopped, false otherwise
	 */
public boolean stopService() throws android.os.RemoteException;
/**
	 * Get the status of the server:<br>
	 * {@link ServerService.STATUS_RUNNING} The server is running <br>
	 * {@link ServerService.STATUS_STOPED} The server is stopped <br>
	 *
	 * @return True if the server has been initialized, false otherwise
	 */
public int getStatus() throws android.os.RemoteException;
public void setVibrate(boolean vibrate) throws android.os.RemoteException;
/**
	 * Get the servDroid software version
	 * @return The ServDroid.web version
	 */
public java.lang.String getVersion() throws android.os.RemoteException;
/**
	 * Create a new log entry using the the IP, request path, some extra
	 * information. If the log is added successfully return the new rowId for
	 * that log entry, otherwise return a -1 to indicate failure.
	 * 
	 * @param msg
	 *            The message to be stored in the log
	
	 * @return rowId or -1 if failed
	 */
public long addLog(org.servDroid.db.LogMessage msg) throws android.os.RemoteException;
/**
	 * Return the ArrayList which contains the log list
	 * 
	 * @param numRows
	 *            The number of rows to get
	 * 
	 * @return List with the log entries
	 */
public java.util.List<org.servDroid.db.LogMessage> getLogList(int numRows) throws android.os.RemoteException;
}
