package nio_sims.test.trace;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class SocketChannelWritting extends VectorTimedSocketChannelDataInfo {

	public SocketChannelWritting(String aMessage, Object aFinder, SocketChannel aSocketChannel,
			ByteBuffer aByteBuffer) {
		super(aMessage, aFinder, aSocketChannel, aByteBuffer, VectorTimedType.SEND);
	}

	public static SocketChannelWritting newCase(Object aSource, SocketChannel aSocketChannel, ByteBuffer aByteBuffer) {
		String aMessage = "error!error";
		try {
			aMessage = addr2Str(aSocketChannel.getLocalAddress())+"_"+addr2Str(aSocketChannel.getRemoteAddress()) + "!"; // + new String(aByteBuffer.array());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		SocketChannelWritting retVal = new SocketChannelWritting(aMessage, aSource, aSocketChannel, aByteBuffer);
		retVal.announce();
		return retVal;
	}
}
