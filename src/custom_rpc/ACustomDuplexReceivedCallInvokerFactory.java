package custom_rpc;

import inputport.datacomm.duplex.DuplexInputPort;
import inputport.rpc.DuplexReceivedCallInvokerFactory;
import inputport.rpc.RPCRegistry;
import inputport.rpc.duplex.DuplexReceivedCallInvoker;
import inputport.rpc.duplex.LocalRemoteReferenceTranslator;

public class ACustomDuplexReceivedCallInvokerFactory implements DuplexReceivedCallInvokerFactory{

	@Override
	public DuplexReceivedCallInvoker createDuplexReceivedCallInvoker(
			LocalRemoteReferenceTranslator aRemoteHandler,
			DuplexInputPort<Object> aReplier, RPCRegistry anRPCRegistry) {
		return new ACustomDuplexReceivedCallInvoker(aRemoteHandler, aReplier, anRPCRegistry);
	}

}
