package rmisims;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import StringProcessors.HalloweenCommandProcessor;
import port.trace.nio.RemoteCommandExecuted;

public class RspHandlerImpl extends UnicastRemoteObject implements IRspHandler {
	private static final long serialVersionUID = -2300278155702440864L;
	
	private HalloweenCommandProcessor cp;
	private Server server;
	
	public RspHandlerImpl(HalloweenCommandProcessor cp, Server server) throws RemoteException {
		this.cp = cp;
		this.server = server;
	}
	
	public void handleLocalCommand(String cmd) {
		try {
			if (!this.server.isAtomic()) {
				this.handleRemoteCommand(cmd);
			}
			
			this.server.broadcast(cmd, this);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		
	}
	
	@Override
	public void handleRemoteCommand(String cmd) {		
		// Has a full command
		RemoteCommandExecuted.newCase(this, cmd);
		this.cp.processCommand(cmd);
		
		// For timing debug
		if (RMIHalloweenSimulation.WAIT_FOR_CMD > 0) {
			if (--RMIHalloweenSimulation.WAIT_FOR_CMD == 0) {
				System.out.println("Completed in " + (System.currentTimeMillis()-RMIHalloweenSimulation.TIMING_START) + "ms");
			}
		}
	}
}

