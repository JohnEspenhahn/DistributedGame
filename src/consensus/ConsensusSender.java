package consensus;

public interface ConsensusSender<E extends Enum<?>> {
	
	void send(E val);

}
