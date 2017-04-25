package consensus.dewan;

import consensus.ConsensusListener;
import consensus.ProposalState;
import gipc_sims.Simulation;

public class CommandConsensusListener implements ConsensusListener<String> {

	private Simulation sim;
	
	public CommandConsensusListener(Simulation sim) {
		this.sim = sim;
	}
	
	@Override
	public void newConsensusState(String aState) {
		// System.out.println("Command:" + aState);
		sim.executeCommand(aState);
	}

	@Override
	public void newLocalProposalState(float aProposalNumber, String aProposal, ProposalState aProposalState) {

	}

	@Override
	public void newRemoteProposalState(float aProposalNumber, String aProposal, ProposalState aProposalState) {

	}

	@Override
	public void newProposalState(float aProposalNumber, String aProposal, ProposalState aProposalState) {

	}

}
