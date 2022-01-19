package net.minecraft.util.logging;

import com.mojang.logging.LogUtils;
import java.io.OutputStream;
import java.io.PrintStream;
import javax.annotation.Nullable;
import org.slf4j.Logger;

public class LoggerPrintStream extends PrintStream {
	private static final Logger LOGGER = LogUtils.getLogger();
	protected final String name;

	public LoggerPrintStream(String name, OutputStream out) {
		super(out);
		this.name = name;
	}

	public void println(@Nullable String message) {
		this.log(message);
	}

	public void println(Object object) {
		this.log(String.valueOf(object));
	}

	protected void log(@Nullable String message) {
		LOGGER.info("[{}]: {}", this.name, message);
	}
}
