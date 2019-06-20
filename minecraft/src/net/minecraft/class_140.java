package net.minecraft;

import java.lang.Thread.UncaughtExceptionHandler;
import org.apache.logging.log4j.Logger;

public class class_140 implements UncaughtExceptionHandler {
	private final Logger field_1113;

	public class_140(Logger logger) {
		this.field_1113 = logger;
	}

	public void uncaughtException(Thread thread, Throwable throwable) {
		this.field_1113.error("Caught previously unhandled exception :", throwable);
	}
}
