package distrosims;

import StringProcessors.HalloweenCommandProcessor;

public class RspHandler {
	private byte[] rsp;
	private HalloweenCommandProcessor cp;
	
	public RspHandler(HalloweenCommandProcessor cp) {
		this.cp = cp;
	}
	
	public synchronized boolean handleResponse(byte[] rsp) {
		this.rsp = rsp;
		this.notify();
		return false;
	}
	
	public synchronized void waitForResponse() {
		while(true) {
			if (this.rsp == null) {
				try {
					this.wait();
				} catch (InterruptedException e) { }
			} else {
				String rsp_str = new String(this.rsp);
				System.out.println("RspHandler received: " + rsp_str);
				this.cp.processCommand(rsp_str);
				
				this.rsp = null;
			}
		}
	}
}

