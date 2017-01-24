package distrosims;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import StringProcessors.HalloweenCommandProcessor;
import port.trace.nio.RemoteCommandExecuted;

public class RspHandler implements Runnable {
	private BlockingQueue<byte[]> rsp;
	private HalloweenCommandProcessor cp;
	
	public RspHandler(HalloweenCommandProcessor cp) {
		this.cp = cp;
		this.rsp = new LinkedBlockingQueue<byte[]>(10);
	}
	
	public boolean handleResponse(byte[] rsp) {
		this.rsp.offer(rsp);
		return false;
	}
	
	@Override
	public void run() {
		while (true) {
			try {
				String rsp_str = new String(this.rsp.take());
				RemoteCommandExecuted.newCase(this, rsp_str);
				this.cp.processCommand(rsp_str);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}

