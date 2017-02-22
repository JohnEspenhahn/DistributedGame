package gipcsims;

import java.rmi.Remote;

public interface Server extends Remote {
	void join(RspHandlerGIPCRemote r);
	void leave(RspHandlerGIPCRemote r);
	void broadcast(String msg, RspHandlerGIPCRemote src);
	
	boolean isModeChanging();
	boolean setMode(SimuMode m);
	SimuMode getMode();
	IPCMode getIPC();
}
