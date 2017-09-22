package nio_sims.test.trace;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class SocketChannelRead extends VectorTimedSocketChannelDataInfo {

	public SocketChannelRead(String aMessage, Object aFinder, SocketChannel aSocketChannel, ByteBuffer aByteBuffer) {
		super(aMessage, aFinder, aSocketChannel, aByteBuffer, VectorTimedType.RECEIVE);
	}

	public static SocketChannelRead newCase(Object aSource, SocketChannel aSocketChannel, ByteBuffer aByteBuffer) {
		String aMessage = "error?error";
		try {
			aMessage = addr2Str(aSocketChannel.getRemoteAddress())+"_"+addr2Str(aSocketChannel.getLocalAddress()) + "?"; // + new String(aByteBuffer.array());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		SocketChannelRead retVal = new SocketChannelRead(aMessage, aSource, aSocketChannel, aByteBuffer);
		retVal.announce();
		return retVal;
	}
}
