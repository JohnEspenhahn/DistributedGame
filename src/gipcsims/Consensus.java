package gipcsims;

import java.rmi.Remote;

public interface Consensus extends Remote {
	boolean claim();
	void release();
	
	boolean isFree();
}
