package rmi_sims;

public interface RspHandlerLocal {
	void handleLocalCommand(String cmd);
	void setServerMode(SimuMode mode);
}
