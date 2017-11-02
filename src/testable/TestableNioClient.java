package testable;

import java.util.List;

import nio_sims.ChangeRequest;

public interface TestableNioClient {
	
	List<ChangeRequest> getPendingChanges();

}
