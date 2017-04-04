package custom_rpc;

import java.nio.ByteBuffer;

import inputport.datacomm.ReceiveRegistrarAndNotifier;
import inputport.datacomm.duplex.DuplexClientInputPort;
import inputport.datacomm.duplex.object.ADuplexObjectClientInputPort;
import inputport.datacomm.duplex.object.explicitreceive.AReceiveReturnMessage;
import inputport.datacomm.duplex.object.explicitreceive.ReceiveReturnMessage;
import port.trace.objects.ReceivedMessageDequeued;
import port.trace.objects.ReceivedMessageQueueCreated;

public class ACustomDuplexObjectClientInputPort extends ADuplexObjectClientInputPort implements QueueProvider {	
	private BlockingQueueWrapper queue;
	
	public ACustomDuplexObjectClientInputPort(
			DuplexClientInputPort<ByteBuffer> aBBClientInputPort) {
		super(aBBClientInputPort);
		
		this.queue = new BlockingQueueWrapper("Client");
		ReceivedMessageQueueCreated.newCase(this, this.queue, "Created client input port queue");
	}
	
	@Override
	public BlockingQueueWrapper getQueueToNotify(String remoteClientName) {
		return queue;
	}
	
	@Override
	protected ReceiveRegistrarAndNotifier<Object> createReceiveRegistrarAndNotifier() {
		return new ACustomReceiveNotifier(this);
	}
	
	@Override
	public ReceiveReturnMessage<Object> receive(String aSource) {		
//		System.err.println("Receive started");
		Object obj = queue.take();
		ReceivedMessageDequeued.newCase(this, queue, "Got message from queue");
		ReceiveReturnMessage<Object> retVal = new AReceiveReturnMessage<Object>(aSource, obj);
		return retVal;
	}
}
