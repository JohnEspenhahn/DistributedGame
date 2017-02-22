package gipcsims.and_rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

import gipcsims.SimuMode;

public interface RspHandlerRMIRemote extends Remote {
	void handleRemoteCommand(String cmd) throws RemoteException;
	void setInstanceMode(SimuMode mode) throws RemoteException;
}
