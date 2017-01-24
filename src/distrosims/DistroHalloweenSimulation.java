package distrosims;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import StringProcessors.HalloweenCommandProcessor;
import distrosims.NioClient.NioSender;
import main.BeauAndersonFinalProject;
import port.trace.nio.LocalCommandObserved;
import util.trace.TraceableInfo;
import util.trace.Tracer;

public class DistroHalloweenSimulation implements PropertyChangeListener {
	public static final String SIMULATION1_PREFIX = "1:";
	public static int SIMULATION_COMMAND_Y_OFFSET = 0;
	public static int SIMULATION_WIDTH = 1200;
	public static int SIMULATION_HEIGHT = 765;
	
	public static void main (String[] args) {
		Tracer.showWarnings(false);
		Tracer.showInfo(true);
		Tracer.setKeywordPrintStatus(DistroHalloweenSimulation.class, true);
		Tracer.setKeywordPrintStatus(NioClient.class, true);
		Tracer.setKeywordPrintStatus(RspHandler.class, true);
		// Show the current thread in each log item
		Tracer.setDisplayThreadName(true);
		 // show the name of the traceable class in each log item
		TraceableInfo.setPrintTraceable(true);
		// show the current time in each log item
		TraceableInfo.setPrintTime(true);
		
		HalloweenCommandProcessor cp = BeauAndersonFinalProject.createSimulation(
				SIMULATION1_PREFIX, 0, SIMULATION_COMMAND_Y_OFFSET, SIMULATION_WIDTH, SIMULATION_HEIGHT, 100, 100);
		
		// Command processor
		new DistroHalloweenSimulation(cp);
	}
	
	private HalloweenCommandProcessor cp;
	private NioSender sender;
	
	public DistroHalloweenSimulation(HalloweenCommandProcessor cp) {
		this.cp = cp;
		this.cp.addPropertyChangeListener(this);
		
		this.sender = NioClient.startInThread(new RspHandler(cp)); 
	}

	@Override
	public void propertyChange(PropertyChangeEvent anEvent) {
		if (!anEvent.getPropertyName().equals("InputString")) return;
		
		String newCommand = (String) anEvent.getNewValue();
		LocalCommandObserved.newCase(this, newCommand);
		
		if (this.sender == null) System.err.println("Null sender!");
		else this.sender.send(newCommand.getBytes());
	}

}
