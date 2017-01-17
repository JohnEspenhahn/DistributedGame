package distrosims;

import java.nio.channels.SocketChannel;

public class ServerDataEvent {
	public NioBroadcastServer server;
	public SocketChannel socket;
	public byte[] data;
	
	public ServerDataEvent(NioBroadcastServer server, SocketChannel socket, byte[] data) {
		this.server = server;
		this.socket = socket;
		this.data = data;
	}
}
