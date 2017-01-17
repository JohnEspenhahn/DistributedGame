package distrosims;

import main.BeauAndersonFinalProject;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import StringProcessors.HalloweenCommandProcessor;
import distrosims.NioClient.NioSender;

public class DistroHalloweenSimulation implements PropertyChangeListener {
	public static final String SIMULATION1_PREFIX = "1:";
	public static int SIMULATION_COMMAND_Y_OFFSET = 0;
	public static int SIMULATION_WIDTH = 1200;
	public static int SIMULATION_HEIGHT = 765;
	
	public static void main (String[] args) {
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
		if (this.sender == null) System.err.println("Null sender!");
		else this.sender.send(newCommand.getBytes());
	}

}
