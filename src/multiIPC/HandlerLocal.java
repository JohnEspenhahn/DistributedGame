package multiIPC;

import multiIPC.modes.IPCMode;
import multiIPC.modes.SimuMode;

public interface HandlerLocal {
	void broadcast(String msg, SimuMode mode);
	void sendSimuMode(SimuMode mode);
	void sendIPCMode(IPCMode mode);
	void sendConsensusModes(boolean simu, boolean ipc);
}
