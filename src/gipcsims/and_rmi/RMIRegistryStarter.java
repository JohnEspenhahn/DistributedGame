package gipcsims.and_rmi;

import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;
import java.util.Scanner;

import gipcsims.GIPCHalloweenSimulation;
import gipcsims.SimuModeObj;

public class RMIRegistryStarter {

	public static void start() {
		Scanner s = null;
		try {
			LocateRegistry.createRegistry(1099);
			
			RMIServer server = new RMIServerImpl();
			Naming.rebind(GIPCHalloweenSimulation.SERVER_OBJ, server);
			
			System.out.println("Registry started, press enter to quit...");
			s = new Scanner(System.in);
			s.nextLine();
		} catch (Exception e) { 
		} finally {
			if (s != null) s.close();
		}
	}
}
