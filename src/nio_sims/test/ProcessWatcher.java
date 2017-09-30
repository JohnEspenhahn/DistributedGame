package nio_sims.test;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;

public class ProcessWatcher {
	static final String HEADLESS = "-Djava.awt.headless=true";

	private final String name;
	private final Class<?> clazz;
	
	private Process process;
	private PrintWriter printWriter;
	
	private InputDeserializer inputDeserializer;
	private InputForwarder inputForwarder;
	private InputWatcher inputWatcher;
	
	private InputForwarder errForwarder;
	private InputWatcher errWatcher;
	
	public ProcessWatcher(String name, Class<?> clazz) {
		this.name = name;
		this.clazz = clazz;
	}
	
	public ProcessWatcher start(String... args) throws IOException {
		if (process != null) {
			throw new RuntimeException("Already started");
		}
		
		String javaHome = System.getProperty("java.home");
		String javaBin = javaHome + File.separator + "bin" + File.separator + "java";
		String classPath = System.getProperty("java.class.path");
		String className = clazz.getCanonicalName();

		String[] all_args = new String[args.length + 5];
		all_args[0] = javaBin;
		all_args[1] = "-cp";
		all_args[2] = classPath;
		all_args[3] = HEADLESS;
		all_args[4] = className;
		System.arraycopy(args, 0, all_args, 5, args.length);
		
		ProcessBuilder builder = new ProcessBuilder(all_args);
		this.process = builder.start();
		
		// Allow writing to stdin
		 this.printWriter = new PrintWriter(this.process.getOutputStream());
		
		// Watch stdout for events
		this.inputDeserializer = new InputDeserializer();
		this.inputForwarder = new InputForwarder("[" + name + "] ");
		this.inputWatcher = new InputWatcher(this.process.getInputStream(), new InputHandler[] {
			inputDeserializer, inputForwarder
		});
		
		this.errForwarder = new InputForwarder("[" + name + "] ", System.err);
		this.errWatcher = new InputWatcher(this.process.getErrorStream(), new InputHandler[] {
			errForwarder
		});
		
		new Thread(inputDeserializer).start();
		new Thread(inputForwarder).start();
		new Thread(inputWatcher).start();
		
		new Thread(errForwarder).start();
		new Thread(errWatcher).start();
		
		return this;
	}
	
	public void waitForEvent(Class<?> event) {
		if (this.inputDeserializer != null)
			this.inputDeserializer.waitForEvent(event);
	}
	
	public void println(String s) {
		if (this.printWriter != null) {
			this.printWriter.println(s);
			this.printWriter.flush();
		}
	}
	
	public void terminate() {
		if (this.process != null)
			this.process.destroy();
	}
	
}
