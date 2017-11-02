package consensus.dewan;

import consensus.ConcurrencyKind;
import consensus.ConsensusMechanism;
import consensus.ConsensusMechanismFactory;
import consensus.ReplicationSynchrony;
import consensus.asynchronous.sequential.AnAsynchronousConsensusMechanismFactory;
import consensus.paxos.sequential.ASequentialPaxosConsensusMechanismFactory;
import consensus.sessionport.AConsensusMemberLauncher;
import examples.gipc.consensus.ExampleMemberLauncher;
import gipc_sims.HandlerLocal;
import gipc_sims.Simulation;
import gipc_sims.modes.IPCMode;
import gipc_sims.modes.SimuMode;

public class SimulationConsensusLauncher extends AConsensusMemberLauncher implements HandlerLocal, ExampleMemberLauncher {
	// SimuMode
	public static final int LOCAL = 0b00001, BASIC = 0b00010, ATOMIC = 0b00011,
			// IPCMode
			NIO = 0b01000, RMI = 0b01001, GIPC = 0b01011, NONATOMIC_ASYNC = 0b11000, NONATOMIC_SYNC = 0b11001,
			ATOMIC_ASYNC = 0b11010, ATOMIC_SYNC = 0b11011, PAXOS = 0b11100;

	protected ConsensusMechanism<Integer> modeMechanism;
	protected ConsensusMechanism<String> commandMechanism;

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
		commandMechanism.setAcceptSynchrony(ReplicationSynchrony.ASYNCHRONOUS);
		commandMechanism.setConcurrencyKind(ConcurrencyKind.NON_ATOMIC);
		commandMechanism.setSequentialAccess(false);
	}
	protected void simulateNonAtomicSynchronous() {
		commandMechanism.setAcceptSynchrony(ReplicationSynchrony.ALL_SYNCHRONOUS);
		commandMechanism.setConcurrencyKind(ConcurrencyKind.NON_ATOMIC);
		commandMechanism.setSequentialAccess(false);
	}
	protected void simulateCentralized() {
		commandMechanism.setCentralized(true);
	}
	protected void simulateCentralizedSynchronous() {
		simulateNonAtomicSynchronous();
		simulateCentralized();
	}
	protected void simulateCentralizedAsynchronous() {
		simulateNonAtomicAsynchronous();
		simulateCentralized();
	}
	protected void simulateSequentialPaxos() {
		commandMechanism.setConcurrencyKind(ConcurrencyKind.SERIALIZABLE);
		commandMechanism.setPrepareSynchrony(ReplicationSynchrony.MAJORITY_SYNCHRONOUS);
		commandMechanism.setAcceptSynchrony(ReplicationSynchrony.MAJORITY_SYNCHRONOUS);
		commandMechanism.setSequentialAccess(true);
	}

	public void onIPCModeChanged(IPCMode mode) {
		switch (mode) {
		case NIO:
		case RMI:
		case GIPC:
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

	protected ConsensusMechanismFactory<Integer> meaningConsensusMechanismFactory() {
		return new AnAsynchronousConsensusMechanismFactory<>();
	}

	protected ConsensusMechanismFactory<String> greetingConsensusMechanismFactory() {
		return new ASequentialPaxosConsensusMechanismFactory<>();
	}

	@Override
	protected void createConsensusMechanisms(short anId) {
		modeMechanism = meaningConsensusMechanismFactory().createConsensusMechanism(SESSION_MANAGER_HOST,
				EXAMPLE_SESSION, memberId, portNumber, MEANING_OF_LIFE_CONSENSUS_MECHANISM_NAME, sessionChoice,
				numMembersToWaitFor());
		
		commandMechanism = greetingConsensusMechanismFactory().createConsensusMechanism(EXAMPLE_SESSION, memberId,
				GREETING_CONSENSUS_MECHANISM_NAME);
	}

	@Override
	protected void customizeConsensusMechanisms() {
		simulateNonAtomicAsynchronous(); // Default
	}

	@Override
	protected void addListenersAndVetoersToConsensusMechanisms() {
		modeMechanism.addConsensusListener(new ModeConsensusListener());
		commandMechanism.addConsensusListener(new CommandConsensusListener(sim));
	}

	protected void proposeMode(Integer aValue) {
		float proposal = modeMechanism.propose(aValue);
		modeMechanism.waitForConsensus(proposal);
	}

	@Override
	public void broadcast(String msg) {
		float proposal = commandMechanism.propose(msg);
		commandMechanism.waitForConsensus(proposal);
	}

	@Override
	public void sendSimuMode(SimuMode mode) {
		System.out.println("Proposing SimuMode " + mode);
		switch (mode) {
		case LOCAL:
			this.proposeMode(LOCAL);
			break;
		case BASIC:
			this.proposeMode(BASIC);
			break;
		case ATOMIC:
			this.proposeMode(ATOMIC);
			break;
		}
	}

	@Override
	public void sendIPCMode(IPCMode mode) {
		System.out.println("Proposing IPCMode " + mode);
		switch (mode) {
		case NIO:
			this.proposeMode(NIO);
			break;
		case RMI:
			this.proposeMode(RMI);
			break;
		case GIPC:
			this.proposeMode(GIPC);
			break;
		case NONATOMIC_ASYNC:
			this.proposeMode(NONATOMIC_ASYNC);
			break;
		case NONATOMIC_SYNC:
			this.proposeMode(NONATOMIC_SYNC);
			break;
		case ATOMIC_ASYNC:
			this.proposeMode(ATOMIC_ASYNC);
			break;
		case ATOMIC_SYNC:
			this.proposeMode(ATOMIC_SYNC);
			break;
		case PAXOS:
			this.proposeMode(PAXOS);
			break;
		}
	}

	@Override
	public void sendConsensusModes(boolean simu, boolean ipc) {
		throw new RuntimeException("SimulationConsensusLauncher does not support concensus mode changing");
	}

	@Override
	protected short numMembersToWaitFor() {
		return 3;
	}

}
