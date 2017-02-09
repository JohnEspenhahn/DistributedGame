package rmisims;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RspHandlerRemote extends Remote {
	void handleRemoteCommand(String cmd) throws RemoteException;
	void setInstanceMode(SimuMode mode) throws RemoteException;
}
