package net.minecraft.util;

import java.io.OutputStream;

public class DebugPrintStreamLogger extends PrintStreamLogger {
	public DebugPrintStreamLogger(String string, OutputStream outputStream) {
		super(string, outputStream);
	}

	@Override
	protected void method_12870(String string) {
		StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
		StackTraceElement stackTraceElement = stackTraceElements[Math.min(3, stackTraceElements.length)];
		LOGGER.info("[{}]@.({}:{}): {}", this.field_13383, stackTraceElement.getFileName(), stackTraceElement.getLineNumber(), string);
	}
}
