package custom_rpc;

import examples.gipc.counter.layers.AMultiLayerCounterServer;
import inputport.datacomm.duplex.object.explicitreceive.ReceiveReturnMessage;
import port.trace.objects.ObjectTraceUtility;

public class ACustomCounterServer extends AMultiLayerCounterServer {
	public static void setFactories() {
		ACustomCounterClient.setFactories();
	}

	public static void main(String[] args) {
		// BufferTraceUtility.setTracing();
		// RPCTraceUtility.setTracing();
//		ObjectTraceUtility.setTracing();
		setFactories();
		init();
		setPort();
		addListeners();
		while (true) {
			ReceiveReturnMessage<Object> aReceivedMessage = gipcRegistry.getRPCServerPort().receive();
			if (aReceivedMessage == null) {
				break;
			}
			System.out.println("Received message:" + aReceivedMessage);
		}
	}

}
