package custom_rpc;

import inputport.datacomm.AReceiveRegistrarAndNotifier;
import port.trace.objects.ReceivedMessageQueued;

public class ACustomReceiveNotifier extends AReceiveRegistrarAndNotifier<Object> {
	
	private QueueProvider qProvider;
	
	public ACustomReceiveNotifier(QueueProvider qProvider) {
		this.qProvider = qProvider;
	}
	
	@Override
	public void notifyPortReceive (String aSource, Object aMessage) {	
		super.notifyPortReceive(aSource, aMessage);
		
		BlockingQueueWrapper queue = this.qProvider.getQueueToNotify(aSource);
		
		try {
			queue.put(aMessage);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		ReceivedMessageQueued.newCase(this, queue, "Added message to queue " + queue.getName());
	}
}
