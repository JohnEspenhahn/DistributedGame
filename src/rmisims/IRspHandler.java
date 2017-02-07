package rmisims;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IRspHandler extends Remote {
	void handleRemoteCommand(String cmd) throws RemoteException;
}
