package nio_sims.test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import com.hahn.doteditdistance.utils.logger.Logger;
import com.hahn.doteditdistance.utils.pmanagement.ProcessWatcher;

import nio_sims.DistroHalloweenSimulation;
import nio_sims.NioBroadcastServer;
import port.trace.nio.SocketChannelConnectFinished;

public class NioTest {	
	static final int RUNTIME = 17;
	static final String FILE_NAME = "log-output";
	static final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
	
	public static void main(String[] args) throws IOException, InterruptedException {		
		ProcessWatcher server, c1, c2, c3;
		String[] processNames = new String[] { "0", "1", "2" };
		
		server = new NioProcessWatcher("0", NioBroadcastServer.class);
		server.start(processNames);	
		server.println("atomic");
		server.flush();
		server.waitForEvent(SocketChannelAccepting.class);
		
		c1 = createClient("1", processNames);
		c1.println("atomic");
		c1.flush();
		c1.waitForEvent(SocketChannelConnectFinished.class);
		
		c2 = createClient("2", processNames);
		c2.println("atomic");
		c2.flush();
		c2.waitForEvent(SocketChannelConnectFinished.class);
		
		Thread.sleep(1500);
		
		// Different cases
		c1.println("move 100 0");
		c1.flush();
		
		Thread.sleep(4000);
		
		// Terminate children
		c1.terminate();
		c2.terminate();
		server.terminate();
		
		System.out.println("DONE");
		System.exit(0);
	}
	
	private static ProcessWatcher createClient(String name, String...otherProcesses) throws IOException {
		ProcessWatcher p = new NioProcessWatcher(name, DistroHalloweenSimulation.class);
		
		String[] args = new String[otherProcesses.length + 1];
		args[0] = name;
		System.arraycopy(otherProcesses, 0, args, 1, otherProcesses.length);
		
		p.start(args);
		
		return p;
	}
	
}
