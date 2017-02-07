package rmisims;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import examples.rmi.counter.RemoteRepository;

public class ClientRepository implements RemoteRepository {
	List<Remote> remotes = new ArrayList<Remote>();
	
	@Override
	public void deposit(Remote aRemote) throws RemoteException {
		this.remotes.add(aRemote);
	}

	@Override
	public List<Remote> getObjects() throws RemoteException {
		return this.remotes;
	}
	
}
