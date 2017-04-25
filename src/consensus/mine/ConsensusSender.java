package consensus.mine;

public interface ConsensusSender<E extends Enum<?>> {
	
	void send(E val);

}
