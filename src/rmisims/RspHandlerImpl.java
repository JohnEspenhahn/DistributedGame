package rmisims;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import StringProcessors.HalloweenCommandProcessor;
import port.trace.nio.RemoteCommandExecuted;

public class RspHandlerImpl extends UnicastRemoteObject implements RspHandlerLocal, RspHandlerRemote {
	private static final long serialVersionUID = -2300278155702440864L;
	
	private HalloweenCommandProcessor cp;
	private Server server;
	private SimuMode mode;
	
	public RspHandlerImpl(HalloweenCommandProcessor cp, Server server) throws RemoteException {
		this.cp = cp;
		this.server = server;
		this.mode = server.getMode();
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
		if (RMIHalloweenSimulation.WAIT_FOR_CMD > 0) {
			if (--RMIHalloweenSimulation.WAIT_FOR_CMD == 0) {
				System.out.println("Completed in " + (System.currentTimeMillis() - RMIHalloweenSimulation.TIMING_START) + "ms");
			}
		}
	}
	
	public SimuMode getMode() {
		return this.mode;
	}
	
	@Override
	public void setInstanceMode(SimuMode mode) {
		this.mode = mode;
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

