package consensus.dewan;

import consensus.ConsensusListener;
import consensus.ProposalState;
import gipc_sims.modes.IPCMode;
import gipc_sims.modes.SimuMode;

import static consensus.dewan.SimulationConsensusLauncher.*;

public class ModeConsensusListener implements ConsensusListener<Integer> {

	@Override
	public void newLocalProposalState(float aProposalNumber, Integer aProposal, ProposalState aProposalState) {

	}

	@Override
	public void newRemoteProposalState(float aProposalNumber, Integer aProposal, ProposalState aProposalState) {

	}

	@Override
	public void newConsensusState(Integer aState) {
		if (aState == LOCAL)
			SimuMode.set(SimuMode.LOCAL);
		else if (aState == BASIC)
			SimuMode.set(SimuMode.BASIC);
		else if (aState == ATOMIC)
			SimuMode.set(SimuMode.ATOMIC);
		else if (aState == NIO)
			IPCMode.set(IPCMode.NIO);
		else if (aState == RMI)
			IPCMode.set(IPCMode.RMI);
		else if (aState == GIPC)
			IPCMode.set(IPCMode.GIPC);
		else if (aState == NONATOMIC_ASYNC)
			IPCMode.set(IPCMode.NONATOMIC_ASYNC);
		else if (aState == NONATOMIC_SYNC)
			IPCMode.set(IPCMode.NONATOMIC_SYNC);
		else if (aState == ATOMIC_ASYNC)
			IPCMode.set(IPCMode.ATOMIC_ASYNC);
		else if (aState == ATOMIC_SYNC)
			IPCMode.set(IPCMode.ATOMIC_SYNC);
		else if (aState == PAXOS)
			IPCMode.set(IPCMode.PAXOS);
		else
			throw new RuntimeException("Unhandled state " + aState);
	}

	@Override
	public void newProposalState(float aProposalNumber, Integer aProposal, ProposalState aProposalState) {

	}

}