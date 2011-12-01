/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: C:\\android\\workspace\\ServDroid\\src\\org\\servDroid\\server\\service\\ServerServiceListener.aidl
 */
package org.servDroid.server.service;
//** NOT IMPLEMENTED*//

public interface ServerServiceListener extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements org.servDroid.server.service.ServerServiceListener
{
private static final java.lang.String DESCRIPTOR = "org.servDroid.server.service.ServerServiceListener";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an org.servDroid.server.service.ServerServiceListener interface,
 * generating a proxy if needed.
 */
public static org.servDroid.server.service.ServerServiceListener asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = (android.os.IInterface)obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof org.servDroid.server.service.ServerServiceListener))) {
return ((org.servDroid.server.service.ServerServiceListener)iin);
}
return new org.servDroid.server.service.ServerServiceListener.Stub.Proxy(obj);
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
case TRANSACTION_onStartServer:
{
data.enforceInterface(DESCRIPTOR);
this.onStartServer();
reply.writeNoException();
return true;
}
case TRANSACTION_onStopServer:
{
data.enforceInterface(DESCRIPTOR);
this.onStopServer();
reply.writeNoException();
return true;
}
case TRANSACTION_onStartingServer:
{
data.enforceInterface(DESCRIPTOR);
this.onStartingServer();
reply.writeNoException();
return true;
}
case TRANSACTION_onStoppingServer:
{
data.enforceInterface(DESCRIPTOR);
this.onStoppingServer();
reply.writeNoException();
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements org.servDroid.server.service.ServerServiceListener
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
public void onStartServer() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_onStartServer, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void onStopServer() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_onStopServer, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void onStartingServer() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_onStartingServer, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void onStoppingServer() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_onStoppingServer, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
}
static final int TRANSACTION_onStartServer = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_onStopServer = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
static final int TRANSACTION_onStartingServer = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
static final int TRANSACTION_onStoppingServer = (android.os.IBinder.FIRST_CALL_TRANSACTION + 3);
}
public void onStartServer() throws android.os.RemoteException;
public void onStopServer() throws android.os.RemoteException;
public void onStartingServer() throws android.os.RemoteException;
public void onStoppingServer() throws android.os.RemoteException;
}
