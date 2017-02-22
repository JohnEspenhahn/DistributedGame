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
		synchronized (SimuModeObj.class) {
			if (SimuModeObj.isChanging())
				return;
			
			System.out.println("Starting handle local");
			SimuMode mode = SimuModeObj.getMode();
			
			if (mode != SimuMode.ATOMIC) {
				this.handleRemoteCommand(cmd);
			}
			
			if (mode != SimuMode.LOCAL) {
				this.server.broadcast(cmd, mode, this);
			} else {
				updateTimingCount();
			}
		}
	}
	
	@Override
	public boolean setModeChanging() {
		SimuModeObj.setModeChanging();
		return true;
	}
	
	@Override
	public void unsetModeChanging() {
		SimuModeObj.unsetModeChanging();
	}
	
	@Override
	public void setServerMode(SimuMode mode) {
		// If not yet notified that the mode is changing, try to change it
		if (SimuModeObj.takeModeChanging()) {
			this.setInstanceMode(mode);
			this.server.setMode(mode, this);
		}
	}
	
	private void updateTimingCount() {
		if (GIPCHalloweenSimulation.WAIT_FOR_CMD > 0) {
			if (--GIPCHalloweenSimulation.WAIT_FOR_CMD == 0) {
				System.out.println("Completed in " + (System.currentTimeMillis() - GIPCHalloweenSimulation.TIMING_START) + "ms");
			}
		}
	}
	
	@Override
	public boolean setInstanceMode(SimuMode mode) {
		SimuModeObj.setMode(mode);
		return true;
	}
	
	@Override
	public void handleRemoteCommand(String cmd) {		
		// Has a full command
		System.err.println("Executed " + cmd);
		RemoteCommandExecuted.newCase(this, cmd);
		this.cp.processCommand(cmd);
		
		updateTimingCount();
	}
}

