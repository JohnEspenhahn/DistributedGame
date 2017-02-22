package gipcsims;

import java.rmi.Remote;

public interface RspHandlerGIPCRemote extends Remote {
	void handleRemoteCommand(String cmd);
	boolean setInstanceMode(SimuMode mode);
	boolean setModeChanging();
	void unsetModeChanging();
}
