package gipcsims.and_rmi;

import java.rmi.ConnectException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashSet;
import java.util.Set;

import gipcsims.SimuMode;
import gipcsims.SimuModeObj;

public class RMIServerImpl extends UnicastRemoteObject implements RMIServer {
	private static final long serialVersionUID = 7685765417117171351L;
	
	private Set<RspHandlerRMIRemote> repository;
	
	protected RMIServerImpl() throws RemoteException {
		super();
		
		this.repository = new HashSet<RspHandlerRMIRemote>();
	}
	
	@Override
	public SimuMode getMode() {
		return SimuModeObj.getMode();
	}
	
	@Override
	public void setMode(SimuMode mode) {
		SimuModeObj.setMode(mode);
		for (RspHandlerRMIRemote r: this.repository) {
			try {
				r.setInstanceMode(mode);
			} catch (ConnectException e) {
				leave(r);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public void join(RspHandlerRMIRemote r) {
		this.repository.add(r);
	}
	
	@Override
	public void leave(RspHandlerRMIRemote r) {
		if (r != null) {
			this.repository.remove(r);
		}
	}

	@Override
	public void broadcast(String msg, RspHandlerRMIRemote src) throws RemoteException {
		// System.out.println(msg);
		for (RspHandlerRMIRemote r: this.repository) {
			if (this.getMode() == SimuMode.ATOMIC || !src.equals(r)) {
				try {
					r.handleRemoteCommand(msg);
				} catch (ConnectException e) {
					leave(r);
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
		}
	}

}
