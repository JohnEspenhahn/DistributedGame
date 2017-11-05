package gipc_sims;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import StringProcessors.HalloweenCommandProcessor;
import consensus.dewan.SimulationConsensusLauncher;
import gipc_sims.modes.ConsensusMode;
import gipc_sims.modes.IPCMode;
import gipc_sims.modes.SimuMode;
import gipc_sims.nio.NioClient;
import gipc_sims.nio.RspHandler;
import inputport.rpc.GIPCLocateRegistry;
import inputport.rpc.GIPCRegistry;
import main.BeauAndersonFinalProject;
import port.trace.nio.LocalCommandObserved;
import port.trace.nio.RemoteCommandExecuted;
import util.trace.TraceableInfo;
import util.trace.Tracer;

public class Simulation implements PropertyChangeListener {
	public static final String SERVER_OBJ = "ServerImpl";
	
	public static final String SIMULATION1_PREFIX = "1:";
	public static int SIMULATION_COMMAND_Y_OFFSET = 0;
	public static int SIMULATION_WIDTH = 500;
	public static int SIMULATION_HEIGHT = 765;
	
	static Scanner in = new Scanner(System.in);
	
	public static void main (String[] args) {
		String ip = args.length > 0 ? args[0] : "localhost";
		System.out.print("Please name yourself > ");
		String name = in.nextLine();
		
		Tracer.showWarnings(false);
		Tracer.showInfo(false);
		Tracer.setKeywordPrintStatus(Simulation.class, true);
		// Show the current thread in each log item
		Tracer.setDisplayThreadName(true);
		 // show the name of the traceable class in each log item
		TraceableInfo.setPrintTraceable(true);
		// show the current time in each log item
		TraceableInfo.setPrintTime(true);
		
		// ExtensibleSerializationTraceUtility.setTracing();

		HalloweenCommandProcessor cp = BeauAndersonFinalProject.createSimulation(
				SIMULATION1_PREFIX, 0, SIMULATION_COMMAND_Y_OFFSET, SIMULATION_WIDTH, SIMULATION_HEIGHT, 100, 100);
		cp.setConnectedToSimulation(false);
		
		// Command processor
		try {
			Simulation sim = new Simulation(cp, name, ip);
			sim.startCommandLineThread();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (NotBoundException e) {
			e.printStackTrace();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}
	
	private HalloweenCommandProcessor cp;
	private Map<IPCMode, HandlerLocal> handlers;
	
	public Simulation(HalloweenCommandProcessor cp, String name, String ip) throws MalformedURLException, NotBoundException, RemoteException {
		this.cp = cp;
		this.cp.addPropertyChangeListener(this);
		
		this.handlers = new HashMap<IPCMode, HandlerLocal>();
		
		// Start GIPC
		port.trace.consensus.ConsensusTraceUtility.setTracing();
//		SerializerSelector.setSerializerFactory(new MySerializerFactory());
		GIPCRegistry gipc_registry = GIPCLocateRegistry.getRegistry(ip, RegistryStarter.GIPC_PORT, name);
		Server gipc_server = (Server) gipc_registry.lookup(Server.class, Simulation.SERVER_OBJ);
		
		HandlerImpl gipc_handlerImpl;
		this.handlers.put(IPCMode.GIPC, gipc_handlerImpl = new HandlerImpl(this, gipc_server));
		gipc_server.join(gipc_handlerImpl);
		
		// Start NIO
		this.handlers.put(IPCMode.NIO, NioClient.startInThread(ip, new RspHandler(this)));
		
		// Start RMI
		Registry rmi_registry = LocateRegistry.getRegistry(ip);
		Server rmi_server = (Server) rmi_registry.lookup(SERVER_OBJ);
		HandlerImpl rmi_handlerImpl;
		this.handlers.put(IPCMode.RMI, rmi_handlerImpl = new HandlerImpl(this, rmi_server));
		Remote stub = UnicastRemoteObject.exportObject(rmi_handlerImpl, 0);
		rmi_server.join((HandlerRemote) stub);
		
		// Start consensus object
		short port = (short) ((name.hashCode() % 2000) + 7000);
		System.out.println("On port/id: " + port); // debug
		SimulationConsensusLauncher scl = SimulationConsensusLauncher.get(this, "" + port, port);
		IPCMode.scl = scl; // Need to store for IPC to update
		this.handlers.put(IPCMode.ATOMIC_ASYNC, scl);
		this.handlers.put(IPCMode.ATOMIC_SYNC, scl);
		this.handlers.put(IPCMode.NONATOMIC_ASYNC, scl);
		this.handlers.put(IPCMode.NONATOMIC_SYNC, scl);
		this.handlers.put(IPCMode.PAXOS, scl);
	}
	
	public void startCommandLineThread() {	
		// Command line input thread
		Thread cmd_thread = (new Thread() {
			@Override
			public void run() {
				while (in.hasNextLine()) {
					String line = in.next();
					if (line.equalsIgnoreCase("atomic")) {
						getHandler(IPCMode.RMI).sendSimuMode(SimuMode.ATOMIC);
					} else if (line.equalsIgnoreCase("basic")) {
						getHandler(IPCMode.RMI).sendSimuMode(SimuMode.BASIC);
					} else if (line.equalsIgnoreCase("local")) {
						getHandler(IPCMode.RMI).sendSimuMode(SimuMode.LOCAL);
					} else if (line.equalsIgnoreCase("gipc")) {
						getHandler(IPCMode.RMI).sendIPCMode(IPCMode.GIPC);
					} else if (line.equalsIgnoreCase("rmi")) {
						getHandler(IPCMode.RMI).sendIPCMode(IPCMode.RMI);
					} else if (line.equalsIgnoreCase("nio")) {
						getHandler(IPCMode.RMI).sendIPCMode(IPCMode.NIO);
					} else if (line.equalsIgnoreCase("simuconsensus")) {
						getHandler(IPCMode.RMI).sendConsensusModes(!ConsensusMode.requireSimuConsensus, ConsensusMode.requireIPCConsensus);
					} else if (line.equalsIgnoreCase("ipcconsensus")) {
						getHandler(IPCMode.RMI).sendConsensusModes(ConsensusMode.requireSimuConsensus, !ConsensusMode.requireIPCConsensus);
					} else if (line.equalsIgnoreCase("NONATOMIC_ASYNC")) {
						getHandler(IPCMode.RMI).sendIPCMode(IPCMode.NONATOMIC_ASYNC);
					} else if (line.equalsIgnoreCase("NONATOMIC_SYNC")) {
						getHandler(IPCMode.RMI).sendIPCMode(IPCMode.NONATOMIC_SYNC);
					} else if (line.equalsIgnoreCase("ATOMIC_ASYNC")) {
						getHandler(IPCMode.RMI).sendIPCMode(IPCMode.ATOMIC_ASYNC);
					} else if (line.equalsIgnoreCase("ATOMIC_SYNC")) {
						getHandler(IPCMode.RMI).sendIPCMode(IPCMode.ATOMIC_SYNC);
					} else if (line.equalsIgnoreCase("PAXOS")) {
						getHandler(IPCMode.RMI).sendIPCMode(IPCMode.PAXOS);
					} else if (line.equalsIgnoreCase("time")) {
						if (cp != null) {
							final int moves = (in.hasNextInt() ? in.nextInt() : 10);
							runTiming(moves);
						} else {
							System.err.println("Timing not supported without command processor!");
						}
					} else if (line.equalsIgnoreCase("showinfo")) {
						Tracer.showInfo(true);
					} else if (line.equalsIgnoreCase("hideinfo")) {
						Tracer.showInfo(false);
					} else if (cp != null) {
						line += in.nextLine();
						cp.setInputString(line);
					}
				}
				in.close();
			}
		});
		cmd_thread.setName("cmdline");
		cmd_thread.setDaemon(true);
		cmd_thread.run();
	}
	
	public void runTiming(int moves) {		
		long TIMING_START = System.currentTimeMillis();
		System.out.println("Timing " + moves + " moves");
		for (int i = 0; i < moves; i++) {
			cp.setInputString(String.format("move %d 0", Math.random() >= 0.5 ? 1 : -1));
		}
		System.out.println("Finished in " + (System.currentTimeMillis() - TIMING_START) + " ms");
	}

	@Override
	public void propertyChange(PropertyChangeEvent anEvent) {
		if (!anEvent.getPropertyName().equals("InputString")) return;
		
		String cmd = (String) anEvent.getNewValue();
		LocalCommandObserved.newCase(this, cmd);
		
		synchronized (SimuMode.class) {
			if (ConsensusMode.requireSimuConsensus && SimuMode.isChanging())
				return;
			
			SimuMode mode = SimuMode.get();
			
			if (mode != SimuMode.ATOMIC) {
				this.executeCommand(cmd);
			}
			
			if (mode != SimuMode.LOCAL) {
				getActiveHandler().broadcast(cmd);
			}
		}
	}
	
	public void executeCommand(String cmd) {
		// System.err.println("Executed " + cmd);
		RemoteCommandExecuted.newCase(this, cmd);
		this.cp.processCommand(cmd);
	}
	
	protected HandlerLocal getHandler(IPCMode mode) {
		return this.handlers.get(mode);
	}
	
	protected HandlerLocal getActiveHandler() {
		return this.handlers.get(IPCMode.get());
	}
}
