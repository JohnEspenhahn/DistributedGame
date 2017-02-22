package gipcsims;

import java.util.HashSet;
import java.util.Set;

import inputport.rpc.duplex.GIPCRemoteException;

public class ServerImpl implements Server {	
	private Set<RspHandlerGIPCRemote> repository;
	
	private Consensus simucons;
	
	protected ServerImpl() {
		this.simucons = new ConsensusImpl();
		this.repository = new HashSet<RspHandlerGIPCRemote>();
	}
	
	@Override
	public boolean isModeChanging() {
		return !this.simucons.isFree();
	}
	
	@Override
	public IPCMode getIPC() {
		return SimuModeObj.getIPC();
	}
	
	@Override
	public SimuMode getMode() {
		return SimuModeObj.getMode();
	}
	
	@Override
	public boolean setMode(SimuMode mode) {
		if (!this.simucons.claim()) {
			System.out.println("Cannot change mode, already changing");
			return false;
		}
		
		SimuModeObj.setMode(mode);
		for (RspHandlerGIPCRemote r: this.repository) {
			r.setInstanceMode(mode);
		}
		
		this.simucons.release();
		return true;
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
	public void broadcast(String msg, RspHandlerGIPCRemote src) {
		if (this.isModeChanging()) {
			System.out.println("Cannot send command, mode is changing");
			return;
		}
		
		// System.out.println(msg);
		for (RspHandlerGIPCRemote r: this.repository) {
			if (this.getMode() == SimuMode.ATOMIC || !src.equals(r)) {
				try {
					r.handleRemoteCommand(msg);
				} catch (GIPCRemoteException e) {
					leave(r);
				}
			}
		}
	}

}
