package gipcsims;

public class IPCModeObj {

	private static IPCMode mode = IPCMode.GIPC;
	private static boolean mode_changing = false;
	
	public synchronized static void setModeChanging() {
		mode_changing = true;
	}
	
	public synchronized static void unsetModeChanging() {
		mode_changing = false;
		mode.notifyAll();
	}
	
	public synchronized static void setMode(IPCMode m) {
		IPCModeObj.mode = m;
	}
	
	public synchronized static IPCMode getMode() {
		while (mode_changing) {
			try {
				mode.wait();
			} catch (InterruptedException e) { }
		}
		
		return mode;
	}
	
}
