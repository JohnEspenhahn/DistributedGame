package rmisims;

import java.rmi.registry.LocateRegistry;
import java.util.Scanner;

public class RMIRegistryStarter {

	public static void main(String[] args) {
		Scanner s = null;
		try {
			LocateRegistry.createRegistry(1099);
			System.out.println("Registry started, press enter to quit...");
			s = new Scanner(System.in);
			s.nextLine();
		} catch (Exception e) { 
		} finally {
			if (s != null) s.close();
		}
	}
}
