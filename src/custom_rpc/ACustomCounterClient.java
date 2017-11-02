package custom_rpc;

import java.util.Scanner;

import examples.gipc.counter.layers.AMultiLayerCounterClient;
import inputport.datacomm.duplex.object.DuplexObjectInputPortSelector;
import inputport.datacomm.duplex.object.explicitreceive.ReceiveReturnMessage;
import inputport.rpc.duplex.DuplexReceivedCallInvokerSelector;
import inputport.rpc.duplex.DuplexSentCallCompleterSelector;
import port.trace.objects.ObjectTraceUtility;
import serialization.SerializerSelector;

public class ACustomCounterClient extends AMultiLayerCounterClient {
	public static void setFactories() {
		DuplexReceivedCallInvokerSelector
				.setReceivedCallInvokerFactory(new AnAsynchronousCustomDuplexReceivedCallInvokerFactory());
		
		DuplexSentCallCompleterSelector
				.setDuplexSentCallCompleterFactory(new ACustomSentFuncOrProcCallCompleterFactory());
		
		DuplexObjectInputPortSelector.setDuplexInputPortFactory(new ACustomDuplexObjectInputPortFactory());
	}

	public static void main(String[] args) {
		Scanner s = new Scanner(System.in);
		System.out.print("Name yourself: ");
		String name = s.nextLine();

		// BufferTraceUtility.setTracing();
		// RPCTraceUtility.setTracing();
		ObjectTraceUtility.setTracing();
		setFactories();
		init(name);
		setPort();
		sendByteBuffers();
		sendObjects();
		doOperations();
		while (true) {			
			ReceiveReturnMessage<Object> aReceivedMessage = gipcRegistry.getRPCClientPort().receive();
			if (aReceivedMessage == null) {
				break;
			}
			System.out.println("Received message:" + aReceivedMessage);
		}
	}

}
