package custom_rpc;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class BlockingQueueWrapper {
	private static final int QUEUE_SIZE = 200;

	private String name;
	private BlockingQueue<Object> queue;
	private int waiting;
	
	public BlockingQueueWrapper(String name) {
		this.name = name;
		this.queue = new ArrayBlockingQueue<Object>(QUEUE_SIZE);
		this.waiting = 0;
	}
	
	public String getName() {
		return this.name;
	}
	
	public boolean isWaiting() {
		return this.waiting > 0;
	}
	
	public Object take() {
		Object obj;
		this.waiting += 1;
		while (true) {
			try {
				obj = this.queue.take();
				break;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		this.waiting -= 1;
		return obj;
	}
	
	public void put(Object obj) throws InterruptedException {
		this.queue.put(obj);
	}
}
