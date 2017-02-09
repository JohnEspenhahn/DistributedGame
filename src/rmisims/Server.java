package rmisims;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Server extends Remote {
	void join(RspHandlerRemote r) throws RemoteException;
	void leave(RspHandlerRemote r) throws RemoteException;
	void broadcast(String msg, RspHandlerRemote src) throws RemoteException;
	
	void setMode(SimuMode m) throws RemoteException;
	SimuMode getMode() throws RemoteException;
}
