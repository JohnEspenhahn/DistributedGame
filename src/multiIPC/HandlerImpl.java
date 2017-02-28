package multiIPC;

import java.rmi.RemoteException;

import multiIPC.modes.ConsensusMode;
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
	public boolean setSimuModeChanging() {
		SimuMode.setModeChanging();
		return true;
	}
	
	@Override
	public void sendSimuMode(SimuMode mode) {
		// If not yet notified that the mode is changing, try to change it
		if (SimuMode.takeModeChanging()) {
			this.setSimuMode(mode);
			try {
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
	public void sendConsensusMode(boolean consensusRequired) {
		setConsensusMode(consensusRequired);
		
		try {
			server.setConsensusMode(consensusRequired, this);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void setConsensusMode(boolean consensusRequired) {
		ConsensusMode.requireConsensus = consensusRequired;
	}
	
	@Override
	public boolean setSimuMode(SimuMode mode) {
		SimuMode.setMode(mode);
		return true;
	}
	
	@Override
	public void executeCommand(String cmd) {		
		// Has a full command
		this.sim.executeCommand(cmd);
	}
}

