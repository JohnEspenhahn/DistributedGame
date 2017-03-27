package gipc_sims;

import gipc_sims.modes.IPCMode;
import gipc_sims.modes.SimuMode;

public interface HandlerLocal {
	void broadcast(String msg);
	void sendSimuMode(SimuMode mode);
	void sendIPCMode(IPCMode mode);
	void sendConsensusModes(boolean simu, boolean ipc);
}
