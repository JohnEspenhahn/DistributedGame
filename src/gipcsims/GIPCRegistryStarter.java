package gipcsims;

import gipcsims.and_nio.NioBroadcastServer;
import gipcsims.and_rmi.RMIRegistryStarter;
import inputport.rpc.GIPCLocateRegistry;
import inputport.rpc.GIPCRegistry;

public class GIPCRegistryStarter {
	public static final int PORT = 15247;

	public static void main(String[] args) {
		GIPCRegistryStarter.start();
		NioBroadcastServer.start();
		RMIRegistryStarter.start();
	}
	
	public static void start() {
		GIPCRegistry reg = GIPCLocateRegistry.createRegistry(PORT);
		
		Server server = new ServerImpl();
		reg.rebind(GIPCHalloweenSimulation.SERVER_OBJ, server);
	}
}
