package multiIPC;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import inputport.rpc.duplex.GIPCRemoteException;
import multiIPC.modes.ConsensusMode;
import multiIPC.modes.IPCMode;
import multiIPC.modes.ServersSynchronizedMode;
import multiIPC.modes.SimuMode;

public class ServerImpl extends UnicastRemoteObject implements Server {
	private static final long serialVersionUID = 8479972969340308906L;
	private List<HandlerRemote> repository;
	
	public ServerImpl() throws RemoteException {
		this.repository = new ArrayList<HandlerRemote>();
	}

	@Override
	public void setSimuMode(SimuMode mode, HandlerRemote src) {
		// Ignore if the mode is already changing
		if (!SimuMode.takeModeChanging()) return;
		
		// Tell everyone the mode is changing (asynchronous)
		Iterator<HandlerRemote> it;
		if (ConsensusMode.requireSimuConsensus) {
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
		SimuMode.set(mode);
		
		// Atomically send an asynchronous message to every client that the mode has finished changing
		if (ConsensusMode.requireSimuConsensus) {
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
	
	@Override
	public void setIPCMode(IPCMode mode, HandlerRemote src) {
		// Ignore if the mode is already changing
		if (!IPCMode.takeModeChanging()) return;
		
		// Tell everyone the mode is changing (asynchronous)
		Iterator<HandlerRemote> it;
		it = this.repository.listIterator();
		while (it.hasNext()) {
			HandlerRemote r = it.next();
			if (r.equals(src)) continue;
			
			try {
				r.setIPCModeChanging();
			} catch (RemoteException e) {
				it.remove();
			}
		}
		
		// Change the mode (synchronous)
		it = this.repository.listIterator();
		while (it.hasNext()) {
			HandlerRemote r = it.next();
			if (ConsensusMode.requireSimuConsensus && r.equals(src)) 
				continue;
			
			try {
				r.setIPCMode(mode);
			} catch (RemoteException e) {
				it.remove();
			}
		}
		
		// Atomically send an asynchronous message to every client that the mode has finished changing
		IPCMode.unsetChanging();
		it = this.repository.listIterator();
		while (it.hasNext()) {
			HandlerRemote r = it.next();
			try {
				r.unsetIPCModeChanging();
			} catch (RemoteException e) {
				it.remove();
			}
		}
	}
	
	@Override
	public void setConsensusModes(boolean simuConsensus, boolean ipcConsensus, HandlerRemote src) {
		ConsensusMode.requireSimuConsensus = simuConsensus;
		ConsensusMode.requireIPCConsensus = ipcConsensus;
		
		Iterator<HandlerRemote> it = this.repository.listIterator();
		while (it.hasNext()) {
			HandlerRemote r = it.next();
			if (r.equals(src)) continue;
			
			try {
				r.setConsensusModes(simuConsensus, ipcConsensus);
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
	public void broadcast(String msg, HandlerRemote src) {
		ServersSynchronizedMode.take();
		
		try {
			SimuMode mode = SimuMode.get();
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
		} finally {
			ServersSynchronizedMode.release();
		}
	}
}
