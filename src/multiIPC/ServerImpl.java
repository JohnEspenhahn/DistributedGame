package multiIPC;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import inputport.rpc.duplex.GIPCRemoteException;
import multiIPC.modes.ConsensusMode;
import multiIPC.modes.SimuMode;

public class ServerImpl implements Server {
	private List<HandlerRemote> repository;
	
	public ServerImpl() {
		this.repository = new ArrayList<HandlerRemote>();
	}

	@Override
	public void setSimuMode(SimuMode mode, HandlerRemote src) {
		// Ignore if the mode is already changing
		if (ConsensusMode.requireConsensus && !SimuMode.takeModeChanging()) return;
		
		// Tell everyone the mode is changing (synchronous)
		Iterator<HandlerRemote> it;
		it = this.repository.listIterator();
		while (it.hasNext()) {
			HandlerRemote r = it.next();
			if (r.equals(src)) continue;
			
			try {
				r.setSimuModeChanging();
			} catch (RemoteException e) {
				it.remove();
			}
		}
		
		// Change the mode (synchronous)
		it = this.repository.listIterator();
		while (it.hasNext()) {
			HandlerRemote r = it.next();
			if (r.equals(src)) continue;
			
			try {
				r.setSimuMode(mode);
			} catch (RemoteException e) {
				it.remove();
			}
		}
		
		// Atomically send an asynchronous message to every client that the mode has finished changing
		if (ConsensusMode.requireConsensus) {
			synchronized (SimuMode.class) {
				SimuMode.unsetModeChanging();
				it = this.repository.listIterator();
				while (it.hasNext()) {
					HandlerRemote r = it.next();
					try {
						r.unsetSimuModeChanging();
					} catch (RemoteException e) {
						it.remove();
					}
				}
			}
		}
	}
	
	@Override
	public void setConsensusMode(boolean consensusRequired, HandlerRemote src) {
		ConsensusMode.requireConsensus = consensusRequired;
		
		Iterator<HandlerRemote> it = this.repository.listIterator();
		while (it.hasNext()) {
			HandlerRemote r = it.next();
			if (r.equals(src)) continue;
			
			try {
				r.setConsensusMode(consensusRequired);
			} catch (RemoteException e) {
				it.remove();
			}
		}
	}
	
	@Override
	public void join(HandlerRemote r) {
		if (r != null)
			this.repository.add(r);
	}

	@Override
	public boolean broadcast(String msg, SimuMode mode, HandlerRemote src) {
		Iterator<HandlerRemote> it = this.repository.listIterator();
		while (it.hasNext()) {
			HandlerRemote r = it.next();
			if (mode == SimuMode.ATOMIC || !src.equals(r)) {
				try {
					r.executeCommand(msg);
				} catch (GIPCRemoteException e) {
					it.remove();
				} catch (RemoteException e) {
					it.remove();
				}
			}
		}
		return true;
	}
}
