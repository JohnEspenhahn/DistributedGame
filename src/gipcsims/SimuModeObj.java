package gipcsims;

public class SimuModeObj {
	
	private static SimuMode mode = SimuMode.ATOMIC;
	private static boolean mode_changing = false;
	
	public synchronized static boolean takeModeChanging() {
		if (mode_changing) return false;
		
		System.out.println("mode_changing = true");
		mode_changing = true;
		return true;
	}
	
	public synchronized static void setModeChanging() {
		System.out.println("mode_changing = true");
		SimuModeObj.mode_changing = true;
	}
	
	public synchronized static void unsetModeChanging() {
		System.out.println("mode_changing = false");
		SimuModeObj.mode_changing = false;
		SimuModeObj.class.notifyAll();
	}
	
	public synchronized static void setMode(SimuMode m) {
		SimuModeObj.mode = m;
		System.out.println("mode = " + m);
	}
	
	public synchronized static boolean isChanging() {
		return SimuModeObj.mode_changing;
	}
	
	public synchronized static SimuMode getMode() {
		while (mode_changing) {
			try {
				SimuModeObj.class.wait();
			} catch (InterruptedException e) { }
		}
		
		System.out.println("Got mode = " + mode);
		return mode;
	}
	
}
