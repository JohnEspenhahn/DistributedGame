package nio_sims.test;

import java.io.IOException;
import java.io.InputStream;

public class InputWatcher implements Runnable {
	private static int INPUT_WATCHERS = 0;

	private final InputHandler[] handlers;
	private final InputStream stream;
	
	protected InputWatcher(InputStream stream, InputHandler... handlers) {
		this.stream = stream;
		this.handlers = handlers;
	}
	
	@Override
	public void run() {
		System.out.println("InputWatcher starting");
		synchronized (InputWatcher.class) {
			INPUT_WATCHERS += 1;
		}
		
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
		} catch (IOException e) { }
		
		synchronized (InputWatcher.class) {
			INPUT_WATCHERS -= 1;
			System.out.println(INPUT_WATCHERS + " watcher(s) left running");
		}
	}

}