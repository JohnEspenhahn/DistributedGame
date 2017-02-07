package rmisims;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class ServerImpl extends UnicastRemoteObject implements Server {
	private static final long serialVersionUID = 7685765417117171351L;
	
	private ClientRepository repository;
	private boolean atomic;
	
	protected ServerImpl(ClientRepository repo) throws RemoteException {
		super();
		
		this.repository = repo;
		this.atomic = false;
	}
	
	@Override
	public boolean isAtomic() {
		return this.atomic;
	}
	
	@Override
	public void setAtomic(boolean a) {
		this.atomic = a;
	}
	
	@Override
	public void join(IRspHandler r) {
		try {
			this.repository.deposit(r);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void broadcast(String msg, IRspHandler src) throws RemoteException {
		try {
			for (Remote r: this.repository.getObjects()) {
				if (r instanceof IRspHandler && (this.isAtomic() || !r.equals(this))) {
					((IRspHandler) r).handleRemoteCommand(msg);
				}
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

}
