package nio_sims.test;

import com.hahn.doteditdistance.utils.pmanagement.InputDeserializer;

import port.trace.nio.SocketChannelConnectFinished;
import port.trace.nio.SocketChannelRead;
import port.trace.nio.SocketChannelWritten;

public class SimInputDeserializer extends InputDeserializer {

	protected SimInputDeserializer(Process p) {
		super(p);
		
		registerEvent(SocketChannelConnectFinished.class);
		registerEvent(SocketChannelAccepting.class);
		registerEvent(SocketChannelWritten.class);
		registerEvent(SocketChannelRead.class);
	}

}
