package gipcsims.and_rmi;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import StringProcessors.HalloweenCommandProcessor;
import gipcsims.GIPCHalloweenSimulation;
import gipcsims.SimuMode;
import gipcsims.SimuModeObj;

import port.trace.nio.RemoteCommandExecuted;

public class RspHandlerRMIImpl extends UnicastRemoteObject implements RspHandlerRMILocal, RspHandlerRMIRemote {
	private static final long serialVersionUID = -2300278155702440864L;
	
	private HalloweenCommandProcessor cp;
	private RMIServer server;
	
	public RspHandlerRMIImpl(HalloweenCommandProcessor cp, RMIServer server) throws RemoteException {
		this.cp = cp;
		this.server = server;
	}
	
	@Override
	public void handleLocalCommand(String cmd) {
		try {
			if (getMode() != SimuMode.ATOMIC) {
				this.handleRemoteCommand(cmd);
			}
			
			if (getMode() != SimuMode.LOCAL) {
				this.server.broadcast(cmd, this);
			} else {
				updateTimingCount();
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		
	}
	
	@Override
	public void setServerMode(SimuMode mode) {
		try {
			this.server.setMode(mode);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
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
	public void setInstanceMode(SimuMode mode) {
		SimuModeObj.setMode(mode);
		System.out.println("Mode set to " + mode);
	}
	
	@Override
	public void handleRemoteCommand(String cmd) {		
		// Has a full command
		RemoteCommandExecuted.newCase(this, cmd);
		this.cp.processCommand(cmd);
		
		updateTimingCount();
	}
}

