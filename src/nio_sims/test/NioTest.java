package nio_sims.test;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import nio_sims.DistroHalloweenSimulation;
import nio_sims.NioBroadcastServer;
import nio_sims.test.trace.SocketChannelAccepting;
import nio_sims.test.trace.SocketChannelRead;
import port.trace.nio.SocketChannelConnectFinished;

public class NioTest {	
	static final int RUNTIME = 17;
	static final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
	
	public static void main(String[] args) throws IOException, InterruptedException {
		ProcessWatcher server, c1, c2, c3;
		
		server = new ProcessWatcher("server", NioBroadcastServer.class);
		server.start(new String[] { "c1", "c2" });	
		server.waitForEvent(SocketChannelAccepting.class);
		server.println("atomic");
		
		c1 = createClient("c1", new String[] { "server", "c2" });
		c1.waitForEvent(SocketChannelConnectFinished.class);
		c1.println("atomic");
		
		c2 = createClient("c2", new String[] { "server", "c1" });
		c2.waitForEvent(SocketChannelConnectFinished.class);
		c2.println("atomic");
		
		Thread.sleep(200);
		
		c1.println("move 100 0");
		c2.println("take 1");
		
		// Client 1 should get both back (in atomic mode)
		c1.waitForEvent(SocketChannelRead.class);
		c1.waitForEvent(SocketChannelRead.class);
		
		// Terminate children
		c1.terminate();
		c2.terminate();
		server.terminate();
	}
	
	private static ProcessWatcher createClient(String name, String...otherProcesses) throws IOException {
		ProcessWatcher p = new ProcessWatcher(name, DistroHalloweenSimulation.class);
		
		String[] args = new String[otherProcesses.length + 1];
		args[0] = name;
		System.arraycopy(otherProcesses, 0, args, 1, otherProcesses.length);
		
		p.start(args);
		
		return p;
	}
	
}
