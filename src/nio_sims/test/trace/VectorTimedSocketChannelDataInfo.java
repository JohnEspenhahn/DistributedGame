package nio_sims.test.trace;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import org.github.com.jvec.JVec;

import port.trace.nio.SocketChannelDataInfo;

public class VectorTimedSocketChannelDataInfo extends SocketChannelDataInfo {

	private static final long serialVersionUID = 7715086359484383293L;
	private static JVec vcInfo;
	
	public enum VectorTimedType {
		SEND, RECEIVE
	}
	
	private VectorTimedType type;
	private ByteBuffer processedByteBuffer;
	
	public VectorTimedSocketChannelDataInfo(String aMessage, Object aFinder, SocketChannel channel, ByteBuffer buff, VectorTimedType type) {
		super(aMessage, aFinder, channel, buff);
		
		this.type = type;
	}
	
	protected static String addr2Str(SocketAddress addr) {
		String str = addr.toString();
		if (str.matches("^.*/\\d+\\.\\d+\\.\\d+\\.\\d+\\.\\:\\d+$")) {
			return str.replaceAll("^.*/", "");
		} else {
			return str;
		}
	}
	
	private byte[] getBytes(ByteBuffer bb) {
		byte[] bytes = new byte[bb.remaining()];
		bb.get(bytes);
		bb.flip();
		
		return bytes;
	}
	
	private void writeBytes(byte[] bytes) {
		this.processedByteBuffer = ByteBuffer.wrap(bytes);
	}
	
	private void writeBytes(ByteBuffer bb, byte[] bytes) {
		bb.put(bytes);
		bb.flip();
		
		this.processedByteBuffer = bb;
	}
	
	public ByteBuffer getProcessedBuffer() {
		if (this.processedByteBuffer != null)
			return this.processedByteBuffer;
		else
			return this.byteBuffer;
	}
	
	@Override
	public void announce() {
		// If enabled
		if (vcInfo != null) {
			try {
				if (type == VectorTimedType.SEND)
					writeBytes(vcInfo.prepareSend(getMessage(), getBytes(this.byteBuffer)));
				else if (type == VectorTimedType.RECEIVE) {
					this.byteBuffer.flip();
					writeBytes(this.byteBuffer, vcInfo.unpackReceive(getMessage(), getBytes(this.byteBuffer)));
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		super.announce();
	}
	
	public static void setVectorTimed(String processName) {
		if (vcInfo == null) {
			vcInfo = new JVec(processName, "basiclog");
			vcInfo.enableLogging();
		}
	}

}
