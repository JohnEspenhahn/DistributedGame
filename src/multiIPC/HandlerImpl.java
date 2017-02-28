package multiIPC;

import java.rmi.RemoteException;

import multiIPC.modes.ConsensusMode;
import multiIPC.modes.IPCMode;
import multiIPC.modes.SimuMode;

public class HandlerImpl implements HandlerLocal, HandlerRemote {		
	private Server server;
	
	private Simulation sim;
	
	public HandlerImpl(Simulation sim, Server server) {
		this.server = server;
		this.sim = sim;
	}
	
	@Override
	public void broadcast(String msg, SimuMode mode) {
		try {
			this.server.broadcast(msg, mode, this);
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
		if (!ConsensusMode.requireSimuConsensus) {
			try {
				this.setSimuMode(mode);
				this.server.setSimuMode(mode, this);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		} else if (SimuMode.takeModeChanging()) {
			try {
				this.setSimuMode(mode);
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
		if (!ConsensusMode.requireIPCConsensus) {
			try {
				this.setIPCMode(mode);
				this.server.setIPCMode(mode, this);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		} else if (IPCMode.takeModeChanging()) {
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

