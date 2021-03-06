package gipc_sims;

import java.rmi.Remote;
import java.rmi.RemoteException;

import gipc_sims.modes.IPCMode;
import gipc_sims.modes.SimuMode;

public interface Server extends Remote {
	void join(HandlerRemote r) throws RemoteException;
	void broadcast(String msg, HandlerRemote src) throws RemoteException;
	
	void setSimuMode(SimuMode m, HandlerRemote src) throws RemoteException;
	void setIPCMode(IPCMode m, HandlerRemote src) throws RemoteException;
	void setConsensusModes(boolean simuConsensus, boolean ipcConsensus, HandlerRemote src) throws RemoteException;
}
