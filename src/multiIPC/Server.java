package multiIPC;

import java.rmi.Remote;
import java.rmi.RemoteException;

import multiIPC.modes.SimuMode;

public interface Server extends Remote {
	void join(HandlerRemote r) throws RemoteException;
	boolean broadcast(String msg, SimuMode mode, HandlerRemote src) throws RemoteException;
	
	void setSimuMode(SimuMode m, HandlerRemote src) throws RemoteException;
	void setConsensusMode(boolean consensusRequired, HandlerRemote src) throws RemoteException;
}
