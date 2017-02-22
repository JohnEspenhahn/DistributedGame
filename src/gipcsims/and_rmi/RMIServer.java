package gipcsims.and_rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

import gipcsims.SimuMode;

public interface RMIServer extends Remote {
	void join(RspHandlerRMIRemote r) throws RemoteException;
	void leave(RspHandlerRMIRemote r) throws RemoteException;
	void broadcast(String msg, RspHandlerRMIRemote src) throws RemoteException;
	
	void setMode(SimuMode m) throws RemoteException;
	SimuMode getMode() throws RemoteException;
}
