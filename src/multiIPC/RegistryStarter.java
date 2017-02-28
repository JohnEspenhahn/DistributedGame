package multiIPC;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;

import inputport.rpc.GIPCLocateRegistry;
import inputport.rpc.GIPCRegistry;
import multiIPC.nio.NioBroadcastServer;

public class RegistryStarter {
	public static final int GIPC_PORT = 15247;
	public static final int RMI_PORT = 1099;

	public static void main(String[] args) {
		// Start different IPC modes, each with their own server stack
		RegistryStarter.startGIPC(new ServerImpl());
		RegistryStarter.startRMI(new ServerImpl());
		NioBroadcastServer.start();
		
		System.out.println("Registries started");
	}
	
	public static void startGIPC(Server server) {
		GIPCRegistry reg = GIPCLocateRegistry.createRegistry(GIPC_PORT);
		reg.rebind(Simulation.SERVER_OBJ, server);
	}
	
	public static void startRMI(Server server) {
		try {
			LocateRegistry.createRegistry(RMI_PORT);
			
			Remote stub = UnicastRemoteObject.exportObject(server, 0);
			Naming.rebind(Simulation.SERVER_OBJ, stub);
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}
}
