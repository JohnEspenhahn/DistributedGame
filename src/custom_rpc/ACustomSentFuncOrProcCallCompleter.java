package custom_rpc;

import inputport.rpc.duplex.DuplexRPCInputPort;
import inputport.rpc.duplex.LocalRemoteReferenceTranslator;

public class ACustomSentFuncOrProcCallCompleter extends ACustomSentCallCompleter {
	
	public ACustomSentFuncOrProcCallCompleter(DuplexRPCInputPort anInputPort, LocalRemoteReferenceTranslator aRemoteHandler) {
		super(anInputPort, aRemoteHandler);
	}
	
	@Override
	public Object getReturnValueOfRemoteProcedureCall(String aRemoteEndPoint, Object aMessage) {
//		System.out.println ("getReturnValueOfRemoteProcedureCall called");
		// Do the same thing as remote function call. Will always get back null
		Object retVal = super.getReturnValueOfRemoteFunctionCall(aRemoteEndPoint, aMessage);
//		System.out.println ("Returning:" + retVal);
		return retVal;
	}

}
