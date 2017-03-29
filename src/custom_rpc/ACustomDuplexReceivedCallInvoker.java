package custom_rpc;


import inputport.datacomm.duplex.DuplexInputPort;
import inputport.rpc.RPCRegistry;
import inputport.rpc.duplex.ADuplexReceivedCallInvoker;
import inputport.rpc.duplex.LocalRemoteReferenceTranslator;


public class ACustomDuplexReceivedCallInvoker extends ADuplexReceivedCallInvoker {
	
	public ACustomDuplexReceivedCallInvoker(LocalRemoteReferenceTranslator aRemoteHandler, DuplexInputPort<Object> aReplier, RPCRegistry theRPCRegistry) {
		super(aRemoteHandler, aReplier, theRPCRegistry);
	}
	
	protected void handleProcedureReturn(String aSender, Exception e) {
		System.out.println("Procedure call returning from:" + aSender + " with exception:" + e);
		replyPossiblyTransformedMethodReturnValue(aSender, null, null, e);
	}
	
}
