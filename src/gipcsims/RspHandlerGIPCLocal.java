package gipcsims;

public interface RspHandlerGIPCLocal {
	void handleLocalCommand(String cmd);
	void setServerMode(SimuMode mode);
}
