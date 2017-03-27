package gipc_sims;

import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import StringProcessors.HalloweenCommandProcessor;
import gipc_sims.modes.IPCMode;
import gipc_sims.modes.SimuMode;
import inputport.datacomm.simplex.buffer.nio.AScatterGatherSelectionManager;
import main.BeauAndersonFinalProject;
import util.trace.TraceableInfo;
import util.trace.Tracer;

public class TimeTester {

	
	public static void main (String[] args) {
		String ip = args.length > 0 ? args[0] : "localhost";
		String name = "timertester";
		
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
				Simulation.SIMULATION1_PREFIX, 0, Simulation.SIMULATION_COMMAND_Y_OFFSET, Simulation.SIMULATION_WIDTH, Simulation.SIMULATION_HEIGHT, 100, 100);
		cp.setConnectedToSimulation(false);
		
		// Command processor
		try {
			AScatterGatherSelectionManager.setMaxOutstandingWrites(500);
			Simulation sim = new Simulation(cp, name, ip);
			
			HandlerLocal RMI = sim.getHandler(IPCMode.RMI);
			RMI.sendIPCMode(IPCMode.GIPC);
			for (SimuMode simuMode: SimuMode.values()) {
				RMI.sendSimuMode(simuMode);
				sim.runTiming(500);	
			}
			
			RMI.sendIPCMode(IPCMode.NIO);
			for (SimuMode simuMode: SimuMode.values()) {
				RMI.sendSimuMode(simuMode);
				sim.runTiming(500);	
			}
			
			RMI.sendIPCMode(IPCMode.RMI);
			for (SimuMode simuMode: SimuMode.values()) {
				RMI.sendSimuMode(simuMode);
				sim.runTiming(500);	
			}
			
			System.out.println("Finished timing");
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (NotBoundException e) {
			e.printStackTrace();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}
	
}
