package multiIPC.modes;

public class ServersSynchronizedMode {
	private static boolean taken = false;
	private static boolean isSynchronized = true;
	
	public static synchronized void take() {
		while (isSynchronized && taken) {
			try {
				ServersSynchronizedMode.class.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		if (isSynchronized) taken = true;
	}
	
	public static synchronized void release() {
		if (isSynchronized) {
			taken = false;
			ServersSynchronizedMode.class.notify();
		}
	}
	
	public static synchronized void setSynchronized(boolean sync) {
		System.out.println("ServersSynchronizedMode = " + sync);
		ServersSynchronizedMode.isSynchronized = sync;
		if (!sync) {
			taken = false;
			ServersSynchronizedMode.class.notifyAll();
		}
	}
}
