package nio_sims.test;

import java.io.PrintStream;
import java.util.LinkedList;
import java.util.Queue;

public class InputForwarder implements InputHandler {

	private Queue<String> q;
	private PrintStream out;
	private String prefix;
	
	public InputForwarder(String prefix, PrintStream out) {
		if (out == null) 
			throw new IllegalArgumentException("out cannot be null in InputForwarder");
		
		this.q = new LinkedList<String>();
		this.prefix = prefix;
		this.out = out;
	}
	
	public InputForwarder(String prefix) {
		this(prefix, System.out);
	}
	
	@Override
	public void run() {
		while (true) {
			String value;
			synchronized (q) {
				while (q.isEmpty()) {
					try {
						q.wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				
				value = q.remove();
			}
			
			forwardValue(value);
		}
	}
	
	protected void forwardValue(String value) {
		out.print(value);
	}

	@Override
	public void handleInputAsync(String input) {
		synchronized (q) {
			q.add(input);
			q.notify();
		}
	}

}
