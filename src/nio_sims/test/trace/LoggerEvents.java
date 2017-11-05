package nio_sims.test.trace;

import java.nio.channels.SocketChannel;

public class LoggerEvents {
	
	public static void onConnected(SocketChannel channel, String otherProcessName) {
		Logger.get().registerChannel(channel, otherProcessName);
	}
	
	public static void onFinishedConnecting() {
		Logger.get().logLocalEvent("finishedConnecting");
	}
	
	public static void onExecute(String event) {
		Logger.get().logLocalEvent("execute " + event);
	}

	public static void onStateChange(String state) {
		Logger.get().logLocalEvent("state " + state);
	}
}
