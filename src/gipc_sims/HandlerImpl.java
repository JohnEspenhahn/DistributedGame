package gipc_sims;

import java.rmi.RemoteException;

import gipc_sims.modes.ConsensusMode;
import gipc_sims.modes.IPCMode;
import gipc_sims.modes.SimuMode;

public class HandlerImpl implements HandlerLocal, HandlerRemote {		
	private Server server;
	
	private Simulation sim;
	
	public HandlerImpl(Simulation sim, Server server) {
		this.server = server;
		this.sim = sim;
	}
	
	@Override
	public void broadcast(String msg) {
		try {
			this.server.broadcast(msg, this);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void setSimuModeChanging() {
		SimuMode.setModeChanging();
	}
	
	@Override
	public void sendSimuMode(SimuMode mode) {
		// If not yet notified that the mode is changing, try to change it
		if (SimuMode.takeModeChanging()) {
			try {
				if (ConsensusMode.requireSimuConsensus) this.setSimuMode(mode);
				this.server.setSimuMode(mode, this);
				SimuMode.waitForModeChanging();
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void unsetSimuModeChanging() {
		SimuMode.unsetModeChanging();
	}
	
	@Override
	public void setIPCModeChanging() {
		IPCMode.setChanging();
	}
	
	@Override
	public void sendIPCMode(IPCMode mode) {
		// If not yet notified that the mode is changing, try to change it
		if (IPCMode.takeModeChanging()) {
			try {
				this.setIPCMode(mode);
				this.server.setIPCMode(mode, this);
				IPCMode.waitForModeChanging();
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public void unsetIPCModeChanging() {
		IPCMode.unsetChanging();
	}
	
	@Override
	public boolean setIPCMode(IPCMode mode) {
		IPCMode.set(mode);
		return true;
	}
	
	@Override
	public void sendConsensusModes(boolean simu, boolean ipc) {	
		try {
			setConsensusModes(simu, ipc);
			server.setConsensusModes(ConsensusMode.requireSimuConsensus, ConsensusMode.requireIPCConsensus, this);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void setConsensusModes(boolean simuConsensus, boolean ipcConsensus) {
		ConsensusMode.requireSimuConsensus = simuConsensus;
		ConsensusMode.requireIPCConsensus = ipcConsensus;
		System.out.println("consensus: simu = " + simuConsensus + "; ipc = " + ipcConsensus + ";");
	}
	
	@Override
	public boolean setSimuMode(SimuMode mode) {
		SimuMode.set(mode);
		return true;
	}
	
	@Override
	public void executeCommand(String cmd) {		
		// Has a full command
		this.sim.executeCommand(cmd);
	}
}

