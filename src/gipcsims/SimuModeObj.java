package gipcsims;

public class SimuModeObj {
	
	private static SimuMode mode = SimuMode.ATOMIC;
	private static IPCMode ipc = IPCMode.GIPC;
	
	public static void setMode(SimuMode mode) {
		SimuModeObj.mode = mode;
	}
	
	public static SimuMode getMode() {
		return mode;
	}
	
	public static void setIPC(IPCMode ipc) {
		SimuModeObj.ipc = ipc;
	}
	
	public static IPCMode getIPC() {
		return ipc;
	}
	
}
