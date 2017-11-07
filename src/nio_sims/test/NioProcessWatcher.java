package nio_sims.test;

import com.hahn.doteditdistance.utils.pmanagement.InputDeserializer;
import com.hahn.doteditdistance.utils.pmanagement.ProcessWatcher;

public class NioProcessWatcher extends ProcessWatcher {

	public NioProcessWatcher(String name, Class<?> clazz) {
		super(name, clazz);
	}
	
	@Override
	public InputDeserializer getInputDeserializer() {
		return new SimInputDeserializer(getProcess());
	}

}
