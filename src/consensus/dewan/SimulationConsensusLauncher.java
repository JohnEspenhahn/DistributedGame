package consensus.dewan;

import consensus.ConcurrencyKind;
import consensus.ReplicationSynchrony;
import examples.gipc.consensus.paxos.APaxosMemberLauncher;
import gipc_sims.HandlerLocal;
import gipc_sims.Simulation;
import gipc_sims.modes.IPCMode;
import gipc_sims.modes.SimuMode;

public class SimulationConsensusLauncher extends APaxosMemberLauncher implements HandlerLocal {
							// SimuMode
	public static final int LOCAL  = 0b00001, 
							BASIC  = 0b00010, 
							ATOMIC = 0b00011,
							// IPCMode
							NIO    = 0b01000,
							RMI    = 0b01001,
							GIPC   = 0b01011, 
							NONATOMIC_ASYNC 
								   = 0b11000,
			 				NONATOMIC_SYNC
			 					   = 0b11001,
							ATOMIC_ASYNC
								   = 0b11010,
							ATOMIC_SYNC
								   = 0b11011,
							PAXOS  = 0b11100;
	
	private SimulationConsensusLauncher(String aLocalName, int aPortNumber) {
		super(aLocalName, aPortNumber);
	}
	
	private static Simulation sim;
	private static SimulationConsensusLauncher singleton;
	public static SimulationConsensusLauncher get(Simulation sim, String aLocalName, int aPortNumber) {
		if (singleton == null) {
			SimulationConsensusLauncher.sim = sim;
			singleton = new SimulationConsensusLauncher(aLocalName, aPortNumber);
		}
		
		return singleton;
	}
	
	protected void simulateNonAtomicAsynchronous() {
		greetingMechanism.setAcceptSynchrony(ReplicationSynchrony.ASYNCHRONOUS);
		greetingMechanism.setConcurrencyKind(ConcurrencyKind.NON_ATOMIC);
		
		meaningOfLifeMechanism.setAcceptSynchrony(ReplicationSynchrony.ASYNCHRONOUS);
		meaningOfLifeMechanism.setConcurrencyKind(ConcurrencyKind.NON_ATOMIC);
	}
	protected void simulateNonAtomicSynchronous() {
		greetingMechanism.setAcceptSynchrony(ReplicationSynchrony.ALL_SYNCHRONOUS);
		greetingMechanism.setConcurrencyKind(ConcurrencyKind.NON_ATOMIC);
		
		meaningOfLifeMechanism.setAcceptSynchrony(ReplicationSynchrony.ALL_SYNCHRONOUS);
		meaningOfLifeMechanism.setConcurrencyKind(ConcurrencyKind.NON_ATOMIC);
	}
	protected void simulateCentralized() {
		greetingMechanism.setCentralized(true);
		meaningOfLifeMechanism.setCentralized(true);
	}
	protected void simulateCentralizedSynchronous() {
		simulateNonAtomicSynchronous();
		simulateCentralized();
	}
	protected void simulateCentralizedAsynchronous() {
		simulateNonAtomicAsynchronous();
		simulateCentralized();
	}
	protected void simulateBasicPaxos() {
		overrideRetry = true;
		
		greetingMechanism.setCentralized(false);
		greetingMechanism.setConcurrencyKind(ConcurrencyKind.SERIALIZABLE);
		greetingMechanism
				.setPrepareSynchrony(ReplicationSynchrony.MAJORITY_SYNCHRONOUS);
		greetingMechanism
				.setAcceptSynchrony(ReplicationSynchrony.MAJORITY_SYNCHRONOUS);
		
		meaningOfLifeMechanism.setCentralized(false);
		meaningOfLifeMechanism.setConcurrencyKind(ConcurrencyKind.SERIALIZABLE);
		meaningOfLifeMechanism
				.setPrepareSynchrony(ReplicationSynchrony.MAJORITY_SYNCHRONOUS);
		meaningOfLifeMechanism
				.setAcceptSynchrony(ReplicationSynchrony.MAJORITY_SYNCHRONOUS);
	}
	protected void simulateSequentialPaxos() {
		simulateBasicPaxos();
		greetingMechanism.setSequentialAccess(true);
		meaningOfLifeMechanism.setSequentialAccess(true);
		overrideRetry = false;
	}
	
	public void onIPCModeChanged(IPCMode mode) {
		switch (mode) {
		case NIO: case RMI: case GIPC:
			break;
		case NONATOMIC_ASYNC:
			simulateNonAtomicAsynchronous();
			break;
		case NONATOMIC_SYNC:
			simulateNonAtomicSynchronous();
			break;
		case ATOMIC_ASYNC:
			simulateCentralizedAsynchronous();
			break;
		case ATOMIC_SYNC:
			simulateCentralizedSynchronous();
			break;
		case PAXOS:
			simulateSequentialPaxos();
			break;
		}
	}
	
	@Override
	protected void customizeConsensusMechanisms() {
		simulateNonAtomicAsynchronous();
	}
	
	@Override
	protected void addListenersAndVetoersToMeaningMechanism() {
		meaningOfLifeMechanism.addConsensusListener(new ModeConsensusListener());
	}
	
	@Override
	protected void addListenersAndVetoersToGreetingMechanism() {
		greetingMechanism.addConsensusListener(new CommandConsensusListener(sim));
	}

	@Override
	public void broadcast(String msg) {
		this.proposeGreeting(msg);
	}

	@Override
	public void sendSimuMode(SimuMode mode) {
		System.out.println("Proposing SimuMode " + mode);		
		switch (mode) {
		case LOCAL:
			this.proposeMeaning(LOCAL);
			break;
		case BASIC:
			this.proposeMeaning(BASIC);
			break;
		case ATOMIC:
			this.proposeMeaning(ATOMIC);
			break;
		}
	}

	@Override
	public void sendIPCMode(IPCMode mode) {
		System.out.println("Proposing IPCMode " + mode);
		switch (mode) {
		case NIO:
			this.proposeMeaning(NIO);
			break;
		case RMI:
			this.proposeMeaning(RMI);
			break;
		case GIPC:
			this.proposeMeaning(GIPC);
			break;
		case NONATOMIC_ASYNC:
			this.proposeMeaning(NONATOMIC_ASYNC);
			break;
		case NONATOMIC_SYNC:
			this.proposeMeaning(NONATOMIC_SYNC);
			break;
		case ATOMIC_ASYNC:
			this.proposeMeaning(ATOMIC_ASYNC);
			break;
		case ATOMIC_SYNC:
			this.proposeMeaning(ATOMIC_SYNC);
			break;
		case PAXOS:
			this.proposeMeaning(PAXOS);
			break;
		}
	}

	@Override
	public void sendConsensusModes(boolean simu, boolean ipc) {
		throw new RuntimeException("SimulationConsensusLauncher does not support concensus mode changing");		
	}

}
