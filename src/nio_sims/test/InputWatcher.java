package nio_sims.test;

import java.io.IOException;
import java.io.InputStream;

public class InputWatcher implements Runnable {

	private final InputHandler[] handlers;
	private final InputStream stream;
	
	protected InputWatcher(InputStream stream, InputHandler... handlers) {
		this.stream = stream;
		this.handlers = handlers;
	}
	
	@Override
	public void run() {
		int c;
		try {
			StringBuilder builder = new StringBuilder();
			while ((c = stream.read()) != -1) {
				builder.append((char) c);
				if (c != '\n') {
					continue;
				} else {
					String line = builder.toString();
					for (InputHandler h: handlers) {
						h.handleInputAsync(line);
					}
					
					builder.setLength(0);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		System.out.println("InputWatcher quitting");
	}

}
