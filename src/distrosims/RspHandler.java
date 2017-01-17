package distrosims;
public class RspHandler {
	private byte[] rsp;	
	
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
				System.out.println("RspHandler received: " + new String(this.rsp));
				this.rsp = null;
			}
		}
	}
}

