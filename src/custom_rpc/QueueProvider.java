package custom_rpc;

import java.util.concurrent.BlockingQueue;

public interface QueueProvider {
	
	BlockingQueueWrapper getQueueToNotify(String aSource);

}
