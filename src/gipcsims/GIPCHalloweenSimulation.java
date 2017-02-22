package gipcsims;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Scanner;

import StringProcessors.HalloweenCommandProcessor;
import gipcsims.and_nio.NioClient;
import gipcsims.and_nio.NioClient.NioSender;
import gipcsims.and_nio.RspHandler;
import gipcsims.and_rmi.RMIServer;
import gipcsims.and_rmi.RspHandlerRMIImpl;
import gipcsims.and_rmi.RspHandlerRMILocal;
import inputport.rpc.GIPCLocateRegistry;
import inputport.rpc.GIPCRegistry;
import main.BeauAndersonFinalProject;
import port.trace.nio.LocalCommandObserved;
import util.trace.TraceableInfo;
import util.trace.Tracer;

public class GIPCHalloweenSimulation implements PropertyChangeListener {
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
		Tracer.showInfo(true);
		Tracer.setKeywordPrintStatus(GIPCHalloweenSimulation.class, true);
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
			new GIPCHalloweenSimulation(cp, name);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (NotBoundException e) {
			e.printStackTrace();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}
	
	private HalloweenCommandProcessor cp;
	private RspHandlerGIPCLocal gipc_handler;
	private RspHandlerRMILocal rmi_handler;
	private NioSender nio_sender;
	
	public GIPCHalloweenSimulation(HalloweenCommandProcessor cp, String name) throws MalformedURLException, NotBoundException, RemoteException {
		this.cp = cp;
		this.cp.addPropertyChangeListener(this);
		
		// Start GIPC
		GIPCRegistry registry = GIPCLocateRegistry.getRegistry("localhost", GIPCRegistryStarter.PORT, name);
		Server gipc_server = (Server) registry.lookup(Server.class, GIPCHalloweenSimulation.SERVER_OBJ);
		
		RspHandlerGIPCImpl gipc_handlerImpl;
		this.gipc_handler = gipc_handlerImpl = new RspHandlerGIPCImpl(cp, gipc_server);
		gipc_server.join(gipc_handlerImpl);
		
		// Start NIO
		this.nio_sender = NioClient.startInThread(new RspHandler(cp));
		
		// Start RMI
		RMIServer rmi_server = (RMIServer) Naming.lookup(SERVER_OBJ);
		
		RspHandlerRMIImpl rmi_handlerImpl;
		rmi_handler = rmi_handlerImpl = new RspHandlerRMIImpl(cp, rmi_server);
		rmi_server.join(rmi_handlerImpl);
		
		GIPCHalloweenSimulation.startCommandLineThread(cp, gipc_handler);
	}
	
	public static void startCommandLineThread(HalloweenCommandProcessor cp, RspHandlerGIPCLocal handler) {	
		// Command line input thread
		Thread cmd_thread = (new Thread() {
			@Override
			public void run() {
				while (in.hasNextLine()) {
					String line = in.next();
					if (line.equalsIgnoreCase("atomic")) {
						handler.setServerMode(SimuMode.ATOMIC);
					} else if (line.equalsIgnoreCase("basic")) {
						handler.setServerMode(SimuMode.BASIC);
					} else if (line.equalsIgnoreCase("local")) {
						handler.setServerMode(SimuMode.LOCAL);
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
		
		String newCommand = (String) anEvent.getNewValue();
		LocalCommandObserved.newCase(this, newCommand);
		
		gipc_handler.handleLocalCommand(newCommand);
	}

}
