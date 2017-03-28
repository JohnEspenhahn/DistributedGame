package custom_rpc;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import inputport.datacomm.ReceiveRegistrarAndNotifier;
import inputport.datacomm.duplex.DuplexServerInputPort;
import inputport.datacomm.duplex.object.ADuplexObjectServerInputPort;
import inputport.datacomm.duplex.object.explicitreceive.AReceiveReturnMessage;
import inputport.datacomm.duplex.object.explicitreceive.ReceiveReturnMessage;
import port.trace.objects.ReceivedMessageDequeued;

public class ACustomDuplexObjectServerInputPort extends ADuplexObjectServerInputPort implements QueueProvider {
	private static final int QUEUE_SIZE = 200;
	
	private Map<String, BlockingQueue<Object>> queues;
	private BlockingQueue<Object> genericQueue;
	
	public ACustomDuplexObjectServerInputPort(
			DuplexServerInputPort<ByteBuffer> aBBDuplexServerInputPort) {
		super(aBBDuplexServerInputPort);
		
		this.queues = new HashMap<String, BlockingQueue<Object>>();
		this.genericQueue = new ArrayBlockingQueue<Object>(QUEUE_SIZE);
	}
	
	@Override
	public BlockingQueue<Object> getQueue(String remoteClientName) {
		if (remoteClientName == null) {
			return this.genericQueue;
		}
		
		BlockingQueue<Object> queue = queues.get(remoteClientName);
		if (queue == null) {
			queue = new ArrayBlockingQueue<Object>(QUEUE_SIZE);
			queues.put(remoteClientName, queue);
		}
		return queue;
	}
	
	@Override
	protected ReceiveRegistrarAndNotifier<Object> createReceiveRegistrarAndNotifier() {
		return new ACustomReceiveNotifier(this);
	}
	
	@Override
	public ReceiveReturnMessage<Object> receive(String aSource) {
		System.err.println("Receive started");
		
		BlockingQueue<Object> queue = getQueue(aSource);
		
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
