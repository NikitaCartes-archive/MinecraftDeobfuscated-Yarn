package net.minecraft.util;

import org.apache.logging.log4j.Logger;

public class UncaughtExceptionLogger implements java.lang.Thread.UncaughtExceptionHandler {
	private final Logger LOGGER;

	public UncaughtExceptionLogger(Logger logger) {
		this.LOGGER = logger;
	}

	public void uncaughtException(Thread thread, Throwable throwable) {
		this.LOGGER.error("Caught previously unhandled exception :", throwable);
	}
}
