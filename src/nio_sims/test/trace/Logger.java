package nio_sims.test.trace;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedMap;

import org.github.com.jvec.JVec;

import com.espenhahn.jformatter.JFormatter;
import com.espenhahn.jformatter.map.JSortedMapFormatter;
import com.espenhahn.jformatter.socket.SocketAddressFormatter;

public class Logger {
	
	private String REGISTER_CHANNEL_LOG = "connect";
	private JFormatter<? super SortedMap<?,?>> map_formatter = new JSortedMapFormatter<SortedMap<?,?>>();
	private JFormatter<SocketAddress> address_formatter = new SocketAddressFormatter();
	
	private Map<String,String> channels;
	private JVec vcInfo;
	
	private Logger() {
		this.channels = new HashMap<String,String>();
	}
	
	public String getProcessName() {
		if (vcInfo == null) return "";
		
		return vcInfo.getPid();
	}

	public void enable(String processName, String...allProcessNames) {
		if (vcInfo == null) {
			vcInfo = new JVec("basiclog", map_formatter, processName, allProcessNames);
			vcInfo.enableLogging(); // Format as a sorted map (key excluded)
			vcInfo.setWarnDynamicJoin(); // Warn if dynamic join b/c we're using sorted display (key excluded)		
		}
	}
	
	public void logLocalEvent(String log) {
		if (vcInfo != null) {
			vcInfo.logLocalEvent(log);
		}
	}
	
	public void registerChannel(SocketChannel channel, String otherProcessName) {
		if (vcInfo == null) return;
		
		try {
			channels.put(getChannelId(channel), otherProcessName);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		logLocalEvent(String.format("%s %s", REGISTER_CHANNEL_LOG, otherProcessName));
	}
	
	public String getChannelString() {
		StringBuilder b = new StringBuilder();
		for (Entry<String,String> e: channels.entrySet()) {
			b.append(e.getKey());
			b.append(":");
			b.append(getProcessName());
			b.append("->");
			b.append(e.getValue());
			b.append(";");
		}
		
		return b.toString();
	}
	
	private ByteBuffer wrap(String log, ByteBuffer bb) throws IOException {
		if (vcInfo == null) return bb;
		
		return wrapBytes(vcInfo.prepareSend(log, getBytes(bb)));
	}
	
	public ByteBuffer prepareSend(SocketChannel channel, ByteBuffer bb) throws IOException {
		return wrap(getChannelId(channel), bb);
	}
	
	private ByteBuffer unwrap(String log, ByteBuffer bb) throws IOException {
		if (vcInfo == null) return bb;
		
		bb.flip();
		byte[] unwrapped = vcInfo.unpackReceive(log, getBytes(bb));
		bb.put(unwrapped);
		bb.flip();
		
		return bb;
	}
	
	public ByteBuffer prepareReceive(SocketChannel channel, ByteBuffer bb) throws IOException {
		return unwrap(getChannelId(channel), bb);
	}
	
	private String getChannelId(SocketChannel channel) throws IOException {
		return String.format("%s_%s!", 
				address_formatter.format(channel.getLocalAddress()), 
				address_formatter.format(channel.getRemoteAddress()));
	}
	
	private byte[] getBytes(ByteBuffer bb) {
		byte[] bytes = new byte[bb.remaining()];
		bb.get(bytes);
		bb.flip();
		
		return bytes;
	}
	
	private ByteBuffer wrapBytes(byte[] bytes) {
		return ByteBuffer.wrap(bytes);
	}
	
	private static Logger instance;
	public static Logger get() {
		if (instance == null)
			instance = new Logger();
		
		return instance;
	}
	
}
