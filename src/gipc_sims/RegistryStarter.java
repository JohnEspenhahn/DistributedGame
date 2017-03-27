package gipc_sims;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;

import gipc_sims.modes.ServersSynchronizedMode;
import gipc_sims.nio.NioBroadcastServer;
import inputport.datacomm.simplex.buffer.nio.AScatterGatherSelectionManager;
import inputport.rpc.GIPCLocateRegistry;
import inputport.rpc.GIPCRegistry;

public class RegistryStarter {
	public static final int GIPC_PORT = 15247;
	public static final int RMI_PORT = 1099;

	public static void main(String[] args) {		
		try {
			// Start different IPC modes, each with their own server stack
			RegistryStarter.startGIPC(new ServerImpl());
			RegistryStarter.startRMI(new ServerImpl());
			NioBroadcastServer.start();
			
			System.out.println("Registries started");
			
			// Control server settings from terminal
			Scanner s = new Scanner(System.in);
			while (s.hasNext()) {
				String line = s.nextLine();
				if (line.equals("synchronized")) {
					ServersSynchronizedMode.setSynchronized(true);
				} else if (line.equals("unsynchronized")) {
					ServersSynchronizedMode.setSynchronized(false);
				}
			}
			s.close();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}
	
	public static void startGIPC(Server server) {
		AScatterGatherSelectionManager.setMaxOutstandingWrites(500);
		GIPCRegistry reg = GIPCLocateRegistry.createRegistry(GIPC_PORT);
		reg.rebind(Simulation.SERVER_OBJ, server);
	}
	
	public static void startRMI(Server server) {
		try {
			Registry reg = LocateRegistry.createRegistry(RMI_PORT);
			reg.rebind(Simulation.SERVER_OBJ, server);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}
}
