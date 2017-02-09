package rmisims;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RspHandlerRemote extends Remote {
	void handleRemoteCommand(String cmd) throws RemoteException;
	void setMode(SimuMode mode) throws RemoteException;
}
