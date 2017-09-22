package nio_sims.test;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import nio_sims.NioBroadcastServer;
import nio_sims.DistroHalloweenSimulation;

public class NioTest {
	
	static final String headless = "-Djava.awt.headless=true";
	
	static final int RUNTIME = 17;
	static final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
	
	public static void main(String[] args) throws IOException, InterruptedException {
		ProcessWatcher server, c1, c2, c3;
		
		server = new ProcessWatcher("server", NioBroadcastServer.class);
		server.start();
		server.println("atomic");
		
		server.waitForEvent(InputDeserializer.SocketChannelAcceptingEvent);
		
		c1 = createClient("c1");
		c2 = createClient("c2");
		c3 = createClient("c3");
		
		Thread.sleep(2000);
		
		// Terminate children
		c1.terminate();
		c2.terminate();
		c3.terminate();
		server.terminate();
	}
	
	private static ProcessWatcher createClient(String name) throws IOException {
		ProcessWatcher p = new ProcessWatcher(name, DistroHalloweenSimulation.class);
		p.start(headless);
		p.println("atomic");
		
		return p;
	}
	
}
