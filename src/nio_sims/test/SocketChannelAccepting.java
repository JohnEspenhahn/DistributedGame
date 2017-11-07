package nio_sims.test;

import java.nio.channels.ServerSocketChannel;

import port.trace.nio.SocketChannelConnectFinished;
import util.trace.TraceableInfo;

public class SocketChannelAccepting extends TraceableInfo {
	public SocketChannelAccepting(String aMessage, Object aFinder, ServerSocketChannel aSocketChannel) {
		super(aMessage, aFinder);
	}

	public static SocketChannelAccepting newCase(Object aSource, ServerSocketChannel aSocketChannel) {
		String aMessage = aSocketChannel.toString();
		SocketChannelAccepting retVal = new SocketChannelAccepting(aMessage, aSource, aSocketChannel);
		retVal.announce();
		return retVal;
	}
}
