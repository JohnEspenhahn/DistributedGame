package rmi_sims;

import java.rmi.ConnectException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashSet;
import java.util.Set;

public class ServerImpl extends UnicastRemoteObject implements Server {
	private static final long serialVersionUID = 7685765417117171351L;
	
	private Set<RspHandlerRemote> repository;
	private SimuMode mode;
	
	protected ServerImpl() throws RemoteException {
		super();
		
		this.repository = new HashSet<RspHandlerRemote>();
		this.mode = SimuMode.ATOMIC;
	}
	
	@Override
	public SimuMode getMode() {
		return this.mode;
	}
	
	@Override
	public void setMode(SimuMode m) {
		this.mode = m;
		for (RspHandlerRemote r: this.repository) {
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
	public void join(RspHandlerRemote r) {
		this.repository.add(r);
	}
	
	@Override
	public void leave(RspHandlerRemote r) {
		if (r != null) {
			this.repository.remove(r);
		}
	}

	@Override
	public void broadcast(String msg, RspHandlerRemote src) throws RemoteException {
		// System.out.println(msg);
		for (RspHandlerRemote r: this.repository) {
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
