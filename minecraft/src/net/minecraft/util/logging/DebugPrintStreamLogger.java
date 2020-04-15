package net.minecraft.util.logging;

import java.io.OutputStream;

public class DebugPrintStreamLogger extends PrintStreamLogger {
	public DebugPrintStreamLogger(String string, OutputStream outputStream) {
		super(string, outputStream);
	}

	@Override
	protected void log(String message) {
		StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
		StackTraceElement stackTraceElement = stackTraceElements[Math.min(3, stackTraceElements.length)];
		LOGGER.info("[{}]@.({}:{}): {}", this.name, stackTraceElement.getFileName(), stackTraceElement.getLineNumber(), message);
	}
}