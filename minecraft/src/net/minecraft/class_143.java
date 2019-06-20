package net.minecraft;

import java.lang.Thread.UncaughtExceptionHandler;
import org.apache.logging.log4j.Logger;

public class class_143 implements UncaughtExceptionHandler {
	private final Logger field_1115;

	public class_143(Logger logger) {
		this.field_1115 = logger;
	}

	public void uncaughtException(Thread thread, Throwable throwable) {
		this.field_1115.error("Caught previously unhandled exception :");
		this.field_1115.error(thread.getName(), throwable);
	}
}
