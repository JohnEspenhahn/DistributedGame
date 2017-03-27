package gipc_sims;

import java.rmi.Remote;
import java.rmi.RemoteException;

import gipc_sims.modes.IPCMode;
import gipc_sims.modes.SimuMode;

public interface HandlerRemote extends Remote {
	void executeCommand(String cmd) throws RemoteException;
	
	void setSimuModeChanging() throws RemoteException;
	boolean setSimuMode(SimuMode mode) throws RemoteException;
	void unsetSimuModeChanging() throws RemoteException;
	
	void setIPCModeChanging() throws RemoteException;
	boolean setIPCMode(IPCMode mode) throws RemoteException;
	void unsetIPCModeChanging() throws RemoteException;
	
	void setConsensusModes(boolean simuConsensus, boolean ipcConsensus) throws RemoteException;
}
