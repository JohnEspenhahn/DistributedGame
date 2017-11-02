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
//			port.trace.consensus.ConsensusTraceUtility.setTracing();
			AScatterGatherSelectionManager.setMaxOutstandingWrites(500);
			Simulation sim = new Simulation(cp, name, ip);
			
			int runs = 2;
			
			HandlerLocal ATOMIC_ASYNC = sim.getHandler(IPCMode.ATOMIC_ASYNC);
			ATOMIC_ASYNC.sendIPCMode(IPCMode.ATOMIC_ASYNC);
			sim.runTiming(runs);
			
			HandlerLocal ATOMIC_SYNC = sim.getHandler(IPCMode.ATOMIC_SYNC);
			ATOMIC_SYNC.sendIPCMode(IPCMode.ATOMIC_SYNC);
			sim.runTiming(runs);
			
			HandlerLocal NONATOMIC_ASYNC = sim.getHandler(IPCMode.NONATOMIC_ASYNC);
			NONATOMIC_ASYNC.sendIPCMode(IPCMode.NONATOMIC_ASYNC);
			sim.runTiming(runs);
			
			HandlerLocal NONATOMIC_SYNC = sim.getHandler(IPCMode.NONATOMIC_SYNC);
			NONATOMIC_SYNC.sendIPCMode(IPCMode.NONATOMIC_SYNC);
			sim.runTiming(runs);
			
			HandlerLocal PAXOS = sim.getHandler(IPCMode.PAXOS);
			PAXOS.sendIPCMode(IPCMode.PAXOS);
			sim.runTiming(runs);
			
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
