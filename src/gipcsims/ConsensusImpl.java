package gipcsims;

public class ConsensusImpl implements Consensus {
	private boolean changing;
	
	public ConsensusImpl() {
		this.changing = false;
	}

	@Override
	public boolean claim() {
		synchronized (this) {
			if (!this.changing) {
				this.changing = true;
				return true;
			} else {
				return false;
			}
		}
	}

	@Override
	public void release() {
		this.changing = false;
	}
	
	@Override
	public boolean isFree() {
		synchronized (this) {
			return !this.changing;
		}
	}

}
