package multiIPC;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Scanner;

import StringProcessors.HalloweenCommandProcessor;
import inputport.rpc.GIPCLocateRegistry;
import inputport.rpc.GIPCRegistry;
import main.BeauAndersonFinalProject;
import multiIPC.modes.ConsensusMode;
import multiIPC.modes.SimuMode;
import multiIPC.nio.NioClient;
import multiIPC.nio.NioClient.NioSender;
import multiIPC.nio.RspHandler;
import port.trace.nio.LocalCommandObserved;
import port.trace.nio.RemoteCommandExecuted;
import util.trace.TraceableInfo;
import util.trace.Tracer;

public class Simulation implements PropertyChangeListener {
	public static final String SERVER_OBJ = "ServerImpl";
	
	public static long TIMING_START = 0;
	public static int WAIT_FOR_CMD = 0;
	
	public static final String SIMULATION1_PREFIX = "1:";
	public static int SIMULATION_COMMAND_Y_OFFSET = 0;
	public static int SIMULATION_WIDTH = 500;
	public static int SIMULATION_HEIGHT = 765;
	
	static Scanner in = new Scanner(System.in);
	
	public static void main (String[] args) {
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
		
		HalloweenCommandProcessor cp = BeauAndersonFinalProject.createSimulation(
				SIMULATION1_PREFIX, 0, SIMULATION_COMMAND_Y_OFFSET, SIMULATION_WIDTH, SIMULATION_HEIGHT, 100, 100);
		cp.setConnectedToSimulation(false);
		
		// Command processor
		try {
			new Simulation(cp, name);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (NotBoundException e) {
			e.printStackTrace();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}
	
	private HalloweenCommandProcessor cp;
	private HandlerLocal gipc_handler;
	private HandlerLocal rmi_handler;
	private NioSender nio_sender;
	
	private HandlerLocal active_handler;
	
	public Simulation(HalloweenCommandProcessor cp, String name) throws MalformedURLException, NotBoundException, RemoteException {
		this.cp = cp;
		this.cp.addPropertyChangeListener(this);
		
		// Start GIPC
		GIPCRegistry gipc_registry = GIPCLocateRegistry.getRegistry("localhost", RegistryStarter.GIPC_PORT, name);
		Server gipc_server = (Server) gipc_registry.lookup(Server.class, Simulation.SERVER_OBJ);
		
		HandlerImpl gipc_handlerImpl;
		this.gipc_handler = gipc_handlerImpl = new HandlerImpl(this, gipc_server);
		gipc_server.join(gipc_handlerImpl);
		
		// Start NIO
		this.nio_sender = NioClient.startInThread(new RspHandler(cp));
		
		// Start RMI
		Registry rmi_registry = LocateRegistry.getRegistry();
		Server rmi_server = (Server) rmi_registry.lookup(SERVER_OBJ);
		HandlerImpl rmi_handlerImpl;
		rmi_handler = rmi_handlerImpl = new HandlerImpl(this, rmi_server);
		Remote stub = UnicastRemoteObject.exportObject(rmi_handlerImpl, 0);
		rmi_server.join((HandlerRemote) stub);
		
		// Set GIPC as active handler
		this.active_handler = this.gipc_handler;
		startCommandLineThread();
	}
	
	public void startCommandLineThread() {	
		// Command line input thread
		Thread cmd_thread = (new Thread() {
			@Override
			public void run() {
				while (in.hasNextLine()) {
					String line = in.next();
					if (line.equalsIgnoreCase("atomic")) {
						active_handler.sendSimuMode(SimuMode.ATOMIC);
					} else if (line.equalsIgnoreCase("basic")) {
						active_handler.sendSimuMode(SimuMode.BASIC);
					} else if (line.equalsIgnoreCase("local")) {
						active_handler.sendSimuMode(SimuMode.LOCAL);
					} else if (line.equalsIgnoreCase("time")) {
						if (cp != null) {
							final int moves = (in.hasNextInt() ? in.nextInt() : 10);
							WAIT_FOR_CMD = moves;
							TIMING_START = System.currentTimeMillis();
							System.out.println("Timing " + moves + " moves");
							for (int i = 0; i < moves; i++) {
								cp.setInputString("move 1 0");
							}
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

	@Override
	public void propertyChange(PropertyChangeEvent anEvent) {
		if (!anEvent.getPropertyName().equals("InputString")) return;
		
		String cmd = (String) anEvent.getNewValue();
		LocalCommandObserved.newCase(this, cmd);
		
		synchronized (SimuMode.class) {
			if (ConsensusMode.requireConsensus && SimuMode.isChanging())
				return;
			
			SimuMode mode = SimuMode.getMode();
			
			if (mode != SimuMode.ATOMIC) {
				this.executeCommand(cmd);
			}
			
			if (mode != SimuMode.LOCAL) {
				this.active_handler.broadcast(cmd, mode);
			} else {
				updateTimingCount();
			}
		}
	}
	
	public void executeCommand(String cmd) {
		System.err.println("Executed " + cmd);
		RemoteCommandExecuted.newCase(this, cmd);
		this.cp.processCommand(cmd);
		
		updateTimingCount();
	}

	private void updateTimingCount() {
		if (Simulation.WAIT_FOR_CMD > 0) {
			if (--Simulation.WAIT_FOR_CMD == 0) {
				System.out.println("Completed in " + (System.currentTimeMillis() - Simulation.TIMING_START) + "ms");
			}
		}
	}
}
