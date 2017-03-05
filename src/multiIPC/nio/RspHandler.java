package multiIPC.nio;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import multiIPC.Simulation;
import port.trace.nio.RemoteCommandExecuted;

public class RspHandler implements Runnable {	
	private BlockingQueue<byte[]> rsp;
	private Simulation sim;
	private String partial_cmd;
	
	public RspHandler(Simulation sim) {
		this.sim = sim;
		this.rsp = new LinkedBlockingQueue<byte[]>(501);
		this.partial_cmd = "";
	}
	
	public boolean handleResponse(byte[] rsp) {
		boolean accepted = this.rsp.offer(rsp);
		if (!accepted) System.err.println("Dropped message!");
		return false;
	}
	
	@Override
	public void run() {
		while (true) {
			try {
				String rsp_str = this.partial_cmd + new String(this.rsp.take());
				
				int start = 0;
				int end = rsp_str.indexOf(NioClient.SEPERATOR, start);
				while (end >= 0) {
					// Has a full command
					String cmd = rsp_str.substring(start, end);
					RemoteCommandExecuted.newCase(this, cmd);
					this.sim.executeCommand(cmd);
					
					// Look for next command
					start = end+1;
					end = rsp_str.indexOf(NioClient.SEPERATOR, start);
				}
				
				// Save any partial command
				if (start >= rsp_str.length() - 1) this.partial_cmd = "";
				else this.partial_cmd = rsp_str.substring(start);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}

