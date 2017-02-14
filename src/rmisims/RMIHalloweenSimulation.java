package rmisims;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Scanner;

import StringProcessors.HalloweenCommandProcessor;
import main.BeauAndersonFinalProject;
import port.trace.nio.LocalCommandObserved;
import util.trace.TraceableInfo;
import util.trace.Tracer;

public class RMIHalloweenSimulation implements PropertyChangeListener {
	public static final String SERVER_OBJ = "ServerImpl";
	
	public static long TIMING_START = 0;
	public static int WAIT_FOR_CMD = 0;
	
	public static final String SIMULATION1_PREFIX = "1:";
	public static int SIMULATION_COMMAND_Y_OFFSET = 0;
	public static int SIMULATION_WIDTH = 500;
	public static int SIMULATION_HEIGHT = 765;
	
	public static void main (String[] args) {
		Tracer.showWarnings(false);
		Tracer.showInfo(true);
		Tracer.setKeywordPrintStatus(RMIHalloweenSimulation.class, true);
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
			new RMIHalloweenSimulation(cp);
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (NotBoundException e) {
			e.printStackTrace();
		}
	}
	
	private HalloweenCommandProcessor cp;
	private RspHandlerLocal handler;
	
	public RMIHalloweenSimulation(HalloweenCommandProcessor cp) throws RemoteException, MalformedURLException, NotBoundException {
		this.cp = cp;
		this.cp.addPropertyChangeListener(this);
		
		Server server = (Server) Naming.lookup(SERVER_OBJ);
		
		RspHandlerImpl handlerImpl;
		handler = handlerImpl = new RspHandlerImpl(cp, server);
		server.join(handlerImpl);
		
		RMIHalloweenSimulation.startCommandLineThread(cp, handler);
	}
	
	public static void startCommandLineThread(HalloweenCommandProcessor cp, RspHandlerLocal handler) {	
		// Command line input thread
		Thread cmd_thread = (new Thread() {
			@Override
			public void run() {
				Scanner in = new Scanner(System.in);
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
		
		handler.handleLocalCommand(newCommand);
	}

}
