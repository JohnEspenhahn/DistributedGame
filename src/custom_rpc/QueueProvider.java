package custom_rpc;

import java.util.concurrent.BlockingQueue;

public interface QueueProvider {
	
	BlockingQueue<Object> getQueue(String aSource);

}
