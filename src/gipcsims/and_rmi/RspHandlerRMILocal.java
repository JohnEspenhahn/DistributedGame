package gipcsims.and_rmi;

import gipcsims.SimuMode;

public interface RspHandlerRMILocal {
	void handleLocalCommand(String cmd);
	void setServerMode(SimuMode mode);
}
