package nio_sims.test.trace;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.SortedMap;
import java.util.TreeMap;

import org.github.com.jvec.JVec;
import org.github.com.jvec.formatter.JFormatter;
import org.github.com.jvec.formatter.JSortedMapFormatter;

import port.trace.nio.SocketChannelDataInfo;

public class VectorTimedSocketChannelDataInfo extends SocketChannelDataInfo {

	private static final long serialVersionUID = 7715086359484383293L;
	
	private static final JFormatter<? super SortedMap<?,?>> VC_SORTED_FORMATTER = new JSortedMapFormatter<SortedMap<?,?>>();
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
		String[] ip_port = addr.toString().replaceFirst("^.*/", "").split(":");
		String[] ips = ip_port[0].split("\\.");
		
		StringBuilder builder = new StringBuilder();
		
		int ip32bit = 0;
		for (int i = 0; i < ips.length; i++) {
			ip32bit |= Integer.parseInt(ips[ips.length-(i+1)]) << (8*i);
		}
		String ipHex = Integer.toHexString(ip32bit);
		for (int i = 8; i > ipHex.length(); i--)
			builder.append("0");
		builder.append(ipHex);
		builder.append("p");
		builder.append(ip_port[1]);
		
		return builder.toString();
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
	
	public static void setVectorTimed(String processName, String... otherProcessNames) {
		if (vcInfo == null) {
			vcInfo = new JVec("basiclog", VC_SORTED_FORMATTER, processName, otherProcessNames);
			vcInfo.enableLogging(); // Format as a sorted map (key excluded)
			vcInfo.setWarnDynamicJoin(); // Warn if dynamic join b/c we're using sorted display (key excluded)		
		}
	}

}
