package multiIPC.modes;

public enum SimuMode { 
	LOCAL, BASIC, ATOMIC;
	
	private static SimuMode mode = SimuMode.ATOMIC;
	private static boolean mode_changing = false;
	
	public synchronized static boolean takeModeChanging() {
		if (mode_changing) return false;
		
		System.out.println("Simu mode_changing = true");
		mode_changing = true;
		return true;
	}
	
	public synchronized static void setModeChanging() {
		System.out.println("Simu mode_changing = true");
		SimuMode.mode_changing = true;
	}
	
	public synchronized static void unsetModeChanging() {
		System.out.println("Simu mode_changing = false");
		SimuMode.mode_changing = false;
		SimuMode.class.notifyAll();
	}
	
	public synchronized static void set(SimuMode m) {
		SimuMode.mode = m;
		System.out.println("Simu mode = " + m);
	}
	
	public synchronized static boolean isChanging() {
		return SimuMode.mode_changing;
	}
	
	public synchronized static SimuMode get() {
		if (ConsensusMode.requireSimuConsensus) waitForModeChanging();		
		System.out.println("Got Simu mode = " + mode);
		return mode;
	}
	
	public synchronized static void waitForModeChanging() {
		while (mode_changing) {
			try {
				System.out.println("Simu Mode waiting");
				SimuMode.class.wait();
				System.out.println("Simu Done mode waiting");
			} catch (InterruptedException e) { }
		}
	}
}