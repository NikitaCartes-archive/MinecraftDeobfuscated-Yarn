package net.minecraft.util.logging;

import com.mojang.logging.LogUtils;
import java.io.OutputStream;
import org.slf4j.Logger;

public class DebugLoggerPrintStream extends LoggerPrintStream {
	private static final Logger field_36382 = LogUtils.getLogger();

	public DebugLoggerPrintStream(String string, OutputStream outputStream) {
		super(string, outputStream);
	}

	@Override
	protected void log(String message) {
		StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
		StackTraceElement stackTraceElement = stackTraceElements[Math.min(3, stackTraceElements.length)];
		field_36382.info("[{}]@.({}:{}): {}", this.name, stackTraceElement.getFileName(), stackTraceElement.getLineNumber(), message);
	}
}
