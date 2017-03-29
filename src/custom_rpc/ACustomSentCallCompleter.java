package custom_rpc;

import inputport.rpc.duplex.ADuplexSentCallCompleter;
import inputport.rpc.duplex.DuplexRPCInputPort;
import inputport.rpc.duplex.LocalRemoteReferenceTranslator;

public class ACustomSentCallCompleter extends ADuplexSentCallCompleter {
	private DuplexRPCInputPort rpcInputPort;
	
	public ACustomSentCallCompleter(DuplexRPCInputPort anInputPort, LocalRemoteReferenceTranslator aRemoteHandler) {
		super(anInputPort, aRemoteHandler);
		
		rpcInputPort = anInputPort;
	}
	
	@Override
	protected Object waitForReturnValue(String aRemoteEndPoint) {
		return this.rpcInputPort.receive(aRemoteEndPoint);
	}
	
	@Override
	protected void returnValueReceived(String source, Object message) {
		// Doesn't need to do anything special
	}

}
