package gipcsims;

import java.util.HashSet;
import java.util.Set;

import inputport.rpc.duplex.GIPCRemoteException;

public class ServerImpl implements Server {	
	private Set<RspHandlerGIPCRemote> repository;
	
	protected ServerImpl() {
		this.repository = new HashSet<RspHandlerGIPCRemote>();
	}
	
	@Override
	public void setMode(SimuMode mode, RspHandlerGIPCRemote src) {
		// Ignore if the mode is already changing
		if (!SimuModeObj.takeModeChanging()) return;
		
		// Tell everyone the mode is changing (synchronous)
		for (RspHandlerGIPCRemote r: this.repository) {
			if (r.equals(src)) continue;
			
			r.setModeChanging();
		}
		
		// Change the mode (synchronous)
		for (RspHandlerGIPCRemote r: this.repository) {
			if (r.equals(src)) continue;
			
			r.setInstanceMode(mode);
		}
		
		// Atomically send an asynchronous message to every client that the mode has finished changing
		synchronized (SimuModeObj.class) {
			SimuModeObj.unsetModeChanging();
			for (RspHandlerGIPCRemote r: this.repository) {
				r.unsetModeChanging();
			}
		}
	}
	
	@Override
	public void join(RspHandlerGIPCRemote r) {
		this.repository.add(r);
	}
	
	@Override
	public void leave(RspHandlerGIPCRemote r) {
		if (r != null) {
			this.repository.remove(r);
		}
	}

	@Override
	public void broadcast(String msg, SimuMode mode, RspHandlerGIPCRemote src) {
		for (RspHandlerGIPCRemote r: this.repository) {
			if (mode == SimuMode.ATOMIC || !src.equals(r)) {
				try {
					r.handleRemoteCommand(msg);
				} catch (GIPCRemoteException e) {
					leave(r);
				}
			}
		}
	}

}
