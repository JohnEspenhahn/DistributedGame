package gipcsims;

import StringProcessors.HalloweenCommandProcessor;
import port.trace.nio.RemoteCommandExecuted;

public class RspHandlerGIPCImpl implements RspHandlerGIPCLocal, RspHandlerGIPCRemote {		
	private Server server;
	
	private HalloweenCommandProcessor cp;
	
	public RspHandlerGIPCImpl(HalloweenCommandProcessor cp, Server server) {
		this.server = server;
		
		this.cp = cp;
	}
	
	@Override
	public void handleLocalCommand(String cmd) {
		if (this.server.isModeChanging()) {
			System.out.println("Cannot send command, mode is changing");
			return;
		}
		
		if (getMode() != SimuMode.ATOMIC) {
			RspHandlerGIPCImpl.this.handleRemoteCommand(cmd);
		}
		
		if (getMode() != SimuMode.LOCAL) {
			RspHandlerGIPCImpl.this.server.broadcast(cmd, RspHandlerGIPCImpl.this);
		} else {
			updateTimingCount();
		}
	}
	
	@Override
	public void setServerMode(SimuMode mode) {		
		this.server.setMode(mode);
	}
	
	private void updateTimingCount() {
		if (GIPCHalloweenSimulation.WAIT_FOR_CMD > 0) {
			if (--GIPCHalloweenSimulation.WAIT_FOR_CMD == 0) {
				System.out.println("Completed in " + (System.currentTimeMillis() - GIPCHalloweenSimulation.TIMING_START) + "ms");
			}
		}
	}
	
	public SimuMode getMode() {
		return SimuModeObj.getMode();
	}
	
	@Override
	public boolean setInstanceMode(SimuMode mode) {
		SimuModeObj.setMode(mode);
		System.out.println("Mode set to " + mode);
		return true;
	}
	
	@Override
	public void handleRemoteCommand(String cmd) {		
		// Has a full command
		RemoteCommandExecuted.newCase(this, cmd);
		this.cp.processCommand(cmd);
		
		updateTimingCount();
	}
}

