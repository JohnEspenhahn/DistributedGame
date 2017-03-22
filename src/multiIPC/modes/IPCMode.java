package multiIPC.modes;

public enum IPCMode {
	NIO, RMI, GIPC;
	
	private static IPCMode mode = IPCMode.GIPC;
	private static boolean mode_changing = false;
	
	public synchronized static boolean takeModeChanging() {
		if (mode_changing) return false;
		else if (!ConsensusMode.requireIPCConsensus) return true;
		
		System.out.println("ipc mode_changing = true");
		mode_changing = true;
		return true;
	}
	
	public synchronized static void setChanging() {
		// System.out.println("ipc mode_changing = true");
		mode_changing = true;
	}
	
	public synchronized static void unsetChanging() {
		// System.out.println("ipc mode_changing = false");
		mode_changing = false;
		IPCMode.class.notifyAll();
	}
	
	public synchronized static void set(IPCMode m) {
		mode = m;
		System.out.println("ipc mode = " + m);
	}
	
	public synchronized static boolean isChanging() {
		return mode_changing;
	}
	
	public synchronized static IPCMode get() {
		waitForModeChanging();		
		// System.out.println("Got ipc mode = " + mode);
		return mode;
	}
	
	public synchronized static void waitForModeChanging() {
		while (mode_changing && ConsensusMode.requireIPCConsensus) {
			try {
				//System.out.println("IPC Mode waiting");
				IPCMode.class.wait();
				//System.out.println("Done IPC mode waiting");
			} catch (InterruptedException e) { }
		}
	}
}
