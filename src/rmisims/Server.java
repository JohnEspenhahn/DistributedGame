package rmisims;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Server extends Remote {
	void join(IRspHandler r) throws RemoteException;
	void broadcast(String msg, IRspHandler src) throws RemoteException;
	
	boolean isAtomic() throws RemoteException;
	void setAtomic(boolean a) throws RemoteException;
}
