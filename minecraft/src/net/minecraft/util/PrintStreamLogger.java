package net.minecraft.util;

import java.io.OutputStream;
import java.io.PrintStream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PrintStreamLogger extends PrintStream {
	protected static final Logger LOGGER = LogManager.getLogger();
	protected final String field_13383;

	public PrintStreamLogger(String string, OutputStream outputStream) {
		super(outputStream);
		this.field_13383 = string;
	}

	public void println(String string) {
		this.method_12870(string);
	}

	public void println(Object object) {
		this.method_12870(String.valueOf(object));
	}

	protected void method_12870(String string) {
		LOGGER.info("[{}]: {}", this.field_13383, string);
	}
}
