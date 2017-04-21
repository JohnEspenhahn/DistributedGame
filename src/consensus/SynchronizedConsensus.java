package consensus;

public class SynchronizedConsensus<E extends Enum<?>> {
	private boolean invalid;
	private E state;
	
	private synchronized void waitForConsensus() {
		while (!invalid) {
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	public E getState() {
		waitForConsensus();
		return state;
	}

	public void invalidate() {
		invalid = true;
	}
	
	public void propose(ConsensusSender<E> cs, E val) {
		if (!invalid) {
			invalidate();
			cs.send(val);
			waitForConsensus();
		}
	}
	
	public void learn(E s) {
		if (!invalid) return;
		
		state = s;
		invalid = false;
	}
}
