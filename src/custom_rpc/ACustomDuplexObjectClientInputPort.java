package custom_rpc;

import java.nio.ByteBuffer;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import inputport.datacomm.ReceiveRegistrarAndNotifier;
import inputport.datacomm.duplex.DuplexClientInputPort;
import inputport.datacomm.duplex.object.ADuplexObjectClientInputPort;
import inputport.datacomm.duplex.object.explicitreceive.AReceiveReturnMessage;
import inputport.datacomm.duplex.object.explicitreceive.ReceiveReturnMessage;
import port.trace.objects.ReceivedMessageDequeued;
import port.trace.objects.ReceivedMessageQueueCreated;

public class ACustomDuplexObjectClientInputPort extends ADuplexObjectClientInputPort implements QueueProvider {
	private static final int QUEUE_SIZE = 200;
	
	private BlockingQueue<Object> queue;
	
	public ACustomDuplexObjectClientInputPort(
			DuplexClientInputPort<ByteBuffer> aBBClientInputPort) {
		super(aBBClientInputPort);
		
		this.queue = new ArrayBlockingQueue<Object>(QUEUE_SIZE);
		ReceivedMessageQueueCreated.newCase(this, this.queue, "Created client input port queue");
	}
	
	@Override
	public BlockingQueue<Object> getQueue(String remoteClientName) {
		return queue;
	}
	
	@Override
	protected ReceiveRegistrarAndNotifier<Object> createReceiveRegistrarAndNotifier() {
		return new ACustomReceiveNotifier(this);
	}
	
	@Override
	public ReceiveReturnMessage<Object> receive(String aSource) {		
		System.err.println("Receive started");
		Object obj;
		while (true) {
			try {
				obj = queue.take();
				break;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		ReceivedMessageDequeued.newCase(this, queue, "take");
		ReceiveReturnMessage<Object> retVal = new AReceiveReturnMessage(aSource, obj); // TODO how to deserialize?
		System.out.println (aSource + "<-" + retVal);
		return retVal;
	}
}
