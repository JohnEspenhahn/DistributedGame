package multiIPC;

import java.rmi.Remote;
import java.rmi.RemoteException;

import multiIPC.modes.SimuMode;

public interface HandlerRemote extends Remote {
	void executeCommand(String cmd) throws RemoteException;
	
	boolean setSimuModeChanging() throws RemoteException;
	boolean setSimuMode(SimuMode mode) throws RemoteException;
	void unsetSimuModeChanging() throws RemoteException;
	
	void setConsensusMode(boolean consensusRequired) throws RemoteException;
}
