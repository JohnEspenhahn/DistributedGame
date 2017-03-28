package custom_rpc;

import java.util.concurrent.BlockingQueue;

import inputport.datacomm.AReceiveRegistrarAndNotifier;
import port.trace.objects.ReceivedMessageQueued;

public class ACustomReceiveNotifier extends AReceiveRegistrarAndNotifier<Object> {
	
	private QueueProvider qProvider;
	
	public ACustomReceiveNotifier(QueueProvider qProvider) {
		this.qProvider = qProvider;
	}
	
	@Override
	public void notifyPortReceive (String aSource, Object aMessage) {	
		System.out.println (aSource + "->" + aMessage);
		super.notifyPortReceive(aSource, aMessage);
		
		BlockingQueue<Object> queue = this.qProvider.getQueue(aSource);
		
		while (true) {
			try {
				queue.put(aMessage);  // TODO should producer be blocking?
				break;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		ReceivedMessageQueued.newCase(this, queue, "put");
	}
}
