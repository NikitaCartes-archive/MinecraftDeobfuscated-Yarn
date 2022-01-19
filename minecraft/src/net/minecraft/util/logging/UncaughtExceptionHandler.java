package net.minecraft.util.logging;

import org.slf4j.Logger;

public class UncaughtExceptionHandler implements java.lang.Thread.UncaughtExceptionHandler {
	private final Logger logger;

	public UncaughtExceptionHandler(Logger logger) {
		this.logger = logger;
	}

	public void uncaughtException(Thread thread, Throwable throwable) {
		this.logger.error("Caught previously unhandled exception :");
		this.logger.error(thread.getName(), throwable);
	}
}
