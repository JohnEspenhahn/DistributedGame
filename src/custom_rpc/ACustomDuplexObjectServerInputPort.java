package custom_rpc;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import inputport.datacomm.ReceiveRegistrarAndNotifier;
import inputport.datacomm.duplex.DuplexServerInputPort;
import inputport.datacomm.duplex.object.ADuplexObjectServerInputPort;
import inputport.datacomm.duplex.object.explicitreceive.AReceiveReturnMessage;
import inputport.datacomm.duplex.object.explicitreceive.ReceiveReturnMessage;
import port.trace.objects.ReceivedMessageDequeued;

public class ACustomDuplexObjectServerInputPort extends ADuplexObjectServerInputPort implements QueueProvider {	
	private Map<String, BlockingQueueWrapper> queues;
	private BlockingQueueWrapper genericQueue;
	
	public ACustomDuplexObjectServerInputPort(
			DuplexServerInputPort<ByteBuffer> aBBDuplexServerInputPort) {
		super(aBBDuplexServerInputPort);
		
		this.queues = new HashMap<String, BlockingQueueWrapper>();
		this.genericQueue = new BlockingQueueWrapper();
	}
	
	@Override
	public BlockingQueueWrapper getQueueToNotify(String remoteClientName) {
		BlockingQueueWrapper queue = getQueue(remoteClientName);		
		if (queue.isWaiting()) return queue;
		else return genericQueue;
	}
	
	private BlockingQueueWrapper getQueue(String remoteClientName) {
		if (remoteClientName == null || remoteClientName.equals("*")) {
			return genericQueue;
		}
		
		BlockingQueueWrapper queue = queues.get(remoteClientName);		
		if (queue == null) {
			queue = new BlockingQueueWrapper();
			queues.put(remoteClientName, queue);
		}
		
		return queue;
	}
	
	@Override
	protected ReceiveRegistrarAndNotifier<Object> createReceiveRegistrarAndNotifier() {
		return new ACustomReceiveNotifier(this);
	}
	
	@Override
	public ReceiveReturnMessage<Object> receive() {
		return receive(null);
	}
	
	@Override
	public ReceiveReturnMessage<Object> receive(String aSource) {
		System.err.println("Receive started");
		
		BlockingQueueWrapper queue = getQueue(aSource);
		
		Object obj = queue.take();
		ReceivedMessageDequeued.newCase(this, queue, "take");
		ReceiveReturnMessage<Object> retVal = new AReceiveReturnMessage<Object>(aSource, obj);
		System.out.println (aSource + "<-" + retVal);
		return retVal;
	}

}
