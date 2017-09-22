package nio_sims.test.trace;

import java.nio.channels.ServerSocketChannel;

import port.trace.nio.SocketChannelInfo;

public class SocketChannelAccepting extends SocketChannelInfo {

	public SocketChannelAccepting(String aMessage, Object aFinder, ServerSocketChannel aSocketServerChannel) {
		super(aMessage, aFinder, aSocketServerChannel);
	}
	
	public static SocketChannelAccepting newCase(Object aSource, ServerSocketChannel aServerSocketChannel) {
		String aMessage = aServerSocketChannel.toString();
		SocketChannelAccepting retVal = new SocketChannelAccepting(aMessage, aSource, aServerSocketChannel);
    	retVal.announce();
    	return retVal;
	}
	
}
