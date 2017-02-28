package multiIPC;

import multiIPC.modes.SimuMode;

public interface HandlerLocal {
	void broadcast(String msg, SimuMode mode);
	void sendSimuMode(SimuMode mode);
	void sendConsensusMode(boolean consensusRequired);
}
